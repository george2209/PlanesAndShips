/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.util.HashMap;

import ro.sg.avioane.R;

public class TextureUtils {
    private static TextureUtils _instance = null;

    private HashMap<String, Integer> iTextureHandlers = new HashMap<String, Integer>();


    private TextureUtils(){

    }

    public static TextureUtils getInstance(){
        if(_instance == null) {
            synchronized(TextureUtils.class) {
                if (_instance == null)
                    _instance = new TextureUtils();
            }
        }
        return _instance;
    }

    public static void killInstance(){
        if(_instance != null){
            synchronized (TextureUtils.class){
                _instance = null;
            }
        }
    }

    public int getTextureWithName(final String textureName, final Bitmap textureData){
        Integer textureHandler = this.iTextureHandlers.get(textureName);
        if(textureHandler == null){
            final int textures[] = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE); //or repeat?
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureData, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0 ); //cleanup

            textureHandler = textures[0];
            this.iTextureHandlers.put(textureName, textureHandler);
        }

        return textureHandler.intValue();

    }


    /**
     *
     * @return true if there was anything to be destroyed
     */
    public boolean onDestroy(){
        //not needed. It is executed once the GL Context is deleted automatically.
        //otherwise it will generate
        // "E/libEGL: call to OpenGL ES API with no current context (logged once per thread)"

        boolean isContextDirty = false;

        for (Integer textureID: iTextureHandlers.values()) {
            isContextDirty = !GLES30.glIsTexture(textureID);
            if(isContextDirty)
                break;


//            final int buffers[] = new int[1];
//            buffers[0] = textureID;
//            GLES20.glDeleteTextures(1, buffers, 0); //are deleted by OS.
        }

        if(isContextDirty) {
            System.out.println("ALL TEXTURES DISCARDED FROM Program Utils!!!");
            iTextureHandlers.clear();
        }

        return isContextDirty;
    }


    public static Bitmap loadTextureData(final Context context, final String textureName){
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.me);
        if(bm == null) {
            throw new AssertionError("null bitmap detected");
        }

        return bm;
    }
}
