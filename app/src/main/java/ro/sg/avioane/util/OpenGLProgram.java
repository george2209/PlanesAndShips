/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import android.opengl.GLES20;

import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_KA_CONSTANT;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_KD_CONSTANT;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_OWN_COLOR;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_UV_DATA_MATERIAL;

public class OpenGLProgram {

    public int iProgramHandle;
    public int iVerticesHandle;
    public int iUVTextureHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;//linked against SHADER_VARIABLE_aUVTexture
    public int iColorHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iAmbientColorHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iAmbientStrengthHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iDiffuseLightColorHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iDiffuseDirectionHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;

    //Texture handles
    public int iAmbientKaConstantHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE; //linked against SHADER_VARIABLE_ambientKAConstant
    public int iAmbientKaTexture = OpenGLUtils.INVALID_UNSIGNED_VALUE; //linked against SHADER_VARIABLE_ambientKaTexture
    public int iDiffuseKdConstantHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE; //linked against SHADER_VARIABLE_diffuseKdConstant
    public int iDiffuseKaTexture = OpenGLUtils.INVALID_UNSIGNED_VALUE; //linked against SHADER_VARIABLE_DiffuseKaTexture

    public int iNormalHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iProjectionMatrixHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iViewMatrixHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iModelMatrixHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iModelTransInvHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iVertexShader = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iFragmentShader = OpenGLUtils.INVALID_UNSIGNED_VALUE;


    public OpenGLProgram(final String vertexShaderCode,
                         final String fragmentShaderCode,
                         final int shaderType){
        this.iVertexShader = OpenGLProgramFactory.getLoadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        this.iFragmentShader = OpenGLProgramFactory.getLoadShader(GLES20.GL_FRAGMENT_SHADER,
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

        //MAIN SHADER ATTRIBUTES///////////////
        iModelMatrixHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandle, iModelMatrixHandle, OpenGLProgramFactory.SHADER_VARIABLE_theModelMatrix);
        iModelTransInvHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandle, iModelTransInvHandle, OpenGLProgramFactory.SHADER_VARIABLE_theModelTransInvMatrix);
        iViewMatrixHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandle, iViewMatrixHandle, OpenGLProgramFactory.SHADER_VARIABLE_theViewMatrix);
        iProjectionMatrixHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandle, iProjectionMatrixHandle, OpenGLProgramFactory.SHADER_VARIABLE_theProjectionMatrix);
        iVerticesHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandle, iVerticesHandle, OpenGLProgramFactory.SHADER_VARIABLE_aPosition);

        if((shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
            iColorHandle = attributeIndex++;
            GLES20.glBindAttribLocation(iProgramHandle, iColorHandle, OpenGLProgramFactory.SHADER_VARIABLE_aVertexColor);
        }

        //set the normals
        iNormalHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandle, iNormalHandle, OpenGLProgramFactory.SHADER_VARIABLE_aNormal);


        //FRAGMENT SHADER ATTRIBUTES///////////////
        iAmbientColorHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandle, iAmbientColorHandle, OpenGLProgramFactory.SHADER_VARIABLE_ambientLightColor);
        iAmbientStrengthHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandle, iAmbientStrengthHandle, OpenGLProgramFactory.SHADER_VARIABLE_ambientLightStrength);
        iDiffuseLightColorHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandle, iDiffuseLightColorHandle, OpenGLProgramFactory.SHADER_VARIABLE_diffuseLightColor);
        iDiffuseDirectionHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandle, iDiffuseDirectionHandle, OpenGLProgramFactory.SHADER_VARIABLE_diffuseLightDirection);


        if((shaderType & SHADER_VERTICES_WITH_KA_CONSTANT) != 0) {
            iAmbientKaConstantHandle = attributeIndex++;
            GLES20.glBindAttribLocation(iProgramHandle, iAmbientKaConstantHandle, OpenGLProgramFactory.SHADER_VARIABLE_ambientKaConstant);
        }
        if((shaderType & SHADER_VERTICES_WITH_KD_CONSTANT) != 0) {
            iDiffuseKdConstantHandle = attributeIndex++;
            GLES20.glBindAttribLocation(iProgramHandle, iDiffuseKdConstantHandle, OpenGLProgramFactory.SHADER_VARIABLE_diffuseKdConstant);
        }

        if((shaderType & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) != 0){
            this.iUVTextureHandle = attributeIndex++;
            GLES20.glBindAttribLocation(iProgramHandle, this.iUVTextureHandle, OpenGLProgramFactory.SHADER_VARIABLE_aUVTexture);
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
