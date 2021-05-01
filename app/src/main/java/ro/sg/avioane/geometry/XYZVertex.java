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
    public XYZColor color = null; //opens the possibility to set the color per vertex
    public XYZTexture texture = null;
    public XYZCoordinate normal = null;

    public XYZVertex(final XYZCoordinate coordinate){
        this.coordinate = coordinate;
    }




}
