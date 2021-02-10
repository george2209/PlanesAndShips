package ro.sg.avioane.cavans.primitives;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.util.OpenGLUtils;

public abstract class AbstractGameCavan {
    protected static final byte NO_OF_COORDINATES_PER_VERTEX = 3; //use X,Y,Z
    protected FloatBuffer iVertexBuffer;
    protected ShortBuffer iDrawOrderBuffer;
    protected XYZColor iColor;
    protected int iHandleProgram;
    protected String iVertexShaderCode;
    protected String iFragmentShaderCode;

    public FloatBuffer getVertexBuffer(){
        return this.iVertexBuffer;
    }

    public XYZColor getColor(){
        return this.iColor;
    }

    protected void buildDrawOrderBuffer(final short[] drawOrder){
        // initialize byte buffer for the draw list
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        bb.order(ByteOrder.nativeOrder());
        iDrawOrderBuffer = bb.asShortBuffer();
        iDrawOrderBuffer.put(drawOrder);
        iDrawOrderBuffer.position(0);
    }

    public abstract void draw(final float[] viewProjectionMatrix);

    /**
     * Initialize the Vertex Buffer and do the compilation of the GLSL code
     * @param coordinates
     */
    protected void doBuild(final XYZCoordinate[] coordinates){
        this.buildVertexBuffer(coordinates);
        this.compileGLSL();
    }

    /**
     * do the compilation of the GLSL code
     */
    private void compileGLSL(){
        final int vertexShader = OpenGLUtils.getLoadShader(GLES20.GL_VERTEX_SHADER,
                iVertexShaderCode);
        final int fragmentShader = OpenGLUtils.getLoadShader(GLES20.GL_FRAGMENT_SHADER,
                iFragmentShaderCode);

        // create empty OpenGL ES Program
        iHandleProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(iHandleProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(iHandleProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(iHandleProgram);
    }

    /**
     * initialize the iVertexBuffer
     * @param coordinates the xyz coordinates
     */
    private void buildVertexBuffer(XYZCoordinate[] coordinates) {
        // initialize vertex byte buffer for shape coordinates
        final ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 3 coordinates(x,y,z) * 4 bytes per float)
                coordinates.length * AbstractGameCavan.NO_OF_COORDINATES_PER_VERTEX * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        iVertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        final float[] arrCoords = new float[coordinates.length * 3];
        for(int i=0; i<coordinates.length; i++){
            arrCoords[3*i] = coordinates[i].x;
            arrCoords[3*i + 1] = coordinates[i].y;
            arrCoords[3*i + 2] = coordinates[i].z;
        }
        iVertexBuffer.put(arrCoords);
        // set the buffer to read the first coordinate
        iVertexBuffer.position(0);
    }
}
