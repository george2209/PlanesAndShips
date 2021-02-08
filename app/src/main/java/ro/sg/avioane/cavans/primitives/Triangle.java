package ro.sg.avioane.cavans.primitives;

import java.nio.FloatBuffer;

import ro.sg.avioane.geometry.XYZCoordinate;

/**
 * A class that encapsulate the primitive triangle.
 * The order of building the triangle is: counterclockwise order
 */
public class Triangle extends AbstractGameCavan{
    private FloatBuffer iVertexBuffer;

    public Triangle(final XYZCoordinate triangleCoordinates[]){
        
    }


    public Triangle(final XYZCoordinate top, final XYZCoordinate bottomLeft, final XYZCoordinate bottomRight){
        final float iTriangleCoords[] = new float[9]; //9=(top +  bottomLeft + bottomRight) * 3
        //iTriangleCoords[0]
    }
}
