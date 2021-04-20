package ro.sg.avioane;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.cavans.GameTerrain;
import ro.sg.avioane.cavans.primitives.Line;
import ro.sg.avioane.cavans.primitives.XYZAxis;
import ro.sg.avioane.game.TouchScreenListener;
import ro.sg.avioane.game.TouchScreenProcessor;
import ro.sg.avioane.game.WorldCamera;
import ro.sg.avioane.game.WorldScene;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.util.MathGLUtils;

public class MainGameRenderer implements GLSurfaceView.Renderer, TouchScreenListener {

    //put here some object for test:
    private GameTerrain iGamePlane = null;

    //private XYZPoint iPoint = null;
    private XYZAxis iWorldAxis = null;
    private Line iMovingLine = null;

    private Bundle iPersistenceObject = null; //will be used later for persistence
    private final WorldCamera iCamera = new WorldCamera();
    private final WorldScene iWorld;
    private int iScreenWidth = 0;
    private int iScreenHeight = 0;

    private final TouchScreenProcessor iTouchProcessor = new TouchScreenProcessor();

    public MainGameRenderer() {
        super();
        this.iWorld = new WorldScene(this.iCamera);
        this.iTouchProcessor.addTouchScreenListener(this);
    }


    private void addDrawObjects() {

        this.iWorldAxis = new XYZAxis();
        this.iWorld.add(this.iWorldAxis);

        /*{
            final XYZCoordinate coordinate = new XYZCoordinate(1.0f, 0.0f, 0.0f);
            coordinate.color = new XYZColor(1.0f, 1.0f, 1.0f, 0.0f);
            this.iPoint = new XYZPoint(coordinate);
            this.iCamera.setLookAtPosition(coordinate);
        }
        this.iWorld.add(this.iPoint);*/


        this.iMovingLine = new Line(
                new XYZCoordinate(0, 0, 0),
                new XYZCoordinate(5, 5, 5),
                new XYZColor(0.9f,0.9f,0.9f,1.0f) //white = 1,1,1,1
        );
        this.iWorld.add(this.iMovingLine);


        this.iGamePlane = new GameTerrain(10,20);
        this.iWorld.add(this.iGamePlane);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (this.iPersistenceObject == null) {
            this.iPersistenceObject = new Bundle();
            // Set the background frame color
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.8f);
            this.addDrawObjects();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.iScreenWidth = width;
        this.iScreenHeight = height;
        GLES20.glViewport(0, 0, width, height);
        this.iTouchProcessor.doRecalibration(this.iScreenWidth, this.iScreenHeight);
        iWorld.doRecalibration(width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT |
                GL10.GL_DEPTH_BUFFER_BIT);
        this.iWorld.onDraw();
    }



    public void onTouch(MotionEvent e) {
        this.iWorld.onTouch(e, this.iTouchProcessor);
    }


    @Override
    public void fireMovement(float xPercent, float zPercent) {
        final XYZCoordinate cameraPosition = this.iCamera.getCameraPosition();
        final float ratio = TouchScreenProcessor.TOUCH_MOVEMENT_ACCELERATION_FACTOR * (float) this.iScreenWidth / (float) this.iScreenHeight;

        //System.out.println("xPercent=" + xPercent + " zPercent=" + zPercent);
        cameraPosition.setX( cameraPosition.x() - (ratio/100.0f) * xPercent * 2.0f) ;
        cameraPosition.setZ( cameraPosition.z() - (ratio/100.0f) * zPercent );
        this.iCamera.setCameraPosition(cameraPosition);

        final XYZCoordinate lookAtPosition = this.iCamera.getiLookAtPosition();
        lookAtPosition.setX( lookAtPosition.x() - (ratio/100.0f) * xPercent * 2.0f );
        lookAtPosition.setZ( lookAtPosition.z() - (ratio/100.0f) * zPercent );
        this.iCamera.setLookAtPosition(lookAtPosition);

    }

    @Override
    public void fireTouchClick(final float[] clickVector) {
        final float[] p1 = this.iCamera.getCameraPosition().asArray();
        final float[] p2 = MathGLUtils.getPointOnVector(clickVector, p1, 50.0f);

        final XYZCoordinate p1Line = new XYZCoordinate(p1);
        final XYZCoordinate p2Line = new XYZCoordinate(p2);
        p1Line.color = new XYZColor(1.0f, 0 , 0.0f, 1);
        p2Line.color = new XYZColor(1.0f, 0 , 0.0f, 1);

        this.iMovingLine.updateCoordinates(
                    p1Line,
                    p2Line
        );

        this.iGamePlane.processClickOnObject(this.iCamera.getCameraPosition(), new XYZCoordinate(clickVector));

    }

    /**
     *
     * @param zoomingFactor the zoom factor in percent as real subunit number in range [0..1]
     */
    @Override
    public void fireZoom(float zoomingFactor) {
        System.out.println("ZOOM FACTOR=" + zoomingFactor);
        final XYZCoordinate cameraPosition = this.iCamera.getCameraPosition();
        cameraPosition.setY(cameraPosition.y()*(1-zoomingFactor));
        this.iCamera.setCameraPosition(cameraPosition);
    }

    /**
     * destroy all the components owned by this renderer
     */
    protected void onDestroy(){
        this.iGamePlane.onDestroy();

    }

}
