/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.geometry;

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
    public XYZTextureUV uvTexture = null;

    /**
     * optional X,Y,Z normal coordinate
     */
    public XYZCoordinate normal = null;

    /**
     * opens the possibility to set the color per vertex instead of
     * using the global color defined inside
     * or using a material texture.
     * **Please note**
     * If there is a texture defined for the respective object then this variable will be ignored!
     */
    public XYZColor backgroundColor = null;




    public XYZVertex(final XYZCoordinate coordinate){
        this.coordinate = coordinate;
    }




}
