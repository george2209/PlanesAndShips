/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ro.gdi.canvas.GameObject;
import ro.gdi.geometry.XYZCoordinate;
import ro.sg.avioane.game.TouchScreenListener;
import ro.sg.avioane.game.TouchScreenProcessor;
import ro.sg.avioane.game.WorldCamera;
import ro.sg.avioane.game.WorldScene;

public class MainGameRenderer implements GLSurfaceView.Renderer, TouchScreenListener {

    private final Context iContext;





    //put here some object for test:
    //private GameTerrain iGamePlane = null;

    //private XYZPoint iPoint = null;
    //private XYZAxis iWorldAxis = null;
    //private Line iMovingLine = null;
    //private Square iSquare = null;
    //private BlenderObjCavan iAirPlane = null;

//    private StaticCube staticBlenderCube = null;
//    MovingCube movingCube = null;

    //private GameTerrain iGameTerrain = null;



    private final WorldCamera iCamera = new WorldCamera();
    private final WorldScene iWorld;
    private int iScreenWidth = 0;
    private int iScreenHeight = 0;

    private final TouchScreenProcessor iTouchProcessor = new TouchScreenProcessor();

    public MainGameRenderer(Context context) {
        this.iContext = context;
        this.iWorld = new WorldScene(this.iCamera);
        this.iTouchProcessor.addTouchScreenListener(this);
    }

    public void addEntityInGame(@NonNull final GameObject entity){
        this.iWorld.add(entity);
    }


//    private void addDrawObjects() {

        //this.iGameTerrain = new GameTerrain(50,50, this.iContext);
        //this.iWorld.add(this.iGameTerrain);

//        final ObjParser objParser = new ObjParser();
        
//        try {
//            //final ParsedObjBlender parsedObjBlender = objParser.parseOBJ(this.iContext, "plane.obj");
//            //iAirPlane = new BlenderObjCavan(parsedObjBlender.vertexArray, parsedObjBlender.drawOrderArray);
//            //this.iWorld.add(iAirPlane);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        this.iWorldAxis = new XYZAxis();
//        this.iWorld.add(this.iWorldAxis);

//        final XYZVertex upperLeft = new XYZVertex(new XYZCoordinate(0,0,0));
//        upperLeft.normal = new XYZCoordinate(0,1,0);
//        //upperLeft.backgroundColor = new XYZColor(0,0,0,1);
//        upperLeft.texture = new XYZTextureUV(0,0,
//                "me",
//                BitmapFactory.decodeResource(this.iContext.getResources(), R.drawable.water));
//
//        this.iSquare = new Square(upperLeft, 10);
//        this.iWorld.add(this.iSquare);

//        ParsedObjBlender blenderData = new ObjParser().parseOBJ(this.iContext, R.raw.cube_obj);
//        this.staticBlenderCube = new StaticCube(blenderData.vertexArray, blenderData.drawOrderArray);
//        this.iWorld.add(this.staticBlenderCube);



//        blenderData = new ObjParser().parseOBJ(this.iContext, R.raw.cube_obj);
//        this.movingCube = new MovingCube(blenderData.vertexArray, blenderData.drawOrderArray);
//        this.iWorld.add(this.movingCube);

