/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util.shaders;

import static ro.sg.avioane.util.OpenGLProgramFactory.*;

public class FragmentShaderBuilder {

    private final StringBuilder iStrFragmentShaderBody = new StringBuilder();
    private final StringBuilder iStrFragmentShaderDeclarations = new StringBuilder();

    public FragmentShaderBuilder(){
        iStrFragmentShaderDeclarations.append("\n#version ").append(SHADER_VERSION).append(" es\n");
        iStrFragmentShaderDeclarations.append("uniform vec4 ").append(SHADER_VARIABLE_ambientLightColor).append(";\n");
        iStrFragmentShaderDeclarations.append("uniform vec3 ").append(SHADER_VARIABLE_ambientKaConstant).append(";\n");
        iStrFragmentShaderDeclarations.append("out vec4 fragColor;\n");
        iStrFragmentShaderBody.append("void main() {\n");
    }

    /**
     *
     * @return the complete shader code as String.
     */
    @Override
    public String toString(){
        iStrFragmentShaderBody.append("\t}\n");
        return this.iStrFragmentShaderDeclarations.toString().concat(iStrFragmentShaderBody.toString());
    }

    /**
     * Helper method that shall be used outside production. Normally in "production" stage this method
     * shall always return true thus shall make no sense to be used in production.
     * check if all conditions are meet based on the Project_Documentation.pptx
     * @param shaderType
     * @return
     */
    public static boolean checkConditions(final int shaderType){
        //check color or texture presence
        if( (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) == 0 &&
            (shaderType & SHADER_VERTICES_WITH_KD_CONSTANT) == 0 &&
            (shaderType & SHADER_VERTICES_WITH_GLOBAL_COLOR) == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param shaderType
     */
    public boolean buildColor(final int shaderType) {
        final boolean isColorPerVertex = (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) > 0;
        final boolean isColorGlobal = (shaderType & SHADER_VERTICES_WITH_GLOBAL_COLOR) > 0;
        if(isColorPerVertex){
            //color per vertex
            iStrFragmentShaderDeclarations.append("in vec4 ").append(SHADER_VARIABLE_aVertexColor);
            iStrFragmentShaderBody.append("\t\tfragColor = vec4( ").append(SHADER_VARIABLE_ambientKaConstant)
                .append(", 1) * ").append(SHADER_VARIABLE_ambientLightColor).append(";\n");
            iStrFragmentShaderBody.append("\t\tfragColor = fragColor * ").append(SHADER_VARIABLE_aVertexColor)
                .append(";");
        } else if(isColorGlobal){

        } //else not needed. It is expected then to have textures (see static method "checkConditions")

        return isColorPerVertex || isColorGlobal;
    }

    public static String declareAmbientMaterial(final int shaderType){
        if((shaderType & SHADER_VERTICES_WITH_KA_CONSTANT) != 0){
            return new String("uniform vec3 ").concat(SHADER_VARIABLE_ambientKaConstant)
                    .concat(";\n");
        } else {
            return "";
        }
    }

    public static String withAmbient(final int shaderType){
        if((shaderType & SHADER_VERTICES_WITH_KA_CONSTANT) != 0){
            return new String("fragColor = vec4(").concat(SHADER_VARIABLE_ambientKaConstant)
            .concat(", 1.0) * fragColor;");
        } else {
            return "";
        }
    }
}
