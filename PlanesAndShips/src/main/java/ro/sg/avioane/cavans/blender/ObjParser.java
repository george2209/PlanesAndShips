/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.blender;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZMaterial;
import ro.sg.avioane.geometry.XYZTextureUV;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.OpenGLUtils;

/**
 * This class will parse the bin file from the binary format and load its data into
 * memory.
 * It can be considered a deserializer
 */
public class ObjParser extends AbstractObjParser {
    public ObjParser(Context context) {
        super(context);
    }

    //    public static final int PARSE_NO_OF_OBJS = MaterialParser.PARSE_MATERIAL_END;
//    public static final int PARSE_OBJ_NAME = PARSE_NO_OF_OBJS + 1;
//    public static final int PARSE_OBJ_MATERIAL_ID = PARSE_OBJ_NAME + 1;
//    public static final int PARSE_OBJ_VERTICES = PARSE_OBJ_MATERIAL_ID + 1;
//    public static final int PARSE_DONE = PARSE_OBJ_VERTICES + 1;
//
//    private final MaterialParser iMaterialParser;
//    private BlenderObjCavan[] iOBJArray = null;
//    private int iParsingID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
//    private short iMaterialID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
//    private String iOBJName = null;
//    private int iStateEngine = MaterialParser.PARSE_NO_OF_MATERIALS; //starting point
//
//    public ObjParser(final Context context){
//        this.iMaterialParser = new MaterialParser(context);
//    }
//
//    public BlenderObjCavan[] getParsedObjects(){
//        return this.iOBJArray;
//    }
//
//    /**
//     *
//     * @return the state engine status
//     */
//    public int getStateEngine(){
//        return this.iStateEngine;
//    }
//
//    /**
//     *
//     * @param inputStream the OBJ as stream
//     * @return true if the stream was successful processed.
//     */
//    @Override
//    public boolean processStream(){
//        boolean retValue = false;
//        if(this.iStateEngine < MaterialParser.PARSE_MATERIAL_END){
//            this.iStateEngine = iMaterialParser.processStream(inputStream, this.iStateEngine);
//            retValue =  this.iStateEngine != OpenGLUtils.INVALID_UNSIGNED_VALUE;
//        } else {
//            retValue = true;
//            try {
//                switch (this.iStateEngine) {
//                    case PARSE_NO_OF_OBJS: {
//                        byte[] tmpArr = new byte[2]; //2 bytes short size
//                        inputStream.read(tmpArr, 0, 2);
//                        final short noOfOBJ = this.getByteArrayAsShort(tmpArr);
//                        iOBJArray = new BlenderObjCavan[noOfOBJ];
//                        iParsingID = 0;
//                    }
//                    break;
//                    case PARSE_OBJ_NAME: {
//                        byte[] tmpArr = new byte[2];
//                        inputStream.read(tmpArr, 0, 2);
//                        final short noOfChars = this.getByteArrayAsShort(tmpArr);
//                        tmpArr = new byte[noOfChars];
//                        final int r = inputStream.read(tmpArr, 0, noOfChars);
//                        this.iOBJName = new String(tmpArr, StandardCharsets.US_ASCII);
//                    }
//                    break;
//                    case PARSE_OBJ_MATERIAL_ID: {
//                        byte[] tmpArr = new byte[2];
//                        inputStream.read(tmpArr, 0, 2);
//                        iMaterialID = this.getByteArrayAsShort(tmpArr);
//                    }
//                    break;
//                    case PARSE_OBJ_VERTICES: {
//                        byte[] tmpArr = new byte[Integer.BYTES];
//                        inputStream.read(tmpArr, 0, Integer.BYTES);
//                        final int noOfVertices = this.getByteArrayAsInt(tmpArr);
//                        if(BuildConfig.DEBUG){
//                            assert (noOfVertices <= Short.MAX_VALUE);
//                        }
//                        final  XYZVertex[] verticesArr = new XYZVertex[noOfVertices];
//                        final short[] drawOrderArr = new short[3*noOfVertices];
//                        int drawOrderCounter = 0;
//
//                        int SIZE = 3 * Float.BYTES;
//                        tmpArr = new byte[SIZE];
//                        for (short i = 0; i < noOfVertices; i++) {
//                            //vertex X,Y,Z
//                            inputStream.read(tmpArr, 0, SIZE);
//                            final XYZCoordinate vertexCoordinate = XYZCoordinate.fromByteArray(tmpArr);
//                            //draw order CCW triangles
//                            for (int doa=0; doa<3; doa++) {
//                                drawOrderArr[drawOrderCounter + doa] = (short) (drawOrderCounter + doa);
//                            }
//                            drawOrderCounter+=3;
//                            //U,V
//                            inputStream.read(tmpArr, 0, 1);
//                            XYZTextureUV textureUV = null;
//                            if (tmpArr[0] == 1) {
//                                //tmpArr = new byte[Float.BYTES];
//                                inputStream.read(tmpArr, 0, Float.BYTES);
//                                final float u = super.getByteArrayAsFloat(tmpArr);
//                                inputStream.read(tmpArr, 0, Float.BYTES);
//                                final float v = super.getByteArrayAsFloat(tmpArr);
//                                textureUV = new XYZTextureUV(u, v);
//                            }
//                            //Normal
//                            inputStream.read(tmpArr, 0, 1);
//                            XYZCoordinate normal = null;
//                            if (tmpArr[0] == 1) {
//                                inputStream.read(tmpArr, 0, SIZE);
//                                normal = XYZCoordinate.fromByteArray(tmpArr);
//                            }
//                            verticesArr[i] = new XYZVertex(vertexCoordinate, normal, textureUV);
//                        }
//
//                        XYZMaterial material = null;
//                        if(iMaterialID >=0) {
//                            material = this.iMaterialParser.getMaterialAt(iMaterialID);
//                        }
//
//                        iOBJArray[iParsingID] = new BlenderObjCavan(iOBJName, verticesArr, drawOrderArr, material);
//
//                        iMaterialID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
//                        iOBJName = null;
//                        iParsingID++;
//                    }
//                    break;
//                    default: {
//                        try {
//                            inputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        throw new AssertionError("This shall never happened: unknown state engine=" + iStateEngine);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                retValue = false;
//            }
//
//            if (retValue) { //finish parsing condition
//                if (this.iStateEngine == PARSE_OBJ_VERTICES) {
//                    if (iParsingID < iOBJArray.length) {
//                        this.iStateEngine = PARSE_OBJ_NAME;
//                    } else {
//                        this.iStateEngine = PARSE_DONE;
//                    }
//                } else {
//                    this.iStateEngine++;
//                }
//            }
//        }
//
//        return retValue;
//    }




}
