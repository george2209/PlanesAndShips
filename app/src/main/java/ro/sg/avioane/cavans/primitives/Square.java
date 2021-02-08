package ro.sg.avioane.cavans.primitives;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;

public class Square extends AbstractGameCavan{

    private final static short[] SQUARE_DRAW_ORDER = { 0, 1, 2, 0, 2, 3 }; //counterclockwise order

    /**
     * creates a square from the four coordinates.
     * @param squareCoordinates coordinates of the square defined as follows:
     *                            top-left
     *                            bottom-left
     *                            bottom-right
     *                            top-right
     * @param color RGBA color of this triangle
     */
    public Square(final XYZCoordinate[] squareCoordinates, final XYZColor color){
        if (BuildConfig.DEBUG && !(squareCoordinates.length == 4)) {
            throw new AssertionError("Assertion failed");
        }

        this.iColor = color;
        buildVertexBuffer(squareCoordinates);
        buildDrawOrderBuffer(SQUARE_DRAW_ORDER);
    }
}
