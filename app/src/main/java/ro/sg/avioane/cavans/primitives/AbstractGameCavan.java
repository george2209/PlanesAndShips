/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.primitives;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;

import androidx.annotation.CallSuper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.DebugUtils;
import ro.sg.avioane.util.OpenGLBufferArray3;
import ro.sg.avioane.util.OpenGLProgram;
import ro.sg.avioane.util.OpenGLProgramUtils;
import ro.sg.avioane.util.TextureUtils;

import static ro.sg.avioane.util.OpenGLProgramUtils.SHADER_ONLY_VERTICES;
import static ro.sg.avioane.util.OpenGLProgramUtils.SHADER_UNDEFINED;
import static ro.sg.avioane.util.OpenGLProgramUtils.SHADER_VERTICES_WITH_NORMALS;
import static ro.sg.avioane.util.OpenGLProgramUtils.SHADER_VERTICES_WITH_OWN_COLOR;
import static ro.sg.avioane.util.OpenGLProgramUtils.SHADER_VERTICES_WITH_TEXTURE;

/**
 * How the vertices shader is organized:
 * X,Y,Z, [R,G,B,A,] [U,V], [Xn, Yn, Zn]
 *      * where:
 *      * - X,Y,Z: mandatory
 *      * - R,G,B,A: optional (if missing then a global color will be set under a uniform).
 *      * - U, V: optional (only if texture is supported)
 *      * - Xn, Yn, Zn: optional. You can use the helper functions if the information is not available
 *
 *
 *  1. only vertices are provided with no color (or one color as UNIFORM color):
 *      array_of_float[] {X1,Y1,Z1, ... Xn,Yn,Zn}; SHADER_ONLY_VERTICES will by used.
 *  2. vertices with color per vertex info (as VARYING color):
 *      array_of_float[] {X1,Y1,Z1,R1,G1,B1,A1 ... Xn,Yn,Zn,Rn,Gn,Bn,An}; SHADER_ONLY_VERTICES will
 *      be used.
 *  3. vertices with texture (and implicit no color per vertex):
 *      array_of_float[] {X1,Y1,Z1,U1,V1 .... Xn,Yn,Zn,Un,Vn};  SHADER_VERTICES_AND_TEXTURE will
 *      be used.
 *  4. vertices with texture and color (and implicit no color per vertex):
 *  *      array_of_float[] {X1,Y1,Z1,,R1,G1,B1,A1,U1,V1 .... Xn,Yn,Zn,Un,Vn};
 *  SHADER_VERTICES_AND_TEXTURE & SHADER_VERTICES_WITH_OWN_COLOR will be used.
 */
public abstract class AbstractGameCavan{

    private final static short BYTES_PER_FLOAT = 4;
    private final static short BYTES_PER_SHORT = 2;
    private static final byte NO_OF_COORDINATES_PER_VERTEX = 3; //use X,Y,Z
    private static final byte NO_OF_COLORS_PER_VERTEX = 4; //use R,G,B,A
    private static final byte NO_OF_TEXTURES_PER_VERTEX = 2; //use U,V
    private static final int VERTICES_OFFSET = 0;
    private int COLOR_OFFSET = -1; //BYTES_PER_FLOAT * NO_OF_COORDINATES_PER_VERTEX;
    private int TEXTURE_OFFSET = -1; //COLOR_OFFSET + BYTES_PER_FLOAT * NO_OF_COLORS_PER_VERTEX;
    private int NORMAL_OFFSET = -1;

    private OpenGLProgram iProgram = null;
    private OpenGLBufferArray3 iOpenGL3Buffers = null;

    private int iTextureDataIdx = -1;

    protected XYZColor iColor = new XYZColor(0.03671875f, 0.76953125f, 0.82265625f, 1.0f);
    private int iShaderType = SHADER_UNDEFINED;
    private int iIndexOrderLength = 0;
    private int iShaderStride = 0;


    public abstract void draw(final float[] viewMatrix, final float[] projectionMatrix);
    public abstract void onRestore();


    /**
     * do the compilation & build of the GLSL code
     * @param arrVertices the vertices array
     * @param drawOrderArr the order of drawing the vertices
     */
    protected void build(final XYZVertex[] arrVertices, final short[] drawOrderArr){
        this.calculateShaderType(arrVertices[0]);
        this.iProgram = OpenGLProgramUtils.getInstance().getProgramForShader(this.iShaderType);
        this.buildVertexBuffer(arrVertices);
        this.buildDrawOrderBuffer(drawOrderArr);
    }

