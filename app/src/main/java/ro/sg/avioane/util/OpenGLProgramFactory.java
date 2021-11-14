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

public class OpenGLProgramFactory {

    public static final int SHADER_UNDEFINED = 0;
    public static final int SHADER_ONLY_VERTICES = 1;
    public static final int SHADER_VERTICES_WITH_OWN_COLOR = SHADER_ONLY_VERTICES<<1;
    public static final int SHADER_VERTICES_WITH_GLOBAL_COLOR = SHADER_VERTICES_WITH_OWN_COLOR<<1;
    public static final int SHADER_VERTICES_WITH_UV_TEXTURE = SHADER_VERTICES_WITH_GLOBAL_COLOR <<1; ///needed???///////////////////
    public static final int SHADER_VERTICES_WITH_NORMALS = SHADER_VERTICES_WITH_UV_TEXTURE <<1;
    //illumination/material constants
    public static final int SHADER_VERTICES_WITH_KA_CONSTANT = SHADER_VERTICES_WITH_NORMALS <<1;
    public static final int SHADER_VERTICES_WITH_KD_CONSTANT = SHADER_VERTICES_WITH_KA_CONSTANT <<1;
    public static final int SHADER_VERTICES_WITH_KS_CONSTANT = SHADER_VERTICES_WITH_KD_CONSTANT <<1;
    public static final int SHADER_VERTICES_WITH_KE_CONSTANT = SHADER_VERTICES_WITH_KS_CONSTANT <<1;
    //texture materials
    public static final int SHADER_VERTICES_WITH_KA_TEXTURE = SHADER_VERTICES_WITH_KE_CONSTANT <<1;
    public static final int SHADER_VERTICES_WITH_KD_TEXTURE = SHADER_VERTICES_WITH_KA_TEXTURE <<1;


