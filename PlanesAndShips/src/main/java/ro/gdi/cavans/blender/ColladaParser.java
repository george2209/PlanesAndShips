/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.gdi.cavans.blender;

import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_UNDEFINED;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_NORMALS;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_OWN_COLOR;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_UV_DATA_MATERIAL;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import ro.gdi.cavans.blender.collada.ColladaFileObjectDescriptor;
import ro.gdi.cavans.blender.collada.MaterialParserHelper;
import ro.gdi.cavans.GameObject;
import ro.gdi.cavans.GameCavanMesh;
import ro.gdi.cavans.GameObjectComponent;
import ro.gdi.cavans.blender.collada.ColladaParserListener;
import ro.gdi.cavans.blender.collada.MeshParserHelper;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.BackgroundTask;
import ro.sg.avioane.util.OpenGLUtils;

public class ColladaParser extends AbstractObjParser{

    private final MaterialParserHelper iMaterialParser = new MaterialParserHelper();
    private final MeshParserHelper iMeshParser = new MeshParserHelper();
    private GameObject[] iGameObjects = null;
    private BufferedInputStream iInputStream = null;
    private BackgroundTask iOBJLoaderProcessor = null;
    private ColladaParserListener iListener = null;
    private int iGameObjectsIndex = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    private ColladaFileObjectDescriptor[] iColladaArray = null;
    private int iVerticesProperties = SHADER_UNDEFINED;
    private int iVertexStride = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    private final short TEMP_BUFF_SIZE = 4096;

    public ColladaParser(Context context) {
        super(context);
    }

    /***
     * As there is only one class that listen to the events of this class object then there will be
     * only one listener.
     * @param listener
     */
    public void addColladaParserListener(final ColladaParserListener listener){
        assert (this.iListener == null);
        this.iListener = listener;
    }

    /**
     * Load a Collada (.DAE) file collection and prepare the objects with their names <code>objectNames</code>.
     * @param colladaFilesArray
     */
    private void loadFiles(final ColladaFileObjectDescriptor[] colladaFilesArray)  {
        assert (this.iInputStream == null);
        assert (colladaFilesArray.length > 0);
        assert (iListener != null);

        this.iGameObjectsIndex = 0;
        this.iColladaArray = colladaFilesArray;
        this.iGameObjects = new GameObject[iColladaArray.length];
    }


    /**
     * safe stop the parsing thread.
     */
    public void stopParsing(){
        if(iOBJLoaderProcessor.isRunning())
            iOBJLoaderProcessor.stop();
    }

