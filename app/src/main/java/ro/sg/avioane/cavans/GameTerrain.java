package ro.sg.avioane.cavans;

import android.opengl.GLES20;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;


import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.cavans.primitives.AbstractGameCavan;
import ro.sg.avioane.geometry.XYZCoordinate;

public class GameTerrain extends AbstractGameCavan {
    private static final float TILE_LENGTH = 0.3f;
    private int iIndicesLength = 0;
    private float[] iMVPMatrix = new float[16]; //mode view projection final matrix

    //Limited for keeping the iArrIndexOrder as short type
    //Note:
    // if you exceed ~180 tiles then the iArrIndexOrder must be (signed)INT
    //as a refactor model this part can be done in C++ with unsigned short so than the
    //~180 tiles can be increased to the ~255 and the 2bytes-short size kept
    public static final short MAX_TILES_NO = 100;

    public GameTerrain(short width, short length){
        super.buildVertexBuffer(buildModelMatrix(width, length));
        {
            //tmp
            final short[] indexes = buildIndexDrawOrder(width, length);

            System.out.print("\n");
            for(int i=0; i<indexes.length; i++){
                System.out.print( indexes[i] + ",");
            }
            System.out.print("\n");

            super.buildDrawOrderBuffer(indexes);
        }


        //full matrix to be updated with all params
        //gl_Position = projection_matrix*view_matrix* world_matrix * vec4(vertex_position, 1.0);
        super.iVertexShaderCode =
                        "uniform mat4 vpmMatrix;" + //projection matrix
                        "attribute vec4 vPosition;" + //vertex coordinates
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
    }

    private XYZCoordinate[] buildModelMatrix(int width, int length) {

        final XYZCoordinate[] arrVertices = new XYZCoordinate[(width+1) * (length + 1)];
        int index = 0;
        for(int i=length; i>=0; i--){
            for (int j = 0; j < width+1; j++) {
                System.out.println("Tile no: " + index);
                arrVertices[index] = new XYZCoordinate();
                arrVertices[index].x = j * TILE_LENGTH;
                arrVertices[index].y = 0;
                arrVertices[index].z = i * TILE_LENGTH;
                System.out.println("XYZCoordinate[" + arrVertices[index].x + ", " +
                        arrVertices[index].y + ", " +
                        arrVertices[index].z + "]");
                index++;
            }
        }
        return arrVertices;
    }

    /**
     * build the order the vertices will be draw (CCW) as triangle strip
     * @param width the number of tiles this map will have on width
     * @param length the number of tiles this map will have on length
     * @return
     */
    private short[] buildIndexDrawOrder(short width, short length) {
        if (BuildConfig.DEBUG && (width < 1 || length < 1 || width > MAX_TILES_NO || length > MAX_TILES_NO)) {
            throw new AssertionError(
                    new StringBuilder("Assertion failed width=")
                            .append(width).append(" length")
                            .append(length).toString());
        }

        if(width == 1) {
            final short[] indexOrder = new short[]{0, 2, 1, 3};
            this.iIndicesLength = indexOrder.length;
            return indexOrder;
        }

        final short[] indexOrder = new short[(width-1)*length*2 + (width-2)*2];
        short degenerationFactor = 0;

        for( short i=0; i<width-1; i++){
            if(i>0){
                short j=0;
                indexOrder[degenerationFactor + i*(2*length) + 2*j] = (short) ((i*length)+j);
                degenerationFactor++;
            }

            for(short j=0; j<length; j++){
                indexOrder[degenerationFactor + i*(2*length) + 2*j] = (short) ((i*length)+j);
                indexOrder[degenerationFactor + i*(2*length) + 2*j +1] = (short) (((i+1)*length)+j);
            }
            if(i<width-2){
                short j= (short) (length-1);
                degenerationFactor++;
                indexOrder[degenerationFactor + i*(2*length) + 2*j +1] = (short) (((i+1)*length)+j);
            }
        }
        this.iIndicesLength = indexOrder.length;
        return indexOrder;
    }

    @Override
    public void draw(final float[] viewMatrix, final float[] modelMatrix, final float[] projectionMatrix) {
        // counterclockwise orientation of the ordered vertices
        GLES20.glFrontFace(GL10.GL_CCW);
        // Enable face culling.
        //GLES20.glEnable(GL10.GL_CULL_FACE); //--> make sure is disabled at clean up!
        // What faces to remove with the face culling.
        //GLES20.glCullFace(GL10.GL_FRONT);

        GLES20.glUseProgram(super.iHandleProgram);
        final int positionHandle = this.loadPositionVector();
        final int colorHandle = this.loadColorData();




        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(this.iMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        //Matrix.multiplyMM(this.iMVPMatrix, 0, projectionMatrix, 0, this.iMVPMatrix, 0);


        final int vpmatrixHandle = this.runShaderCalculations();


        super.iDrawOrderBuffer.position(0);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 12);
        GLES20.glDrawElements(GL10.GL_TRIANGLE_STRIP, this.iIndicesLength, GL10.GL_UNSIGNED_SHORT, super.iDrawOrderBuffer);

        //cleanup
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    /**
     * loadPositionVector
     */
    private int loadPositionVector(){
        super.iVertexBuffer.position(0);
        final int positionHandle = GLES20.glGetAttribLocation(super.iHandleProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, AbstractGameCavan.NO_OF_COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                AbstractGameCavan.NO_OF_COORDINATES_PER_VERTEX * 4 /*4=FLOAT SIZE*/, super.iVertexBuffer);
        return positionHandle;
    }

    /**
     * loadColorData
     */
    private int loadColorData(){
        final int colorHandle = GLES20.glGetUniformLocation(super.iHandleProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, super.iColor.asFloatArray(), 0);
        return colorHandle;
    }

    /**
     * runShaderCalculations
     */
    private int runShaderCalculations(){
        final int vpmatrixHandle = GLES20.glGetUniformLocation(super.iHandleProgram, "vpmMatrix");
        GLES20.glUniformMatrix4fv(vpmatrixHandle, 1, false, this.iMVPMatrix, 0);
        return vpmatrixHandle;
    }
}
