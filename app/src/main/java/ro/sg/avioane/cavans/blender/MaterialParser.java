/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.blender;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZMaterial;
import ro.sg.avioane.util.OpenGLUtils;
import ro.sg.avioane.util.TextureUtils;

public class MaterialParser extends AbstractObjParser{
    public final static int PARSE_NO_OF_MATERIALS = 1;
    public final static int PARSE_MATERIAL_NAME = 2;
    public final static int PARSE_MATERIAL_KA = 3;
    public final static int PARSE_MATERIAL_KD = 4;
    public final static int PARSE_MATERIAL_KS = 5;
    public final static int PARSE_MATERIAL_KE = 6;
    public final static int PARSE_MATERIAL_NS = 7;
    public final static int PARSE_MATERIAL_Ni = 8;
    public final static int PARSE_MATERIAL_d = 9;
    public final static int PARSE_MATERIAL_ILLUM = 10;
    public final static int PARSE_MATERIAL_MAP_KA_FILE_NAME = 11;
    public final static int PARSE_MATERIAL_MAP_KD_FILE_NAME = 12;
    public final static int PARSE_MATERIAL_MAP_KS_FILE_NAME = 13;
    public final static int PARSE_MATERIAL_MAP_NS_FILE_NAME = 14;
    public final static int PARSE_MATERIAL_MAP_D_FILE_NAME = 15;
    public final static int PARSE_MATERIAL_END = 16;

    private XYZMaterial[] iArrMaterials = null;
    private int iParsingID = -1;
    private final Context iContext;

    public MaterialParser(final Context context){
        this.iContext = context;
    }

    /**
     * The method is not catching NullPointerException nor ArrayIndexOutOfBoundsException.
     * @param index
     * @return the material from the specified index
     */
    public XYZMaterial getMaterialAt(short index){
        return this.iArrMaterials[index];
    }

