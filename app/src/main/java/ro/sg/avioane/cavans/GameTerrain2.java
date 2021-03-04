package ro.sg.avioane.cavans;

import android.opengl.GLES20;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.cavans.primitives.AbstractGameCavan;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;

public class GameTerrain2 extends AbstractGameCavan {

    //private float[] iMVPMatrix = new float[16]; //view projection final matrix
    private static final float TILE_LENGTH = 0.3f;
    //private final XYZCoordinate[] iTriangleCoordinates;
    //private final short[] iIndexOrder;

    /**
     *
     * @param width as number of tiles
     * @param length as number of tiles
     */
    public GameTerrain2(final short width, final short length) {
        super.iColor = new XYZColor(0.1f, 0.9f, 0.1f, 1.0f);
        super.buildDrawOrderBuffer(this.buildIndexes());
        super.buildVertexBuffer(this.buildCoordinates(width, length));
        super.compileGLSL();
    }

    /**
     *
     * @param width as number of tiles
     * @param length as number of tiles
     * @return an array with all coordinates fixed
     */
    private XYZCoordinate[] buildCoordinates(final short width, final short length){
        /*if (BuildConfig.DEBUG && !(triangleCoordinates.length == 3)) {
            throw new AssertionError("Assertion failed");
        }*/

        final XYZCoordinate[] arrVertices = new XYZCoordinate[(width+1) * (length + 1)];
        int index = 0;
        for(int i=length; i>=0; i--){
            for (int j = 0; j < width+1; j++) {
                System.out.println("Tile no: " + index);
                arrVertices[index] = new XYZCoordinate();
                arrVertices[index].x = j * TILE_LENGTH;
                arrVertices[index].y = 0;
                arrVertices[index].z = 0 - (i * TILE_LENGTH);
                System.out.println("XYZCoordinate[" + arrVertices[index].x + ", " +
                        //arrVertices[index].y + ", " +
                        arrVertices[index].z + "]");
                index++;
            }
        }
        return arrVertices;
    }

    /**
     *
     * @return the array of indexes
     */
    private short[] buildIndexes(){
        final short[] indexOrder = new short[]{0, 2, 1, 3, /*degeneration*/3, 2, 2, 4, 3, 5};
        return indexOrder;
    }

    @Override
    public void draw(final float[] viewMatrix, final float[] modelMatrix, final float[] projectionMatrix) {
        super.doDraw(viewMatrix, modelMatrix, projectionMatrix, GL10.GL_TRIANGLE_STRIP);
/*
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
                AbstractGameCavan.NO_OF_COORDINATES_PER_VERTEX * 4 , super.iVertexBuffer);

        //5. Now load the color
        final int colorHandle = GLES20.glGetUniformLocation(super.iHandleProgram, "vColor");

        // 6. Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, super.iColor.asFloatArray(), 0);

        Matrix.setIdentityM(this.iMVPMatrix,0);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(this.iMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        //7. do calculate transformation matrix
        final int vpmatrixHandle = GLES20.glGetUniformLocation(super.iHandleProgram, "vpmMatrix");

        //8. Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vpmatrixHandle, 1, false, this.iMVPMatrix, 0);

        //9. Draw the triangle
        GLES20.glDrawElements(GL10.GL_TRIANGLE_STRIP, this.iIndexOrder.length, GL10.GL_UNSIGNED_SHORT, super.iDrawOrderBuffer);


        //10. Cleanup: Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisable(GL10.GL_CULL_FACE);*/

    }
}
