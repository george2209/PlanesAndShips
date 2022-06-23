/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans;

import android.os.SystemClock;

import ro.sg.avioane.cavans.util.GameObjectArray;

public class GameObjectComponent extends GameObjectArray<GameCavanMesh> {
    private final String iName;

    /**
     *
     * @param name the name of this component of the object.
     *             It shall be unique "per <i>GameObject</i>" in order to be able to quickly
     *             identify and locate it if you want to apply movements / rotation / animations for
     *             a certain component of a game object.
     */
    public GameObjectComponent(final String name) {
        this.iName = name;
    }


    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        for(int i=0; i<super.size(); i++){
            GameCavanMesh m = super.getComponentAt(i);
//            {
//                //rotate it every 10 seconds
//                long time = SystemClock.uptimeMillis() % 10000L;
//                float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
//                m.resetModelMatrix();
//                m.rotate(angleInDegrees, 0, 1, 0);
//            }
            m.draw(viewMatrix, projectionMatrix);
        }
    }

    public void onRestore() {
        for(int i=0; i<super.size(); i++){
            GameCavanMesh m = super.getComponentAt(i);
            m.onRestore();
        }
    }

    public void destroy(){
        for(int i=0; i<super.size(); i++){
            GameCavanMesh m = super.getComponentAt(i);
            m.destroy();
        }
    }
}
