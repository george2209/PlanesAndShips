/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.opengl.GLES30;

import java.util.HashMap;

import ro.sg.avioane.BuildConfig;

public class OpenGLProgramUtils {

    public static final int SHADER_UNDEFINED = 0;
    public static final int SHADER_ONLY_VERTICES = 1;
    public static final int SHADER_VERTICES_WITH_OWN_COLOR = SHADER_ONLY_VERTICES<<1;
    public static final int SHADER_VERTICES_WITH_TEXTURE = SHADER_VERTICES_WITH_OWN_COLOR <<1;
    public static final int SHADER_VERTICES_WITH_NORMALS = SHADER_VERTICES_WITH_TEXTURE <<1;

    public static final String SHADER_VARIABLE_aPosition = "aPosition";
    public static final String SHADER_VARIABLE_aColor = "aColor";
    public static final String SHADER_VARIABLE_aTexture = "aTexture";

    private HashMap<Integer, OpenGLProgram> iProgramMap = new HashMap<Integer, OpenGLProgram>();

    private static OpenGLProgramUtils _instance = null;

    private OpenGLProgramUtils(){

    }

    public static OpenGLProgramUtils getInstance(){
        if(_instance == null){
            synchronized (OpenGLProgramUtils.class){
                if(_instance == null){
                    _instance = new OpenGLProgramUtils();
                }
            }
        }
        return _instance;
    }

    public static void killInstance(){
        if(_instance != null){
            synchronized (OpenGLProgramUtils.class){
                _instance = null;
            }
        }
    }

    /**
     * This is the central point for building and retrieving a program for a specific shader type.
     * @param shaderType
     * @return the existing to be reused or a new program
     */
    public OpenGLProgram getProgramForShader(final int shaderType){
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
                    getBuildFragmentShader(shaderType),
                    shaderType);
            this.iProgramMap.put(shaderType, program);
        }
        return program;
    }


    /**
     * build the fragment shader based on the param shaderType.
     * Example:
     * <code>
     *     String fragment =
     *     OpenGLProgramUtils.getBuildFragmentShader(
     *     SHADER_ONLY_VERTICES | SHADER_VERTICES_WITH_OWN_COLOR
     *     );
     * </code>
     * This will build a fragment for a shader having the X,Y,Z, R,G,B,A coordinates.
     *
     * @param shaderType one of the values or a bitwise mix of the:
     *                   - SHADER_ONLY_VERTICES
     *                   - SHADER_VERTICES_WITH_OWN_COLOR
     *                   - SHADER_VERTICES_WITH_TEXTURE
     * @return
     */
    private String getBuildFragmentShader(final int shaderType){
        StringBuilder sb = new StringBuilder();
        //set GPU to medium precision
        // (highp is not by all devices supported)
        //alternatively can be set to lowp for tests or low performance devices.
        //remove this in case of a Desktop App!!!
        sb.append("precision mediump float;");
        if( (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
            sb.append("varying vec4 vColor;");
        } else {
            sb.append("uniform vec4 aColor;");//vColor
        }

        if((shaderType & SHADER_VERTICES_WITH_TEXTURE) != 0){
            sb.append("varying vec2 vTexture;");
            sb.append("uniform sampler2D vImageTexture;");
        }

        sb.append("void main() {");
        if((shaderType & SHADER_VERTICES_WITH_TEXTURE) != 0){
            //TODO: mix texture with color?
            sb.append("  gl_FragColor = texture2D(vImageTexture, vTexture);");
        } else {
            //color only
            if( (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
                sb.append("  gl_FragColor = vColor;");
            } else {
                sb.append("  gl_FragColor = aColor;");
            }
        }


        sb.append("}");

        return sb.toString();
    }

    /**
     * build the shader code based on the param shaderType.
     Example:
     * <code>
     *     String fragment =
     *     OpenGLProgramUtils.getBuildShader(
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
        ////shader variable definition////
        sb.append("uniform mat4 vpmMatrix;"); //projection matrix
        sb.append("attribute vec4 ").append(SHADER_VARIABLE_aPosition).append(";"); //vertex coordinates

        if( (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
            sb.append("attribute vec4 ").append(SHADER_VARIABLE_aColor).append(";"); //vertex color
            sb.append("varying vec4 vColor;"); // to be passed into the fragment shader.
        }

        if((shaderType & SHADER_VERTICES_WITH_TEXTURE) != 0){
            sb.append("attribute vec4 ").append(SHADER_VARIABLE_aTexture).append(";");
            sb.append("varying vec2 vTexture;");
        }

        ////shader main function////
        sb.append("void main() {");
        if( (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
            sb.append("  vColor = ").append(SHADER_VARIABLE_aColor).append(";");
        }

        if((shaderType & SHADER_VERTICES_WITH_TEXTURE) != 0){
            sb.append("  vTexture = vec2(").append(SHADER_VARIABLE_aTexture).append(".x, ")
                    .append(SHADER_VARIABLE_aTexture). append(".y);");
        }
        //gl_Position = vpmMatrix * aPosition;
        sb.append("  gl_Position = vpmMatrix * ").append(SHADER_VARIABLE_aPosition).append(";"); //set coordinates on the projection clip

//        if (BuildConfig.DEBUG)
//            sb.append("  gl_PointSize = 10.0;");
        sb.append("}");

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
                throw new RuntimeException("\"FATAL ERROR !!! Compile shader error: \\n\\n" + errorStr + "\n\n");
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
