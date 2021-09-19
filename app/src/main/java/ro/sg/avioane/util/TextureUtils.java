/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLES30;
import android.opengl.GLU;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

import ro.sg.avioane.R;

public class TextureUtils {
    public static class TextureSharerInfo {
        public Bitmap bitmap = null;
        public Integer shaderHandler = new Integer(OpenGLUtils.INVALID_UNSIGNED_VALUE);
    }

    private static TextureUtils _instance = null;
    private static int TOTAL_TEXTURES = 0;


    /**
     * keep the matching {<texture ID>, <texture binary, shader handler>}
     * used with a life time between <code>loadTextures</code> and <code>releaseTextures</code>
     */
    private HashMap<Integer, TextureSharerInfo> iTextureData = new HashMap<Integer, TextureSharerInfo>();

    /**
     * keep the matching {<texture file name>, <texture ID>}
     */
    //private HashMap<String, Integer> iTextureNames = new HashMap<String, Integer>(); //needed this match?


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

    /**
     *
     * @param context
     * @param fileName
     * @return the texture ID was assigned or -1 in case of a failure.
     * @throws IOException
     */
    public int addTextureFromAssets(final Context context, final String fileName) throws IOException {
        final String fn = "obj/" + fileName;
        final InputStream inputStream = context.getAssets().open(fn);
        final int textureID = TOTAL_TEXTURES;
        final TextureSharerInfo textureSharerInfo = new TextureSharerInfo();
        textureSharerInfo.bitmap = BitmapFactory.decodeStream(inputStream);
        TOTAL_TEXTURES++;
        iTextureData.put(textureID, textureSharerInfo);
        //iTextureNames.put(fileName, textureID);
        inputStream.close();
        return textureID;
    }

    /**
     * Used to get a static handler to be used when setting inside the shader the respective texture.
     * @param textureID
     * @return a handler to the respective texture inside the OpenGL memory
     */
    public int getTextureDataBuffer(final int textureID){
        final TextureSharerInfo textureInfo = this.iTextureData.get(textureID);
        if(textureInfo == null){
            throw new AssertionError("texture not loaded " + textureID);
        }

        if(textureInfo.shaderHandler == OpenGLUtils.INVALID_UNSIGNED_VALUE){
            final int textures[] = new int[1];
            GLES30.glGenTextures(1, textures, 0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);

            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE); //or repeat?
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, textureInfo.bitmap, 0);
            //cleanup bitmap as it is not needed anymore
            {
                final Bitmap b = textureInfo.bitmap;
                textureInfo.bitmap = null;
                b.recycle();
            }
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0 ); //cleanup

            textureInfo.shaderHandler = textures[0];
        }

        return textureInfo.shaderHandler;
    }


    /**
     * TODO: a review is needed.
     * @return true if there was anything to be destroyed
     */
    public boolean onDestroy(){
        //not needed. It is executed once the GL Context is deleted automatically.
        //otherwise it will generate
        // "E/libEGL: call to OpenGL ES API with no current context (logged once per thread)"

        boolean isContextDirty = false;

        for (final TextureSharerInfo textureInfo: iTextureData.values()) {
            isContextDirty = !GLES30.glIsTexture(textureInfo.shaderHandler);
            if(isContextDirty)
                break;
//            final int buffers[] = new int[1];
//            buffers[0] = textureID;
//            GLES30.glDeleteTextures(1, buffers, 0); //are deleted by OS.
        }

        this.iTextureData.clear();

        if(isContextDirty) {
            System.out.println("ALL TEXTURES DISCARDED FROM Program Utils!!!");
            iTextureData.clear();
            //iTextureHandlers.clear();
        }

        return isContextDirty;
    }
}
