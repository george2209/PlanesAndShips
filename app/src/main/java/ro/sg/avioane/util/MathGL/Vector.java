/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util.MathGL;

import ro.sg.avioane.geometry.XYZCoordinate;

public class Vector {

    /**
     * @param vector the vector (not position!) array coordinates
     * @return the length/magnitude of a vector into float (low precision).
     */
    public static float getVectorLength(final float[] vector) {
        float sum = 0;
        for (float u : vector) {
            sum += u * u;
        }
        return (float) Math.sqrt(sum);
    }

    public static float[] getVectorDirectionFromTwoPoints(final XYZCoordinate p1, final XYZCoordinate p2) {
        return new float[]{
                p2.x() - p1.x(),
                p2.y() - p1.y(),
                p2.z() - p1.z()
        };
    }

    /**
     * normalize a 3D vector:
     *      â = a / |a|
     *      a = original vector
     *     |a|= magnitude of "a"
     *     â = resulting normalized vector
     * The determinant is calculated as follows:
     * <code>d=Math.sqrt(m[0]*m[0] + m[1]*m[1] + ... m[n]*m[n])</code>
     *
     * @param v the vector to be normalized
     * @return v normalized
     */
    public static float[] normalize(float[] v) {
        final int SIZE = v.length;
        final float determinant = Vector.getVectorLength(v);

        for (int i = 0; i < SIZE; i++) {
            v[i] = v[i] / (float) determinant;
        }

        return v;
    }

    /**
     * @param vector
     * @param val
     * @return a new arrays having the results (V x val)
     */
    public static float[] multiplyByValue(final float[] vector, final float val) {
        final float[] res = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            res[i] = vector[i] * val;
        }
        return res;
    }

}
