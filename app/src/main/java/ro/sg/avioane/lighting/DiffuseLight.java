/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.lighting;

import android.graphics.Path;

import java.util.Optional;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;

/**
 * Diffuse light is a kind of light that is placed "far away" and as a result instead of having
 * a "location" of light all that is needed is actually a "direction of the light" as the location
 * is "too far away" to be taken in consideration anymore.
 * At this moment we only use one such light (the Sun) and as a result the class constructor is
 * accessible via the Singleton design pattern.
 */
public class DiffuseLight{

    private static final DiffuseLight iInstance = new DiffuseLight();

    private final XYZColor iLightColor = new XYZColor(1.0f,1.0f,1.0f, XYZColor.OPAQUE);
    private final XYZCoordinate iLightDirection = new XYZCoordinate(0.0f, 0.0f, 0.0f);

    private DiffuseLight(){
        this.setDiffuseLightColor(AmbientLight.getStaticInstance().getAmbientColor());
    }

    /**
     * The method shall be called inside the OpenGL thread. If the access is made from more than
     * the OpenGL thread then a better synchronization is required (speed penalties may apply ;) ).
     * @return a static instance with standard values over the all App.
     */
    public static synchronized DiffuseLight getStaticInstance(){
        return iInstance;
    }

    /**
     * IMPORTANT!
     *      * The method is NOT synchronized.
     *      * This method must be called inside the OpenGL thread.
     * @param color a non-null instance of XYZColor as RGBA
     */
    public void setDiffuseLightColor(final XYZColor color){
        this.iLightColor.setRed(color.red());
        this.iLightColor.setGreen(color.green());
        this.iLightColor.setBlue(color.blue());
        this.iLightColor.setAlpha(color.alpha());
    }

    /**
     *
     * @return a non null instance of the diffuse color
     */
    public XYZColor getDiffuseColor(){
        return this.iLightColor;
    }

    /**
     * IMPORTANT!
     *      *      * The method is NOT synchronized.
     *      *      * This method must be called inside the OpenGL thread.
     * @param direction a vector of the diffuse light
     */
    public void setDiffuseLightDirection(final XYZCoordinate direction){
        this.iLightDirection.setX(direction.x());
        this.iLightDirection.setY(direction.y());
        this.iLightDirection.setZ(direction.z());
    }

    /**
     *
     * @return a non null instance of the diffuse light vector
     */
    public XYZCoordinate getDiffuseLightDirection(){
        return this.iLightDirection;
    }

}
