/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.gdi.cavans;

import android.opengl.GLES20;
import android.opengl.GLES30;

import androidx.annotation.CallSuper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import ro.sg.avioane.BuildConfig;
import ro.gdi.cavans.features.CavanMovements;
import ro.sg.avioane.game.TheSun;
import ro.sg.avioane.geometry.XYZMaterial;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.DebugUtils;
import ro.sg.avioane.util.OpenGLBufferArray3;
import ro.sg.avioane.util.OpenGLProgram;
import ro.sg.avioane.util.OpenGLProgramFactory;
import ro.sg.avioane.util.OpenGLUtils;
import ro.sg.avioane.util.TextureUtils;

import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_ONLY_VERTICES;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_UNDEFINED;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_DIFFUSE_TEXTURE;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_GLOBAL_DIFFUSE_COLOR;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_AMBIENT_TEXTURE;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_NORMALS;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_OWN_COLOR;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_UV_DATA_MATERIAL;

/**
 * How the vertices shader is organized VBO:
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
public abstract class AbstractGameCavan extends CavanMovements {

    private static final byte NO_OF_COORDINATES_PER_VERTEX = 3; //use X,Y,Z
    private static final byte NO_OF_COLORS_PER_VERTEX = 4; //use R,G,B,A
    private static final byte NO_OF_TEXTURES_PER_VERTEX = 2; //use U,V
    private static final byte NO_OF_NORMAL_COORDINATES_PER_VERTEX = NO_OF_COORDINATES_PER_VERTEX;
    private static final int VERTICES_OFFSET = 0;
    private int COLOR_OFFSET = -1; //BYTES_PER_FLOAT * NO_OF_COORDINATES_PER_VERTEX;
    private int TEXTURE_OFFSET = -1; //COLOR_OFFSET + BYTES_PER_FLOAT * NO_OF_COLORS_PER_VERTEX;
    private int NORMAL_OFFSET = -1;

    private OpenGLProgram iProgram = null;
    private OpenGLBufferArray3 iOpenGL3Buffers = null;

    /**
     * the material that is associated with this MESH
     */
    private XYZMaterial iMaterial = null;

    //Light part
    private TheSun iTheSun = TheSun.getStaticInstance();

    //Textures part

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
    protected void build(final XYZVertex[] arrVertices, final int[] drawOrderArr, final XYZMaterial material){
        if(BuildConfig.DEBUG && material == null){
            throw new AssertionError("cannot call build with NULL material. Make sure a material is set!");
        }
        this.iMaterial = material;
        this.calculateShaderType(arrVertices[0]);
        this.iProgram = OpenGLProgramFactory.getInstance().getProgramForShader(this.iShaderType);
        this.buildVertexBuffer(arrVertices);
        this.buildDrawOrderBuffer(drawOrderArr);
        this.buildUniforms();
    }

    /**
     * initialize the uniforms
     */
    private void buildUniforms(){
        this.iProgram.iModelMatrixHandle = GLES30.glGetUniformLocation(this.iProgram.iProgramHandlePtr, OpenGLProgramFactory.SHADER_VARIABLE_theModelMatrix);
        DebugUtils.checkPrintGLError();
        this.iProgram.iModelTransInvHandle = GLES30.glGetUniformLocation(this.iProgram.iProgramHandlePtr, OpenGLProgramFactory.SHADER_VARIABLE_theModelTransInvMatrix);
        DebugUtils.checkPrintGLError();
        this.iProgram.iViewMatrixHandle = GLES30.glGetUniformLocation(this.iProgram.iProgramHandlePtr, OpenGLProgramFactory.SHADER_VARIABLE_theViewMatrix);
        DebugUtils.checkPrintGLError();
        this.iProgram.iProjectionMatrixHandle = GLES30.glGetUniformLocation(this.iProgram.iProgramHandlePtr, OpenGLProgramFactory.SHADER_VARIABLE_theProjectionMatrix);
        DebugUtils.checkPrintGLError();


        //the lightning
        this.iProgram.iAmbientLightStrengtPtr = GLES30.glGetUniformLocation(this.iProgram.iProgramHandlePtr, OpenGLProgramFactory.SHADER_VARIABLE_ambientLightStrength);
        DebugUtils.checkPrintGLError();
        this.iProgram.iAmbientLightColorPtr = GLES30.glGetUniformLocation(this.iProgram.iProgramHandlePtr, OpenGLProgramFactory.SHADER_VARIABLE_ambientLightColor);
        DebugUtils.checkPrintGLError();
        this.iProgram.iDiffuseLightColorPtr = GLES30.glGetUniformLocation(this.iProgram.iProgramHandlePtr, OpenGLProgramFactory.SHADER_VARIABLE_diffuseLightColor);
        DebugUtils.checkPrintGLError();
        this.iProgram.iDiffuseDirectionPtr = GLES30.glGetUniformLocation(this.iProgram.iProgramHandlePtr, OpenGLProgramFactory.SHADER_VARIABLE_diffuseLightDirection);
        DebugUtils.checkPrintGLError();

        //texture or background color
        if((this.iShaderType & SHADER_VERTICES_WITH_AMBIENT_TEXTURE) != 0){
            this.iProgram.iAmbientTexturePtr = GLES30.glGetUniformLocation(this.iProgram.iProgramHandlePtr, OpenGLProgramFactory.SHADER_VARIABLE_ambientTexture);
            DebugUtils.checkPrintGLError();
        } else {
            final boolean isColorPerVertex = (this.iShaderType & SHADER_VERTICES_WITH_OWN_COLOR) > 0;
            final boolean isColorGlobal = (this.iShaderType & SHADER_VERTICES_WITH_GLOBAL_DIFFUSE_COLOR) > 0;

            if(isColorGlobal) {
                this.iProgram.iDiffuseColorPtr = GLES30.glGetUniformLocation(this.iProgram.iProgramHandlePtr, OpenGLProgramFactory.SHADER_VARIABLE_diffuseMaterialColor);
                DebugUtils.checkPrintGLError();
            } else if(!isColorPerVertex && BuildConfig.DEBUG)
                throw new AssertionError("unknown color setting! No color??");
        }

//        if((this.iShaderType & SHADER_VERTICES_WITH_UV_TEXTURE) != 0){
//            if((this.iShaderType & SHADER_VERTICES_WITH_KA_CONSTANT) != 0) {
//                //Ka constant
//                this.iProgram.iAmbientKaHandle = GLES30.glGetUniformLocation(this.iProgram.iProgramHandle, OpenGLProgramFactory.SHADER_VARIABLE_ambientKaConstant);
//                DebugUtils.checkPrintGLError();
//
//                if((this.iShaderType & SHADER_VERTICES_WITH_KA_TEXTURE) != 0) {
//                    this.iProgram.iAmbientKaTexture = GLES30.glGetUniformLocation(this.iProgram.iProgramHandle, OpenGLProgramFactory.SHADER_VARIABLE_ambientKaTexture);
//                    DebugUtils.checkPrintGLError();
//                } else if( (this.iShaderType & SHADER_VERTICES_WITH_GLOBAL_COLOR) != 0) {
//                    this.iProgram.iColorHandle = GLES30.glGetUniformLocation(this.iProgram.iProgramHandle, OpenGLProgramFactory.SHADER_VARIABLE_diffuseColor);
//                    DebugUtils.checkPrintGLError();
//                }
//            }
//        } else {
//            //color only
//            if( (this.iShaderType & SHADER_VERTICES_WITH_GLOBAL_COLOR) != 0) {
//                this.iProgram.iColorHandle = GLES30.glGetUniformLocation(this.iProgram.iProgramHandle, OpenGLProgramFactory.SHADER_VARIABLE_diffuseColor);
//                DebugUtils.checkPrintGLError();
//            }
//
//            //KA constant
//            if((this.iShaderType & SHADER_VERTICES_WITH_KA_CONSTANT) != 0) {
//                this.iProgram.iAmbientKaHandle = GLES30.glGetUniformLocation(this.iProgram.iProgramHandle, OpenGLProgramFactory.SHADER_VARIABLE_ambientKaConstant);
//                DebugUtils.checkPrintGLError();
//            }
//        }
    }

    /**
     *
     * @param drawOrder
     */
    protected void buildDrawOrderBuffer(final int[] drawOrder){
        if(BuildConfig.DEBUG &&
                this.iShaderType == SHADER_UNDEFINED)
            throw new AssertionError("SHADER_UNDEFINED ERROR!");

        this.iIndexOrderLength = drawOrder.length;
        // initialize byte buffer for the draw list
        final ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per int)
                drawOrder.length * Integer.BYTES);
        bb.order(ByteOrder.nativeOrder());
        final IntBuffer drawOrderBuffer = bb.asIntBuffer();
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
                drawOrderBuffer.capacity() * Integer.BYTES,
                drawOrderBuffer, GLES30.GL_STATIC_DRAW);

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

        final int vertexStride = this.iShaderStride/(Float.BYTES); //getVertexStride(arrVertices[0]);
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
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].vertexColor.red();
                dynamicStride++;
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].vertexColor.green();
                dynamicStride++;
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].vertexColor.blue();
                dynamicStride++;
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].vertexColor.alpha();
                dynamicStride++;
            }
            if((this.iShaderType & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) != 0){
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].getUvTexture().get().u;
                dynamicStride++;
                arrBufferVertices[vertexStride*i + dynamicStride] = arrVertices[i].getUvTexture().get().v;
                dynamicStride++;
            }

            //add the normals
            if(BuildConfig.DEBUG && !arrVertices[i].getNormal().isPresent()){
                throw new UnsupportedOperationException("normals must be present!");
                //TODO: remove the needs for normals and make them into the shaders also optional
            }
            arrBufferVertices[vertexStride * i + dynamicStride] = arrVertices[i].getNormal().get().x();
            dynamicStride++;
            arrBufferVertices[vertexStride * i + dynamicStride] = arrVertices[i].getNormal().get().y();
            dynamicStride++;
            arrBufferVertices[vertexStride * i + dynamicStride] = arrVertices[i].getNormal().get().z();
            //dynamicStride++;
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
                vertexBuffer.capacity() * (Float.BYTES), vertexBuffer,
                GLES30.GL_STATIC_DRAW, this.iProgram.iVerticesHandle,
                AbstractGameCavan.NO_OF_COORDINATES_PER_VERTEX,
                GLES30.GL_FLOAT, false,this.iShaderStride, VERTICES_OFFSET);

        if((this.iShaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0){
            this.iOpenGL3Buffers.addBuildVDOBuffer(this.iProgram.iDiffuseColorPtr,
                    AbstractGameCavan.NO_OF_COLORS_PER_VERTEX, this.iShaderStride,
                    COLOR_OFFSET);
        }

        if((this.iShaderType & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) != 0) {
            this.iOpenGL3Buffers.addBuildVDOBuffer(this.iProgram.iUVTextureHandle,
                    AbstractGameCavan.NO_OF_TEXTURES_PER_VERTEX, this.iShaderStride,
                    TEXTURE_OFFSET);
        }

        //set the normals
        this.iOpenGL3Buffers.addBuildVDOBuffer(this.iProgram.iNormalHandle,
                AbstractGameCavan.NO_OF_NORMAL_COORDINATES_PER_VERTEX, this.iShaderStride,
                NORMAL_OFFSET);


        this.iOpenGL3Buffers.finishBuildBuffers();


        //release main memory
        vertexBuffer.limit(0);
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
        int vertexStride = NO_OF_COORDINATES_PER_VERTEX; // X, Y, Z, --> 0, 1, 2
        int offset = (Float.BYTES) * NO_OF_COORDINATES_PER_VERTEX;
        this.iShaderType = SHADER_ONLY_VERTICES;

        //RGBA color
        if (vertex.vertexColor != null) {
            this.iShaderType |= SHADER_VERTICES_WITH_OWN_COLOR;
            // R, G, B, A --> 3, 4, 5, 6
            vertexStride += NO_OF_COLORS_PER_VERTEX;
            COLOR_OFFSET = offset;
            offset += (Float.BYTES) * NO_OF_COLORS_PER_VERTEX;

        } else if (this.iMaterial.iDiffuseMaterialColor != null) {
            this.iShaderType |= SHADER_VERTICES_WITH_GLOBAL_DIFFUSE_COLOR;
        }

        //U,V
        if(vertex.getUvTexture().isPresent()){
            // U, V --> 7,8
            this.iShaderType |= SHADER_VERTICES_WITH_UV_DATA_MATERIAL;
            //U,V
            vertexStride += NO_OF_TEXTURES_PER_VERTEX;
            TEXTURE_OFFSET = offset;
            offset += (Float.BYTES) * NO_OF_TEXTURES_PER_VERTEX;
        }

        //Normal
        //Xn,Yn,Zn
        //Xn,Yn,Zn --> 9,10,11
        if(vertex.getNormal().isPresent()) {
            this.iShaderType |= SHADER_VERTICES_WITH_NORMALS;
            vertexStride += NO_OF_NORMAL_COORDINATES_PER_VERTEX;
            NORMAL_OFFSET = offset;
            offset += (Float.BYTES) * NO_OF_NORMAL_COORDINATES_PER_VERTEX;
        }


        //material properties
        {
            //Ambient texture
            if(this.iMaterial.iAmbientFileNameID != OpenGLUtils.INVALID_UNSIGNED_VALUE){
                this.iShaderType |= SHADER_VERTICES_WITH_AMBIENT_TEXTURE;
            }
            //Diffuse texture
            if(this.iMaterial.iDiffuseFileNameID != OpenGLUtils.INVALID_UNSIGNED_VALUE){
                this.iShaderType |= SHADER_VERTICES_WITH_DIFFUSE_TEXTURE;
            }

            if(BuildConfig.DEBUG){
                final boolean isCheckOK = (
                        (this.iShaderType & SHADER_VERTICES_WITH_AMBIENT_TEXTURE) +
                        (this.iShaderType & SHADER_VERTICES_WITH_DIFFUSE_TEXTURE)
                ) > 0;
                if(!isCheckOK && (this.iShaderType & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) != 0){
                    //throw new AssertionError("fatal error: U,V data is defined but there is no material to be applied on!");
                    System.out.println("fatal error: U,V data is defined but there is no material to be applied on!");
                }
            }
        }

        this.iShaderStride =  vertexStride * (Float.BYTES);
    }


    /**
     *
     * @return this object material instance
     */
    public XYZMaterial getOBJMaterial() {
        return this.iMaterial;
    }

    /**
     * process the main draw having in mind that all the matrices are already set into OpenGl memory.
     *     used for the main shader:
     *     TransformedVector = TranslationMatrix * RotationMatrix * ScaleMatrix * OriginalVector;
     * @param viewMatrix
     * @param projectionMatrix
     * @param FORM_TYPE
     */
    @CallSuper
    protected void doDraw(final float[] viewMatrix, final float[] projectionMatrix,
                       final int FORM_TYPE){
        int textureCounter = 0;
        // counterclockwise orientation of the ordered vertices
        GLES30.glFrontFace(GLES30.GL_CCW);
        
        // Enable face culling.
        GLES30.glEnable(GLES30.GL_CULL_FACE); //--> make sure is disabled at clean up!
        // What faces to remove with the face culling.
        GLES30.glCullFace(GLES30.GL_BACK);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);


        //1. used program
        GLES30.glUseProgram(this.iProgram.iProgramHandlePtr);
        DebugUtils.checkPrintGLError();

        //set data via VAO
        GLES30.glBindVertexArray(this.iOpenGL3Buffers.VAO());

        //set matrices
        GLES30.glUniformMatrix4fv(this.iProgram.iModelMatrixHandle, 1, false, super.getModelMatrix(), 0);
        DebugUtils.checkPrintGLError();
        GLES30.glUniformMatrix4fv(this.iProgram.iModelTransInvHandle, 1, false, super.getModelTransInvMatrixMatrix(), 0);
        DebugUtils.checkPrintGLError();
        GLES30.glUniformMatrix4fv(this.iProgram.iViewMatrixHandle, 1, false, viewMatrix, 0);
        DebugUtils.checkPrintGLError();
        GLES30.glUniformMatrix4fv(this.iProgram.iProjectionMatrixHandle, 1, false, projectionMatrix, 0);
        DebugUtils.checkPrintGLError();

        //set ambient color
        GLES30.glUniform4fv(this.iProgram.iAmbientLightColorPtr, 1, this.iTheSun.getAmbientLight().getAmbientColor().asFloatArray(), 0);
        DebugUtils.checkPrintGLError();
        GLES30.glUniform1f(this.iProgram.iAmbientLightStrengtPtr, this.iTheSun.getAmbientLight().getAmbientColorStrength());
        DebugUtils.checkPrintGLError();
        //set diffuse color
        GLES30.glUniform4fv(this.iProgram.iDiffuseLightColorPtr, 1, this.iTheSun.getDiffuseLight().getDiffuseColor().asFloatArray(), 0);
        DebugUtils.checkPrintGLError();
        GLES30.glUniform3fv(this.iProgram.iDiffuseDirectionPtr, 1, this.iTheSun.getSunLightDirection().asArray(), 0);
        DebugUtils.checkPrintGLError();

        //texture or background color
        final boolean isColorPerVertex = (this.iShaderType & SHADER_VERTICES_WITH_OWN_COLOR) > 0;
        final boolean isColorGlobal = (this.iShaderType & SHADER_VERTICES_WITH_GLOBAL_DIFFUSE_COLOR) > 0;

        if((this.iShaderType & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) != 0){
            GLES30.glEnable(GLES20.GL_TEXTURE_2D);
            //set U,V
            GLES30.glEnableVertexAttribArray(this.iProgram.iUVTextureHandle);
            DebugUtils.checkPrintGLError();
            //texture for ambient light
            if((this.iShaderType & SHADER_VERTICES_WITH_AMBIENT_TEXTURE) != 0) {
                final int tmp = GLES30.GL_TEXTURE0 + (textureCounter++);
                GLES30.glActiveTexture(tmp);
                DebugUtils.checkPrintGLError();
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, TextureUtils.getInstance().getTextureDataBuffer(this.iMaterial.iAmbientFileNameID));
                DebugUtils.checkPrintGLError();
                GLES30.glUniform1i(this.iProgram.iAmbientTexturePtr, tmp);
                DebugUtils.checkPrintGLError();
            }
        } else {
            if(isColorPerVertex){
                GLES30.glEnableVertexAttribArray(this.iProgram.iDiffuseColorPtr);
                DebugUtils.checkPrintGLError();
            } else if(isColorGlobal){
                GLES30.glUniform4fv(this.iProgram.iDiffuseColorPtr, 1, this.iMaterial.iDiffuseMaterialColor.asFloatArray(), 0);
                DebugUtils.checkPrintGLError();
            } else if(BuildConfig.DEBUG){
                throw new AssertionError("missing background color");
            }
        }

