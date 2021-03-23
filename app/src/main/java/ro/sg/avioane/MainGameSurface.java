package ro.sg.avioane;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.health.SystemHealthManager;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;

public class MainGameSurface extends GLSurfaceView {

    private MainGameRenderer iGameRenderer;

    public MainGameSurface(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        super.setPreserveEGLContextOnPause(true);

        this.iGameRenderer = new MainGameRenderer();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //System.out.println("touch on x=" + event.getX() + " y=" + event.getY());
            //final float[] someMatrix = new float[16];
            //final float ratio = 2.0f;
            //Matrix.setIdentityM(someMatrix, 0);
            //Matrix.frustumM(someMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 0.1f, 10.0f);
//            for(int i=0; i<4; i++){
//                for(int j=0; j<4; j++){
//                    System.out.print(someMatrix[i*4 + j] + ",");
//                }
//                System.out.println("\n");
//            }


            this.iGameRenderer.onTouch(event);
            return true;
        }

        /*if (event.getAction() == MotionEvent.ACTION_UP) {
            System.out.println("x=" + event.getX() + " y=" + event.getY());
            this.iGameRenderer.getCamera().moveCameraUp(10.0f);
            return true;
        }*/

        return super.onTouchEvent(event);
    }


}
