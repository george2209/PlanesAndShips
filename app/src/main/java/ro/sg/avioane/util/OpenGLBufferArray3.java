/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;

import ro.sg.avioane.geometry.XYZTexture;

/**
 * a class maintaining the VBO and the VAO of a specific vertex array
 * It is requiring OpenGL3.0 or more.
 */
public class OpenGLBufferArray3 {
    private int VAO = -1;
    private int VBO = -1;
    private int iVertexOrderBuffer = -1;
    private int iTextureDataBuffer = -1;

    public OpenGLBufferArray3(){
    }

    /**
     * Start building the VAO, VBO (and binding it to the GLES30.GL_ARRAY_BUFFER)
     * as well as linking the VBO to the shader pointer.
     * IMPORTANT:
     *  - once you build all your buffers call @link #finishBuildBuffers
     * @param size
     * @param buffer
     * @param usage
     * @param verticesPointer shader pointer ID
     * @param noCoordinatesPerVertex
     * @param dataType
     * @param isNormalized
     * @param shaderStride
     * @param offset
     * @return an instance of this object if you need to call other addBuildBufferXXX methods.
     */
    public OpenGLBufferArray3 startBuildBuffers(final int size,
                                                final java.nio.Buffer buffer,
                                                final int usage, int verticesPointer,
                                                int noCoordinatesPerVertex,
                                                int dataType, boolean isNormalized,
                                                int shaderStride,
                                                int offset){
        //create VertexArrayObject
        final int buffers[] = new int[1];
        if(this.VAO == -1) {
            GLES30.glGenVertexArrays(1, buffers, 0);
            VAO = buffers[0];
        } else {
            System.out.println("VAO*************already build! Continue...");
        }

        //create VertexBufferObject
        if(this.VBO == -1) {
            GLES30.glGenBuffers(1, buffers, 0);
            VBO = buffers[0];
        } else {
            System.out.println("VAO*************already build! Continue...");
        }

        GLES30.glBindVertexArray(VAO);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, size, buffer, usage);

        GLES30.glVertexAttribPointer(verticesPointer, noCoordinatesPerVertex,
                dataType, isNormalized,
                shaderStride, offset);

        GLES30.glEnableVertexAttribArray(verticesPointer);

        return this;
    }

    /**
     *
     * @param colorPointer the shader color pointerID
     * @param noColorsPerVertex normally is 4 (see AbstractGameCavan.NO_OF_COLORS_PER_VERTEX)
     * @param shaderStride
     * @param offset the offset of the color inside the vertex buffer
     * @return
     */
    public OpenGLBufferArray3 addBuildBufferColors(int colorPointer, int noColorsPerVertex,
                                                   int shaderStride,
                                                   int offset){
        GLES30.glVertexAttribPointer(colorPointer, noColorsPerVertex ,
                    GLES30.GL_FLOAT, false,
                shaderStride, offset);
        GLES30.glEnableVertexAttribArray(colorPointer);
        return this;
    }

    /**
     *
     * @param texturePointer shader textureID
     * @param noTexturesPerVertex normally is 2 (U,V)
     * @param shaderStride
     * @param offset the offset of the texture inside the vertex buffer
     * @return
     */
    public OpenGLBufferArray3 addBuildTextures(int texturePointer, int noTexturesPerVertex,
                                               int shaderStride, int offset, final XYZTexture textureObj){
        this.iTextureDataBuffer = TextureUtils.getInstance().
                getTextureWithName(textureObj.getTextureName(), textureObj.getTextureData());
        return this.addBuildBufferColors(texturePointer, noTexturesPerVertex, shaderStride,offset);
    }

    /**
     * see @link #startBuildBuffers
     */
    public void finishBuildBuffers(/*int shaderType*/){
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindVertexArray(0);
    }

//    /**
//     * Rebuild with new values.
//     * @param bufferTarget GLES20.GL_ELEMENT_ARRAY_BUFFER or GLES20.GL_ARRAY_BUFFER
//     * @param usage for the moment GLES20.GL_STATIC_DRAW is used
//     * @param buffer a buffer with the data loaded already.
//     *
//     */
//    public void rebuildBuffer(final int bufferTarget, final int size, final java.nio.Buffer buffer,
//                              final int usage, int programPointer, int noCoordinatesPerVertex,
//                              int dataType, boolean isNormalized, int shaderStride,
//                              int offset){
//
//        //create VertexArrayObject
//        final int buffers[] = new int[1];
//        if(this.VAO == -1) {
//            GLES30.glGenVertexArrays(1, buffers, 0);
//            VAO = buffers[0];
//        } else {
//            System.out.println("VAO*************already build! Continue...");
//        }
//
//        //create VertexBufferObject
//        if(this.VBO == -1) {
//            GLES30.glGenBuffers(1, buffers, 0);
//            VBO = buffers[0];
//        } else {
//            System.out.println("VAO*************already build! Continue...");
//        }
//
//        GLES30.glBindVertexArray(VAO);
//        GLES30.glBindBuffer(bufferTarget, VBO);
//        GLES30.glBufferData(bufferTarget, size, buffer, usage);
//
//        GLES30.glVertexAttribPointer(programPointer, noCoordinatesPerVertex,
//                dataType, isNormalized,
//                shaderStride, offset);
//
//        GLES30.glEnableVertexAttribArray(0);
//        GLES30.glBindBuffer(bufferTarget, 0);
//        GLES30.glBindVertexArray(0);
//    }

    public void rebuildVertexDrawOrder(final int size, final java.nio.Buffer buffer,
                                       final int usage){
        if(this.iVertexOrderBuffer == -1) {
            final int buffers[] = new int[1];
            GLES30.glGenBuffers(1, buffers, 0);
            this.iVertexOrderBuffer = buffers[0];
        } else {
            System.out.println("iVertexOrderBuffer*************already build! Continue...");
        }

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, iVertexOrderBuffer);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, size, buffer, usage);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }


    /**
     * delete all the existing buffers from the memory.
     */
    public void cleanVBOandVAO(){
        if(this.VBO != -1){
            final int buffers[] = new int[1];
            buffers[0] = this.VBO;
            GLES30.glDeleteBuffers(1, buffers, 0);
            this.VBO = -1;
        }
        if(this.VAO != -1){
            final int buffers[] = new int[1];
            buffers[0] = this.VAO;
            GLES30.glDeleteVertexArrays(1, buffers,0);
            this.VAO = -1;
        }
    }

    /**
     * clean the vertex draw-order buffer from the memory.
     */
    public void cleanVORBF(){
        if(this.iVertexOrderBuffer != -1){
            final int buffers[] = new int[1];
            buffers[0] = this.iVertexOrderBuffer;
            GLES30.glDeleteBuffers(1, buffers, 0);
            this.iVertexOrderBuffer = -1;
        }
    }

    public int VAO(){
        return this.VAO;
    }

    public int VBO(){
        return this.VBO;
    }

    public int getVertexOrderBuffer(){
        return this.iVertexOrderBuffer;
    }

    public int getTextureDataBuffer(){
        return this.iTextureDataBuffer;
    }
}
