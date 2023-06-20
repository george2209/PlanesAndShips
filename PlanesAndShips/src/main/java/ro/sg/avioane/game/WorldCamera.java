/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game;

import ro.gdi.canvas.AbstractWorldCamera;
import ro.gdi.geometry.XYZCoordinate;


public class WorldCamera extends AbstractWorldCamera {

    private int screenWidth;
    private int screenHeight;

    public WorldCamera() {
        super(
                new XYZCoordinate(0.0f,80.0f,70.0f), //cameraPosition
                new XYZCoordinate(0.0f,0.0f,0.0f), //lookAtPosition
                new XYZCoordinate(0.0f,1.0f,0.0f) //cameraUpPosition
        );
    }

    /**
     * This method must be called whenever the display resolution of the device was changed
     * I.E.: call it on "onSurfaceChanged" of the Renderer.
     * @param screenWidth device display width
     * @param screenHeight device display height
     */
    public void doRecalibration(final int screenWidth, final int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }



    /**
     * move the camera when the user touched the display as follows:
     * 1/2 half upper of the display will move the camera 10.0f upper
     * otherwise will move down.
     * the same with left / right
     * @param x
     * @param y
     */
    public void doMoveCamera(final float x, final float y){
        final float ratio = 0.1f;
        final XYZCoordinate cameraPosition = super.getCameraPosition();
        if(x < this.screenWidth / 4.0f){
            cameraPosition.setX(cameraPosition.x() - ratio);
            //this.iLookAtPosition.x -= ratio;
        } else if(x > this.screenWidth*3.0f / 4.0f){
            cameraPosition.setX(cameraPosition.x() + ratio);
            //this.iLookAtPosition.x += ratio;
        } else if(y < this.screenHeight / 3.0f){
            cameraPosition.setZ(cameraPosition.z() - ratio);
            //this.iLookAtPosition.z -= ratio;
        } else if(y > this.screenHeight*2.0f / 3.0f){
            cameraPosition.setZ(cameraPosition.z() + ratio);
            //this.iLookAtPosition.z += ratio;
        }
        super.setCameraPosition(cameraPosition);
    }





//    /**
//     * Sets the x,y,z of the vector on the far clip scene (starting from center of view and going thru
//     * cameraUpPosition vertex).
//     * Imagine you looking from "cameraPosition" to the point "viewCenterPosition" having your camera
//     * rotated to have the upper side in the same direction with the "cameraUpPosition" vector.
//     * @param cameraUpPosition you can consider this vector as Normal vector to the camera.
//     */
//    public void setCameraUp(final XYZCoordinate cameraUpPosition){
//        this.iCameraUpPosition = cameraUpPosition;
//    }




}
