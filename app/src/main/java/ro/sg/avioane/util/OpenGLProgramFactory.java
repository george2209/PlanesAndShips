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
import ro.sg.avioane.geometry.XYZVertex;

public class OpenGLProgramFactory {

    public static final int SHADER_UNDEFINED = 0;
    public static final int SHADER_ONLY_VERTICES = 1;
    public static final int SHADER_VERTICES_WITH_OWN_COLOR = SHADER_ONLY_VERTICES<<1;
    public static final int SHADER_VERTICES_WITH_TEXTURE = SHADER_VERTICES_WITH_OWN_COLOR <<1;
    //TODO: update documentation to make sure they are generated from Blender as follows:
    //http://www.opengl-tutorial.org/beginners-tutorials/tutorial-7-model-loading/
    public static final int SHADER_VERTICES_WITH_NORMALS = SHADER_VERTICES_WITH_TEXTURE <<1;

    private static final short SHADER_VERSION = 300;
    public static final String SHADER_VARIABLE_aPosition = "aPosition";
    public static final String SHADER_VARIABLE_aNormal = "aNormal";
    public static final String SHADER_VARIABLE_aColor = "aColor";
    public static final String SHADER_VARIABLE_aTexture = "aTexture";
    public static final String SHADER_VARIABLE_theModelMatrix = "theModelMatrix";
    public static final String SHADER_VARIABLE_theViewMatrix = "theViewMatrix";
    public static final String SHADER_VARIABLE_theProjectionMatrix = "theProjectionMatrix";
    public static final String SHADER_VARIABLE_ambientLightColor = "ambientLightColor";
    public static final String SHADER_VARIABLE_textureShader = "textureShader";


