/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util.shaders;

import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_GLOBAL_COLOR;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_KD_CONSTANT;
import static ro.sg.avioane.util.OpenGLProgramFactory.SHADER_VERTICES_WITH_OWN_COLOR;

import androidx.annotation.CallSuper;


public abstract class AbstractShaderBuilder {
    protected final StringBuilder iStrShaderBody = new StringBuilder();
    protected final StringBuilder iStrShaderDeclarations = new StringBuilder();

    protected boolean isStreamEditable = true;

    public AbstractShaderBuilder(){
        iStrShaderBody.append("void main() {\n");
    }




    /**
     *
     * @return the complete shader code as String.
     */
    @CallSuper
    public String asString(){
        if(this.isStreamEditable) {
            this.isStreamEditable = false;
            iStrShaderBody.append("\t}\n");
        }
        return this.iStrShaderDeclarations.toString().concat(iStrShaderBody.toString());
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
    public abstract AbstractShaderBuilder withBackground(final int shaderType);
}
