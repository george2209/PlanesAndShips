/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game;

import android.opengl.GLU;
import android.view.MotionEvent;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.MathGLUtils;

/**
 * The class is processing the touch events and movements as well as making sure
 * that they are translated into the game world from the display coordinates.
 */
public class TouchScreenProcessor {

    //use this to speed up the movement over the map.
    public static final float TOUCH_MOVEMENT_ACCELERATION_FACTOR = 3.0f;

    private enum TOUCH_STATE_ENGINE{
        E_NONE,
        E_CLICK_DOWN,
        E_MOVING,
        E_ZOOMING,
        E_CLICK_UP
    }

    private TOUCH_STATE_ENGINE iStateEngine = TOUCH_STATE_ENGINE.E_NONE;

    private int iScreenWidth = 0;
    private int iScreenHeight = 0;

    private static final byte NO_OF_SUPPORTED_FINGER_POINTERS = 2;

    /**
     * the start point of the respective "mouse" (human finger on the display) pointer
     * in case on one finger there will be only index = 0
     * A max of 2 mouse pointers / fingers are at this moment supported
     */
    private final float[] iStartX = new float[NO_OF_SUPPORTED_FINGER_POINTERS];

    /**
     * the start point of the respective "mouse" (human finger on the display) pointer
     * in case on one finger there will be only index = 0
     * A max of 2 mouse pointers / fingers are at this moment supported
     */
    private final float[] iStartY = new float[NO_OF_SUPPORTED_FINGER_POINTERS];

    private float iInitialX = 0.0f;
    private float iInitialY = 0.0f;

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
                this.iInitialX = e.getX();
                this.iInitialY = e.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if(this.iStateEngine == TOUCH_STATE_ENGINE.E_CLICK_DOWN){
                    this.iStartX[0] = e.getX();
                    this.iStartY[0] = e.getY();
                    this.iStateEngine = TOUCH_STATE_ENGINE.E_MOVING;
                } else if(this.iStateEngine == TOUCH_STATE_ENGINE.E_ZOOMING){
                    ///////////////////////////////////////////System.out.println("zm x=" + e.getX() + " y=" + e.getY());
                    //TODO to be implemented




                } else if(this.iStateEngine == TOUCH_STATE_ENGINE.E_MOVING){
                    this.fireMovementEvent(e.getX(), e.getY());
                    this.iStartX[0] = e.getX();
                    this.iStartY[0] = e.getY();
                } else {
                    throw new UnsupportedOperationException("unknown state engine:" + this.iStateEngine);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN:{
                this.iStateEngine = TOUCH_STATE_ENGINE.E_ZOOMING;
                //System.out.println("ZOOMING START");
                for (int i = 0; i<e.getPointerCount() && i <NO_OF_SUPPORTED_FINGER_POINTERS; i++) {
//                    System.out.println("\tPointer " + i + " ID=" + e.getPointerId(i) +
//                            "\tx=" + e.getX(i) + " y=" + e.getY(i));
                    this.iStartX[i] = e.getX(i);
                    this.iStartY[i] = e.getY(i);
                }


            } break;
            case MotionEvent.ACTION_POINTER_UP: {
                //System.out.println("ZOOMING POINTER UP");
                if (BuildConfig.DEBUG &&
                        e.getPointerCount() < NO_OF_SUPPORTED_FINGER_POINTERS){
                    throw new AssertionError("low pointer number detected=" + e.getPointerCount());
                }

                XYZCoordinate p1 = new XYZCoordinate(this.iStartX[0], this.iStartY[0], 0);
                XYZCoordinate p2 = new XYZCoordinate( this.iStartX[1], this.iStartY[1], 0);

                final float distanceInitial = MathGLUtils.get3DPointsDistance(p1, p2);

                p1 = new XYZCoordinate(e.getX(0), e.getY(0), 0);
                p2 = new XYZCoordinate(e.getX(1), e.getY(1), 0);

                final float distanceEnd = MathGLUtils.get3DPointsDistance(p1, p2);

                if(distanceInitial < distanceEnd ){
                    //System.out.println("zoom in");
                    this.fireZoomEvent((distanceEnd-distanceInitial) / (float)(Math.sqrt(this.iScreenHeight*this.iScreenHeight + this.iScreenWidth*this.iScreenWidth)));
                } else {
                    //System.out.println("zoom out");
                    this.fireZoomEvent((distanceEnd-distanceInitial) / (float)(Math.sqrt(this.iScreenHeight*this.iScreenHeight + this.iScreenWidth*this.iScreenWidth)));
                }

//                for (int i = 0; i<e.getPointerCount() && i <NO_OF_SUPPORTED_FINGER_POINTERS; i++) {
//                    System.out.println("zooming delta for pointer " + i + "" +
//                            " is on x=" + (e.getX(i)-this.iStartX[i]) +
//                            " y=" + (e.getY(i) - this.iStartY[i]));
//                }


            } break;
            case MotionEvent.ACTION_UP:{
                switch (this.iStateEngine) {
                    case E_MOVING: {
                        //calculate the movement in percent to judge if it was a move or a click.
                        float xPercentage = Math.abs(e.getX() - this.iInitialX) / this.iScreenWidth;
                        float yPercentage = Math.abs(e.getY() - this.iInitialY) / this.iScreenHeight;

                        /*System.out.println("movement percentage is: \n" + "xPercentage=" + xPercentage +
                                "\nyPercentage=" + yPercentage +
                                "\ntotal:" + (2.0f - (xPercentage + yPercentage)));*/
                        if (xPercentage > 0.01f || yPercentage > 0.01f) {
                            //System.out.println("MOVEMENT");
                            this.fireMovementEvent(e.getX(), e.getY());
                            break;
                        } //else continue..don`t break it
                    }
                    case E_CLICK_DOWN: {
                        //System.out.println("process CLICK");
                        final float x = e.getX();
                        final float y = (float) this.iScreenHeight - e.getY();
                        this.processClick(x, y, viewMatrix, projectionMatrix);
                    } break;
                    case E_ZOOMING: {
                        //System.out.println("ZOOMING END");

//                        for (int i = 0; i<e.getPointerCount() && i <NO_OF_SUPPORTED_FINGER_POINTERS; i++) {
//                            System.out.println("zooming delta for pointer " + i + "" +
//                                    " is on x=" + (e.getX(i)-this.iStartX[i]) +
//                                    " y=" + (e.getY(i) - this.iStartY[i]));
//                        }



                    } break;
                    default:
                        throw new UnsupportedOperationException("unknown state engine:" + this.iStateEngine);
                }
                this.iStateEngine = TOUCH_STATE_ENGINE.E_NONE;
                break;
            }
            default:
                System.out.println("WARNING! un-catch mouse event:" + e.getActionMasked());
        }
    }

    private void fireZoomEvent(final float zoomPercent){
        if(this.iEventsListener!=null){
            this.iEventsListener.fireZoom(zoomPercent);
        } else {
            throw new AssertionError("WARNING: null iEventsListener!");
        }
    }

