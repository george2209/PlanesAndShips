/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game;

import android.opengl.GLU;
import android.opengl.Matrix;
import android.view.MotionEvent;

import java.util.List;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.util.DebugUtils;
import ro.sg.avioane.util.MathGLUtils;

/**
 * The class is processing the touch events and movements as well as making sure
 * that they are translated into the game world from the display coordinates.
 */
public class TouchScreenProcessor {

    private enum TOUCH_STATE_ENGINE{
        E_NONE,
        E_CLICK_DOWN,
        E_MOVING,
        E_CLICK_UP
    }

    private TOUCH_STATE_ENGINE iStateEngine = TOUCH_STATE_ENGINE.E_NONE;

    private int iScreenWidth = 0;
    private int iScreenHeight = 0;

    private float iStartX = 0.0f;
    private float iStartY = 0.0f;
    private float iEndX = 0.0f;
    private float iEndY = 0.0f;

    //if needed replace this implementation with a List for more listeners.
    //at this moment we only have one listener to we keep it for later
    //you may also want in this case to add a synchronization access.
    private TouchScreenListener iEventsListener = null;

    public void addTouchScreenListener(final TouchScreenListener l){
        if (BuildConfig.DEBUG && iEventsListener!=null){
            throw new AssertionError("listener non NULL. If you need more then an implementation change is suggested");
        }
        this.iEventsListener = l;
    }

    public void removeTouchScreenListener(final TouchScreenListener l){
        this.iEventsListener = null;
    }

    /**
     * This method must be called whenever the display resolution of the device was changed
     * I.E.: call it on "onSurfaceChanged" of the Renderer.
     * @param screenWidth device display width
     * @param screenHeight device display height
     */
    public void doRecalibration(final int screenWidth, final int screenHeight){
        this.iScreenWidth = screenWidth;
        this.iScreenHeight = screenHeight;
    }

    public void onTouch(MotionEvent e, final float[] viewMatrix, final float[] projectionMatrix){
        //System.out.println("MotionEvent=" + MotionEvent.actionToString(e.getActionMasked()));
        switch(e.getActionMasked()){
            case MotionEvent.ACTION_DOWN: {
                if (BuildConfig.DEBUG &&
                        (this.iStateEngine != TOUCH_STATE_ENGINE.E_NONE)) {
                    throw new AssertionError("inconsistent state engine detected =" + this.iStateEngine);
                }

                this.iStateEngine = TOUCH_STATE_ENGINE.E_CLICK_DOWN;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if(this.iStateEngine == TOUCH_STATE_ENGINE.E_CLICK_DOWN){
                    this.iStartX = e.getX();
                    this.iStartY = e.getY();
                } else if(this.iStateEngine == TOUCH_STATE_ENGINE.E_MOVING){
                    this.iEndX = e.getX();
                    this.iEndY = e.getY();
                } else {
                    throw new UnsupportedOperationException("unknown state engine:" + this.iStateEngine);
                }
                this.iStateEngine = TOUCH_STATE_ENGINE.E_MOVING;
                break;
            }
            case MotionEvent.ACTION_UP:{
                if(this.iStateEngine == TOUCH_STATE_ENGINE.E_MOVING){
                    float deltaX = this.iEndX - this.iStartX;
                    float deltaY = this.iEndY - this.iStartY;
                    if(this.iEventsListener!=null){
                        this.iEventsListener.fireMovement(deltaX/this.iScreenWidth * 100.0f, deltaY/this.iScreenHeight * 100.0f);
                    } else {
                        System.out.println("WARNING: null iEventsListener!");
                    }

//                    System.out.println("process movement of the camera at START:\n" +
//                            this.iStartX + ", " + this.iStartY +
//                            "\nEND:\n" +
//                            this.iEndX + ", " + this.iEndY);
//
//                    System.out.println("\n\n");
//                    System.out.println("deltaX=" + deltaX*100.0f/this.iScreenWidth + " deltaY=" + deltaY*100.0f/this.iScreenHeight);
//                    System.out.println("\n\n");

                } else if(this.iStateEngine == TOUCH_STATE_ENGINE.E_CLICK_DOWN){
                    System.out.println("process click");
                    final float x = e.getX();
                    final float y = (float)this.iScreenHeight - e.getY();
                    this.processClick(x,y, viewMatrix, projectionMatrix);
                } else {
                    throw new UnsupportedOperationException("unknown state engine:" + this.iStateEngine);
                }
                this.iStateEngine = TOUCH_STATE_ENGINE.E_NONE;
                break;
            }
            default:
                System.out.println("WARNING! un-catch mouse event:" + e.getActionMasked());
        }
    }


    /**
     * the method is processing a touch on the display that will translate the display device
     * coordinates that were touched (click) into a 3D world space coordinate.
     * They can be then used to check if there is an object that the user wants to select.
     * TODO: the notification mechanism.
     * @param x
     * @param y
     */
    private void processClick(final float x,  final float y, final float[] viewMatrix,
                              final float[] projectionMatrix){
        final int[] viewport = { 0, 0, this.iScreenWidth, this.iScreenHeight };

        final float[] resultNear = getUnProjectMatrix(x, y, 0.0f, viewport, viewMatrix, projectionMatrix);
        final float[] resultFar = getUnProjectMatrix(x, y, 1.0f, viewport, viewMatrix, projectionMatrix);
        final float[] result = MathGLUtils.matrixDifference(resultFar, resultNear);
        MathGLUtils.matrixNormalize(result);

        System.out.println("\nWORLD touched NEAR:\n objX=" +  resultNear[0] +
                "\nobjY=" + resultNear[1] +
                "\nobjZ=" + resultNear[2] +
                "\nobjW=" + resultNear[3]);

        System.out.println("\nWORLD touched VECTOR:\n objX=" +  result[0] +
                "\nobjY=" + result[1] +
                "\nobjZ=" + result[2] +
                "\nobjW=" + result[3]);

        //TODO: implement the right notification for this event.
    }


    /**
     * getUnProjectMatrix
     * @param x
     * @param y
     * @param winZ
     * @param viewport
     * @return the x,y coordinated from screen translated into world coordinates
     */
    private float[] getUnProjectMatrix(float x, float y, float winZ,
                                       int[] viewport,
                                       final float[] viewMatrix,
                                       final float[] projectionMatrix) {
        final float[] result = new float[4];

        GLU.gluUnProject(x, y, winZ,
                viewMatrix, 0,
                projectionMatrix,0,
                viewport, 0,
                result, 0);

        result[0] /= result[3];
        result[1] /= result[3];
        result[2] /= result[3];
        result[3] = 1.0f;

        return result;
    }

}
