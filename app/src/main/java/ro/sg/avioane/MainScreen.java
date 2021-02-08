package ro.sg.avioane;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.WindowManager;

import java.util.Objects;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;

import ro.sg.avioane.util.OpenGLUtils;

public class MainScreen extends AppCompatActivity {

    private GLSurfaceView mGLView;
    private MainGameRenderer iGameRenderer;
    //private static boolean isTexturesLoaded = false;

    /***
     *
     * @param savedInstanceState the Bundle that may receive data from other Activity
     *                           see https://www.journaldev.com/15872/android-bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //remove the status and battery.
        //this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Remove title bar
        Objects.requireNonNull(this.getSupportActionBar()).hide();

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (OpenGLUtils.isOpenGL2Supported(activityManager))
        {
            if(mGLView == null) {
                mGLView = new GLSurfaceView(getApplication());
                // Request an OpenGL ES 2.0 compatible context.
                mGLView.setEGLContextClientVersion(2);
                mGLView.setEGLConfigChooser((egl, display) -> {
                    // Ensure that we get a 16bit framebuffer.
                    int[] attributes = new int[]{EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE};
                    EGLConfig[] configs = new EGLConfig[1];
                    int[] result = new int[1];
                    egl.eglChooseConfig(display, attributes, configs, 1, result);
                    return configs[0];
                });
            }

            if(iGameRenderer == null)
                iGameRenderer = new MainGameRenderer(this.getApplicationContext());
            //////////////////////////////////////////this.loadTextures();
            mGLView.setRenderer(iGameRenderer);
            setContentView(mGLView);

        } else {
            //TODO: make a layout frame where you display the non-supported message.
            //TBD if this part is really needed as the App shall be not installed from the
            //Market on a non-compatible device.
            //return;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        this.mGLView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        this.mGLView.onPause();
    }

    /*private void loadTextures(){
        if(!MainScreen.isTexturesLoaded){
            MainScreen.isTexturesLoaded = true;
            //create texture
            final Context myAppContext = this.getApplicationContext();
            TextureManager.getInstance().addTexture("bump",
                    new Texture(myAppContext.getDrawable(R.drawable.plane_bump)));
            TextureManager.getInstance().addTexture("diff",
                    new Texture(myAppContext.getDrawable(R.drawable.plane_diff)));
        }
    }*/
}