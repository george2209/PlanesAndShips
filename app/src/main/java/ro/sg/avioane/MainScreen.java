package ro.sg.avioane;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;

import ro.sg.avioane.util.OpenGLUtils;

public class MainScreen extends AppCompatActivity {

    private MainGameSurface iGameSurface;

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
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(this.getSupportActionBar()).hide();

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (OpenGLUtils.isOpenGL2Supported(activityManager))
        {
            if(this.iGameSurface == null) {
                this.iGameSurface = new MainGameSurface(getApplication());
                setContentView(this.iGameSurface);
            }


            // Render the view only when there is a change in the drawing data


            //////////////////////////////////////////this.loadTextures();



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
        this.iGameSurface.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        this.iGameSurface.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.iGameSurface.onDestroy();
    }

    /*
    @Override
    protected void onStop(){
        super.onStop();
    }*/

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