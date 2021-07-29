/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;

public class OpenGLUtils {

    public static final byte INVALID_UNSIGNED_VALUE = -1;

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
