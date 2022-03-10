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
        iStrShaderDeclarations.append("in vec3 ").append(SHADER_VARIABLE_FR_aVectorNormal).append(";\n");
        iStrShaderDeclarations.append("out vec4 fragColor;\n");
        iStrShaderBody.append("\t\t vec4 ambientColor = vec4(0,0,0,0);\n");
        iStrShaderBody.append("\t\t vec4 diffuseColor = vec4(0,0,0,0);\n");
    }

    public FragmentShaderBuilder build(final int shaderType){
        final boolean isColorPerVertex = (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) > 0;
        final boolean isColorGlobal = (shaderType & SHADER_VERTICES_WITH_GLOBAL_COLOR) > 0;

        //build light
        this.addAmbientLight(shaderType).addDiffuseLight(shaderType);

        //apply light on the material color or texture
        if((shaderType & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) != 0) {
            iStrShaderDeclarations.append("in vec2 ")
                    .append(SHADER_VARIABLE_FR_aUVTexture).append(";\n");
        }

        //ambient texture material
        if((shaderType & SHADER_VERTICES_WITH_KA_TEXTURE) != 0) {
            iStrShaderDeclarations.append("uniform sampler2D ")
                    .append(SHADER_VARIABLE_ambientKaTexture)
                    .append(";\n");

            iStrShaderBody.append("\t\t ambientColor *= texture(")
                    .append(SHADER_VARIABLE_ambientKaTexture)
                    .append(", ")
                    .append(SHADER_VARIABLE_FR_aUVTexture)
                    .append(");\n");
                    //.append(").rgb;\n");
        } else {
            //ambient and diffuse color material
            if(isColorPerVertex){
                iStrShaderDeclarations.append("in vec4 ").append(SHADER_VARIABLE_diffuseMaterialColor).append(";\n");
            } else if(isColorGlobal){
                iStrShaderDeclarations.append("uniform vec4 ").append(SHADER_VARIABLE_diffuseMaterialColor).append(";\n");
            } else if(BuildConfig.DEBUG){
                throw new AssertionError("diffuse color is not defined neither global nor 'per vertex'");
            }

            iStrShaderBody.append("\t\t ambientColor *= ")
                    .append(SHADER_VARIABLE_diffuseMaterialColor)
                    .append(";\n");
            iStrShaderBody.append("\t\t diffuseColor *= ")
                    .append(SHADER_VARIABLE_diffuseMaterialColor)
                    .append(";\n");
        }

        iStrShaderBody.append("\t\tfragColor = ambientColor + diffuseColor;\n");

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
        iStrShaderDeclarations.append(
                "struct AmbientLight { \n").append(
                "   vec4 color;\n").append(
                "   float strength;\n").append(
                "};\n");
        iStrShaderDeclarations.append("uniform AmbientLight ambientLight;\n");

        //BODY
        if((shaderType & SHADER_VERTICES_WITH_KA_CONSTANT) != 0) {
            iStrShaderDeclarations.append("uniform vec3 ").append(SHADER_VARIABLE_ambientKaConstant).append(";\n");
            iStrShaderBody.append("\t\t ambientColor = vec4(")
                    .append(SHADER_VARIABLE_ambientKaConstant)
                    .append(",1);\n");

        } else {
            iStrShaderBody.append("\t\t ambientColor = vec4(1,1,1,1);\n");
        }

        iStrShaderBody.append("\t\t ambientColor *= ").append(
                "vec4( vec3(ambientLight.color.x, ambientLight.color.y, ambientLight.color.z) ")
                .append("* ambientLight.strength, ambientLight.color.w );\n"
        ); //leave alpha untouched

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


        iStrShaderBody.append("\t\t vec3 norm = normalize( ").
                append(SHADER_VARIABLE_FR_aVectorNormal).
                append(") ;\\n");

        // not needed as long as the direction is already calculated
        // and the Sun is far away so a calculus "per vertex" is not needed.
        // vec3 lightDir = normalize(light.position - FragPos);

        //add a minus sign so that the light goes from the object to the "viewer" of the scene
        iStrShaderBody.append("\t\t vec3 lightDir = normalize( -").
                append(SHADER_VARIABLE_diffuseLightDirection).
                append(") ;\\n");
        iStrShaderBody.append("\t\t  float diff = max(dot(norm, lightDir), 0.0);");

        if((shaderType & SHADER_VERTICES_WITH_KD_CONSTANT) != 0) {
            iStrShaderDeclarations.append("uniform vec3 ").append(SHADER_VARIABLE_diffuseKdConstant).append(";\n");
            iStrShaderBody.append("\t\t  diffuseColor = vec4(" ).append(SHADER_VARIABLE_diffuseKdConstant)
                    .append(", 1) * ").append(SHADER_VARIABLE_diffuseLightColor).append(" * diff")
                    .append(";\n ");
        } else {
            iStrShaderBody.append("\t\t  diffuseColor = ")
                    .append(SHADER_VARIABLE_diffuseLightColor).append(" * diff").append(";\n ");
        }

//        iStrShaderBody.append("\t\t  diffuseColor = vec4(" ).append(SHADER_VARIABLE_diffuseLightColor)
//                .append(" * diff * ")..inmultit cu culoarea difuse cred...sau texture..
//                .append(" );\n ");


        //vec3 lightDir = normalize(-diffuseLight.direction);
        //float diff = max(dot(norm, lightDir), 0.0);
        //vec3 diffuse = light.diffuse * diff * texture(material.diffuse, TexCoords).rgb;


        return this;
    }

    /**
     *
     * @param shaderType the shader type as it comes from AbstractGameCavan instance.
     * @return this instance.
     */
    @Override
    public FragmentShaderBuilder withBackground(final int shaderType){
        this.addAmbientLight(shaderType)
                .addDiffuseLight(shaderType);

        final boolean isColorPerVertex = (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) > 0;
        final boolean isColorGlobal = (shaderType & SHADER_VERTICES_WITH_GLOBAL_COLOR) > 0;

        if(!(isColorGlobal || isColorPerVertex))
            this.withMaterial(shaderType);
        else
            this.withColor(shaderType);

        return this;
    }

    /**
     * Here we build the fragment shader with the color calculation.
     * @param shaderType the shader type as it comes from AbstractGameCavan instance.
     * Hint:
     * During development you can use the method
     * <code>checkConditions(shaderType) == true</code>
     * to check if the shaderType makes sense.
     */
    private void withColor(final int shaderType) {
        if(BuildConfig.DEBUG){
            if(!super.isStreamEditable)
                throw new AssertionError("isStreamEditable=false");
        }

        final boolean isColorPerVertex = (shaderType & SHADER_VERTICES_WITH_OWN_COLOR) > 0;
        final boolean isColorGlobal = (shaderType & SHADER_VERTICES_WITH_GLOBAL_COLOR) > 0;



        //DIFFUSE COLOR IS A MUST
        if(isColorPerVertex){
            iStrShaderDeclarations.append("in vec4 ").append(SHADER_VARIABLE_diffuseMaterialColor).append(";\n");
        } else if(isColorGlobal){
            iStrShaderDeclarations.append("uniform vec4 ").append(SHADER_VARIABLE_diffuseMaterialColor).append(";\n");
        } else if(BuildConfig.DEBUG){
            throw new AssertionError("diffuse color is not defined neither global nor 'per vertex'");
        }

        //DIFFUSE CONSTANT
        if((shaderType & SHADER_VERTICES_WITH_KD_CONSTANT) != 0) {
            iStrShaderDeclarations.append("uniform vec3 ").append(SHADER_VARIABLE_diffuseKdConstant).append(";\n");
            iStrShaderBody.append("\t\tfragColor = fragColor * vec4( ")
                    .append(SHADER_VARIABLE_diffuseKdConstant).append(", 1) * ")
                    .append(SHADER_VARIABLE_diffuseMaterialColor)
                    .append(";\n");
        } else {
            iStrShaderBody.append("\t\tfragColor = fragColor * ").append(SHADER_VARIABLE_diffuseMaterialColor)
                    .append(";\n");
        }

        iStrShaderBody.append("\t\tfragColor = ambientColor + diffuseColor;\n");
    }

    /**
     * build the code ready for applying the ambient color over the material.
     * The Ka constant and Ka texture or Ka color of the material are here used.
     * @param shaderType the shader type as int
     */
    private void withMaterial(final int shaderType){
        if(BuildConfig.DEBUG){
            if(!super.isStreamEditable)
                throw new AssertionError("isStreamEditable=false");
        }

        //DIFFUSE CONSTANT
        if((shaderType & SHADER_VERTICES_WITH_KD_CONSTANT) != 0) {
            iStrShaderDeclarations.append("uniform vec3 ").append(SHADER_VARIABLE_diffuseKdConstant).append(";\n");
        }


        if( (shaderType & SHADER_VERTICES_WITH_UV_DATA_MATERIAL) != 0){
            //check the kind of material is available

            //ambient (Ka) texture
            if((shaderType & SHADER_VERTICES_WITH_KA_TEXTURE) != 0){

            }

        } else if(BuildConfig.DEBUG){
            throw new AssertionError("no U,V defined? This shall never happen!");
        }
    }

    //public boolean withTexture


}
