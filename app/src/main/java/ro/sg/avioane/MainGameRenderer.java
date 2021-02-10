package ro.sg.avioane;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.cavans.primitives.Triangle;
import ro.sg.avioane.game.WorldCamera;
import ro.sg.avioane.game.WorldScene;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;

public class MainGameRenderer implements GLSurfaceView.Renderer{

    //put here some object for test:
    private Triangle iTestObject = null;
    private Bundle iPersistenceObject = null; //will be used later for persistence
    private final WorldCamera iCamera = new WorldCamera();
    private final WorldScene iWorld;

    public MainGameRenderer(){
        super();
        this.iWorld = new WorldScene(this.iCamera);
    }


    private void testDrawObject(){
        final XYZCoordinate[] triangleCoordinates = new XYZCoordinate[3];
        for(int i=0; i<3; i++) {
            triangleCoordinates[i] = new XYZCoordinate();
            triangleCoordinates[i].x = i * (i-1) * 5; // 0,0,10
            triangleCoordinates[i].y = (i - 1)*(i - 2) * 5; //10,0,0
            triangleCoordinates[i].z = 0; //0,0,0
        }

        this.iTestObject = new Triangle(triangleCoordinates, new XYZColor(0.0f, 0.8f, 0.8f, 1.0f));
        this.iWorld.add(this.iTestObject);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if(this.iPersistenceObject == null) {
            this.iPersistenceObject = new Bundle();
            // Set the background frame color
            GLES20.glClearColor(0.5f, 0.0f, 0.5f, 1.0f);
            this.testDrawObject();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        iWorld.doRecalibration(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        this.iWorld.onDraw();
    }

    /***
     *
     * @return this renderer camera
     */
    public WorldCamera getCamera(){
        return this.iCamera;
    }


}
