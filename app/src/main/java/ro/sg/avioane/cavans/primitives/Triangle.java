package ro.sg.avioane.cavans.primitives;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;

/**
 * A class that encapsulate the primitive triangle.
 * The order of building the triangle is: counterclockwise order
 */
public class Triangle extends AbstractGameCavan{

    /**
     * creates a triangle from three coordinates.
     * @param triangleCoordinates coordinates of the triangle defined as follows:
     *                            top
     *                            bottom-left
     *                            bottom-right
     * @param color RGBA color of this triangle
     */
    public Triangle(final XYZCoordinate[] triangleCoordinates, final XYZColor color) {
        if (BuildConfig.DEBUG && !(triangleCoordinates.length == 3)) {
            throw new AssertionError("Assertion failed");
        }

        this.iColor = color;
        buildVertexBuffer(triangleCoordinates);
    }


}
