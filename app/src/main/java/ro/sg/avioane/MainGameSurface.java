/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import ro.sg.avioane.cavans.AbstractGameCavan;
import ro.sg.avioane.cavans.blender.BlenderObjCavan;

public class MainGameSurface extends GLSurfaceView {

    private MainGameRenderer iGameRenderer;

    public MainGameSurface(Context context) {
        super(context);

        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(3);

        //not preserved anymore...save some GPU resources.
        super.setPreserveEGLContextOnPause(true);

        this.iGameRenderer = new MainGameRenderer(context);
        /*super.setEGLConfigChooser((egl, display) -> {
            // Ensure that we get a 16bit framebuffer.
            int[] attributes = new int[]{EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE};
            EGLConfig[] configs = new EGLConfig[1];
            int[] result = new int[1];
            egl.eglChooseConfig(display, attributes, configs, 1, result);
            return configs[0];
        });*/
        super.setRenderer(this.iGameRenderer);


        // Render the view only when there is a change in the drawing data
        // if you uncomment this line then the rendering will take place only when you call
        // super.requestRender();
        //super.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    /**
     * this method must be called from the GUI thread!
     * @param blenderOBJArray
     */
    public void loadBlenderObjects(@NonNull final BlenderObjCavan[] blenderOBJArray){
        for (BlenderObjCavan gameEntity:blenderOBJArray) {
            gameEntity.onRestore();
            this.iGameRenderer.addEntityInGame(gameEntity);
        }
    }

    /**
     *
     * @param gameEntity a non null instance of AbstractGameCavan that will be added into the "world"
     */
    public void loadNonBlenderObject(@NonNull final AbstractGameCavan gameEntity){
        this.iGameRenderer.addEntityInGame(gameEntity);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
         this.iGameRenderer.onTouch(event);
         return true;
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}
