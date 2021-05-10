/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.lighting;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.util.MathGLUtils;

/**
 * a class keeping and managing the ambient light.
 * You may want to do any operations on the AmbientLight inside the respective
 * onDraw method of the cavan as the access to the data is not synchronized to avoid
 * speed penalties.
 */
public class AmbientLight {
    private XYZColor iAmbientColor = new XYZColor(5,5,5, XYZColor.OPAQUE); //grey
    private float iAmbientColorStrength = 0.1f;

    /**
     *
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
