/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.features;

import android.opengl.Matrix;

public class CavanMovements {
    private final float[] iModelMatrix = new float[16];

    //translation is not affecting the normals (*)
    //In case of calculating normals we only need the 3x3 matrix
    private final float[] iModelTransInvMatrix = new float[16];

    protected CavanMovements(){
        Matrix.setIdentityM(this.iModelMatrix, 0);
        Matrix.setIdentityM(iModelTransInvMatrix, 0);
    }



    /**
     * implement a translation on a vector v[x,y,z]
     * @param x length on x
     * @param y length on y
     * @param z length on z
     */
    public void translate(final float x, final float y, final float z){
        Matrix.translateM(this.iModelMatrix, 0, x, y, z);

        //todo: investigate if the method is also dividing each member over the the magnitude of x,y,z
        //I think this shall be done only if the non-uniform scales ??
        //see https://solarianprogrammer.com/2013/05/22/opengl-101-matrices-projection-view-model/
        //      ⎡ x /w ⎤
        //v =   ⎢ y /w ⎥
        //      ⎣ z /w ⎦

        this.doCalculateInvTranspose();
    }

    /**
     *
     * @return a reference to the model matrix
     */
    protected final float[] getModelMatrix(){
        return this.iModelMatrix;
    }

    /**
     *
     * @return a reference to the model matrix
     */
    protected final float[] getModelTransInvMatrixMatrix(){
        return this.iModelTransInvMatrix;
    }

    /**
     * the method is recalculating the Inv(Transpose(model)) and store the result inside the
     * <code>iModelTransInvMatrix</code>
     */
    private void doCalculateInvTranspose(){
        Matrix.invertM(this.iModelTransInvMatrix, 0, this.iModelTransInvMatrix, 0);
        Matrix.transposeM(this.iModelTransInvMatrix, 0, this.iModelMatrix,0 );
    }
}