    /**
     *
     * @param inputStream
     * @param stateEngine current state engine ID
     * @return the next state engine ID or -1 in case of error
     */
    public int processStream(final BufferedInputStream inputStream, int stateEngine){
        try {
            switch (stateEngine) {
                case PARSE_NO_OF_MATERIALS: {
                    byte tmpArr[] = new byte[2];
                    inputStream.read(tmpArr, 0, 2);
                    this.parseData(tmpArr, stateEngine);
                }
                break;
                case PARSE_MATERIAL_MAP_KA_FILE_NAME:
                case PARSE_MATERIAL_MAP_KD_FILE_NAME:
                case PARSE_MATERIAL_MAP_KS_FILE_NAME:
                case PARSE_MATERIAL_MAP_NS_FILE_NAME:
                case PARSE_MATERIAL_MAP_D_FILE_NAME:
                case PARSE_MATERIAL_NAME: {
                    byte tmpArr[] = new byte[2];
                    inputStream.read(tmpArr, 0, 2);
                    final short noOfCharacters = this.getByteArrayAsShort(tmpArr);
                    if(noOfCharacters > 0) {
                        tmpArr = new byte[noOfCharacters];
                        inputStream.read(tmpArr, 0, noOfCharacters);
                        this.parseData(tmpArr, stateEngine);
                    }
                } break;
                case PARSE_MATERIAL_KA:
                case PARSE_MATERIAL_KD:
                case PARSE_MATERIAL_KS:
                case PARSE_MATERIAL_KE: {
                    byte tmpArr[] = new byte[1];
                    inputStream.read(tmpArr, 0, 1);
                    if(tmpArr[0] == 1){
                        final int SIZE = 3*Float.BYTES;
                        tmpArr = new byte[SIZE];
                        inputStream.read(tmpArr, 0, SIZE);
                        this.parseData(tmpArr, stateEngine);
                    } else {
                        stateEngine++;
                    }
                } break;
                case PARSE_MATERIAL_NS:
                case PARSE_MATERIAL_Ni:
                case PARSE_MATERIAL_d:
                {
                    byte tmpArr[] = new byte[4];
                    inputStream.read(tmpArr, 0, 4);
                    this.parseData(tmpArr, stateEngine);
                }break;
                case PARSE_MATERIAL_ILLUM:
                {
                    byte tmpArr[] = new byte[1];
                    inputStream.read(tmpArr, 0, 1);
                    this.parseData(tmpArr, stateEngine);
                } break;
                default: {
                    throw new AssertionError("unknown state engine=" + stateEngine);
                }
            }

            if(stateEngine == PARSE_MATERIAL_MAP_D_FILE_NAME &&
                    (iParsingID + 1 < iArrMaterials.length)){
                iParsingID++;
                stateEngine = PARSE_MATERIAL_NAME;
            } else {
                stateEngine++;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return OpenGLUtils.INVALID_UNSIGNED_VALUE;
        }
        return stateEngine;
    }

    /**
     * @param data
     * @throws IOException
     */
    private void parseData(final byte[] data, final int stateEngine) throws IOException {
        switch (stateEngine){
            case PARSE_NO_OF_MATERIALS:
            {
                short noOfMaterials = this.getByteArrayAsShort(data);
                if(noOfMaterials > 0){
                    iArrMaterials = new XYZMaterial[noOfMaterials];
                    for (int i = 0; i < noOfMaterials; i++) {
                        iArrMaterials[i] = new XYZMaterial();
                    }
                }
                iParsingID = 0;
            }break;
            case PARSE_MATERIAL_NAME:
            {
                iArrMaterials[iParsingID].materialName = new String(data, StandardCharsets.US_ASCII);
            }break;
            case PARSE_MATERIAL_KA:
            {
                iArrMaterials[iParsingID].materialKA = XYZCoordinate.fromByteArray(data);
            }break;
            case PARSE_MATERIAL_KD:
            {
                iArrMaterials[iParsingID].materialKD = XYZCoordinate.fromByteArray(data);
            }break;
            case PARSE_MATERIAL_KS:
            {
                iArrMaterials[iParsingID].materialKS = XYZCoordinate.fromByteArray(data);
            }break;
            case PARSE_MATERIAL_KE:
            {
                iArrMaterials[iParsingID].materialKE = XYZCoordinate.fromByteArray(data);
            }break;
            case PARSE_MATERIAL_NS:
            {
                iArrMaterials[iParsingID].NS = this.getByteArrayAsFloat(data);
            }break;
            case PARSE_MATERIAL_Ni:
            {
                iArrMaterials[iParsingID].Ni = this.getByteArrayAsFloat(data);
            }break;
            case PARSE_MATERIAL_d:
            {
                iArrMaterials[iParsingID].d = this.getByteArrayAsFloat(data);
            }break;
            case PARSE_MATERIAL_ILLUM:
            {
                iArrMaterials[iParsingID].ILLUM = data[0];
            } break;
            case PARSE_MATERIAL_MAP_KA_FILE_NAME:
            {
                iArrMaterials[iParsingID].mapKA_FileNameID = TextureUtils.getInstance().addTextureFromAssets(this.iContext, new String(data, StandardCharsets.US_ASCII));
            } break;
            case PARSE_MATERIAL_MAP_KD_FILE_NAME:
            {
                iArrMaterials[iParsingID].mapKD_FileNameID = TextureUtils.getInstance().addTextureFromAssets(this.iContext, new String(data, StandardCharsets.US_ASCII));
            } break;
            case PARSE_MATERIAL_MAP_KS_FILE_NAME:
            {
                iArrMaterials[iParsingID].mapKS_FileNameID = TextureUtils.getInstance().addTextureFromAssets(this.iContext, new String(data, StandardCharsets.US_ASCII));
            } break;
            case PARSE_MATERIAL_MAP_NS_FILE_NAME:
            {
                iArrMaterials[iParsingID].mapNS_FileNameID = TextureUtils.getInstance().addTextureFromAssets(this.iContext, new String(data, StandardCharsets.US_ASCII));
            } break;
            case PARSE_MATERIAL_MAP_D_FILE_NAME:
            {
                iArrMaterials[iParsingID].mapD_FileNameID = TextureUtils.getInstance().addTextureFromAssets(this.iContext, new String(data, StandardCharsets.US_ASCII));
            } break;
            default:
                throw new AssertionError("unknown state engine=" + stateEngine);
        }
    }

}
