/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game.spirits;


import ro.gdi.canvas.GameObject;

public class StaticCube extends GameObject {
    /**
     * @param objName the name of this object. Not sure if it has any practicability in production
     */
    public StaticCube(String objName) {
        super(objName, 0);
    }


//    public StaticCube(final XYZVertex[] arr, short[] indexDrawOrder){
//        super("StaticCube", arr, indexDrawOrder, null);
//        this.translate(-1f,0.5f,0.5f);
//    }
}