    /**
     *
     * @param drawOrder
     */
    protected void buildDrawOrderBuffer(final short[] drawOrder){
        if(BuildConfig.DEBUG &&
                this.iShaderType == SHADER_UNDEFINED)
            throw new AssertionError("SHADER_UNDEFINED ERROR!");

        this.iIndexOrderLength = drawOrder.length;
        // initialize byte buffer for the draw list
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        bb.order(ByteOrder.nativeOrder());
        final ShortBuffer drawOrderBuffer = bb.asShortBuffer();
        drawOrderBuffer.put(drawOrder);
        drawOrderBuffer.position(0);

        //Load data into OpenGL memory
        //cleanup and previous unloaded buffer
        if(this.iOpenGL3Buffers != null) {
            this.iOpenGL3Buffers.cleanVORBF();
        } else {
            this.iOpenGL3Buffers = new OpenGLBufferArray3();
        }

        this.iOpenGL3Buffers.rebuildVertexDrawOrder(
                drawOrderBuffer.capacity() * BYTES_PER_SHORT,
                drawOrderBuffer, GLES20.GL_STATIC_DRAW);

        //release main memory
        drawOrderBuffer.limit(0);
    }

    /**
     * initialize the iVertexBuffer
     * The expected stream of data of the iVertexBuffer has this profile stride (including optional
     * information such as color per vertex and texture arrVertices)
     *      X,Y,Z, [R,G,B,A,] [U,V], [Xn, Yn, Zn]
     * where:
     * - X,Y,Z: mandatory
     * - R,G,B,A: optional (if missing then a global color will be set under a uniform).
     * - U, V: optional (only if texture is supported)
     * - Xn, Yn, Zn: optional. You can use the helper functions if the information is not available
     * ex: MathGLUtils.getTriangleNormal(...)
     * @param arrVertices the xyz arrVertices
     */
    protected void buildVertexBuffer(final XYZVertex[] arrVertices) {
        if(BuildConfig.DEBUG &&
                this.iShaderType == SHADER_UNDEFINED)
            throw new AssertionError("SHADER_UNDEFINED ERROR!");

        final int vertexStride = this.iShaderStride/BYTES_PER_FLOAT; //getVertexStride(arrVertices[0]);
        final int coordinatesLength = arrVertices.length * vertexStride;

        // initialize vertex byte buffer for shape arrVertices
        final ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 3 arrVertices(x,y,z) * 4 bytes per float)
                arrVertices.length * this.iShaderStride);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        final FloatBuffer vertexBuffer = bb.asFloatBuffer();

        // add the arrVertices to the FloatBuffer
        final float[] arrBufferVertices = new float[coordinatesLength];
        for(int i=0; i<arrVertices.length; i++){
            int dynamicStride = 0;
            arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].coordinate.x(); dynamicStride++;
            arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].coordinate.y(); dynamicStride++;
            arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].coordinate.z(); dynamicStride++;
            if((this.iShaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0){
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].color.red;
                dynamicStride++;
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].color.green;
                dynamicStride++;
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].color.blue;
                dynamicStride++;
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].color.alpha;
                dynamicStride++;
            }
            if((this.iShaderType & SHADER_VERTICES_WITH_TEXTURE) != 0){
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].texture.u();
                dynamicStride++;
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].texture.v();
                dynamicStride++;
            }

            if((this.iShaderType & SHADER_VERTICES_WITH_NORMALS) != 0){
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].normal.x();
                dynamicStride++;
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].normal.y();
                dynamicStride++;
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].normal.z();
                //dynamicStride++;
            }

        }
        vertexBuffer.put(arrBufferVertices);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        //Load data into OpenGL memory
        // cleanup and previous unloaded buffer
        if(this.iOpenGL3Buffers != null) {
            this.iOpenGL3Buffers.cleanVBOandVAO();
        } else {
            this.iOpenGL3Buffers = new OpenGLBufferArray3();
        }

        this.iOpenGL3Buffers.startBuildBuffers(
                vertexBuffer.capacity() * BYTES_PER_FLOAT, vertexBuffer,
                GLES20.GL_STATIC_DRAW, this.iProgram.iVerticesHandle,
                AbstractGameCavan.NO_OF_COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT, false,this.iShaderStride, VERTICES_OFFSET);

        if((this.iShaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0){
            this.iOpenGL3Buffers.addBuildBufferColors(this.iProgram.iColorHandle,
                    AbstractGameCavan.NO_OF_COLORS_PER_VERTEX, this.iShaderStride,
                    COLOR_OFFSET);
        }

        this.iOpenGL3Buffers.finishBuildBuffers();


        //release main memory
        vertexBuffer.limit(0);

        if((this.iShaderType & SHADER_VERTICES_WITH_TEXTURE) != 0) {
            //TODO: implement texture per vertex info? Otherwise one texture for a collection of vertices?
            this.iTextureDataIdx = TextureUtils.getInstance().
                    getTextureWithName("test", arrVertices[0].texture.getTextureData());
        }
    }

    /**
     * Calculate the vertex stride and shader type.
     * See @link #buildVertexBuffer description for the format details
     * Usually you call this method in case of an array of vertices with
     * <code>getVertexStride(arr[0])</code>
     * as all vertices will share the same format (data is of course different per vertex).
     * @param vertex the vertex that needs this calculated.
     * @return a value you can use to build the program against.
     */
    private void calculateShaderType(XYZVertex vertex) {
        int offset = VERTICES_OFFSET;
        int vertexStride = 3; // X, Y, Z, --> 0, 1, 2
        this.iShaderType = SHADER_ONLY_VERTICES;

        if(vertex.color != null){
            this.iShaderType |= SHADER_VERTICES_WITH_OWN_COLOR;
            // X, Y, Z, --> 0, 1, 2,
            // R, G, B, A --> 3, 4, 5, 6
            vertexStride += 4;
            offset += BYTES_PER_FLOAT * NO_OF_COORDINATES_PER_VERTEX;
            COLOR_OFFSET = offset;
        }

        if(vertex.texture != null){
            this.iShaderType |= SHADER_VERTICES_WITH_TEXTURE;
            //U,V
            vertexStride += 2;
            offset += BYTES_PER_FLOAT * NO_OF_COLORS_PER_VERTEX;
            TEXTURE_OFFSET = offset;
        }

        if(vertex.normal != null ){
            this.iShaderType |= SHADER_VERTICES_WITH_NORMALS;
            //Xn,Yn,Zn
            vertexStride += 3;
            offset += BYTES_PER_FLOAT * NO_OF_TEXTURES_PER_VERTEX;
            NORMAL_OFFSET = offset;
        }

        this.iShaderStride =  vertexStride * BYTES_PER_FLOAT;
    }




    /**
     * process the main draw having in mind that all the matrices are already set into OpenGl memory.
     * TODO: to be updated for checking
     *      *      TransformedVector = TranslationMatrix * RotationMatrix * ScaleMatrix * OriginalVector;
     * @param viewMatrix
     * @param projectionMatrix
     * @param FORM_TYPE
     */
    protected void doDraw(final float[] viewMatrix, final float[] projectionMatrix, final int FORM_TYPE){
        // counterclockwise orientation of the ordered vertices
        GLES30.glFrontFace(GLES20.GL_CCW);
        
        // Enable face culling.
        GLES30.glEnable(GLES20.GL_CULL_FACE); //--> make sure is disabled at clean up!
        // What faces to remove with the face culling.
        GLES30.glCullFace(GLES20.GL_BACK); //GL_FRONT
        //GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        //1. Ask OpenGL ES to load the program
        GLES30.glUseProgram(this.iProgram.iProgramHandle);
        DebugUtils.checkPrintGLError();

        GLES30.glBindVertexArray(this.iOpenGL3Buffers.VAO());


        //set color
        if(0 < (this.iShaderType & SHADER_VERTICES_WITH_OWN_COLOR)){
            GLES20.glEnableVertexAttribArray(this.iProgram.iColorHandle);
        } else {
            this.iProgram.iColorHandle = GLES20.glGetUniformLocation(this.iProgram.iProgramHandle, OpenGLProgramUtils.SHADER_VARIABLE_aColor);
            DebugUtils.checkPrintGLError();
            GLES20.glUniform4fv(this.iProgram.iColorHandle, 1, this.iColor.asFloatArray(), 0);
        }
        DebugUtils.checkPrintGLError();


        final float[] theMVPMatrix = new float[16];

        //needed to have the 4th last position as 1 (vector and not 0 as direction)!
        Matrix.setIdentityM(theMVPMatrix,0);

        //6. This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        Matrix.multiplyMM(theMVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        //7. do calculate transformation matrix
        final int vpmatrixHandle = GLES20.glGetUniformLocation(this.iProgram.iProgramHandle, "vpmMatrix");
        DebugUtils.checkPrintGLError();

        //8. Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vpmatrixHandle, 1, false, theMVPMatrix, 0);
        DebugUtils.checkPrintGLError();


        //2 bind the drawing order
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, this.iOpenGL3Buffers.getVertexOrderBuffer());
        DebugUtils.checkPrintGLError();



        //9. Draw the form
        if(GLES30.GL_LINES == FORM_TYPE) {
            GLES30.glDrawElements(GLES20.GL_LINES, this.iIndexOrderLength, GLES30.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
        } else if(GLES30.GL_TRIANGLE_STRIP == FORM_TYPE) {
            GLES30.glDrawElements(GLES20.GL_TRIANGLE_STRIP, this.iIndexOrderLength, GLES30.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
        } else if(GLES20.GL_TRIANGLES == FORM_TYPE) {
            GLES30.glDrawElements(GLES20.GL_TRIANGLES, this.iIndexOrderLength, GLES30.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
        }else if(GLES20.GL_POINTS == FORM_TYPE) {
            GLES30.glDrawElements(GLES20.GL_POINTS, this.iIndexOrderLength, GLES30.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
        } else if(GLES20.GL_LINES == FORM_TYPE) {
            GLES30.glDrawElements(GLES20.GL_LINES, this.iIndexOrderLength, GLES30.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
        } else {
            throw new RuntimeException("FATAL ERROR !!! unknown FORM_TYPE=" + FORM_TYPE);
        }

        DebugUtils.checkPrintGLError();

        if(0 < (this.iShaderType & SHADER_VERTICES_WITH_OWN_COLOR)){
            GLES30.glDisableVertexAttribArray(this.iProgram.iColorHandle);
        }

        //GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES30.glBindVertexArray(0);













        //2. Load the coordinate data
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.iOpenGL3Buffers.VBO());
//        DebugUtils.checkPrintGLError();
//        GLES20.glVertexAttribPointer(this.iProgram.iVerticesHandle, AbstractGameCavan.NO_OF_COORDINATES_PER_VERTEX,
//                GLES20.GL_FLOAT, false,
//                this.iShaderStride, VERTICES_OFFSET);
//        DebugUtils.checkPrintGLError();
//        //3. Enable the loaded handle to load the data into
//        GLES20.glEnableVertexAttribArray(this.iProgram.iVerticesHandle); //positionHandle);
//        DebugUtils.checkPrintGLError();

//        //.4 Load color data
//        if((this.iShaderType & SHADER_VERTICES_WITH_OWN_COLOR) > 0){
//            GLES20.glVertexAttribPointer(this.iProgram.iColorHandle, AbstractGameCavan.NO_OF_COLORS_PER_VERTEX , //4=RGBA
//                    GLES20.GL_FLOAT, false,
//                    this.iShaderStride, COLOR_OFFSET);
//            DebugUtils.checkPrintGLError();
//            GLES20.glEnableVertexAttribArray(this.iProgram.iColorHandle); //colorHandle);
//            DebugUtils.checkPrintGLError();
//        } else { //all other shader types will load one single color then
//            this.iProgram.iColorHandle = GLES20.glGetUniformLocation(this.iProgram.iProgramHandle, OpenGLProgramUtils.SHADER_VARIABLE_aColor);
//            DebugUtils.checkPrintGLError();
//            GLES20.glUniform4fv(this.iProgram.iColorHandle, 1, this.iColor.asFloatArray(), 0);
//            DebugUtils.checkPrintGLError();
//        }
//
//        //5. load the texture if available
//        if((this.iShaderType & SHADER_VERTICES_WITH_TEXTURE) != 0){
//            GLES20.glVertexAttribPointer(this.iProgram.iTextureHandle,
//                    AbstractGameCavan.NO_OF_TEXTURES_PER_VERTEX, //2=U,V
//                    GLES20.GL_FLOAT, false,
//                    this.iShaderStride, TEXTURE_OFFSET);
//            DebugUtils.checkPrintGLError();
//            GLES20.glEnableVertexAttribArray(this.iProgram.iTextureHandle);
//            DebugUtils.checkPrintGLError();
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//            DebugUtils.checkPrintGLError();
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.iTextureDataIdx);
//            DebugUtils.checkPrintGLError();
//        }
//
//
//        final float[] theMVPMatrix = new float[16];
//
//        //needed to have the 4th last position as 1 (vector and not 0 as direction)!
//        Matrix.setIdentityM(theMVPMatrix,0);
//
//        //6. This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
//        Matrix.multiplyMM(theMVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
//
//        //7. do calculate transformation matrix
//        final int vpmatrixHandle = GLES20.glGetUniformLocation(this.iProgram.iProgramHandle, "vpmMatrix");
//        DebugUtils.checkPrintGLError();
//
//        //8. Pass the projection and view transformation to the shader
//        GLES20.glUniformMatrix4fv(vpmatrixHandle, 1, false, theMVPMatrix, 0);
//        DebugUtils.checkPrintGLError();
//
//        //8.1 bind the drawing order
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, this.iOpenGL3Buffers.VORBF());
//        DebugUtils.checkPrintGLError();
//
//        //9. Draw the form
//        if(GLES20.GL_LINES == FORM_TYPE) {
//            GLES20.glDrawElements(GLES20.GL_LINES, this.iIndexOrderLength, GLES20.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
//        } else if(GLES20.GL_TRIANGLE_STRIP == FORM_TYPE) {
//            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, this.iIndexOrderLength, GLES20.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
//        } else if(GLES20.GL_TRIANGLES == FORM_TYPE) {
//            GLES20.glDrawElements(GLES20.GL_TRIANGLES, this.iIndexOrderLength, GLES20.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
//        }else if(GLES20.GL_POINTS == FORM_TYPE) {
//            GLES20.glDrawElements(GLES20.GL_POINTS, this.iIndexOrderLength, GLES20.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
//        } else if(GLES20.GL_LINES == FORM_TYPE) {
//            GLES20.glDrawElements(GLES20.GL_LINES, this.iIndexOrderLength, GLES20.GL_UNSIGNED_SHORT, 0/*this.iDrawOrderBuffer*/);
//        } else {
//            throw new RuntimeException("FATAL ERROR !!! unknown FORM_TYPE=" + FORM_TYPE);
//        }
//
//        DebugUtils.checkPrintGLError();
//
//        //10. Cleanup: Disable vertex array
//        // Unbind element array.
//        if((this.iShaderType & SHADER_VERTICES_WITH_TEXTURE) != 0) {
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//        }
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
//
//        if((this.iShaderType & SHADER_VERTICES_WITH_TEXTURE) != 0) {
//            GLES20.glDisableVertexAttribArray(this.iProgram.iTextureHandle);
//        }
//
//        if((this.iShaderType & SHADER_VERTICES_WITH_OWN_COLOR) > 0){
//            GLES20.glDisableVertexAttribArray(this.iProgram.iColorHandle);
//        }
//        GLES20.glDisableVertexAttribArray(this.iProgram.iVerticesHandle);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    @CallSuper
    public void destroy(){
        COLOR_OFFSET = -1;
        TEXTURE_OFFSET = -1;
        NORMAL_OFFSET = -1;

        iProgram = null;
        iOpenGL3Buffers = null;

        iTextureDataIdx = -1;

        iShaderType = SHADER_UNDEFINED;
        iIndexOrderLength = 0;
        iShaderStride = 0;
    }

    /**
     * From GameCavanLifeCycle
     */
//    public boolean isOnPause(){
//        return this.iVertexBufferIdx == -1;
//    }

    /**
     * call this at the end of your implementation of the onResume of the class that extend this
     * class.
     * Example:
     * <code>
     * class MyClass{
     *     @Override
     *     public void onResume() {
     *      if(super.isOnPause()){
     *             //we must rebuild the indexes as we are coming back from onPause
     *             super.buildDrawOrderBuffer(...);
     *             super.buildVertexBuffer(...);
     *      }
     *
     *      ....your implementation here...then at the end you call this:
     *      super.onResume()
     *     }
     * }
     * </code>
     * Otherwise you will have an exception thrown that the indexes are empty (in case of Activity
     * going back from pause to resume)
     */
//    @CallSuper
//    public void onResume() {
//        if(this.iVertexBufferIdx == -1){
//            throw new IndexOutOfBoundsException("no vertices buffer found! " +
//                    "Are you calling super.onResume() " +
//                    "method before building the vector buffer?");
//        }
//    }

    /**
     * From GameCavanLifeCycle
     * calling this method will result on releasing all data inside of all buffers.
     * It this object is about to be reused make sure that you build them back before draw is call.
     */
//    @CallSuper
//    public void onPause(){
//        this.iDrawOrderBufferIdx = OpenGLProgramUtils.deleteBuffer(this.iDrawOrderBufferIdx);
//        this.iVertexBufferIdx = OpenGLProgramUtils.deleteBuffer(this.iVertexBufferIdx);
//
//        //cleanup is managed by TextureUtils and the textures will be only removed with the
//        //main program at the "onStop" Activity request.
//        //Reason:
//        // - to avoid big loading timing in case of transitions of Activity
//        this.iTextureDataIdx = -1;
//    }


//    @Override
//    public void onRestart() {
//        this.compileGLSL();
//    }


}
