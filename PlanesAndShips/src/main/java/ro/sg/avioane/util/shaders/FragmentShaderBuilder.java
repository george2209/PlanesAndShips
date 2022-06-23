/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util.shaders;

import static ro.sg.avioane.util.OpenGLProgramFactory.*;

import ro.sg.avioane.BuildConfig;

public class FragmentShaderBuilder extends AbstractShaderBuilder{

    public FragmentShaderBuilder(){
        //super(shaderType);
        iStrShaderDeclarations.append("\n#version ").append(SHADER_VERSION).append(" es\n");
        iStrShaderDeclarations.append(
                "struct ColorStrength { \n").append(
                "   vec4 color;\n").append(
                "   float strength;\n").append(
                "};\n");
        iStrShaderDeclarations.append("out vec4 fragColor;\n");
        iStrShaderBody.append("\t\t vec4 ambientColor = vec4(0,0,0,0);\n");
        iStrShaderBody.append("\t\t vec4 diffuseColor = vec4(0,0,0,0);\n");
    }

    @Override
    public FragmentShaderBuilder build(final int shaderType){
        final boolean isColorPerVertex = (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) > 0;
        final boolean isColorGlobal = (shaderType & SHADER_VERTICES_WITH_GLOBAL_DIFFUSE_COLOR) > 0;
        final boolean isNormalPerVertex = (shaderType & SHADER_VERTICES_WITH_NORMALS) > 0;

        if(isNormalPerVertex){
            iStrShaderDeclarations.append("in vec3 ").append(SHADER_VARIABLE_FR_aVectorNormal).append(";\n");
        }

        //prepare this declaration for lightning support
        if((shaderType & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) != 0) {
            iStrShaderDeclarations.append("in vec2 ")
                    .append(SHADER_VARIABLE_FR_aUVTexture).append(";\n");
        }
        //DIFFUSE COLOR IS A MUST
        if(isColorPerVertex){
            iStrShaderDeclarations.append("in vec4 ").append(SHADER_VARIABLE_diffuseMaterialColor).append(";\n");
        } else if(isColorGlobal){
            iStrShaderDeclarations.append("uniform vec4 ").append(SHADER_VARIABLE_diffuseMaterialColor).append(";\n");
        } else if(BuildConfig.DEBUG && ((shaderType & SHADER_VERTICES_WITH_DIFFUSE_TEXTURE) == 0)) {
                throw new AssertionError("No diffuse color nor texture is defined.");
        }

        //START THE LIGHT
        if(isNormalPerVertex) {
            this.addAmbientLight(shaderType).addDiffuseLight(shaderType);
        }

        if(isNormalPerVertex) {
            iStrShaderBody.append("\t\tfragColor = ambientColor + diffuseColor;\n");
            //iStrShaderBody.append("\t\tfragColor = vec4(0.9, 0.9, 0.5, 1.0);\n");
            //iStrShaderBody.append("\t\tfragColor = ").append(SHADER_VARIABLE_diffuseMaterialColor).append(";\n");
        } else {
            iStrShaderBody.append("\t\tfragColor = ").append(SHADER_VARIABLE_diffuseMaterialColor).append(";\n");
        }

        return this;
    }

