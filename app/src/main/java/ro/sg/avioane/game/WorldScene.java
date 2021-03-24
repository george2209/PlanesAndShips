package ro.sg.avioane.game;

import android.opengl.GLU;
import android.opengl.Matrix;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import ro.sg.avioane.cavans.primitives.AbstractGameCavan;
import ro.sg.avioane.util.MathGLUtils;

public class WorldScene {
    //TODO: update this number once the game is ready to be released!!!!!
    private static final int MAX_NO_SUPPORTED_CAVANS = 100;

    private static final float NEAR_CAMERA_FIELD = 1.0f;
    private static final float FAR_CAMERA_FIELD = 100.0f;

    private int iScreenWidth = 0;
    private int iScreenHeight = 0;

    private final float[] iProjectionMatrix = new float[16];

    private final WorldCamera iCamera;
    private final List<AbstractGameCavan> iGameEntities = new ArrayList<>(MAX_NO_SUPPORTED_CAVANS);

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
    public void add(final AbstractGameCavan entity){
        this.iGameEntities.add(entity);
    }

    public float[] getProjectionMatrix(){
        return this.iProjectionMatrix;
    }


    /**
     * This method must be called whenever the display resolution of the device was changed
     * I.E.: call it on "onSurfaceChanged" of the Renderer.
     * @param screenWidth device display width
     * @param screenHeight device display height
     */
    public void doRecalibration(final int screenWidth, final int screenHeight){
        System.out.println("screenWidth=" + screenWidth + " screenHeight=" + screenHeight);

        this.iScreenWidth = screenWidth;
        this.iScreenHeight = screenHeight;

        this.iCamera.doRecalibration(screenWidth, screenHeight);
        final float ratio = (float) screenWidth / (float) screenHeight; //calculate the aspect ration on the far clip
        Matrix.frustumM(iProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, NEAR_CAMERA_FIELD, FAR_CAMERA_FIELD);

        System.out.println("ratio=" + ratio + "\n");

        for(int i=0;i<4; i++){
            System.out.print("[");
            for (int j = 0; j < 4; j++) {
                String s = "";
                if(j!=3)
                    s = ", ";
                System.out.print("" + iProjectionMatrix[i*4+j] + s);
            }
            System.out.println("]");
        }
    }

    public void onDraw(){
        //Matrix.setIdentityM(this.iModelMatrix,0);

        //now you can apply transforms to the model matrix before you send it to the
        //cavans
        //ex:
        // Do a complete rotation every 10 seconds.
        //long time = SystemClock.uptimeMillis() % 10000L;
        //float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        // Matrix.rotateM(iModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);

        //call this to have the view matrix build depending on the camera movement
        //we can choose to call this only if the camera was moved
        //TODO: draw only when camera was moved this!
        this.iCamera.onDraw();





        /*Matrix.multiplyMM(this.iProjectionMatrix,
                0,
                this.iModelMatrix,
                0,
                this.iCamera.getViewMatrix(),
                0);*/

        //draw all entities of the map on the projection clip
        for (final AbstractGameCavan entity: this.iGameEntities) {
            entity.draw(this.iCamera.getViewMatrix(), this.iProjectionMatrix);
        }
    }


    public float[] onTouch(MotionEvent e){
        final float x = e.getX();
        final float y = (float)this.iScreenHeight - e.getY();
        final int[] viewport = { 0, 0, this.iScreenWidth, this.iScreenHeight };

        final float[] resultNear = getUnProjectMatrix(x, y, 0.0f, viewport);
        final float[] resultFar = getUnProjectMatrix(x, y, 1.0f, viewport);
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
    private float[] getUnProjectMatrix(float x, float y, float winZ, int[] viewport) {
        final float[] result = new float[4];

        GLU.gluUnProject(x, y, winZ,
                this.iCamera.getViewMatrix(), 0,
                this.iProjectionMatrix,0,
                viewport, 0,
                result, 0);

        result[0] /= result[3];
        result[1] /= result[3];
        result[2] /= result[3];
        result[3] = 1.0f;

        return result;
    }
}
