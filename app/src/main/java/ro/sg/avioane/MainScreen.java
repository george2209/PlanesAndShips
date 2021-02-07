package ro.sg.avioane;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

import java.util.Objects;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class MainScreen extends AppCompatActivity {

    private GLSurfaceView mGLView;
    private GameRenderer2 iGameRenderer;
    private static boolean isTexturesLoaded = false;

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

        mGLView = new GLSurfaceView(getApplication());
        mGLView.setEGLConfigChooser((egl, display) -> {
            // Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
            // back to Pixelflinger on some device (read: Samsung I7500)
            int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
            EGLConfig[] configs = new EGLConfig[1];
            int[] result = new int[1];
            egl.eglChooseConfig(display, attributes, configs, 1, result);
            return configs[0];
        });
        
        this.loadTextures();

        iGameRenderer = new GameRenderer2(this.getApplicationContext());
        mGLView.setRenderer(iGameRenderer);
        setContentView(mGLView);

    }

    private void loadTextures(){
        if(!MainScreen.isTexturesLoaded){
            MainScreen.isTexturesLoaded = true;
            //create texture
            final Context myAppContext = this.getApplicationContext();
            TextureManager.getInstance().addTexture("bump",
                    new Texture(myAppContext.getDrawable(R.drawable.plane_bump)));
            TextureManager.getInstance().addTexture("diff",
                    new Texture(myAppContext.getDrawable(R.drawable.plane_diff)));
        }
    }
}