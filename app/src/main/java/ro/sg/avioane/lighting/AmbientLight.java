/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.lighting;

import java.util.Optional;
import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZColor;

/**
 * a class keeping and managing the ambient light.
 * You may want to do any operations on the AmbientLight inside the respective
 * onDraw method of the cavan as the access to the data is not synchronized to avoid
 * speed penalties.
 * The constructor is a singleton as it is expected to have only one Ambient light source (the Sun)
 */
public class AmbientLight {

    private static final AmbientLight iStaticInstance = new AmbientLight();
    private float iLightStrength = 1.0f;
    private final XYZColor iLightColor = new XYZColor(1.0f,1.0f,1.0f, XYZColor.OPAQUE);

    private AmbientLight(){
        this.setAmbientLightColor(new XYZColor(0.50f,0.80f,0.60f, XYZColor.OPAQUE));
        this.setAmbientColorStrength(1.0f);
    }

    /**
     * The method shall be called inside the OpenGL thread.If the access is made from more than
     * the OpenGL thread then a better synchronization is required (speed penalties may apply ;) ).
     * @return a static instance with standard values over the all App.
     */
    public static AmbientLight getStaticInstance(){
        return iStaticInstance;
    }

    /**
     * IMPORTANT!
     *      * The method is NOT synchronized.
     *      * This method must be called inside the OpenGL thread.
     * @param color a non-null instance of XYZColor as RGBA
     */
    public void setAmbientLightColor(final XYZColor color){
        this.iLightColor.setRed(color.red());
        this.iLightColor.setGreen(color.green());
        this.iLightColor.setBlue(color.blue());
        this.iLightColor.setAlpha(color.alpha());
    }

    /**
     *
     * @return a non null instance of the ambient color
     */
    public XYZColor getAmbientColor(){
        return this.iLightColor;
    }


    /**
     * @return the intensity of the ambient light as a float [0.0f..1.0f]
     */
    public float getAmbientColorStrength() {
        return this.iLightStrength;
    }

    /**
     * Set the ambient light intensity.
     * IMPORTANT!
     * The method is NOT synchronized. It is expected that access is only from the OpenGL thread.
     * @param ambientColorStrength the strength from [0.0 .. 1.0].
     */
    public void setAmbientColorStrength(final float ambientColorStrength) {
        if(BuildConfig.DEBUG && (
                ambientColorStrength < 0.0f || ambientColorStrength > 1.0f))
            throw new AssertionError("invalid ambient color strength=" + ambientColorStrength + " Expected range is from [0.0 .. 1.0] .");

        this.iLightStrength = ambientColorStrength;
    }





}
