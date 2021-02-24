package ro.sg.avioane.util;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;

import ro.sg.avioane.BuildConfig;

public class OpenGLUtils {

    /**
     * Creates a Vertex or Fragment shader depending on the param "shaderType"
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
            throw new RuntimeException("FATAL ERROR !!! \n\n shader=" + shader + " ERROR =>>>> " + GLES20.glGetError() + "\n\n" + "shader source=" + shaderCode + " isShader=" + GLES20.glIsShader(shader));
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

    /***
     * check if the user`s device supports at least the OpenGL V2
     * @param activityManager use the one from the main Activity
     * @return true if supported.
     */
    public static boolean isOpenGL2Supported(final ActivityManager activityManager){
        // Check if the system supports OpenGL ES 2.0.
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }
}