//    /**
//     * This method is start preparing the camera zooming.
//     * The zoom operation is composed (or better saying decomposed :) ) from two separate parts:
//     * 1. When the zoom is initiated the camera is set to point to that world coordinate on
//     * the map (very important to be on the map or on the object you are about to zoom into/from !).
//     * In the map case pointing on the map means basically a coordinate of y=0.0f as the map floor is
//     * referenced against that y coordinate.
//     * 2. During the zooming the camera will only move on delta Z, delta Y of the world coordinate system.
//     * This method is only taking part of the part 1. The part two is a series of calls as long as user is
//     * performing the zoom.
//     * @param x
//     * @param y
//     * @param viewMatrix
//     * @param projectionMatrix
//     */
//    private void initiateStartZooming(final float x,  final float y, final float[] viewMatrix,
//                                      final float[] projectionMatrix){
//        final float[] result = getVectorFromDisplayCoordinate(x, y, viewMatrix, projectionMatrix);
//        if(this.iEventsListener!=null){
//            this.iEventsListener.fireLookTowardsVector(result);
//        }
//    }

    /**
     * delegate the movement event to its class listeners (at this moment only one).
     * @param currentX current X axis value on the screen
     * @param currentY current Y axis value on the screen
     */
    private void fireMovementEvent(final float currentX, final float currentY) {
        final float deltaX = currentX - this.iStartX[0];
        final float deltaY = currentY - this.iStartY[0];

        if(this.iEventsListener!=null){
            this.iEventsListener.fireMovement(deltaX/this.iScreenWidth * 100.0f, deltaY/this.iScreenHeight * 100.0f);
        } else {
            throw new AssertionError("WARNING: null iEventsListener!");
        }
    }


    /**
     * the method is processing a touch on the display that will translate the display device
     * coordinates that were touched (click) into a 3D world space coordinate.
     * A vector with a certain direction will be then sent via the Events Listener
     * <code>fireTouchClick</code> method.
     * The vector is normalized so the magnitude is ignored and it will only contain..direction.
     * They can be then used to check if there is an object that the user wants to select.
     * @param x display coordinate
     * @param y display coordinate
     * @param viewMatrix the current view matrix
     * @param projectionMatrix the current projection matrix
     */
    private void processClick(final float x,  final float y, final float[] viewMatrix,
                              final float[] projectionMatrix){
        final float[] result = getVectorFromDisplayCoordinate(x, y, viewMatrix, projectionMatrix);

//        System.out.println("\nWORLD touched NEAR:\n objX=" +  resultNear[0] +
//                "\nobjY=" + resultNear[1] +
//                "\nobjZ=" + resultNear[2] +
//                "\nobjW=" + resultNear[3]);
//
//        System.out.println("\nWORLD touched VECTOR:\n objX=" +  result[0] +
//                "\nobjY=" + result[1] +
//                "\nobjZ=" + result[2] +
//                "\nobjW=" + result[3]);

        if(this.iEventsListener!=null){
            this.iEventsListener.fireTouchClick(result);
        }
    }

    private float[] getVectorFromDisplayCoordinate(float x, float y, float[] viewMatrix, float[] projectionMatrix) {
        final int[] viewport = { 0, 0, this.iScreenWidth, this.iScreenHeight };

        final float[] resultNear = getUnProjectMatrix(x, y, 0.0f, viewport, viewMatrix, projectionMatrix);
        final float[] resultFar = getUnProjectMatrix(x, y, 1.0f, viewport, viewMatrix, projectionMatrix);
        final float[] result = MathGLUtils.matrixDifference(resultFar, resultNear);
        MathGLUtils.matrixNormalize(result);
        return result;
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
