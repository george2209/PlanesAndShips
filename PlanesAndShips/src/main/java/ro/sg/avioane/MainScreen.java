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
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import java.util.Objects;

import ro.gdi.canvas.GameObject;
import ro.gdi.canvas.blender.ColladaParser;
import ro.gdi.canvas.blender.collada.ColladaFileObjectDescriptor;
import ro.gdi.canvas.blender.collada.ColladaParserListener;
import ro.gdi.util.OpenGLProgramFactory;
import ro.gdi.util.OpenGLUtils;
import ro.gdi.util.TextureUtils;

public class MainScreen extends AppCompatActivity implements ColladaParserListener {

    private MainGameSurface iGameSurface;
    //private final AtomicBoolean iActivityAlive = new AtomicBoolean(true);
    private ColladaParser iColladaParser = null;

    //private static boolean isTexturesLoaded = false;

    public MainScreen() {
        super();
        if (BuildConfig.DEBUG)
            StrictMode.enableDefaults();
    }

    /***
     *
     * @param savedInstanceState the Bundle that may receive data from other Activity
     *                           see https://www.journaldev.com/15872/android-bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("onCreate");

        //Remove title bar
        removeWindowTitleBar();

        if(this.iGameSurface == null) {
            this.iGameSurface = new MainGameSurface(getBaseContext());
            setContentView(this.iGameSurface);
        } else {
            //TODO:
            if(BuildConfig.DEBUG)
                throw new AssertionError("missing code: failed to load the Blender objects. Important later when we handle the downloading of the objects from a server.)");
        }

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (OpenGLUtils.isOpenGL2Supported(activityManager))
        {
            this.setScreenProperties();
            this.loadGameActors();
        } else {
            //TODO: make a layout frame where you display the non-supported message.
            //TBD if this part is really needed as the App shall be not installed from the
            //Market on a non-compatible device. Needed?

            throw new UnsupportedOperationException("wrong GLES version!");
        }
    }

    /**
     * remove the title bar if possible.
     */
    private void removeWindowTitleBar() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }catch (NullPointerException npe){
            System.out.println("Warning: null ActionBar detected. App will run with ActionBar not " +
                    "hidden. Stacktrace:");
            npe.printStackTrace();
        }
    }

    /**
     * This method is handling the load of the Blender objects.
     * Here you load the game`s spirits :
     *          trees, houses, airplanes, etc..
     *          TODO: to be updated with more examples
     */
    private void loadGameActors()  {
        if(BuildConfig.DEBUG && this.iColladaParser != null){
            throw new AssertionError("thread alive exception!");
        }

        //as an example let`s load some exported Blender files. Use in Blender: Export->Collada (.dae)
        //and then generate the (.bin) files by using the "ColladaAssimpConverter" (easy to use!)
        final ColladaFileObjectDescriptor[] colladaFiles = new ColladaFileObjectDescriptor[1];
        colladaFiles[0] = new ColladaFileObjectDescriptor("floor_earth.bin","floor");

        this.iColladaParser = new ColladaParser(this.getApplicationContext());
        this.iColladaParser.addColladaParserListener(this);
        this.iColladaParser.startParsing(colladaFiles, this);
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
        if(this.iColladaParser != null ){
            this.iColladaParser.stopParsing();
        }
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



    /***
     * set the screen properties such us hiding the clock and battery and keep the display ON
     */
    private void setScreenProperties() {
        //keep screen ON.
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //remove the status and battery.
        final WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController != null) {
            insetsController.hide(WindowInsets.Type.statusBars());
        }
    }

    /**
     * From <code>ColladaParserListener</code>
     * @param gameObjects the parsed game object.
     */
    @Override
    public void notifyParseFinished(GameObject[] gameObjects) {
        this.iGameSurface.queueEvent(new Runnable() {
            @Override
            public void run() {
                iGameSurface.loadBlenderObjects(gameObjects);
                iColladaParser = null;
            }
        });
    }

    /**
     * From <code>ColladaParserListener</code>
     */
    @Override
    public void notifyParseFailed() {
        this.runOnUiThread(() -> {
            this.iColladaParser = null;
        });

        //TODO: decide what to show the user here in case of failure.
        throw new UnsupportedOperationException("FATAL ERROR!");
    }
}