    /**
     * build the ambient light fragment shader code.
     * The code will adjust the ambient color with the light strength factor and build the final
     * ambient light ready to be applied on the object`s surface.
     * TODO: if needed we can also add DiffuseLight as well as SpecularLight similar method(s)
     * @param shaderType the shader type as it comes from AbstractGameCavan instance.
     */
    private FragmentShaderBuilder addAmbientLight(final int shaderType){
        if(BuildConfig.DEBUG){
            if(!super.isStreamEditable)
                throw new AssertionError("isStreamEditable=" + (super.isStreamEditable));
        }

        //DECLARATIONS
        iStrShaderDeclarations.append("uniform ColorStrength ambientLight;\n");

        //AMBIENT LIGHT CALCULUS
        iStrShaderBody.append("\t\t ambientColor = ").append(
                "vec4( vec3(ambientLight.color.x, ambientLight.color.y, ambientLight.color.z) ")
                .append("* ambientLight.strength, ambientLight.color.w );\n"
                ); //leave alpha untouched

        //DETECT MATERIAL TYPE AND CALCULATE THE BACKGROUND COLOR
        //using Phong method (inside the device space not world space).
        if((shaderType & SHADER_VERTICES_WITH_AMBIENT_TEXTURE) != 0){
            //texture is present so use it.
            iStrShaderDeclarations.append("uniform sampler2D ")
                    .append(SHADER_VARIABLE_ambientTexture)
                    .append(";\n");
            iStrShaderBody.append("\t\t ambientColor *= texture( ")
                    .append(SHADER_VARIABLE_ambientTexture)
                    .append(", ")
                    .append(SHADER_VARIABLE_FR_aUVTexture)
                    .append(").rgba;").append("\n");
        } else if((shaderType & SHADER_VERTICES_WITH_OWN_COLOR) != 0) {
            // use the diffuse parameter "per vertex" that is already declared as diffuse color
            iStrShaderBody.append("\t\t ambientColor *= ")
                    .append(SHADER_VARIABLE_diffuseMaterialColor)
                    .append(";\n");

        } else if((shaderType & SHADER_VERTICES_WITH_GLOBAL_DIFFUSE_COLOR) != 0) {
            // use the diffuse "global" parameter.
            iStrShaderBody.append("\t\t ambientColor *= ")
                    .append(SHADER_VARIABLE_diffuseMaterialColor)
                    .append(";\n");
        } else throw new AssertionError(
                "unknown color for calculating ambient light.");

        return this;
    }

    /**
     *
     * @param shaderType the shader type as it comes from AbstractGameCavan instance.
     */
    private FragmentShaderBuilder addDiffuseLight(final int shaderType) {
        if (BuildConfig.DEBUG) {
            if (!super.isStreamEditable)
                throw new AssertionError("isStreamEditable=" + (super.isStreamEditable));
        }

        //DECLARATIONS
        iStrShaderDeclarations.append(
                "struct DiffuseLight { \n").append(
                "   vec4 color;\n").append(
                "   vec3 direction;\n").append(
                "};\n");
        iStrShaderDeclarations.append("uniform DiffuseLight diffuseLight;\n");

        //CALCULUS
        iStrShaderBody.append("\t\t vec3 norm = normalize( ").
                append(SHADER_VARIABLE_FR_aVectorNormal).
                append(") ;\n");

        // not needed as long as the direction is already calculated
        // and the Sun is far away so a calculus "per vertex" is not needed.
        // vec3 lightDir = normalize(light.position - FragPos);

        //add a minus sign so that the light goes from the object to the "viewer" of the scene
        iStrShaderBody.append("\t\t vec3 lightDir = normalize( -").
                append(SHADER_VARIABLE_diffuseLightDirection).
                append(") ;\n");
        iStrShaderBody.append("\t\t  float diff = max(dot(lightDir, norm), 0.0)").append(";\n");
        iStrShaderBody.append("\t\t  diffuseColor = ")
                .append(SHADER_VARIABLE_diffuseLightColor).append(" * diff").append(";\n ");
        //vec3 lightDir = normalize(-diffuseLight.direction);
        //float diff = max(dot(norm, lightDir), 0.0);
        //vec3 diffuse = light.diffuse * diff * texture(material.diffuse, TexCoords).rgb;
        if((shaderType & SHADER_VERTICES_WITH_DIFFUSE_TEXTURE) != 0){
            //texture is present so use it.
            iStrShaderDeclarations.append("uniform sampler2D ")
                    .append(SHADER_VARIABLE_diffuseTexture)
                    .append(";\n");
            iStrShaderBody.append("\t\t diffuseColor *= texture( ")
                    .append(SHADER_VARIABLE_diffuseTexture)
                    .append(", ")
                    .append(SHADER_VARIABLE_FR_aUVTexture)
                    .append(").rgba;").append("\n");
        }

        return this;
    }
}