//        if((this.iShaderType & SHADER_VERTICES_WITH_MATERIAL) != 0){
//            //texture instead of color
//            //set U,V
//            GLES30.glEnableVertexAttribArray(this.iProgram.iUVTextureHandle);
//            DebugUtils.checkPrintGLError();
//
//            if((this.iShaderType & SHADER_VERTICES_WITH_KA_CONSTANT) != 0) {
//                GLES30.glUniform3fv(this.iProgram.iAmbientKaConstantHandle, 1, this.iMaterial.getConstantKA().get().asArray(), 0);
//                DebugUtils.checkPrintGLError();
//                if((this.iShaderType & SHADER_VERTICES_WITH_KA_TEXTURE) != 0) {
//                    GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
//                    DebugUtils.checkPrintGLError();
//                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, TextureUtils.getInstance().getTextureDataBuffer(this.iMaterial.mapKA_FileNameID));
//                    DebugUtils.checkPrintGLError();
//                    GLES30.glUniform1i(this.iProgram.iAmbientKaTexture, 0);
//                } else if((this.iShaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
//                    GLES30.glEnableVertexAttribArray(this.iProgram.iColorHandle);
//                } else if( (this.iShaderType & SHADER_VERTICES_WITH_GLOBAL_COLOR) != 0) {
//                    GLES30.glUniform4fv(this.iProgram.iColorHandle, 1, this.iMaterial.globalBackgroundColor.asFloatArray(), 0);
//                } else {
//                    throw new AssertionError("unknwon shader type! " + this.iShaderType);
//                }
//                DebugUtils.checkPrintGLError();
//            }
//        } else {
//            //color only
//            if( (this.iShaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
//                GLES30.glEnableVertexAttribArray(this.iProgram.iColorHandle);
//            } else if( (this.iShaderType & SHADER_VERTICES_WITH_GLOBAL_COLOR) != 0) {
//                GLES30.glUniform4fv(this.iProgram.iColorHandle, 1, this.iMaterial.globalBackgroundColor.asFloatArray(), 0);
//            } else {
//                throw new AssertionError("unknwon shader type! " + this.iShaderType);
//            }
//            DebugUtils.checkPrintGLError();
//
//            if((this.iShaderType & SHADER_VERTICES_WITH_KA_CONSTANT) != 0) {
//                GLES30.glUniform3fv(this.iProgram.iAmbientKaConstantHandle, 1, this.iMaterial.getConstantKA().get().asArray(), 0);
//                DebugUtils.checkPrintGLError();
//            }
//
//        }


        //set normals
        if((this.iShaderType & SHADER_VERTICES_WITH_NORMALS) > 0) {
            //set Xn, Yn, Zn
            GLES30.glEnableVertexAttribArray(this.iProgram.iNormalHandle);
            DebugUtils.checkPrintGLError();
        }


        //bind the drawing order
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, this.iOpenGL3Buffers.getVertexOrderBuffer());
        DebugUtils.checkPrintGLError();

        //6. Draw the form
        if(GLES30.GL_LINES == FORM_TYPE) {
            GLES30.glDrawElements(GLES30.GL_LINES, this.iIndexOrderLength, GLES30.GL_UNSIGNED_INT, 0/*this.iDrawOrderBuffer*/);
        } else if(GLES30.GL_TRIANGLE_STRIP == FORM_TYPE) {
            GLES30.glDrawElements(GLES30.GL_TRIANGLE_STRIP, this.iIndexOrderLength, GLES30.GL_UNSIGNED_INT, 0/*this.iDrawOrderBuffer*/);
        } else if(GLES30.GL_TRIANGLES == FORM_TYPE) {
            GLES30.glDrawElements(GLES30.GL_TRIANGLES, this.iIndexOrderLength, GLES30.GL_UNSIGNED_INT, 0/*this.iDrawOrderBuffer*/);
        }else if(GLES30.GL_POINTS == FORM_TYPE) {
            GLES30.glDrawElements(GLES30.GL_POINTS, this.iIndexOrderLength, GLES30.GL_UNSIGNED_INT, 0/*this.iDrawOrderBuffer*/);
        } else {
            throw new RuntimeException("FATAL ERROR !!! unknown FORM_TYPE=" + FORM_TYPE);
        }
        DebugUtils.checkPrintGLError();

        //7 cleanup
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0); //C1

        //normals
        if((this.iShaderType & SHADER_VERTICES_WITH_NORMALS) > 0) {
            GLES30.glDisableVertexAttribArray(this.iProgram.iNormalHandle); //C2
            DebugUtils.checkPrintGLError();
        }


        if((this.iShaderType & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) != 0) {
            if((this.iShaderType & SHADER_VERTICES_WITH_AMBIENT_TEXTURE) != 0) // || (add here with OR all textures!)
            {
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0); //with one call unbind all them all //C3
            }
            //clean U,V
            GLES30.glDisableVertexAttribArray(this.iProgram.iUVTextureHandle); //C4
            DebugUtils.checkPrintGLError();
            GLES30.glDisable(GLES20.GL_TEXTURE_2D); //C5
            DebugUtils.checkPrintGLError();
        } else {
            if(isColorPerVertex) {
                GLES30.glDisableVertexAttribArray(this.iProgram.iDiffuseColorPtr); //C3
                DebugUtils.checkPrintGLError();
            }
        }
        GLES30.glBindVertexArray(0);
        DebugUtils.checkPrintGLError();
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        GLES30.glDisable(GLES30.GL_CULL_FACE);
    }

    @CallSuper
    public void destroy(){
        COLOR_OFFSET = -1;
        TEXTURE_OFFSET = -1;
        NORMAL_OFFSET = -1;

        iProgram = null;
        iOpenGL3Buffers = null;

        iShaderType = SHADER_UNDEFINED;
        iIndexOrderLength = 0;
        iShaderStride = 0;
    }

}
