/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.geometry;

import androidx.annotation.NonNull;

import ro.sg.avioane.BuildConfig;

public class XYZColor {
    public final static float OPAQUE = 1.0f;
    public final static float TRANSPARENT = 0.0f;

    public float red = 0.0f;
    public float green = 0.0f;
    public float blue = 0.0f;
    public float alpha = 0.0f;

    public XYZColor(){

    }

    public XYZColor(final float red, final float green, final float blue, final float alpha){
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     *
     * @param colorArray float array of size 3(alpha will be OPAQUE) or 4 (if alpha is also supplier)
     */
    public XYZColor(@NonNull final float[] colorArray){
        if(BuildConfig.DEBUG &&
                (colorArray.length <3 || colorArray.length > 4))
            throw new AssertionError("index out of range=" + colorArray.length);
        this.red = colorArray[0];
        this.green = colorArray[1];
        this.blue = colorArray[2];
        if(colorArray.length == 4)
            this.alpha = colorArray[3];
        else
            this.alpha = XYZColor.OPAQUE;
    }

    public float[] asFloatArray(){
        return new float[]{this.red, this.green, this.blue, this.alpha};
    }
}
