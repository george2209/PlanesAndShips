package ro.sg.avioane;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.cavans.GameTerrain2;
import ro.sg.avioane.cavans.primitives.Triangle;
import ro.sg.avioane.cavans.primitives.XYZAxis;
import ro.sg.avioane.game.WorldCamera;
import ro.sg.avioane.game.WorldScene;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;

public class MainGameRenderer implements GLSurfaceView.Renderer{

    //put here some object for test:
    //private GameTerrain2 iTestObject = null;
    private Triangle iTestObject = null;
    private XYZAxis iWorldAxis = null;

    private Bundle iPersistenceObject = null; //will be used later for persistence
    private final WorldCamera iCamera = new WorldCamera();
    private final WorldScene iWorld;

    public MainGameRenderer(){
        super();
        this.iWorld = new WorldScene(this.iCamera);
    }


    private void addDrawObjects(){
        final XYZCoordinate[] triangleCoordinates = new XYZCoordinate[4];
        for(int i=0; i<4; i++) {
            triangleCoordinates[i] = new XYZCoordinate();
        }

        triangleCoordinates[0].x = 0.0f;
        triangleCoordinates[0].y = 0.0f;
        triangleCoordinates[0].z = 0.622008459f;

        triangleCoordinates[1].x = 0.5f;
        triangleCoordinates[1].y = 0.0f;
        triangleCoordinates[1].z = 0.622008459f;

        triangleCoordinates[2].x = 0.0f;
        triangleCoordinates[2].y = 0.0f;
        triangleCoordinates[2].z = 0.0f;

        triangleCoordinates[3].x = 0.5f;
        triangleCoordinates[3].y = 0.0f;
        triangleCoordinates[3].z = 0.0f;

        this.iTestObject = new Triangle(triangleCoordinates, new XYZColor(0.2f,0.8f,0.1f,1.0f));

        //this.iTestObject = new GameTerrain2((short)1,(short)2);


        this.iWorld.add(this.iTestObject);

        this.iWorldAxis = new XYZAxis();
        this.iWorld.add(this.iWorldAxis);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if(this.iPersistenceObject == null) {
            this.iPersistenceObject = new Bundle();
            // Set the background frame color
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            this.addDrawObjects();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        iWorld.doRecalibration(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT |
                GL10.GL_DEPTH_BUFFER_BIT);
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
