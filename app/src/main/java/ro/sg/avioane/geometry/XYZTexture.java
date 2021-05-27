/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.geometry;

import android.graphics.Bitmap;

public class XYZTexture {
//    private Bitmap iTextureBitmap = null;
//    private String iTextureName;

    private final float iTextureArray[] = new float[2];
    private int iTextureID = -1;


//    public XYZTexture(final float[] arr){
//        iTextureArray[0] = arr[0];
//        iTextureArray[1] = arr[1];
//    }

    public XYZTexture(final float u, final float v, final int textureID){
        iTextureArray[0] = u;
        iTextureArray[1] = v;
        iTextureID = textureID;
    }

//    public XYZTexture(final float u, final float v, final String textureName, final Bitmap textureBitmap){
//        iTextureArray[0] = u;
//        iTextureArray[1] = v;
//        iTextureArray[2] = 0; //start with texture index 0
//        this.iTextureBitmap = textureBitmap;
//        this.iTextureName = textureName;
//    }

    public float u(){
        return iTextureArray[0];
    }

    public float v(){
        return iTextureArray[1];
    }

    public int textureID(){
        return this.iTextureID;
    }

//    public void setU(final float u){
//        iTextureArray[0] = u;
//    }
//
//    public void setV(final float v){
//        iTextureArray[1] = v;
//    }

//    public Bitmap getTextureBitmap(){
//        return this.iTextureBitmap;
//    }
//
//    public String getTextureName(){
//        return  this.iTextureName;
//    }
}
