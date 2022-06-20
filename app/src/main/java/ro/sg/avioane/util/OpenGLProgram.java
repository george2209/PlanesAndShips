/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import android.opengl.GLES20;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_OWN_COLOR;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_UV_DATA_MATERIAL;

public class OpenGLProgram {

    public int iProgramHandlePtr;
    public int iVerticesHandle;
    public int iUVTextureHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE;//linked against SHADER_VARIABLE_aUVTexture
    public int iDiffuseColorPtr = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iAmbientLightColorPtr = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iAmbientLightStrengtPtr = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iDiffuseLightColorPtr = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int iDiffuseDirectionPtr = OpenGLUtils.INVALID_UNSIGNED_VALUE;

    //Texture handles
    //public int iAmbientKaConstantHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE; //linked against SHADER_VARIABLE_ambientKAConstant
    public int iAmbientTexturePtr = OpenGLUtils.INVALID_UNSIGNED_VALUE; //linked against SHADER_VARIABLE_ambientTexture
    //public int iDiffuseKdConstantHandle = OpenGLUtils.INVALID_UNSIGNED_VALUE; //linked against SHADER_VARIABLE_diffuseKdConstant
    public int iDiffuseKaTexture = OpenGLUtils.INVALID_UNSIGNED_VALUE; //linked against SHADER_VARIABLE_diffuseTexture

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
        iProgramHandlePtr = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(iProgramHandlePtr, iVertexShader);
        DebugUtils.checkPrintGLError();

        // add the fragment shader to program
        GLES20.glAttachShader(iProgramHandlePtr, iFragmentShader);
        DebugUtils.checkPrintGLError();

        // Bind attributes
        int attributeIndex = 0;

        //MAIN SHADER ATTRIBUTES///////////////
        iModelMatrixHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandlePtr, iModelMatrixHandle, OpenGLProgramFactory.SHADER_VARIABLE_theModelMatrix);
        iModelTransInvHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandlePtr, iModelTransInvHandle, OpenGLProgramFactory.SHADER_VARIABLE_theModelTransInvMatrix);
        iViewMatrixHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandlePtr, iViewMatrixHandle, OpenGLProgramFactory.SHADER_VARIABLE_theViewMatrix);
        iProjectionMatrixHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandlePtr, iProjectionMatrixHandle, OpenGLProgramFactory.SHADER_VARIABLE_theProjectionMatrix);
        iVerticesHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandlePtr, iVerticesHandle, OpenGLProgramFactory.SHADER_VARIABLE_aPosition);

        if((shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
            iDiffuseColorPtr = attributeIndex++;
            GLES20.glBindAttribLocation(iProgramHandlePtr, iDiffuseColorPtr, OpenGLProgramFactory.SHADER_VARIABLE_aVertexColor);
        }

        //set the normals
        iNormalHandle = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandlePtr, iNormalHandle, OpenGLProgramFactory.SHADER_VARIABLE_aNormal);


        //FRAGMENT SHADER ATTRIBUTES///////////////
        iAmbientLightColorPtr = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandlePtr, iAmbientLightColorPtr, OpenGLProgramFactory.SHADER_VARIABLE_ambientLightColor);
        iAmbientLightStrengtPtr = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandlePtr, iAmbientLightStrengtPtr, OpenGLProgramFactory.SHADER_VARIABLE_ambientLightStrength);
        iDiffuseLightColorPtr = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandlePtr, iDiffuseLightColorPtr, OpenGLProgramFactory.SHADER_VARIABLE_diffuseLightColor);
        iDiffuseDirectionPtr = attributeIndex++;
        GLES20.glBindAttribLocation(iProgramHandlePtr, iDiffuseDirectionPtr, OpenGLProgramFactory.SHADER_VARIABLE_diffuseLightDirection);

        if((shaderType & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) != 0){
            this.iUVTextureHandle = attributeIndex++;
            GLES20.glBindAttribLocation(iProgramHandlePtr, this.iUVTextureHandle, OpenGLProgramFactory.SHADER_VARIABLE_aUVTexture);
        }

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(iProgramHandlePtr);

        // Get the link status.
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(iProgramHandlePtr, GLES20.GL_LINK_STATUS, linkStatus, 0);

        // If the link failed, delete the program.
        if (linkStatus[0] == 0)
        {
            final String errStr = GLES20.glGetProgramInfoLog(iProgramHandlePtr);
            GLES20.glDeleteProgram(iProgramHandlePtr);
            throw new RuntimeException("FATAL ERROR !!! Program link failed with link status = " + linkStatus[0] + " \n\n " + errStr + " \n\n ");
        }

        //prepare for cleanup
        GLES20.glDeleteShader(iVertexShader);
        GLES20.glDeleteShader(iFragmentShader);
    }

    public void destroy(){
        if(this.iProgramHandlePtr != -1) {
            GLES20.glDeleteProgram(this.iProgramHandlePtr);
            this.iProgramHandlePtr = -1;
        }
    }
}
