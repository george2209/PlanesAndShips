/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.geometry;

import java.util.Optional;

import ro.sg.avioane.BuildConfig;

/**
 * A structure that encapsulate the X,Y,Z coordinates as Float.
 */
public class XYZVertex {

    /**
     * the vertex coordinate as X,Y,Z
     */
    public final XYZCoordinate coordinate;

    /**
     * optional U,V texture coordinate.
     */
    private XYZTextureUV uvTexture = null;

    /**
     * optional X,Y,Z normal coordinate
     */
    private XYZCoordinate normal = null;

    /**
     * opens the possibility to set the color per vertex instead of
     * using the global color defined inside
     * or using a material texture.
     * **Please note**
     * If there is a texture defined for the respective object then this variable will be ignored!
     */
    public XYZColor backgroundColor = null;


    public XYZVertex(final XYZCoordinate coordinate, final XYZCoordinate normal){
        this.coordinate = coordinate;
        this.normal = normal;
    }

    public XYZVertex(final XYZCoordinate coordinate, final XYZCoordinate normal, final XYZTextureUV uvTexture){
        this.coordinate = coordinate;
        this.normal = normal;
        this.uvTexture = uvTexture;
    }

    /**
     * @return the "normal" related to this vertex.
     */
    public XYZCoordinate getNormal(){
        if(BuildConfig.DEBUG && this.normal == null){
            throw new AssertionError("missing normal! illumination support cannot be implemented!");
        }
        return this.normal;
    }

    /**
     * @return a nullable object of XYZTextureUV
     */
    public Optional<XYZTextureUV> getUvTexture(){
        if(this.uvTexture == null)
            return Optional.empty();
        return Optional.of(this.uvTexture);
    }




}