        /*{
            final XYZVertex coordinate = new XYZVertex(1.0f, 0.0f, 0.0f);
            coordinate.color = new XYZColor(1.0f, 1.0f, 1.0f, 0.0f);
            this.iPoint = new XYZPoint(coordinate);
            this.iCamera.setLookAtPosition(coordinate);
        }
        this.iWorld.add(this.iPoint);*/


//        this.iMovingLine = new Line(
//                new XYZVertex(0, 0, 0),
//                new XYZVertex(5, 5, 5),
//                new XYZColor(0.9f,0.9f,0.9f,1.0f) //white = 1,1,1,1
//        );
//        this.iWorld.add(this.iMovingLine);
//
//
//    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

//        final TextureUtils textureUtils = TextureUtils.getInstance();
//        boolean isRestoreNeeded = textureUtils.onDestroy();
//        isRestoreNeeded |= OpenGLProgramFactory.getInstance().onDestroy();
//        //textureUtils.loadTextures(this.iContext);
//        if(isRestoreNeeded && this.iWorld.count() > 0){
//            System.out.println("A restore of the world is needed***");
//            this.iWorld.onRestoreWorld();
//        }
//        textureUtils.releaseTextures();
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES30.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.iScreenWidth = width;
        this.iScreenHeight = height;
        GLES30.glViewport(0, 0, width, height);
        this.iTouchProcessor.doRecalibration(this.iScreenWidth, this.iScreenHeight);
        iWorld.doRecalibration(width, height);

    }


//    float strength = 0.1f;
//    long time = System.currentTimeMillis();

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT |
                GL10.GL_DEPTH_BUFFER_BIT);

        //play a little with the ambient light as parts of the test.
//        final long now = System.currentTimeMillis();
//        if(now - time > 500){
//            time = now;
//            strength += 0.1f;
//            if(strength > 1.0)
//                strength = 0.1f;
//        }
//        AmbientLight.getStaticInstance().setAmbientColorStrength(strength);

        this.iWorld.onDraw();
    }



    public void onTouch(MotionEvent e) {
        this.iWorld.onTouch(e, this.iTouchProcessor);
    }


    @Override
    public void fireMovement(float xPercent, float zPercent) {
        final XYZCoordinate cameraPosition = this.iCamera.getCameraPosition();
        final float ratio = TouchScreenProcessor.TOUCH_MOVEMENT_ACCELERATION_FACTOR * (float) this.iScreenWidth / (float) this.iScreenHeight;
        final float toleranceFactor = 5.0f; //TODO: this shall depend on the map zoom level.

        cameraPosition.setX( cameraPosition.x() - (ratio/100.0f) * xPercent * 2.0f * toleranceFactor) ;
        cameraPosition.setZ( cameraPosition.z() - (ratio/100.0f) * zPercent * toleranceFactor);
        this.iCamera.setCameraPosition(cameraPosition);

        final XYZCoordinate lookAtPosition = this.iCamera.getiLookAtPosition();
        lookAtPosition.setX( lookAtPosition.x() - (ratio/100.0f) * xPercent * 2.0f * toleranceFactor);
        lookAtPosition.setZ( lookAtPosition.z() - (ratio/100.0f) * zPercent * toleranceFactor);
        this.iCamera.setLookAtPosition(lookAtPosition);

    }

    @Override
    public void fireTouchClick(final float[] clickVector) {
//        final float[] p1 = this.iCamera.getCameraPosition().asArray();
//        final float[] p2 = MathGLUtils.getPointOnVector(clickVector, p1, 50.0f);
//
//        final XYZVertex p1Line = new XYZVertex(new XYZCoordinate(p1));
//        final XYZVertex p2Line = new XYZVertex(new XYZCoordinate(p2));
//        p1Line.backgroundColor = new XYZColor(1.0f, 0 , 0.0f, 1);
//        p2Line.backgroundColor = new XYZColor(1.0f, 0 , 0.0f, 1);

//        this.iMovingLine.updateCoordinates(
//                    p1Line,
//                    p2Line
//        );
//
//        this.iGamePlane.processClickOnObject(this.iCamera.getCameraPosition(), new XYZVertex(clickVector));

    }

    /**
     *
     * @param zoomingFactor the zoom factor in percent as real subunit number in range [0..1]
     */
    @Override
    public void fireZoom(float zoomingFactor) {
        //System.out.println("ZOOM FACTOR=" + zoomingFactor);
        final XYZCoordinate cameraPosition = this.iCamera.getCameraPosition();
        cameraPosition.setY(cameraPosition.y()*(1-zoomingFactor));
        this.iCamera.setCameraPosition(cameraPosition);
    }

}
