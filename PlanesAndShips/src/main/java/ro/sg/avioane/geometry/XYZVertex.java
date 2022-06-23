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
    public XYZColor vertexColor = null;

    public XYZVertex(final XYZCoordinate coordinate){
        this.coordinate = coordinate;
    }

    public XYZVertex(final XYZCoordinate coordinate, final XYZCoordinate normal){
        this.coordinate = coordinate;
        this.normal = normal;
    }

    public XYZVertex(final XYZCoordinate coordinate, final XYZCoordinate normal, final XYZTextureUV uvTexture){
        this.coordinate = coordinate;
        this.normal = normal;
        this.uvTexture = uvTexture;
    }

    public XYZVertex(final XYZCoordinate coordinate, final XYZCoordinate normal, final XYZTextureUV uvTexture, final XYZColor vertexColor){
        this.coordinate = coordinate;
        this.normal = normal;
        this.uvTexture = uvTexture;
        this.vertexColor = vertexColor;
    }

    /**
     *
     * @param n an instance of XYZCoordinate or null if you want to disable normals for this object
     */
//    public void setNormal(final XYZCoordinate n){
//        this.normal = n;
//    }

    /**
     * @return the "normal" related to this vertex.
     */
    public Optional<XYZCoordinate> getNormal(){
        if(this.normal == null)
            return Optional.empty();
        return Optional.of(this.normal);
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
