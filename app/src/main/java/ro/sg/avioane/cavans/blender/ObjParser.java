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

import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.OpenGLUtils;

/**
 * This class will parse the bin file from the binary format and load its data into
 * memory.
 * It can be considered a deserializer
 */
public class ObjParser extends AbstractObjParser {
    public static final int PARSE_NO_OF_VERTICES = MaterialParser.PARSE_MATERIAL_END;
    public static final int PARSE_OBJ_NAME = PARSE_NO_OF_VERTICES + 1;
    public static final int PARSE_OBJ_MATERIAL_ID = PARSE_OBJ_NAME + 1;
    public static final int PARSE_OBJ_VERTICES = PARSE_OBJ_MATERIAL_ID + 1;

    private final MaterialParser iMaterialParser;
    private BlenderObjCavan iOBJArray[] = null;
    private int iParsingID = -1;
    private int iStateEngine = MaterialParser.PARSE_NO_OF_MATERIALS; //starting point

    public ObjParser(final Context context){
        this.iMaterialParser = new MaterialParser(context);
    }

    /**
     *
     * @param inputStream
     * @return
     */
    public boolean processStream(final BufferedInputStream inputStream){
        if(this.iStateEngine <= MaterialParser.PARSE_MATERIAL_MAP_D_FILE_NAME){
            this.iStateEngine = iMaterialParser.processStream(inputStream, this.iStateEngine);
            return this.iStateEngine != OpenGLUtils.INVALID_UNSIGNED_VALUE;
        } else {
            try {
                switch (this.iStateEngine) {
                    case PARSE_NO_OF_VERTICES: {
                        byte tmpArr[] = new byte[2]; //2 bytes short size
                        inputStream.read(tmpArr, 0, 2);
                        final short noOfOBJ = this.getByteArrayAsShort(tmpArr);
                        iOBJArray = new BlenderObjCavan[noOfOBJ];
                        for (int i = 0; i < noOfOBJ; i++) {
                            iOBJArray[i] = new BlenderObjCavan();
                        }
                        iParsingID = 0;
                    } break;
                    case PARSE_OBJ_NAME:{
                        byte tmpArr[] = new byte[2];
                        inputStream.read(tmpArr, 0, 2);
                        final short noOfChars = this.getByteArrayAsShort(tmpArr);
                        tmpArr = new byte[noOfChars];
                        inputStream.read(tmpArr, 0, noOfChars);
                        iOBJArray[iParsingID].iOBJName = new String(tmpArr, StandardCharsets.US_ASCII);
                    } break;
                    case PARSE_OBJ_MATERIAL_ID:{
                        byte tmpArr[] = new byte[2];
                        inputStream.read(tmpArr, 0, 2);
                        final short materialID = this.getByteArrayAsShort(tmpArr);
                        if(materialID >=0 ){
                            iOBJArray[iParsingID].iOBJMaterial = this.iMaterialParser.getMaterialAt(materialID);
                        }
                    } break;
                    case PARSE_OBJ_VERTICES:{
                        byte tmpArr[] = new byte[2];
                        inputStream.read(tmpArr, 0, 2);
                        final short noOfVertices = this.getByteArrayAsShort(tmpArr);
                        iOBJArray[iParsingID].initializeVertices(noOfVertices);
                        int SIZE = 3*Float.BYTES;
                        tmpArr = new byte[SIZE];
                        for (short i = 0; i < noOfVertices; i++) {
                            //vertex X,Y,Z
                            inputStream.read(tmpArr, 0, SIZE);
                            final XYZCoordinate vertexCoordinate = XYZCoordinate.fromByteArray(tmpArr);
                            iOBJArray[iParsingID].addVertexAt(new XYZVertex(vertexCoordinate), i);
                            //U,V
                            inputStream.read(tmpArr, 0, 1);
                            if(tmpArr[0] == 1){
                                tmpArr = new byte[Float.BYTES];
                                inputStream.read(tmpArr, 0, Float.BYTES);
                                final float u = super.getByteArrayAsFloat(tmpArr);
                                inputStream.read(tmpArr, 0, Float.BYTES);
                                final float v = super.getByteArrayAsFloat(tmpArr);

                                /////////////////////////////////////////iOBJArray[iParsingID].getVertexAt(i).texture = new XYZTextureUV(u, v, )
                            }
                        }
                    } break;
                    default: {
                        throw new AssertionError("unknown state engine=" + iStateEngine);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            this.iStateEngine++;
            return true;
        }
    }







}
