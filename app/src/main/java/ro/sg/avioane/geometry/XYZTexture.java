/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.geometry;

import android.content.Context;
import android.graphics.Bitmap;

import ro.sg.avioane.util.TextureUtils;

public class XYZTexture {

    public Bitmap iTexture = null;
    private final float iTextureArray[] = new float[2];
    private String iTextureName;

    public XYZTexture(final float[] arr){
        iTextureArray[0] = arr[0];
        iTextureArray[1] = arr[1];
    }

    public float[] asArray(){
        return this.iTextureArray;
    }

//    public XYZTexture(final float u, final float v, final String textureName, final Context context){
//        iTextureArray[0] = u;
//        iTextureArray[1] = v;
//        this.iTexture = TextureUtils.loadTextureData(context,textureName);
//        this.iTextureName = textureName;
//    }

    public XYZTexture(final float u, final float v, final String textureName, final Bitmap textureData){
        iTextureArray[0] = u;
        iTextureArray[1] = v;
        this.iTexture = textureData;
        this.iTextureName = textureName;
    }

    public float u(){
        return iTextureArray[0];
    }

    public float v(){
        return iTextureArray[1];
    }

//    public void setU(final float u){
//        iTextureArray[0] = u;
//    }
//
//    public void setV(final float v){
//        iTextureArray[1] = v;
//    }

    public void setTextureData(final Bitmap textureData, final String textureName){
        this.iTexture = textureData;
        this.iTextureName = textureName;
    }

    public Bitmap getTextureData(){
        return this.iTexture;
    }

    public String getTextureName(){
        return  this.iTextureName;
    }
}
