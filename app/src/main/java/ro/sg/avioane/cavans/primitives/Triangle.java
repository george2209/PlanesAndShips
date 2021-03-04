package ro.sg.avioane.cavans.primitives;

import android.opengl.GLES20;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

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
        /*if (BuildConfig.DEBUG && !(triangleCoordinates.length == 3)) {
            throw new AssertionError("Assertion failed");
        }*/

        super.iColor = color;
        super.buildDrawOrderBuffer(this.buildIndexes());
        super.buildVertexBuffer(triangleCoordinates);
        super.compileGLSL();
    }

    private short[] buildIndexes(){
        return new short[]{0, 2, 1, 3};
    }

    @Override
    public void draw(final float[] viewMatrix, final float[] modelMatrix, final float[] projectionMatrix) {
        super.doDraw(viewMatrix, modelMatrix, projectionMatrix, GL10.GL_TRIANGLE_STRIP);
    }



}
