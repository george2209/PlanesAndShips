/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.features;

import android.opengl.Matrix;

public class CavanMovements {
    protected final float[] iModelMatrix = new float[16];

    protected CavanMovements(){
        Matrix.setIdentityM(this.iModelMatrix, 0);
    }

    /**
     * implement a translation on a vector v[x,y,z]
     * @param x length on x
     * @param y length on y
     * @param z length on z
     */
    public void translate(final float x, final float y, final float z){
        Matrix.translateM(this.iModelMatrix, 0, x, y, z);
    }
}
