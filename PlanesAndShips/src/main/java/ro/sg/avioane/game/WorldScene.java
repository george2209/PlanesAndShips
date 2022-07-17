/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game;

import android.opengl.Matrix;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import ro.gdi.canvas.AbstractWorldScene;
import ro.gdi.canvas.GameObject;
import ro.gdi.canvas.primitives.Line;
import ro.gdi.geometry.XYZColor;
import ro.gdi.geometry.XYZCoordinate;
import ro.gdi.geometry.XYZVertex;
import ro.gdi.util.MathGL.MathGLUtils;

public class WorldScene extends AbstractWorldScene<WorldCamera> {

    private final WorldCamera iCamera;

    public WorldScene(){
        super(new WorldCamera(), 1.0f, 300.0f);
        this.iCamera = super.getCamera();
    }


    /**
     * This method must be called whenever the display resolution of the device was changed
     * I.E.: call it on "onSurfaceChanged" of the Renderer.
     * @param screenWidth device display width
     * @param screenHeight device display height
     */
    public void doRecalibration(final int screenWidth, final int screenHeight){
        System.out.println("screenWidth=" + screenWidth + " screenHeight=" + screenHeight);

        this.iCamera.doRecalibration(screenWidth, screenHeight);

        //Question: This must be made in regards to the phone display orientation??
        final float ratio = ((float) screenWidth) / ((float) screenHeight); //calculate the aspect ration on the far clip
        final float[] projectionMatrix = super.getProjectionMatrix();

        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio,-1.0f, 1.0f,
                super.getNearCameraField(), super.getFarCameraField());

        super.setProjectionMatrix(projectionMatrix);
    }


}
