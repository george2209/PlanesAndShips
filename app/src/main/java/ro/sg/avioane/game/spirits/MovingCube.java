/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game.spirits;

import ro.sg.avioane.cavans.blender.BlenderObjCavan;
import ro.sg.avioane.geometry.XYZVertex;

public class MovingCube extends BlenderObjCavan{

    private float movedSteps = 0.0f;
    private float step = 0.1f;
    private long time = System.currentTimeMillis();
    private final int timeout = 50;

    public MovingCube(final XYZVertex[] arr, short[] indexDrawOrder) {
        //////////////////////////////////////////////////////////////super(arr, indexDrawOrder);
        this.translate(1,3.0f,3.0f);
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        final long now = System.currentTimeMillis();
        if(now - time > timeout){
            time = now;
            this.movedSteps += step;
            if(this.movedSteps >= 10.0f ||
                    this.movedSteps <= 0){
                step = 0 - step;
            }
            this.translate(step, 0 , 0);
        }

        super.draw(viewMatrix, projectionMatrix);
    }

}
