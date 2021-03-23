/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game;

import android.opengl.Matrix;

import ro.sg.avioane.util.DebugUtils;
import ro.sg.avioane.util.MathGLUtils;

/**
 * This class is transforming the VIEW-PORT SPACE "your device's display" coordinated into the
 * game world space.
 * It is used to detect if the user has "touched" on a specific spirit/graphical object
 * inside the game's world.
 *
 * Hint:
 * you can use this class as tutorial to learn how to go from device space display coordinates
 * (i.e. 1200x800) into the world game coordinate (X,Y,Z).
 * Updated by: dumitrageorge@gmail.com
 */
public class TouchScreenProcessor {

    private final float[] iProjectionMatrix = new float[16];
    private final float[] iViewMatrix = new float[16];

    private int iScreenWidth = 0;
    private int iScreenHeight = 0;

    public float[] processScreenTouch(final float x, final float y,
                                   final int screenWidth,
                                   final int screenHeight,
                                   final float[] projectionMatrix,
                                   final float[] viewMatrix){

        this.iScreenHeight = screenHeight;
        this.iScreenWidth = screenWidth;

        //keep a buffer of the original arrays...TBD if it is needed
        // maybe send reference is better if they are not needed for re-usage.
        //TODO: to be decided once 'object collision' is implemented
        for (int i = 0; i < projectionMatrix.length; i++) {
            this.iProjectionMatrix[i] = projectionMatrix[i];
            this.iViewMatrix[i] = viewMatrix[i];
        }

        //STEP1: convert from the display coordinates (i.e. 1028, 75) into OpenGL coordinates [-1...1]
        final float[] normalizedXY = this.getNormalizedDisplayCoordinates(x,y);
        //System.out.println("nX=" + normalizedXY[0] + "  nY=" + normalizedXY[1]);

        //STEP2: build the "clip" 3D coordinates. We will create a point on the far end of
        // the frustum that is Z=-1. To specify that this is a point and not a vector we will put
        //the 4th dimension to 1 instead of 0. This helps in transformation (check on
        // Internet "operation with matrices vector vs vertex")
        final float[] clipMatrix = new float[] {normalizedXY[0], normalizedXY[1], -1.0f, 1.0f};
        DebugUtils.printVector(clipMatrix, "---clipMatrix---");

        //STEP3: convert all into the "EYE" or "CAMERA" space. This means that the resulting
        // coordinates from this step will found themselves  into a space where the ORIGIN IS CAMERA
        //position.
        //please note that this method will return a vector pointing into the screen (w=0)
        final float[] cameraEyeMatrix = this.getConvertClipToCameraCoordinates(clipMatrix);
        DebugUtils.printSymmetricalMatrix(cameraEyeMatrix, "---cameraEyeMatrix---");

        //STEP4: convert from the "EYE / CAMERA" coordinates into the "WORLD" coordinates.
        //now instead of having the camera position as reference we will have the world referenced
        //to the point on the screen
        final float[] vectorWorld = this.getConvertCameraToWorldCoordinates(cameraEyeMatrix);

        DebugUtils.printVector(vectorWorld, "\nvectorWorld vector ************");

        return vectorWorld;
    }

    /**
     * Convert from camera coordinates (where the camera is the reference) into the world
     * coordinates (where we have 0,0,0 as reference)
     * @param cameraEyeCoordinates
     * @return a 3D vector into the world coordinates (X,Y,Z).
     */
    private float[] getConvertCameraToWorldCoordinates(final float[] cameraEyeCoordinates){
        final float[] invertedViewMatrix = new float[this.iViewMatrix.length];
        if(!Matrix.invertM(invertedViewMatrix, 0, this.iProjectionMatrix, 0))
            throw new IllegalStateException("cannot invert the view matrix!");

        //DebugUtils.printSymmetricalMatrix(iProjectionMatrix, "---iProjectionMatrix---");
        DebugUtils.printSymmetricalMatrix(invertedViewMatrix, "---invertedViewMatrix---");
        DebugUtils.printVector(cameraEyeCoordinates, "---cameraEyeCoordinates---");

        final float[] resultBrut = MathGLUtils.matrixMultiplyWithVector(invertedViewMatrix, cameraEyeCoordinates);
        final float[] result = new float[] {resultBrut[0], resultBrut[1], resultBrut[2]};

        MathGLUtils.matrixNormalize(result);
        return result;
    }

    /**
     * Convert from the frustum to the camera coordinates
     * We do this by doing the following operation:
     *  <code>inv_of(ProjectionMatrix) x ClipVector4D </code>
     * @param clipMatrix
     * @return
     */
    private float[] getConvertClipToCameraCoordinates(final float[] clipMatrix){
        final float[] invertedProjectionMatrix = new float[this.iProjectionMatrix.length];
        if(!Matrix.invertM(invertedProjectionMatrix, 0, this.iProjectionMatrix, 0))
            throw new IllegalStateException("cannot invert the projection matrix!");


        final float[] result = MathGLUtils.matrixMultiplyWithVector(invertedProjectionMatrix, clipMatrix);
        //reset Z coordinate to point inside the screen by setting it to -1
        result[2] = -1.0f;
        //reset the matrix into a vector by setting w=0
        result[3] = 0.0f;

        return result;
    }

    /**
     * Convert from display coordinates to OpenGL frustum coordinates.
     * @param x
     * @param y
     * @return
     */
    private float[] getNormalizedDisplayCoordinates(final float x, final float y){
        //subtract -1 because our range is not from 0..screen width but in OpenGL is
        //from -1..1. So a -1 shift is needed
        final float nX = 2.0f * x / this.iScreenWidth - 1.0f;
        final float nY = 2.0f * y / this.iScreenHeight - 1.0f;

        //final float nX = x / this.iScreenWidth * 2.0f - 1.0f;
        //final float nY = 1.0f - y / this.iScreenHeight * 2.0f;

        // -nY because the bottom is not lower-left corned but in OpenGL is top-left
        return new float[] {nX, -nY};
    }
}
