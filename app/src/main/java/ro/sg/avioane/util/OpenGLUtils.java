/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;

public class OpenGLUtils {


//    /**
//     * delete an existing buffer from the memory.
//     * @param bufferID VBO
//     * @param arrayID VAO
//     */
//    public static void deleteBuffer(final int bufferID, final int arrayID){
//        if(bufferID != -1){
//            final int buffers[] = new int[1];
//            buffers[0] = bufferID;
//            GLES30.glDeleteBuffers(1, buffers, 0);
//        }
//        if(arrayID != -1){
//            final int buffers[] = new int[1];
//            buffers[0] = arrayID;
//            GLES30.glDeleteVertexArrays(1, buffers,0);
//        }
//    }

//    /**
//     *
//     * @param bufferTarget GLES20.GL_ELEMENT_ARRAY_BUFFER or GLES20.GL_ARRAY_BUFFER
//     * @param usage for the moment GLES20.GL_STATIC_DRAW is used
//     * @param buffer a buffer with the data loaded already.
//     * @return an array of size two with the following values:
//     *  - index 0 = VAO
//     *  - index 1 = VBO
//     *
//     */
//    public static int[] createBuffer(final int bufferTarget, final int size, final java.nio.Buffer buffer,
//                                   final int usage, int programPointer, int noCoordinatesPerVertex,
//                                     int dataType, boolean isNormalized, int shaderStride,
//                                     int offset){
//        int VAO = -1;
//        int VBO = -1;
//        //create VertexArrayObject
//        final int buffers[] = new int[1];
//        GLES30.glGenVertexArrays(1, buffers, 0);
//        VAO = buffers[0];
//        //create VertexBufferObject
//        GLES30.glGenBuffers(1, buffers, 0);
//        VBO = buffers[0];
//
//        GLES30.glBindVertexArray(VAO);
//        GLES30.glBindBuffer(bufferTarget, VBO);
//        GLES30.glBufferData(bufferTarget, size, buffer, usage);
//        GLES30.glVertexAttribPointer(programPointer, noCoordinatesPerVertex,
//                dataType, isNormalized,
//                shaderStride, offset);
//
//        GLES30.glEnableVertexAttribArray(0);
//        GLES30.glBindBuffer(bufferTarget, 0);
//        GLES30.glBindVertexArray(0);
//
//        return new int[]{VAO, VBO};
//    }

    /***
     * check if the user`s device supports at least the OpenGL V2
     * @param activityManager use the one from the main Activity
     * @return true if supported.
     */
    public static boolean isOpenGL2Supported(final ActivityManager activityManager){
        // Check if the system supports OpenGL ES 3.0.
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        System.out.println("configurationInfo.reqGlEsVersion=" + configurationInfo.reqGlEsVersion);
        return configurationInfo.reqGlEsVersion >= 0x00030000;
    }
}