    private HashMap<Integer, OpenGLProgram> iProgramMap = new HashMap<Integer, OpenGLProgram>();

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
     * @param shaderType
     * @noOfTextureSamplers if there is no texture needed then it is ignored. However if the
     *      * shaderType is of type SHADER_VERTICES_WITH_TEXTURE then this is needed.
     * @return the existing to be reused or a new program
     */
    public OpenGLProgram getProgramForShader(final int shaderType, final int noOfTextureSamplers){
        if (BuildConfig.DEBUG &&
                (
                        (shaderType & SHADER_ONLY_VERTICES) + (shaderType & SHADER_VERTICES_WITH_NORMALS) +
                                (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) +
                                (shaderType & SHADER_VERTICES_WITH_TEXTURE)
                ) == 0) {
            throw new AssertionError("unknown shader type=" + shaderType);
        }

        OpenGLProgram program = this.iProgramMap.get(shaderType);
        if(program == null){
            program = new OpenGLProgram(getBuildShader(shaderType),
                    getBuildFragmentShader(shaderType, noOfTextureSamplers),
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
     * @param shaderType
     * @return
     */
    private String getBuildShader(final int shaderType){
        StringBuilder sb = new StringBuilder();
        sb.append("\n#version ").append(SHADER_VERSION).append(" es\n");
        sb.append("uniform mat4 ").append(SHADER_VARIABLE_theModelMatrix).append(";\n");
        sb.append("uniform mat4 ").append(SHADER_VARIABLE_theViewMatrix).append(";\n");
        sb.append("uniform mat4 ").append(SHADER_VARIABLE_theProjectionMatrix).append(";\n");
        sb.append("in vec3 ").append(SHADER_VARIABLE_aPosition).append(";\n");

        if( (shaderType & SHADER_VERTICES_WITH_NORMALS) != 0) {
            sb.append("in vec3 ").append(SHADER_VARIABLE_aNormal).append(";\n");
        }

        if( (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
            sb.append("in vec4 ").append(SHADER_VARIABLE_aColor).append(";\n"); //vertex color
            sb.append("out vec4 vColor;\n"); // to be passed into the fragment shader.
        }

        if((shaderType & SHADER_VERTICES_WITH_TEXTURE) != 0){
            sb.append("in vec3 ").append(SHADER_VARIABLE_aTexture).append(";\n");//vec2
            sb.append("out vec2 vTexture;\n");
            sb.append("out int iUseTextureID;\n");
        }

        ////shader main function////
        sb.append("void main() {\n");
        if( (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
            sb.append("  vColor = ").append(SHADER_VARIABLE_aColor).append(";\n");
        }

        if((shaderType & SHADER_VERTICES_WITH_TEXTURE) != 0){
            sb.append("  vTexture = vec2(").append(SHADER_VARIABLE_aTexture).append(".x, ")
                    .append(SHADER_VARIABLE_aTexture). append(".y);\n");
            sb.append("  iUseTextureID = int(").append(SHADER_VARIABLE_aTexture).append(".z);\n");
        }
        //Calculus: gl_Position = Projection * View * Model * position;
        sb.append("  gl_Position = ").append(SHADER_VARIABLE_theProjectionMatrix).append(" * ")
                .append(SHADER_VARIABLE_theViewMatrix).append(" * ")
                .append(SHADER_VARIABLE_theModelMatrix).append(" * vec4(")
                .append(SHADER_VARIABLE_aPosition).append(", 1.0);\n");

//        if (BuildConfig.DEBUG)
//            sb.append("  gl_PointSize = 10.0;");
        sb.append("}");

        if((shaderType & SHADER_VERTICES_WITH_TEXTURE) != 0) {
            System.out.println("PROGRAM SHADER=\n\n" + sb.toString() + "\n\n");
        }

        return sb.toString();
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
     * This will build a fragment for a shader having the X,Y,Z, R,G,B,A coordinates.
     *
     * @param shaderType one of the values or a bitwise mix of the:
     *                   - SHADER_ONLY_VERTICES
     *                   - SHADER_VERTICES_WITH_OWN_COLOR
     *                   - SHADER_VERTICES_WITH_TEXTURE
     * @noOfTextureSamplers if there is no texture needed then it is ignored. However if the
     * shaderType is of type SHADER_VERTICES_WITH_TEXTURE then this is needed.
     * @return
     */
    private String getBuildFragmentShader(final int shaderType, final int noOfTextureSamplers){
        StringBuilder sb = new StringBuilder();
        //set GPU to medium precision
        // (highp is not by all devices supported)
        //alternatively can be set to lowp for tests or low performance devices.
        //remove this in case of a Desktop App!!!
        sb.append("\n#version ").append(SHADER_VERSION).append(" es\n");
        //sb.append("precision mediump float;");
        if( (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
            sb.append("in vec4 vColor;\n");
            sb.append("uniform vec4 ").append(SHADER_VARIABLE_ambientLightColor).append(";\n");
        } else {
            if( (shaderType & SHADER_VERTICES_WITH_TEXTURE) == 0) {
                //add a global color only if there is no texture
                sb.append("uniform vec4 ").append(SHADER_VARIABLE_aColor).append(";\n");
            }
            sb.append("uniform vec4 ").append(SHADER_VARIABLE_ambientLightColor).append(";\n");
        }

        if((shaderType & SHADER_VERTICES_WITH_TEXTURE) != 0){
            sb.append("in vec2 vTexture;\n");
            sb.append("flat in int iUseTextureID;\n");
            for (int i = 0; i < noOfTextureSamplers; i++) {
                sb.append("uniform sampler2D ")
                        .append(SHADER_VARIABLE_textureShader)
                        .append("" + i)
                        .append(";\n");
            }
        }

        sb.append("out vec4 fragColor;\n");
        sb.append("void main() {\n");

        //vec3 objectColor = texture(uTexture, mobileTextureCoordinate).xyz;
        //vec3 phong = (ambient + diffuse) * objectColor + specular;
        //finalColor = vec4(phong, 1.0f); //Send lighting results to GPU

        if((shaderType & SHADER_VERTICES_WITH_TEXTURE) != 0){
            sb.append("fragColor = ").append(SHADER_VARIABLE_ambientLightColor).append(";\n");
            sb.append("if (iUseTextureID==0)\n");
            sb.append("   fragColor = fragColor * texture(").append(SHADER_VARIABLE_textureShader +
                    "0, vTexture);\n");
            for (int i = 1; i < TextureUtils.MAX_TEXTURES_SUPPORTED_PER_OBJ; i++) {
                sb.append("else if (iUseTextureID==" + i + ")\n");
                sb.append("   fragColor = fragColor * texture(").append(SHADER_VARIABLE_textureShader +
                        i + ", vTexture);\n");
            }

//            sb.append("fragColor = ").append(SHADER_VARIABLE_ambientLightColor)
//                    .append(" * ").append("texture(tmpTexID, vTexture);\n");

        } else {
            //color only
            if( (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
//                sb.append("  fragColor = vColor;");

                sb.append("  fragColor = ").append(SHADER_VARIABLE_ambientLightColor)
                        .append(" * vColor;\n");
            } else {
                sb.append("  fragColor = ").append(SHADER_VARIABLE_ambientLightColor).append(" * ")
                        .append(SHADER_VARIABLE_aColor).append(";\n");
            }
        }
        sb.append("}");

        if((shaderType & SHADER_VERTICES_WITH_TEXTURE) != 0) {
            System.out.println("FRAGMENT SHADER=\n\n" + sb.toString() + "\n\n");
        }

        return sb.toString();
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

        int shader = GLES20.glCreateShader(shaderType);
        if(shader == 0) {
            DebugUtils.checkPrintGLError();
            throw new RuntimeException("FATAL ERROR !!! \n\n shader=" + shader + " ERROR =>>>> " + GLES20.glGetError() + "\n\n" + "shader source=" + shaderCode + " \nisShader=" + GLES20.glIsShader(shader));
        } else {
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                final String errorStr = GLES20.glGetShaderInfoLog(shader);
                GLES20.glDeleteShader(shader);
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

        //not needed. It is executed once the GL Context is deleted automatically.
        //otherwise it will generate
        // "E/libEGL: call to OpenGL ES API with no current context (logged once per thread)"

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