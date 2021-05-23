/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.lighting;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.MathGLUtils;

/**
 * a class keeping and managing the ambient light.
 * You may want to do any operations on the AmbientLight inside the respective
 * onDraw method of the cavan as the access to the data is not synchronized to avoid
 * speed penalties.
 */
public class AmbientLight {
    private XYZColor iAmbientColor = new XYZColor(0.96f,0.94f,0.56f, XYZColor.OPAQUE); //grey
    private float iAmbientColorStrength = 0.9f;

    private static final AmbientLight iStaticInstance = new AmbientLight();

    /**
     *
     * @return a static instance with standard values over the all App.
     * The reason fo this is to have for all objects the same ambient light behaviour unless
     * an ambient light is set for a specific object (cavan) separately (in such case a new
     * instance of AmbientLight will be separately build for that object).
     */
    public static AmbientLight getStaticInstance(){
        return iStaticInstance;
    }

    /**
     * TODO: to the computation only once when the either AmbientColor or AmbientColorStrength is
     * updated.
     * @return the complete (AmbientColor *  AmbientColorStrength) result as a new
     * XYZColor instance
     */
    public XYZColor getAmbientColorCalculated() {
        //keep the alpha not changed
        final float tmp = this.iAmbientColor.asFloatArray()[3];
        final float[] arr = MathGLUtils.Vector.multiplyByValue(
                this.iAmbientColor.asFloatArray(),
                this.iAmbientColorStrength);
        arr[3] = tmp;
        return new XYZColor(arr);
    }

    public XYZColor getAmbientColor() {
        return iAmbientColor;
    }

    public void setAmbientColor(XYZColor ambientColor) {
        this.iAmbientColor = ambientColor;
    }

    public float getAmbientColorStrength() {
        return iAmbientColorStrength;
    }

    public void setAmbientColorStrength(float ambientColorStrength) {
        this.iAmbientColorStrength = ambientColorStrength;
    }





}
