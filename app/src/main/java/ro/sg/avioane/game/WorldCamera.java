package ro.sg.avioane.game;

import android.opengl.Matrix;

import ro.sg.avioane.geometry.XYZCoordinate;

public class WorldCamera {
    private final float[] iViewMatrix = new float[16];
    private XYZCoordinate iCameraPosition;
    private XYZCoordinate iViewCenterPosition;
    private XYZCoordinate iCameraUpPosition;

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
        Matrix.setLookAtM(this.iViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    /**
     *
     * @return the view matrix with the camera position set.
     */
    public float[] getViewMatrix(){
        return this.iViewMatrix;
    }
}
