package ro.sg.avioane.cavans.primitives;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.util.OpenGLUtils;

public abstract class AbstractGameCavan {
    private static final byte SHADER_WITH_COLOR_PER_VERTEX = 1;
    private static final byte SHADER_WITH_COLOR_UNIFORM = 2;
    protected static final byte NO_OF_COORDINATES_PER_VERTEX = 3; //use X,Y,Z
    protected int iVertexBufferIdx = 0;
    protected int iDrawOrderBufferIdx = 0;
    protected XYZColor iColor = new XYZColor(0.03671875f, 0.76953125f, 0.82265625f, 1.0f);
    protected int iHandleProgram;
    protected String iVertexShaderCode;
    protected String iFragmentShaderCode;
    private byte iShaderType = SHADER_WITH_COLOR_UNIFORM;
    private int iIndexOrderLength = 0;
    protected final static short BYTES_PER_FLOAT = 4;
    protected final static short BYTES_PER_SHORT = 2;

    public abstract void draw(final float[] viewMatrix, final float[] modelMatrix, final float[] projectionMatrix);



    protected void doDraw(final float[] viewMatrix, final float[] modelMatrix, final float[] projectionMatrix, final int FORM_TYPE){
        //this.iVertexBuffer.position(0);
        //this.iDrawOrderBuffer.position(0);

        // counterclockwise orientation of the ordered vertices
        GLES20.glFrontFace(GL10.GL_CCW);
        // Enable face culling.
        GLES20.glEnable(GL10.GL_CULL_FACE); //--> make sure is disabled at clean up!
        // What faces to remove with the face culling.
        GLES20.glCullFace(GL10.GL_FRONT); //GL_BACK

        //GLES20.glDisable(GL10.GL_DEPTH_TEST);


        //1. Ask OpenGL ES to load the program
        GLES20.glUseProgram(this.iHandleProgram);

        //2. Get a handle to the position vector
        final int positionHandle = GLES20.glGetAttribLocation(this.iHandleProgram, "vPosition");
        if(positionHandle < 0) {
            System.err.println("WRONG POSITION HANDLE: " + positionHandle);
            System.out.println("ERROR =>>>> " +       GLES20.glGetError());
        }

        //3. Enable the loaded handle to load the data into
        GLES20.glEnableVertexAttribArray(positionHandle);

        int shaderStride = AbstractGameCavan.NO_OF_COORDINATES_PER_VERTEX * 4; /*4=FLOAT SIZE*/
        if(this.iShaderType == SHADER_WITH_COLOR_PER_VERTEX){
            shaderStride = 7 * 4; //X,Y,Z,R,G,B,A x 4
        }

        //4. Load the coordinate data
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.iVertexBufferIdx);
        GLES20.glVertexAttribPointer(positionHandle, AbstractGameCavan.NO_OF_COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                shaderStride, 0);

