/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import android.opengl.GLES20;
import android.opengl.GLES30;

import java.util.HashMap;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.util.shaders.AbstractShaderBuilder;
import ro.sg.avioane.util.shaders.FragmentShaderBuilder;
import ro.sg.avioane.util.shaders.MainShaderBuilder;

public class OpenGLProgramFactory {

    public static final int SHADER_UNDEFINED = 0;
    public static final int SHADER_ONLY_VERTICES = 1;
    public static final int SHADER_VERTICES_WITH_OWN_COLOR = SHADER_ONLY_VERTICES<<1;
    public static final int SHADER_VERTICES_WITH_GLOBAL_COLOR = SHADER_VERTICES_WITH_OWN_COLOR<<1;
    public static final int SHADER_VERTICES_WITH_UV_DATA_MATERIAL = SHADER_VERTICES_WITH_GLOBAL_COLOR <<1; ///needed???///////////////////
    //public static final int SHADER_VERTICES_WITH_NORMALS = SHADER_VERTICES_WITH_UV_DATA_MATERIAL <<1;
    //illumination/material constants
    public static final int SHADER_VERTICES_WITH_KA_CONSTANT = SHADER_VERTICES_WITH_UV_DATA_MATERIAL <<1;
    public static final int SHADER_VERTICES_WITH_KD_CONSTANT = SHADER_VERTICES_WITH_KA_CONSTANT <<1;
    public static final int SHADER_VERTICES_WITH_KS_CONSTANT = SHADER_VERTICES_WITH_KD_CONSTANT <<1;
    public static final int SHADER_VERTICES_WITH_KE_CONSTANT = SHADER_VERTICES_WITH_KS_CONSTANT <<1;
    //texture materials
    public static final int SHADER_VERTICES_WITH_KA_TEXTURE = SHADER_VERTICES_WITH_KE_CONSTANT <<1;
    public static final int SHADER_VERTICES_WITH_KD_TEXTURE = SHADER_VERTICES_WITH_KA_TEXTURE <<1;


    public static final short SHADER_VERSION = 300;
    //per vertex variables
    public static final String SHADER_VARIABLE_aPosition = "aPosition";
    public static final String SHADER_VARIABLE_aNormal = "aNormal";
    public static final String SHADER_VARIABLE_aVertexColor = "aVertexColor";
    //pass to the fragment shader
    public static final String SHADER_VARIABLE_FR_aVectorNormal = "aVectorNormal";
    public static final String SHADER_VARIABLE_FR_aUVTexture = "aFRUvTexture";

    //public static final String SHADER_VARIABLE_aGlobalColor = "aGlobalColor";
    //public static final String SHADER_VARIABLE_aColor = "aColor"; //replaced by SHADER_VARIABLE_diffuseColor

    //constants / uniforms
    public static final String SHADER_VARIABLE_aUVTexture = "aUvTexture";
    public static final String SHADER_VARIABLE_theModelMatrix = "theModelMatrix";
    public static final String SHADER_VARIABLE_theModelTransInvMatrix = "theModelTrInvMatrix";
    public static final String SHADER_VARIABLE_theViewMatrix = "theViewMatrix";
    public static final String SHADER_VARIABLE_theProjectionMatrix = "theProjectionMatrix";
    //lights
    public static final String SHADER_VARIABLE_ambientLightColor = "ambientLight.color"; //vec4
    public static final String SHADER_VARIABLE_ambientLightStrength = "ambientLight.strength"; //float
    public static final String SHADER_VARIABLE_diffuseLightColor = "diffuseLight.color"; //vec4
    public static final String SHADER_VARIABLE_diffuseLightDirection = "diffuseLight.direction"; //vec3
    //materials
    public static final String SHADER_VARIABLE_ambientKaConstant = "kaConstant"; //apply to ambient light
    public static final String SHADER_VARIABLE_ambientKaTexture = "kaTexture";
    public static final String SHADER_VARIABLE_diffuseMaterialColor = "aDiffuseColor";
    public static final String SHADER_VARIABLE_diffuseKdConstant = "kdConstant";
    public static final String SHADER_VARIABLE_diffuseKdTexture = "kdTexture";
    public static final String SHADER_VARIABLE_specularKsConstant = "ksConstant";
    public static final String SHADER_VARIABLE_specularKsTexture = "ksTexture";
    public static final String SHADER_VARIABLE_emissiveKeConstant = "keConstant";
    public static final String SHADER_VARIABLE_emissiveKeTexture = "keTexture";


    private final HashMap<Integer, OpenGLProgram> iProgramMap = new HashMap<>();

    private static OpenGLProgramFactory _instance = null;

    private OpenGLProgramFactory(){

    }

    public static OpenGLProgramFactory getInstance(){
        if(_instance == null){
            synchronized (OpenGLProgramFactory.class){
                if(_instance == null){
                    _instance = new OpenGLProgramFactory();
                }
            }
        }
        return _instance;
    }

    public static void killInstance(){
        if(_instance != null){
            synchronized (OpenGLProgramFactory.class){
                _instance = null;
            }
        }
    }

