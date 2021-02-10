package ro.sg.avioane.game;

import android.opengl.Matrix;

import ro.sg.avioane.geometry.XYZCoordinate;

public class WorldCamera {
    private final float[] iViewMatrix = new float[16];
    private XYZCoordinate iCameraPosition = new XYZCoordinate(0,0,-5.0f);
    private XYZCoordinate iViewCenterPosition = new XYZCoordinate(0,0,0.0f);
    private XYZCoordinate iCameraUpPosition;
    private int screenWidth;
    private int screenHeight;

    /**
     * This method must be called whenever the display resolution of the device was changed
     * I.E.: call it on "onSurfaceChanged" of the Renderer.
     * @param screenWidth
     * @param screenHeight
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
        final float ratio = 1.0f;
        if(x < this.screenWidth / 4){
            this.iViewCenterPosition.x -= ratio;
        } else if(x > this.screenWidth*3.0f / 4.0f){
            this.iViewCenterPosition.x += ratio;
        } else if(y < this.screenHeight / 3.0f){
            this.iViewCenterPosition.y -= ratio;
        } else if(y > this.screenHeight*2.0f / 3.0f){
            this.iViewCenterPosition.y += ratio;
        }
    }

    /**
     * Sets the x,y,z of the camera vector. Consider the camera the point where "you" will look at the
     * scene.
     * @param cameraPosition
     */
    public void setCameraPosition(final XYZCoordinate cameraPosition){
        this.iCameraPosition = cameraPosition;
    }

    /**
     * Sets the x,y,z of the center vector where the camera is pointing to on the far clip (
     * world scene).
     * @param viewCenterPosition
     */
    public void setCenterOfViewPosition(final XYZCoordinate viewCenterPosition){
        this.iViewCenterPosition = viewCenterPosition;
    }

    /**
     * Sets the x,y,z of the vector on the far clip scene (starting from center of view and going thru
     * cameraUpPosition vertex).
     * Imagine you looking from "cameraPosition" to the point "viewCenterPosition" having your camera
     * rotated to have the upper side in the same direction with the "cameraUpPosition" vector.
     * @param cameraUpPosition
     */
    public void setCameraUp(final XYZCoordinate cameraUpPosition){
        this.iCameraUpPosition = cameraUpPosition;
    }

    /**
     * Sets the camera position (view-matrix)
     */
    public void onDraw(){
        System.out.println("x=" + this.iCameraPosition.x + " y=" + this.iCameraPosition.y + " z=" + this.iCameraPosition.z);
        Matrix.setLookAtM(this.iViewMatrix, 0, iCameraPosition.x, iCameraPosition.y, iCameraPosition.z,
                iViewCenterPosition.x, iViewCenterPosition.y, iViewCenterPosition.z,
                0f, 1.0f, 0.0f);
    }

    /**
     *
     * @return the view matrix with the camera position set.
     */
    public float[] getViewMatrix(){
        return this.iViewMatrix;
    }
}
