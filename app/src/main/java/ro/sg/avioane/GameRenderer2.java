package ro.sg.avioane;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;

import java.util.Objects;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.util.OpenGLUtils;

public class GameRenderer2 implements GLSurfaceView.Renderer{

    private final Context iContext;
    private final World iWorld;
    private final Light iSun;
    private FrameBuffer iFrameBuffer = null;
    private final RGBColor BACKGROUND_COLOR = new RGBColor(50,50,50);

    //put here some object for test:
    private Object3D iAirplane = null;
    private Bundle iPersistenceObject = null; //will be used later for persistence

    public GameRenderer2(final Context context){
        this.iContext = context;
        this.iWorld = new World();
        this.iSun = new Light(this.iWorld);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if(this.iPersistenceObject == null) {
            this.iPersistenceObject = new Bundle();

            //load the OBJ structures
            this.iAirplane = OpenGLUtils.loadModel(this.iContext, R.raw.plane_ju_87_obj);
            Objects.requireNonNull(this.iAirplane).translate(new SimpleVector(
                    50.0f,
                    0.0f,
                    50.0f
            ));
            this.iAirplane.setTexture("diff");
            //this.iAirplane.setTexture("bump");
            this.iAirplane.strip();
            this.iAirplane.build();
            this.iWorld.addObject(this.iAirplane);

            //set the world
            this.iSun.setPosition(new SimpleVector(0, -90, 0));
            this.iSun.setIntensity(140, 120, 120);
            this.iSun.setAttenuation(-1);

            this.iWorld.setAmbientLight(80, 80, 80);

            //this.iWorld.compileAllObjects();
            //this.iWorld.buildAllObjects();

            //set the initial camera position
            final Camera cam = this.iWorld.getCamera();
            SimpleVector camPos = cam.getPosition();
            camPos.x -= this.iAirplane.getCenter().x;
            camPos.y = this.iAirplane.getCenter().y - 700.0f;
            camPos.z -= -500.0f;
            cam.setPosition(camPos);
            cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
            cam.lookAt(this.iAirplane.getCenter());//(new SimpleVector(250,0,250));
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if(this.iFrameBuffer != null ){
            this.iFrameBuffer.dispose();
        }
        this.iFrameBuffer = new FrameBuffer(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        this.iFrameBuffer.clear(BACKGROUND_COLOR);
        this.iWorld.renderScene(this.iFrameBuffer);
        this.iWorld.draw(this.iFrameBuffer);
        this.iFrameBuffer.display();
    }
}
