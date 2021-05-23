/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game.spirits;

import ro.sg.avioane.cavans.blender.BlenderObjCavan;
import ro.sg.avioane.geometry.XYZVertex;

public class StaticCube extends BlenderObjCavan{


    public StaticCube(final XYZVertex[] arr, short[] indexDrawOrder){
        super(arr, indexDrawOrder);
        this.translate(-1f,0.5f,0.5f);
    }
}
