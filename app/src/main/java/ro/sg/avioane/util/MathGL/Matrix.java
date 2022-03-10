/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util.MathGL;

public class Matrix {
    /**
     * calculates M1-M2.
     * Consider to have both matrices at the same size. This will be not check inside this method.
     *
     * @param m1 first matrix
     * @param m2 second matrix
     * @return a new matrix with the difference
     */
    public static float[] matrixDifference(final float[] m1, final float[] m2) {
        final float[] result = new float[m1.length];
        for (int i = 0; i < m1.length; i++) {
            result[i] = m1[i] - m2[i];
        }
        return result;
    }

    /**
     * multiply matrix x value and return the result into a new matrix
     *
     * @param matrix
     * @param val
     * @return
     */
    public static float[] matrixMultiplyWithValue(final float[] matrix, final float val) {
        final float[] result = new float[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = matrix[i] * val;
        }
        return result;
    }

    /**
     * add two matrices with the same size into a new matrix
     * @param m1
     * @param m2
     * @return
     */
    public static float[] matrixAddMatrix(final float[] m1, final float[] m2){
        final float[] result = new float[m1.length];
        for (int i = 0; i < m1.length; i++) {
            result[i] = m1[i] + m2[i];
        }
        return result;
    }
}