    public static final short SHADER_VERSION = 300;
    public static final String SHADER_VARIABLE_aPosition = "aPosition";
    public static final String SHADER_VARIABLE_aNormal = "aNormal";
    public static final String SHADER_VARIABLE_aVertexColor = "aVertexColor";
    public static final String SHADER_VARIABLE_aGlobalColor = "aGlobalColor";
    //public static final String SHADER_VARIABLE_aColor = "aColor"; //replaced by SHADER_VARIABLE_diffuseColor
    public static final String SHADER_VARIABLE_aUVTexture = "aUvTexture";
    public static final String SHADER_VARIABLE_theModelMatrix = "theModelMatrix";
    public static final String SHADER_VARIABLE_theViewMatrix = "theViewMatrix";
    public static final String SHADER_VARIABLE_theProjectionMatrix = "theProjectionMatrix";
    //lights
    public static final String SHADER_VARIABLE_ambientLightColor = "ambientColor";
    //materials
    public static final String SHADER_VARIABLE_ambientKaConstant = "kaConstant"; //apply to ambient light
    public static final String SHADER_VARIABLE_ambientKaTexture = "kaTexture";
    public static final String SHADER_VARIABLE_diffuseColor = "diffuseColor";
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
                        (shaderType & SHADER_ONLY_VERTICES) + (shaderType & SHADER_VERTICES_WITH_NORMALS) +
                                (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) +
                                (shaderType & SHADER_VERTICES_WITH_UV_TEXTURE)
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
            sb.append("in vec4 ").append(SHADER_VARIABLE_diffuseColor).append(";\n"); //vertex color
            sb.append("out vec4 ").append(SHADER_VARIABLE_aVertexColor).append(";\n"); // to be passed into the fragment shader.
        }

        if((shaderType & SHADER_VERTICES_WITH_UV_TEXTURE) != 0){
            sb.append("in vec2 ").append(SHADER_VARIABLE_aUVTexture).append(";\n");
            sb.append("out vec2 uvTexture;\n");
        }

        ////shader main function////
        sb.append("void main() {\n");
        if( (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
            sb.append(SHADER_VARIABLE_aVertexColor).append(" = ").append(SHADER_VARIABLE_diffuseColor).append(";\n");
        }

        if((shaderType & SHADER_VERTICES_WITH_UV_TEXTURE) != 0){
            sb.append("  uvTexture = ").append(SHADER_VARIABLE_aUVTexture).append(";\n");
            //sb.append("  iUseTextureID = int(").append(SHADER_VARIABLE_aTexture).append(".z);\n");
        }
        //Calculus: gl_Position = Projection * View * Model * position;
        sb.append("  gl_Position = ").append(SHADER_VARIABLE_theProjectionMatrix).append(" * ")
                .append(SHADER_VARIABLE_theViewMatrix).append(" * ")
                .append(SHADER_VARIABLE_theModelMatrix).append(" * vec4(")
                .append(SHADER_VARIABLE_aPosition).append(", 1.0);\n");

//        if (BuildConfig.DEBUG)
//            sb.append("  gl_PointSize = 10.0;");
        sb.append("}");


        //System.out.println("PROGRAM SHADER=\n\n" + sb.toString() + "\n\n");


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
        StringBuilder sbDeclaration = new StringBuilder();
        StringBuilder sbBody = new StringBuilder();

        sbDeclaration.append("\n#version ").append(SHADER_VERSION).append(" es\n");
        sbDeclaration.append("uniform vec4 ").append(SHADER_VARIABLE_ambientLightColor).append(";\n");
        sbDeclaration.append("out vec4 fragColor;\n");

        sbBody.append("void main() {\n");
        sbBody.append("  fragColor = ").append(SHADER_VARIABLE_ambientLightColor).append(";\n");

        //set GPU to medium precision
        // (highp is not by all devices supported)
        //alternatively can be set to lowp for tests or low performance devices.
        //remove this in case of a Desktop App!!!
        //sb.append("precision mediump float;");

        if((shaderType & SHADER_VERTICES_WITH_UV_TEXTURE) != 0){
            //texture instead of color

            //KA texture: Issue #7 implementation
            //implementation of the Issue #7
            sbDeclaration.append("in vec2 uvTexture;\n");
            if((shaderType & SHADER_VERTICES_WITH_KA_CONSTANT) != 0) {
                sbDeclaration.append("uniform vec3 ").append(SHADER_VARIABLE_ambientKaConstant).append(";\n");
                sbBody.append("  fragColor = vec4(").append(SHADER_VARIABLE_ambientKaConstant).append(", 1.0) * fragColor").append(";\n");
                if((shaderType & SHADER_VERTICES_WITH_KA_TEXTURE) != 0) {
                    sbDeclaration.append("uniform sampler2D ").append(SHADER_VARIABLE_ambientKaTexture).append(";\n");
                    //Issue #7 test item 2:
                    sbBody.append("fragColor = fragColor * texture(")
                            .append(SHADER_VARIABLE_ambientKaTexture)
                            .append(", uvTexture);\n");
                    System.out.println("TO be tested 7.2");

                } else if((shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
                    sbDeclaration.append("in vec4 vColor;\n");
                    //Issue #7 test item 1: Ka constant with own color per vertex
                    sbBody.append("fragColor = fragColor * vColor").append(";\n");
                    System.out.println("TO be tested 7.1 uv vColor");
                } else {
                    sbDeclaration.append("uniform vec4 ").append(SHADER_VARIABLE_diffuseColor).append(";\n");
                    //Issue #7 test item 1: Ka constant with global color
                    // TESTED OK
                    sbBody.append("  fragColor = fragColor * ").append(SHADER_VARIABLE_diffuseColor).append(";\n");
                }
            } else if(BuildConfig.DEBUG) {
                // will be ignored however
                //this will be the Issue #7 test item 3: no Ka nor mapKa. To be tested
                System.out.println("TO be tested 7.3");
            }
        } else {
            //color only
            if( (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
                sbDeclaration.append("in vec4 vColor;\n");
                sbBody.append("  fragColor = fragColor * vColor;\n");
            } else {
                sbDeclaration.append("uniform vec4 ").append(SHADER_VARIABLE_diffuseColor).append(";\n");
                sbBody.append("  fragColor = fragColor * ").append(SHADER_VARIABLE_diffuseColor).append(";\n");
            }

            if((shaderType & SHADER_VERTICES_WITH_KA_CONSTANT) != 0) {
                sbDeclaration.append("uniform vec3 ").append(SHADER_VARIABLE_ambientKaConstant).append(";\n");
                //tested OK Issue #7 with color per vertex / global color
                sbBody.append("  fragColor = vec4(").append(SHADER_VARIABLE_ambientKaConstant).append(", 1.0) * fragColor").append(";\n");
            } //else {
            //Issue #7 test item 3: only color is defined (probably no material)
            //tested 7.3 OK
            //}
        }







        //vec3 objectColor = texture(uTexture, mobileTextureCoordinate).xyz;
        //vec3 phong = (ambient + diffuse) * objectColor + specular;
        //finalColor = vec4(phong, 1.0f); //Send lighting results to GPU

        sbBody.append("}");
        final String fragmentShaderStr = sbDeclaration.toString().concat(sbBody.toString());

        System.out.println("FRAGMENT SHADER=\n\n" + fragmentShaderStr + "\n\n");


        return fragmentShaderStr;
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