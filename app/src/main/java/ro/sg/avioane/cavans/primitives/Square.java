package ro.sg.avioane.cavans.primitives;

import ro.sg.avioane.geometry.XYZCoordinate;

public class Square extends AbstractGameCavan{

    private final XYZCoordinate iUpperLeftCoordinate;
    private final float iEdgeSize;

    /**
     * create a square with the edgeSize and starting from upper-left coordinate
     * @param upperLeftCoordinate the x,y,z coordinate
     * @param edgeSize size from (0.0 to 1.0]
     */
    public Square(final XYZCoordinate upperLeftCoordinate, final float edgeSize){
        this.iUpperLeftCoordinate = upperLeftCoordinate;
        this.iEdgeSize = edgeSize;
    }

    private XYZCoordinate[] buildCoordinates(){
        final XYZCoordinate[] coordinatesArray = new XYZCoordinate[4];
        return coordinatesArray;
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {

    }
}
