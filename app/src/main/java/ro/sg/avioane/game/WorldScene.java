package ro.sg.avioane.game;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

import ro.sg.avioane.cavans.primitives.AbstractGameCavan;

public class WorldScene {
    //TODO: update this number once the game is ready to be released!!!!!
    private static final int MAX_NO_SUPPORTED_CAVANS = 100;

    private final float[] iModelMatrix = new float[16];
    private final float[] iProjectionMatrix = new float[16];

    private final WorldCamera iCamera;
    private final List<AbstractGameCavan> iGameEntities = new ArrayList<>(MAX_NO_SUPPORTED_CAVANS);

    public WorldScene(final WorldCamera camera){
        this.iCamera = camera;
    }

    /**
     * adds a new 3D object to this world.
     * Warning!
     * This method is not synchronized to you may want to do this operation during the start of the
     * game and not when the game is already started and running.
     * @param entity the 3D object that will be added to this world
     */
    public void add(final AbstractGameCavan entity){
        this.iGameEntities.add(entity);
    }


    /**
     * This method must be called whenever the display resolution of the device was changed
     * I.E.: call it on "onSurfaceChanged" of the Renderer.
     * @param screenWidth device display width
     * @param screenHeight device display height
     */
    public void doRecalibration(final int screenWidth, final int screenHeight){
        System.out.println("screenWidth=" + screenWidth + " screenHeight=" + screenHeight);
        this.iCamera.doRecalibration(screenWidth, screenHeight);
        final float ratio = (float) screenWidth / (float) screenHeight; //calculate the aspect ration on the far clip
        Matrix.frustumM(iProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 0.1f, 10.0f);
    }

    public void onDraw(){
        Matrix.setIdentityM(this.iModelMatrix,0);

        //now you can apply transforms to the model matrix before you send it to the
        //cavans
        //ex:
        // Do a complete rotation every 10 seconds.
        //long time = SystemClock.uptimeMillis() % 10000L;
        //float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        // Matrix.rotateM(iModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);

        //call this to have the view matrix build depending on the camera movement
        //we can choose to call this only if the camera was moved
        //TODO: draw only when camera was moved this!
        this.iCamera.onDraw();





        /*Matrix.multiplyMM(this.iProjectionMatrix,
                0,
                this.iModelMatrix,
                0,
                this.iCamera.getViewMatrix(),
                0);*/

        //draw all entities of the map on the projection clip
        for (final AbstractGameCavan entity: this.iGameEntities) {
            entity.draw(this.iCamera.getViewMatrix(), this.iModelMatrix, this.iProjectionMatrix);
        }
    }
}
