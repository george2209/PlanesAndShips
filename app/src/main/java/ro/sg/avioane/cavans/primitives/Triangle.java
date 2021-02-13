package ro.sg.avioane.cavans.primitives;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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
        if (BuildConfig.DEBUG && !(triangleCoordinates.length == 3)) {
            throw new AssertionError("Assertion failed");
        }

        super.iColor = color;
        super.iVertexShaderCode =
                "uniform mat4 vpmMatrix;" + //view projection matrix
                "attribute vec4 vPosition;" + //item coordinates
                "void main() {" +
                "  gl_Position = vpmMatrix * vPosition;" + //set coordinates on the projection clip
                "}";
        super.iFragmentShaderCode = "precision mediump float;" + //set GPU to medium precision
                // (highp is not by all devices supported)
                //alternatively can be set to lowp for tests or low performance devices.
                //remove this in case of a Desktop App!!!
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}";
        super.doBuild(triangleCoordinates);
    }

    @Override
    public void draw(final float[] viewProjectionMatrix) {

        // counterclockwise orientation of the ordered vertices
        GLES20.glFrontFace(GL10.GL_CCW);
        // Enable face culling.
        GLES20.glEnable(GL10.GL_CULL_FACE); //--> make sure is disabled at clean up!
        // What faces to remove with the face culling.
        GLES20.glCullFace(GL10.GL_FRONT);

        //1. Ask OpenGL ES to load the program
        GLES20.glUseProgram(super.iHandleProgram);

        //2. Get a handle to the position vector
        final int positionHandle = GLES20.glGetAttribLocation(super.iHandleProgram, "vPosition");

        //3. Enable the loaded handle to load the data into
        GLES20.glEnableVertexAttribArray(positionHandle);

        //4. Load the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, AbstractGameCavan.NO_OF_COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                AbstractGameCavan.NO_OF_COORDINATES_PER_VERTEX * 4 /*4=FLOAT SIZE*/, super.iVertexBuffer);

        //5. Now load the color
        final int colorHandle = GLES20.glGetUniformLocation(super.iHandleProgram, "vColor");

        // 6. Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, super.iColor.asFloatArray(), 0);

        //7. do calculate transformation matrix
        final int vpmatrixHandle = GLES20.glGetUniformLocation(super.iHandleProgram, "vpmMatrix");

        //8. Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vpmatrixHandle, 1, false, viewProjectionMatrix, 0);

        //9. Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0,
                3 /*9 = 3 coordinates x 3values per coordinate / super.NO_OF_COORDINATES_PER_VERTEX */);

        //10. Cleanup: Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisable(GL10.GL_CULL_FACE);

    }



}
