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

    public XYZCoordinate coordinate = null;
    public XYZTextureUV uvTexture = null;
    public XYZCoordinate normal = null;

    //opens the possibility to set the color per vertex instead of having a material defined.
    //not sure if it will be used...
    public XYZColor backgroundColor = null;




    public XYZVertex(final XYZCoordinate coordinate){
        this.coordinate = coordinate;
    }




}
