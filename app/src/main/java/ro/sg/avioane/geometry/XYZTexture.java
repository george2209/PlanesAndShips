/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.geometry;

import android.graphics.Bitmap;

public class XYZTexture {

    public Bitmap iTexture = null;
    private final float iTextureArray[] = new float[2];

    public XYZTexture(final float u, final float v, Bitmap textureData){
        iTextureArray[0] = u;
        iTextureArray[1] = v;
        this.iTexture = textureData;
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

//    public void setTextureData(Bitmap textureData){
//        this.iTexture = textureData;
//    }

    public Bitmap getTextureData(){
        return this.iTexture;
    }
}