        //5. Now load the color
        int colorHandle = 0;
        if(this.iShaderType == SHADER_WITH_COLOR_UNIFORM) {
            colorHandle = GLES20.glGetUniformLocation(this.iHandleProgram, "vColor");

            // 6. Set color for drawing the triangle
            GLES20.glUniform4fv(colorHandle, 1, this.iColor.asFloatArray(), 0);
        } else if(this.iShaderType == SHADER_WITH_COLOR_PER_VERTEX){
            colorHandle = GLES20.glGetAttribLocation(this.iHandleProgram, "aColor");
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.iVertexBufferIdx);
            GLES20.glEnableVertexAttribArray(colorHandle);
            GLES20.glVertexAttribPointer(colorHandle, 4 , //4=RGBA
                    GLES20.GL_FLOAT, false,
                    shaderStride, NO_OF_COORDINATES_PER_VERTEX * BYTES_PER_FLOAT);

        } else
            throw new RuntimeException("unkwn shader type=" + this.iShaderType);

        final float[] theMVPMatrix = new float[16];

        Matrix.setIdentityM(theMVPMatrix,0);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(theMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        /////******************************************************
        Matrix.multiplyMM(theMVPMatrix, 0, projectionMatrix, 0, theMVPMatrix, 0);

        //7. do calculate transformation matrix
        final int vpmatrixHandle = GLES20.glGetUniformLocation(this.iHandleProgram, "vpmMatrix");

        //8. Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vpmatrixHandle, 1, false, theMVPMatrix, 0);

        //9. Draw the triangle
        //GLES20.glLineWidth(0.2f);
        //GLES20.glDrawElements(GLES20.GL_LINES, this.iIndexOrder.length, GLES20.GL_UNSIGNED_SHORT, super.iDrawOrderBuffer);

        //8.1 bind the drawing order
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, this.iDrawOrderBufferIdx);

        //9. Draw the form
        if(GL10.GL_LINES == FORM_TYPE) {
            GLES20.glDrawElements(GL10.GL_LINES, this.iIndexOrderLength, GL10.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
        } else if(GL10.GL_TRIANGLE_STRIP == FORM_TYPE) {
            GLES20.glDrawElements(GL10.GL_TRIANGLE_STRIP, this.iIndexOrderLength, GL10.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
        } else {
            throw new RuntimeException("FATAL ERROR !!! unknown FORM_TYPE=" + FORM_TYPE);
        }

        //System.out.println("ERROR =>>>> " +       GLES20.glGetError());



        //10. Cleanup: Disable vertex array
        // Unbind element array.
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        if(this.iShaderType == SHADER_WITH_COLOR_PER_VERTEX){
            GLES20.glDisableVertexAttribArray(colorHandle);
        }
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisable(GL10.GL_CULL_FACE);
    }


    /**
     * loadImplicitShaderPrograms
     */
    private void loadDefaultShaderPrograms(){
        StringBuilder sb = new StringBuilder();
        sb.append("uniform mat4 vpmMatrix;"); //view projection matrix
        sb.append("attribute vec4 vPosition;"); //vertex coordinates
        if(this.iShaderType == SHADER_WITH_COLOR_PER_VERTEX) {
            sb.append("attribute vec4 aColor;"); //vertex color
            sb.append("varying vec4 vColor;"); // to be passed into the fragment shader.
        }

        sb.append("void main() {");
        if(this.iShaderType == SHADER_WITH_COLOR_PER_VERTEX)
            sb.append("  vColor = aColor;");
        sb.append("  gl_Position = vpmMatrix * vPosition;"); //set coordinates on the projection clip
        sb.append("}");


        this.iVertexShaderCode = sb.toString();

        sb = new StringBuilder();

        //set GPU to medium precision
        // (highp is not by all devices supported)
        //alternatively can be set to lowp for tests or low performance devices.
        //remove this in case of a Desktop App!!!
        sb.append("precision mediump float;");
        if(this.iShaderType == SHADER_WITH_COLOR_PER_VERTEX) {
            sb.append("varying vec4 vColor;");
        } else {
            sb.append("uniform vec4 vColor;");
        }
        sb.append("void main() {");
        sb.append("  gl_FragColor = vColor;");
        sb.append("}");

        this.iFragmentShaderCode = sb.toString();
    }

    protected void buildDrawOrderBuffer(final short[] drawOrder){
        this.iIndexOrderLength = drawOrder.length;
        // initialize byte buffer for the draw list
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        bb.order(ByteOrder.nativeOrder());
        final ShortBuffer iDrawOrderBuffer = bb.asShortBuffer();
        iDrawOrderBuffer.put(drawOrder);
        iDrawOrderBuffer.position(0);

        //Load data into OpenGL memory from this point already.
        {
            final int buffers[] = new int[1];
            GLES20.glGenBuffers(1, buffers, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[0]);

            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, iDrawOrderBuffer.capacity() * BYTES_PER_SHORT, iDrawOrderBuffer, GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

            this.iDrawOrderBufferIdx = buffers[0];

            //release main memory
            iDrawOrderBuffer.limit(0);
        }
    }

    /**
     * initialize the iVertexBuffer
     * @param coordinates the xyz coordinates
     */
    protected void buildVertexBuffer(XYZCoordinate[] coordinates) {
        //check if the color is inside each vertex by checking the first vertex
        boolean isColorPerVertex = coordinates[0].color != null;
        int vertexStride = 3;

        if(isColorPerVertex){
            // X, Y, Z, --> 0, 1 , 2,
            // R, G, B, A --> 3, 4, 5, 6
            vertexStride = 7;
        }
        final int coordinatesLength = coordinates.length * vertexStride;

        // initialize vertex byte buffer for shape coordinates
        final ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 3 coordinates(x,y,z) * 4 bytes per float)
                coordinates.length * vertexStride * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        final FloatBuffer iVertexBuffer = bb.asFloatBuffer();

        // add the coordinates to the FloatBuffer
        final float[] arrCoords = new float[coordinatesLength];
        for(int i=0; i<coordinates.length; i++){
            arrCoords[vertexStride*i] = coordinates[i].x;
            arrCoords[vertexStride*i + 1] = coordinates[i].y;
            arrCoords[vertexStride*i + 2] = coordinates[i].z;
            if(isColorPerVertex){
                arrCoords[vertexStride*i + 3] = coordinates[i].color.red;
                arrCoords[vertexStride*i + 4] = coordinates[i].color.green;
                arrCoords[vertexStride*i + 5] = coordinates[i].color.blue;
                arrCoords[vertexStride*i + 6] = coordinates[i].color.alpha;
            }
            //System.out.println("x=" + coordinates[i].x + " y=" + coordinates[i].y + " z=" + coordinates[i].z + "\n");
        }
        iVertexBuffer.put(arrCoords);
        // set the buffer to read the first coordinate
        iVertexBuffer.position(0);

        //Load data into OpenGL memory from this point already.
        {
            final int buffers[] = new int[1];
            GLES20.glGenBuffers(1, buffers, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);

            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, iVertexBuffer.capacity() * BYTES_PER_FLOAT, iVertexBuffer, GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

            this.iVertexBufferIdx = buffers[0];

            //release main memory
            iVertexBuffer.limit(0);
        }

        if(isColorPerVertex){
            this.iShaderType = SHADER_WITH_COLOR_PER_VERTEX;
            this.loadDefaultShaderPrograms();
        } else {
            this.iShaderType = SHADER_WITH_COLOR_UNIFORM;
            this.loadDefaultShaderPrograms();
        }
    }



    /**
     * do the compilation of the GLSL code
     */
    protected void compileGLSL(){
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

        // Bind attributes
        GLES20.glBindAttribLocation(iHandleProgram, 0, "vPosition");
        if(this.iShaderType == SHADER_WITH_COLOR_PER_VERTEX)
            GLES20.glBindAttribLocation(iHandleProgram, 1, "aColor");

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(iHandleProgram);

        // Get the link status.
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(iHandleProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);

        // If the link failed, delete the program.
        if (linkStatus[0] == 0)
        {
            final String errStr = GLES20.glGetProgramInfoLog(iHandleProgram);
            GLES20.glDeleteProgram(iHandleProgram);
            //iHandleProgram = 0;
            throw new RuntimeException("FATAL ERROR !!! Program link failed with link status = " + linkStatus[0] + " \n\n " + errStr + " \n\n ");
        }
    }


}
