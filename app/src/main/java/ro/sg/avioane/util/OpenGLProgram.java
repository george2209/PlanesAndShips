/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import android.opengl.GLES20;

import static ro.sg.avioane.util.OpenGLProgramUtils.SHADER_VERTICES_WITH_OWN_COLOR;
import static ro.sg.avioane.util.OpenGLProgramUtils.SHADER_VERTICES_WITH_TEXTURE;

public class OpenGLProgram {

    public int iProgramHandle = -1;
    public int iVerticesHandle = -1;
    public int iColorHandle = -1;
    public int iTextureHandle = -1;

    public int iVertexShader = -1;
    public int iFragmentShader = -1;


    public OpenGLProgram(final String vertexShaderCode, final String fragmentShaderCode, final int shaderType){
        this.iVertexShader = OpenGLProgramUtils.getLoadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        this.iFragmentShader = OpenGLProgramUtils.getLoadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        iProgramHandle = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(iProgramHandle, iVertexShader);
        DebugUtils.checkPrintGLError();

        // add the fragment shader to program
        GLES20.glAttachShader(iProgramHandle, iFragmentShader);
        DebugUtils.checkPrintGLError();

        // Bind attributes
        int attributeIndex = 0;
        iVerticesHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandle, iVerticesHandle, OpenGLProgramUtils.SHADER_VARIABLE_aPosition);

        if((shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
            iColorHandle = attributeIndex++;
            GLES20.glBindAttribLocation(iProgramHandle, iColorHandle, OpenGLProgramUtils.SHADER_VARIABLE_aColor);
        }

        if((shaderType & SHADER_VERTICES_WITH_TEXTURE) != 0){
            iTextureHandle = attributeIndex++;
            GLES20.glBindAttribLocation(iProgramHandle, iTextureHandle, OpenGLProgramUtils.SHADER_VARIABLE_aTexture);
        }

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(iProgramHandle);

        // Get the link status.
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(iProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

        // If the link failed, delete the program.
        if (linkStatus[0] == 0)
        {
            final String errStr = GLES20.glGetProgramInfoLog(iProgramHandle);
            GLES20.glDeleteProgram(iProgramHandle);
            throw new RuntimeException("FATAL ERROR !!! Program link failed with link status = " + linkStatus[0] + " \n\n " + errStr + " \n\n ");
        }

        //prepare for cleanup
        GLES20.glDeleteShader(iVertexShader);
        GLES20.glDeleteShader(iFragmentShader);
    }

    public void destroy(){
        if(this.iProgramHandle != -1) {
            GLES20.glDeleteProgram(this.iProgramHandle);
            this.iProgramHandle = -1;
        }
    }
}
