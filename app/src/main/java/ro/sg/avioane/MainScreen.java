/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

import ro.sg.avioane.util.OpenGLProgramFactory;
import ro.sg.avioane.util.OpenGLUtils;
import ro.sg.avioane.util.TextureUtils;

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

        System.out.println("onCreate");

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //remove the status and battery.
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }catch (NullPointerException npe){
            System.out.println("Warning: null ActionBar detected. App will run with ActionBar not " +
                    "hidden. Stacktrace:");
            npe.printStackTrace();
        }

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
            //Market on a non-compatible device. Needed?

            throw new UnsupportedOperationException("wrong GLES version!");
        }
    }

//    @Override
//    protected void onResume(){
//        System.out.println("onResume");
//        super.onResume();
//    }

//    @Override
//    protected void onPause(){
//        System.out.println("onPause");
//        super.onPause();
//    }

    @Override
    protected void onStart() {
        System.out.println("onStart");
        super.onStart();
        if(this.iGameSurface != null) {
            this.iGameSurface.onResume();
        }
    }


    @Override
    protected void onStop(){
        System.out.println("onStop");
        if(this.iGameSurface != null)
            this.iGameSurface.onPause();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.out.println("onDestroy!!!");
        OpenGLProgramFactory.killInstance();
        TextureUtils.killInstance();
        this.iGameSurface = null;
        super.onDestroy();
    }

    //    @Override
//    protected void onRestart() {
//        System.out.println("onRestart");
//        super.onRestart();
//    }

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