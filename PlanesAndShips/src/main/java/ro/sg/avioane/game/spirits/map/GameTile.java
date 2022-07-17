/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game.spirits.map;

import android.opengl.GLES30;

import ro.gdi.canvas.GameObjectMesh;
import ro.gdi.geometry.XYZVertex;

public class GameTile extends GameObjectMesh {
    private final static int[] DRAW_ORDER = new int[] {0,2,1,3};

    public final static float TILE_SIZE = 10;

    /**
     * defines a map tile specifying the all 4 corners by each vertex.
     * The drawing mechanism is composed by two layers:
     *
     * @param verticesArray is a 4 elements array as follows:
     *                      0 = top left corner
     *                      1 = top right corner
     *                      2 = bottom left corner
     *                      3 = bottom right corner
     */
    public GameTile(final XYZVertex[] verticesArray){
        super(verticesArray, DRAW_ORDER, GLES30.GL_TRIANGLE_STRIP);
    }

    public void showBorder(){

    }

    public void hideBorder(){

    }

    public void highlightCell(){

    }
}
