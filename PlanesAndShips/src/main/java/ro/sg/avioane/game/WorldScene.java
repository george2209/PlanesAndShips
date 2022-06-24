/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game;

import android.opengl.Matrix;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import ro.gdi.canvas.GameObject;

public class WorldScene {
    private static final float NEAR_CAMERA_FIELD = 1.0f;
    private static final float FAR_CAMERA_FIELD = 300.0f;
    private final float[] iProjectionMatrix = new float[16];
    private final WorldCamera iCamera;
    private final List<GameObject> iGameEntities = new LinkedList<GameObject>();

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
    public void add(@NonNull final GameObject entity){
        this.iGameEntities.add(entity);
    }

    /**
     * remove all referenced entities to this class
     */
//    public void clear(){
//        this.iGameEntities.clear();
//    }

    public void onRestoreWorld(){
        for(GameObject cavan: this.iGameEntities){
            cavan.destroy();
            cavan.onRestore();
        }
    }

    /**
     *
     * @return the number of objects contained by this world
     */
    public int count(){
        return this.iGameEntities.size();
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

        final float ratio = ((float) screenWidth) / ((float) screenHeight); //calculate the aspect ration on the far clip

        Matrix.setIdentityM(iProjectionMatrix, 0);
        Matrix.frustumM(iProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, NEAR_CAMERA_FIELD, FAR_CAMERA_FIELD);
    }

    /**
     * take care on rendering this world and all its objects and camera.
     * TODO: draw only when something was changed. Implement a "dirty" semaphore or notification
     */
    public void onDraw(){
        //call this to have the view matrix build depending on the camera movement
        //we can choose to call this only if the camera was moved
        //TODO: draw only when camera was moved this!
        this.iCamera.onDraw();

        //draw all entities of the map on the projection clip
        for (final GameObject entity: this.iGameEntities) {
            entity.draw(this.iCamera.getViewMatrix(), this.iProjectionMatrix);
        }
    }

    /**
     * process the touch events for this component.
     * The input is the display coordinates the the calculated world-based coordinates
     * and the touch event type (movement, click..) will be sent to the respective touch processor
     * listener in a separate call, however on "this" caller thread, meaning no separate thread for
     * processing & notification will be used.
     * If you want to do the computation on a separate thread then you must be aware that you must
     * send the resulting notification on the OpenGL thread (GUI).
     * This method is called usually from inside the extended class of
     * GLSurfaceView.onTouchEvent(..) method.
     * @param e the mouse event object
     * @param touchProcessor the processing event processor
     */
    public void onTouch(MotionEvent e, final TouchScreenProcessor touchProcessor){
        touchProcessor.onTouch(e, this.iCamera.getViewMatrix(), this.iProjectionMatrix);
    }

}
