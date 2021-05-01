/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.primitives;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZVertex;

/**
 * A class that encapsulate the primitive triangle.
 * The order of building the triangle is: counterclockwise order
 */
public class Triangle //extends AbstractGameCavan
{

//    /**
//     * creates a triangle from three coordinates.
//     * @param triangleCoordinates coordinates of the triangle defined as follows:
//     *                            top
//     *                            bottom-left
//     *                            bottom-right
//     * @param color RGBA color of this triangle
//     */
//    public Triangle(final XYZVertex[] triangleCoordinates, final XYZColor color) {
//        /*if (BuildConfig.DEBUG && !(triangleCoordinates.length == 3)) {
//            throw new AssertionError("Assertion failed");
//        }*/
//
////        super.iColor = color;
////        super.buildDrawOrderBuffer(this.buildIndexes());
////        super.buildVertexBuffer(triangleCoordinates);
////        super.compileGLSL();
//    }
//
//    private short[] buildIndexes(){
//        return new short[]{0, 2, 1, 3};
//    }
//
//    @Override
//    public void draw(final float[] viewMatrix, final float[] projectionMatrix) {
//        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_TRIANGLE_STRIP);
//    }


}