    /**
     * This is the central point for building and retrieving a program for a specific shader type.
     * @param shaderType see the constant bitwise values inside AbstractGameCavan (i.e. SHADER_ONLY_VERTICES)
     * @return the existing to be reused or a new program
     */
    public OpenGLProgram getProgramForShader(final int shaderType){
        if (BuildConfig.DEBUG &&
                (
                        (shaderType & SHADER_ONLY_VERTICES) +
                                (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) +
                                (shaderType & OpenGLProgramFactory.SHADER_VERTICES_WITH_UV_DATA_MATERIAL)
                ) == 0) {
            throw new AssertionError("unknown shader type=" + shaderType);
        }

        OpenGLProgram program = this.iProgramMap.get(shaderType);
        if(program == null){
            program = new OpenGLProgram(getBuildShader(shaderType),
                    getBuildFragmentShader(shaderType),
                    shaderType);
            this.iProgramMap.put(shaderType, program);
        }
        return program;
    }

    /**
     * build the shader code based on the param shaderType.
     Example:
     * <code>
     *     String fragment =
     *     OpenGLProgramFactory.getBuildShader(
     *     SHADER_ONLY_VERTICES | SHADER_VERTICES_WITH_OWN_COLOR
     *     );
     * </code>
     * This will build a shader having the X,Y,Z, R,G,B,A coordinates.
     *
     * TODO: it must be improved to support and do all M,V,P matrices calculus here!
     *
     * @param shaderType one of the values or a bitwise mix of the:
     *                   - SHADER_ONLY_VERTICES
     *                   - SHADER_VERTICES_WITH_OWN_COLOR
     *                   - SHADER_VERTICES_WITH_TEXTURE
     * @return the string representation of the shader code
     */
    private String getBuildShader(final int shaderType){
        final AbstractShaderBuilder mainShader = new MainShaderBuilder().withBackground(shaderType);
        if(BuildConfig.DEBUG)
            System.out.println(mainShader.asString());
        return mainShader.asString();
    }


    /**
     * build the fragment shader based on the param shaderType.
     * Example:
     * <code>
     *     String fragment =
     *     OpenGLProgramFactory.getBuildFragmentShader(
     *     SHADER_ONLY_VERTICES | SHADER_VERTICES_WITH_OWN_COLOR
     *     );
     * </code>
     *
     * The code was tested using the online support of:
     * http://www.cs.toronto.edu/~jacobson/phong-demo/
     *
     * TODO: one may want to replace the "if"s with some dynamic load from array of strings. I will
     * keep it like this as it is for the moment easier to be read however from CPU processing is
     * not optimal. See Issue #13.
     *
     * @param shaderType one of the values or a bitwise mix of the:
     *                   - SHADER_ONLY_VERTICES
     *                   - SHADER_VERTICES_WITH_OWN_COLOR
     *                   - SHADER_VERTICES_WITH_TEXTURE
     * @return the string representation of the fragment code
     */
    private String getBuildFragmentShader(final int shaderType){
        final FragmentShaderBuilder fragmentShaderBuilder = new FragmentShaderBuilder();
        fragmentShaderBuilder.withBackground(shaderType);

        if(BuildConfig.DEBUG)
            System.out.println(fragmentShaderBuilder.asString());

        return fragmentShaderBuilder.asString();
    }


    /**
     * Creates a Vertex or Fragment shader depending on the param "shaderType"
     *
     * added by dumitrageorge@gmail.com:
     * Take care! Shader calls should be within the "OpenGL thread"
     * that is onSurfaceChanged(), onSurfaceCreated() or onDrawFrame() !!!
     *
     * @param shaderType it can be either GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER
     * @param shaderCode the code that will be executed by this shader
     * @return a handle to the respective shader
     */
    public static int getLoadShader(final int shaderType, final String shaderCode){
        if (BuildConfig.DEBUG && (shaderType != GLES20.GL_VERTEX_SHADER && shaderType != GLES20.GL_FRAGMENT_SHADER)) {
            throw new AssertionError("unknown shader type=" + shaderType);
        }

        int shader = GLES30.glCreateShader(shaderType);
        if(shader == 0) {
            DebugUtils.checkPrintGLError();
            throw new RuntimeException("FATAL ERROR !!! \n\n shader=" + shader + " ERROR =>>>> " + GLES20.glGetError() + "\n\n" + "shader source=" + shaderCode + " \nisShader=" + GLES20.glIsShader(shader));
        } else {
            GLES30.glShaderSource(shader, shaderCode);
            GLES30.glCompileShader(shader);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                final String errorStr = GLES30.glGetShaderInfoLog(shader);
                GLES30.glDeleteShader(shader);
                //shader = 0;
                throw new RuntimeException("\"FATAL ERROR !!! Compile shader error: \n\n" + errorStr + "\n\n" +
                        shaderCode + "\n\n");
            }

        }
        return shader;
    }

    /**
     * deletes all OpenGL programs managed by this class (by this App unless programs outside this
     * class were added...).
     * @return true is there was anything to be destroyed
     */
    public boolean onDestroy(){

        //TODO: TBD if we need anymore this.


        //not needed. It is executed once the GL Context is deleted automatically.
        //otherwise it will generate
        // "E/libEGL: call to OpenGL ES API with no current context (logged once per thread)"
        //--> it was generated because was called outside the OPENGL context as there is no
        //destroy method within the respective context.

        boolean isContextDirty = false;

        for (OpenGLProgram program:this.iProgramMap.values()) {
            isContextDirty = !GLES30.glIsProgram(program.iProgramHandle);
            if(isContextDirty)
                break;
            //GLES20.glDeleteProgram(program.iProgramHandle);
        }

        if(isContextDirty) {
            System.out.println("ALL PROGRAMS DISCARDED FROM Program Utils!!!");
            this.iProgramMap.clear();
        }

        return isContextDirty;
    }

}