    /**
     * starts parsing the files in the background on a separate thread.
     * The notification will be sent back via OpenGL thread (GUI thread) so make sure you provide
     * an Activity that is managed by OpenGL (or another active GUI).
     * The notification will come in case of success via notifyParseFinished with the respective
     * array of objects parsed. In order to check if the
     * @param activity
     * @param colladaFilesArray
     */
    public void startParsing(final ColladaFileObjectDescriptor[] colladaFilesArray, final Activity activity){
        this.loadFiles(colladaFilesArray);
        iOBJLoaderProcessor = new BackgroundTask(activity) {
            @Override
            public boolean preloadData() {
                try {
                    iGameObjects[iGameObjectsIndex] = new GameObject(iColladaArray[iGameObjectsIndex].objectName);
                    iInputStream = new BufferedInputStream(
                            iContext.getAssets().open("obj/" + iColladaArray[iGameObjectsIndex].fileName));
                    iSTATUS_ENGINE = PARSER_ENGINE_START;
                } catch (Exception e) {
                    e.printStackTrace();
                    this.stop(); //signal fatal error
                    return false;
                }
                return true;
            }

            @Override
            public boolean runInBackground() {
                final long parsingTime = (System.currentTimeMillis());
                boolean isSuccess = processStream();
                final long diff = System.currentTimeMillis() - parsingTime;
                if(diff > 1)
                    System.out.println("for iSTATUS_ENGINE=" + iSTATUS_ENGINE + " needed:" + diff + " ms");

                if(isSuccess){
                    if(iSTATUS_ENGINE == PARSER_ENGINE_FINISHED){
                        try {
                            iInputStream.close();
                            iInputStream = null;
                            iGameObjectsIndex++;
                            if(iGameObjectsIndex < iColladaArray.length) {
                                isSuccess = preloadData();
                            } else {
                                isSuccess = false; //stop and let it report success
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            this.stop(); //signal fatal error
                        }
                    }
                } else {
                    this.stop(); //signal fatal error
                }
                return isSuccess;
            }

            @Override
            public void notifyThreadFinished() {
                iListener.notifyParseFinished(iGameObjects);
            }

            @Override
            public void notifyThreadInterrupted() {
                iListener.notifyParseFailed();
            }
        };

        iOBJLoaderProcessor.start();
    }

    /**
     * @return true in case of a successful parsing. Error means returning false.
     */
    private boolean processStream(){
        boolean isSuccess = true;
        try {
            if(super.iSTATUS_ENGINE < PARSER_ENGINE_MATERIAL_FINISHED)
                parseMaterials(iInputStream);
            else if (super.iSTATUS_ENGINE >= PARSER_ENGINE_MATERIAL_FINISHED){
                parseMeshObjects(iInputStream);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
            isSuccess = false;
        }

        return isSuccess;
    }

    /***
     *
     * @param inputStream
     * @throws IOException
     */
    private void parseMeshObjects(BufferedInputStream inputStream) throws IOException {
        switch (super.iSTATUS_ENGINE) {
            case PARSER_ENGINE_MATERIAL_FINISHED:
            {
                //no of components
                this.iGameObjects[iGameObjectsIndex].initiateObjectsArray(
                        new GameObjectComponent[super.getByteArrayAsInt(inputStream)]
                );
                super.iSTATUS_ENGINE = PARSER_ENGINE_COMPONENT_NAME;
            } break;
            case PARSER_ENGINE_COMPONENT_NAME:
            {
                final short nameSize = super.getByteArrayAsShort(inputStream);
                this.iGameObjects[iGameObjectsIndex].addGameObjectComponent(
                        new GameObjectComponent(
                                super.getByteArrayAsString(
                                        inputStream, StandardCharsets.US_ASCII,nameSize
                                )
                        )
                );
                super.iSTATUS_ENGINE = PARSER_ENGINE_COMPONENT_MESHES_NO;
            } break;
            case PARSER_ENGINE_COMPONENT_MESHES_NO:{
                final int meshesNo = super.getByteArrayAsInt(inputStream);
                final GameObjectComponent component = this.iGameObjects[iGameObjectsIndex].
                        getLastComponent();
                component.initiateObjectsArray(new GameCavanMesh[meshesNo]);
                this.iMeshParser.reset();
                super.iSTATUS_ENGINE = PARSER_ENGINE_MESH_MATERIAL_ID;
            } break;
            case PARSER_ENGINE_MESH_MATERIAL_ID:{
                final short meshMaterialID = super.getByteArrayAsShort(inputStream);
                this.iMeshParser.iMaterial = this.iMaterialParser.getMaterial(meshMaterialID);
                super.iSTATUS_ENGINE = PARSER_ENGINE_MESH_VERTEX_NO;
            } break;
            case PARSER_ENGINE_MESH_VERTEX_NO:{
                final int noOfVertices = super.getByteArrayAsInt(inputStream);
                this.iMeshParser.iVerticesArray = new XYZVertex[noOfVertices];
                this.iMeshParser.iVertexPositionIndex = 0;
                super.iSTATUS_ENGINE = PARSER_ENGINE_MESH_VERTEX_PROPERTIES;
            } break;
            case PARSER_ENGINE_MESH_VERTEX_PROPERTIES:{
                //uv
                this.iVerticesProperties |= super.getByteArrayAsBoolean(inputStream) ? SHADER_VERTICES_WITH_UV_DATA_MATERIAL : SHADER_UNDEFINED;
                //normal
                this.iVerticesProperties |= super.getByteArrayAsBoolean(inputStream) ? SHADER_VERTICES_WITH_NORMALS : SHADER_UNDEFINED;
                //color per vertex
                this.iVerticesProperties |= super.getByteArrayAsBoolean(inputStream) ? SHADER_VERTICES_WITH_OWN_COLOR : SHADER_UNDEFINED;
                //vertex stride
                iVertexStride = Float.BYTES * (3 + //x,y,z
                        ((this.iVerticesProperties & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) > 0 ? 2 : 0) + //uv
                        ((this.iVerticesProperties & SHADER_VERTICES_WITH_NORMALS) > 0 ? 3 : 0) + //normals
                        ((this.iVerticesProperties & SHADER_VERTICES_WITH_OWN_COLOR) > 0 ? 4 : 0) ); //color
                super.iSTATUS_ENGINE = PARSER_ENGINE_VERTEX_POSITION;
            }break;
            case PARSER_ENGINE_VERTEX_POSITION:{
                final byte[] buffer = new byte[TEMP_BUFF_SIZE];
                final int MAX_PROCESSED_VERTICES = TEMP_BUFF_SIZE / iVertexStride;
                final int VERTICES_TO_BE_PROCESSED =
                        (this.iMeshParser.iVertexPositionIndex + MAX_PROCESSED_VERTICES < this.iMeshParser.iVerticesArray.length) ?
                                MAX_PROCESSED_VERTICES : this.iMeshParser.iVerticesArray.length - this.iMeshParser.iVertexPositionIndex;
                inputStream.read(buffer, 0, VERTICES_TO_BE_PROCESSED * iVertexStride);
                final ByteBuffer bb = ByteBuffer.wrap(buffer);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                for(int vr=0; vr<VERTICES_TO_BE_PROCESSED; vr++){
                    this.iMeshParser.iVerticesArray[this.iMeshParser.iVertexPositionIndex] =
                            super.getByteArrayAsVertex(bb, vr * this.iVertexStride,
                                    this.iVertexStride, this.iVerticesProperties);
                    this.iMeshParser.iVertexPositionIndex++;
                }

                if(this.iMeshParser.iVertexPositionIndex == this.iMeshParser.iVerticesArray.length){
                    super.iSTATUS_ENGINE = PARSER_ENGINE_VERTEX_INDEX_SIZE;
                }
            } break;
            case PARSER_ENGINE_VERTEX_INDEX_SIZE:{
                final int indexNo = super.getByteArrayAsInt(inputStream);
                assert (indexNo > 0);
                this.iMeshParser.iIndexDrawOrder = new int[indexNo];
                this.iMeshParser.iIndexDrawOrderIndex = 0;
                super.iSTATUS_ENGINE = PARSER_ENGINE_VERTEX_INDEX;
            } break;
            case PARSER_ENGINE_VERTEX_INDEX:{
                this.iVertexStride = OpenGLUtils.INVALID_UNSIGNED_VALUE;
                this.iVerticesProperties = SHADER_UNDEFINED;

                final byte[] buffer = new byte[TEMP_BUFF_SIZE];
                final int MAX_PROCESSED_ELEMENTS = TEMP_BUFF_SIZE / Integer.BYTES;
                final int ELEMENTS_TO_BE_PROCESSED =
                        (this.iMeshParser.iIndexDrawOrderIndex + MAX_PROCESSED_ELEMENTS < this.iMeshParser.iIndexDrawOrder.length) ?
                                MAX_PROCESSED_ELEMENTS : this.iMeshParser.iIndexDrawOrder.length - this.iMeshParser.iIndexDrawOrderIndex;
                inputStream.read(buffer, 0, ELEMENTS_TO_BE_PROCESSED * Integer.BYTES);
                final ByteBuffer bb = ByteBuffer.wrap(buffer);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                for(int vr=0; vr<ELEMENTS_TO_BE_PROCESSED; vr++){
                    this.iMeshParser.iIndexDrawOrder[this.iMeshParser.iIndexDrawOrderIndex++] =
                            bb.getInt(vr * Integer.BYTES);
                }
                if(this.iMeshParser.iIndexDrawOrderIndex == this.iMeshParser.iIndexDrawOrder.length){
                    final GameObjectComponent component = this.iGameObjects[iGameObjectsIndex].
                            getLastComponent();
                    component.addGameObjectComponent(new GameCavanMesh(
                            this.iMeshParser.iVerticesArray,
                            this.iMeshParser.iIndexDrawOrder,
                            this.iMeshParser.iMaterial, this.iMeshParser.GL_FORM_TYPE));
                    this.iMeshParser.reset();
                    //check if we have more meshed to be added
                    if(!component.isFull()){
                        super.iSTATUS_ENGINE = PARSER_ENGINE_MESH_MATERIAL_ID;
                    } else {
                        //check if there are more components to be added
                        if(!this.iGameObjects[iGameObjectsIndex].isFull()){
                            super.iSTATUS_ENGINE = PARSER_ENGINE_COMPONENT_NAME;
                        } else {
                            //DONE!
                            super.iSTATUS_ENGINE = PARSER_ENGINE_FINISHED;
                        }
                    }
                }
            } break;
            default:
                throw new UnsupportedOperationException("Unknown status engine: " + super.iSTATUS_ENGINE);
        }
    }

    /***
     *
     * @param inputStream
     * @throws IOException
     */
    private void parseMaterials(BufferedInputStream inputStream) throws IOException {
        switch (super.iSTATUS_ENGINE) {
            case PARSER_ENGINE_START: {
                //get number of materials
                this.iMaterialParser.setNoOfMaterials(super.getByteArrayAsInt(inputStream));
                super.iSTATUS_ENGINE = PARSER_ENGINE_MATERIAL_NAME;
            }
            break;
            case PARSER_ENGINE_MATERIAL_NAME: {
                final short charLength = super.getByteArrayAsShort(inputStream);
                this.iMaterialParser.addNewMaterial(super.getByteArrayAsString(inputStream, StandardCharsets.US_ASCII, charLength));
                super.iSTATUS_ENGINE = PARSER_ENGINE_MATERIAL_AMBIENT_COLOR;
            }
            break;
            case PARSER_ENGINE_MATERIAL_AMBIENT_COLOR:
            case PARSER_ENGINE_MATERIAL_DIFFUSE_COLOR: {
                if (super.getByteArrayAsBoolean(inputStream)) {
                    final XYZColor illuminationColor = new XYZColor(
                            super.getByteArrayAsFloat(inputStream),//r
                            super.getByteArrayAsFloat(inputStream),//g
                            super.getByteArrayAsFloat(inputStream),//b
                            super.getByteArrayAsFloat(inputStream)//a
                    );
                    if (super.iSTATUS_ENGINE == PARSER_ENGINE_MATERIAL_AMBIENT_COLOR) {
                        this.iMaterialParser.setMaterialAmbientColor(illuminationColor);
                    } else if (super.iSTATUS_ENGINE == PARSER_ENGINE_MATERIAL_DIFFUSE_COLOR) {
                        this.iMaterialParser.setMaterialDiffuseColor(illuminationColor);
                    }
                }
                super.iSTATUS_ENGINE++;
            }
            break;
            case PARSER_ENGINE_MATERIAL_KA_TEXTURE: {
                final short noOfChars = super.getByteArrayAsShort(inputStream);
                if (noOfChars > 0) {
                    final String fileName = super.getByteArrayAsString(
                            inputStream,
                            StandardCharsets.US_ASCII, noOfChars);
                    iMaterialParser.setMaterialAmbientTexture(fileName, super.iContext);
                }
                super.iSTATUS_ENGINE = PARSER_ENGINE_MATERIAL_KD_TEXTURE;
            } break;
            case PARSER_ENGINE_MATERIAL_KD_TEXTURE:{
                final short noOfChars = super.getByteArrayAsShort(inputStream);
                if (noOfChars > 0) {
                    final String fileName = super.getByteArrayAsString(
                            inputStream,
                            StandardCharsets.US_ASCII, noOfChars);
                    iMaterialParser.setMaterialDiffuseTexture(fileName, super.iContext);
                }
                if(iMaterialParser.isMaterialsFullLoaded())
                    super.iSTATUS_ENGINE = PARSER_ENGINE_MATERIAL_FINISHED;
                else
                    super.iSTATUS_ENGINE = PARSER_ENGINE_MATERIAL_NAME;

            } break;
            default:
                throw new UnsupportedOperationException("Unknown status engine: " + super.iSTATUS_ENGINE);
        }
    }
}
