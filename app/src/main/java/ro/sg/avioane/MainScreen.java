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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import ro.sg.avioane.cavans.blender.BlenderObjCavan;
import ro.sg.avioane.cavans.blender.ObjParser;
import ro.sg.avioane.cavans.primitives.Triangle;
import ro.sg.avioane.cavans.primitives.XYZAxis;
import ro.sg.avioane.util.BackgroundTask;
import ro.sg.avioane.util.OpenGLProgramFactory;
import ro.sg.avioane.util.OpenGLUtils;
import ro.sg.avioane.util.TextureUtils;

public class MainScreen extends AppCompatActivity {

    private MainGameSurface iGameSurface;
    private final AtomicBoolean iActivityAlive = new AtomicBoolean(true);
    private BackgroundTask iOBJLoaderProcessor = null;

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
        this.iActivityAlive.set(true);

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
            try {
                loadCavans();
            } catch (IOException e) {
                e.printStackTrace();

            }
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
     * loads the game surface and render. Basically this is the entry point for the game as well as
     * all the OpenGL objects.
     * @param blenderOBJArr the array of the loaded OBJs from memory.
     */
    private void addGameObjects(final BlenderObjCavan[] blenderOBJArr){
        if(BuildConfig.DEBUG && this.iGameSurface == null)
            throw new AssertionError("null game surface");

        this.iGameSurface.queueEvent(new Runnable() {
            @Override
            public void run() {
                ///////////////////////////////////////////////////////////////////iGameSurface.loadBlenderObjects(blenderOBJArr);
                //load here non-Blender objects too
                iGameSurface.loadNonBlenderObject(new Triangle());

            }
        });
    }

    /**
     * this method is handling the load of the Blender objects
     * @throws IOException
     */
    private void loadCavans() throws IOException {
        if(BuildConfig.DEBUG && this.iOBJLoaderProcessor != null){
            throw new AssertionError("thread alive exception!");
        }

        this.iOBJLoaderProcessor = new BackgroundTask(this) {
            private final String arrObj[] = {"game_plane.bin"};
            private int index = 0;
            private BufferedInputStream inputStream = null;
            final ObjParser iParser = new ObjParser(getApplicationContext());

            @Override
            public void preloadData() {
                try {
                    if(inputStream != null) {
                        inputStream.close();
                        inputStream = null;
                    }
                    inputStream = new BufferedInputStream(getApplicationContext().getAssets().open("obj/" + arrObj[index++]));
                } catch (IOException e) {
                    if(inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                    inputStream = null;
                    e.printStackTrace();
                }
            }

            @Override
            public boolean runInBackground() {
                if(iParser.processStream(this.inputStream)){
                    if(iParser.getStateEngine() == ObjParser.PARSE_DONE){
                        if(index < arrObj.length) {
                            preloadData();
                        } else {
                            //work done!
                            return false;
                        }
                    }
                    return true;
                } else {
                    this.stop();
                    return false;
                }
            }

            @Override
            public void notifyThreadFinished() {
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                if(!this.isInterrupted()) {
                    addGameObjects(iParser.getParsedObjects());
                }
                else {
                    if(BuildConfig.DEBUG)
                        throw new AssertionError("Interrupted thread detected!");
                    //else TODO: ...
                }
                iOBJLoaderProcessor = null;
            }
        };

        iOBJLoaderProcessor.start();
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
        if(this.iOBJLoaderProcessor != null &&
                this.iOBJLoaderProcessor.isRunning()) {
            this.iOBJLoaderProcessor.stop();
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
}