/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util.shaders;

import static ro.sg.avioane.util.OpenGLProgramFactory.*;

import ro.sg.avioane.BuildConfig;

public class MainShaderBuilder extends AbstractShaderBuilder{


    public MainShaderBuilder(){
        iStrShaderDeclarations.append("#version ").append(SHADER_VERSION).append(" es\n");
        iStrShaderDeclarations.append("uniform mat4 ").append(SHADER_VARIABLE_theModelMatrix).append(";\n");
        iStrShaderDeclarations.append("uniform mat4 ").append(SHADER_VARIABLE_theViewMatrix).append(";\n");
        iStrShaderDeclarations.append("uniform mat4 ").append(SHADER_VARIABLE_theProjectionMatrix).append(";\n");
        iStrShaderDeclarations.append("in vec3 ").append(SHADER_VARIABLE_aPosition).append(";\n");



        iStrShaderBody.append("\t\t gl_Position = ").append(SHADER_VARIABLE_theProjectionMatrix).append(" * ")
                .append(SHADER_VARIABLE_theViewMatrix).append(" * ")
                .append(SHADER_VARIABLE_theModelMatrix).append(" * vec4(")
                .append(SHADER_VARIABLE_aPosition).append(", 1.0);\n");


    }

    /**
     * @param shaderType
     */
    @Override
    public MainShaderBuilder build(final int shaderType) {
        if(BuildConfig.DEBUG){
            if(!isStreamEditable) {
                throw new AssertionError("isStreamEditable=" + (isStreamEditable));
            }
        }
        final boolean isNormalPerVertex = (shaderType & SHADER_VERTICES_WITH_NORMALS) > 0;
        final boolean isColorPerVertex = (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) > 0;
        //final boolean isColorGlobal = (shaderType & SHADER_VERTICES_WITH_GLOBAL_COLOR) > 0;

        if(isNormalPerVertex){
            iStrShaderDeclarations.append("uniform mat4 ").append(SHADER_VARIABLE_theModelTransInvMatrix).append(";\n");
            iStrShaderDeclarations.append("in vec3 ").append(SHADER_VARIABLE_aNormal).append(";\n");
            iStrShaderDeclarations.append("out vec3 ").append(SHADER_VARIABLE_FR_aVectorNormal).append(";\n");

            iStrShaderBody.append("\t\t ").append(SHADER_VARIABLE_FR_aVectorNormal).append(" = ").
                    append("mat3(").append(SHADER_VARIABLE_theModelTransInvMatrix).append(") * ")
                    .append(SHADER_VARIABLE_aNormal).append(";\n");
        }

        if(isColorPerVertex){
            //color per vertex
            iStrShaderDeclarations.append("in vec4 ").append(SHADER_VARIABLE_aVertexColor).append(";\n");
            iStrShaderDeclarations.append("out vec4 ").append(SHADER_VARIABLE_diffuseMaterialColor).append(";\n");
            iStrShaderBody.append("\t\t ").append(SHADER_VARIABLE_diffuseMaterialColor).append(" = ")
                    .append(SHADER_VARIABLE_aVertexColor).append(";\n");
        }
        //else if(isColorGlobal){ implementation moved inside Fragment Shader
        //} //else not needed. It is expected then to have textures (see static method "checkConditions")

        if((shaderType & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) != 0) {
            iStrShaderDeclarations.append("in vec2 ").append(SHADER_VARIABLE_aUVTexture).append(";\n");
            iStrShaderDeclarations.append("out vec2 ").append(SHADER_VARIABLE_FR_aUVTexture).append(";\n");
            iStrShaderBody.append("\t\t ").append(SHADER_VARIABLE_FR_aUVTexture).append(" = ")
                    .append(SHADER_VARIABLE_aUVTexture).append(";\n");
        }

        return this;
    }


}
