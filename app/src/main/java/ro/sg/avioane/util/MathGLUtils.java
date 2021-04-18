package ro.sg.avioane.util;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZCoordinate;

public class MathGLUtils {

    /**
     * get3DPointsDistance
     * Used formula:
     * D(P1, P2) = √ [(Px2 − Px1)² + (Py2 − Py1)² + (Pz2 − Pz1)²]
     * @param p1
     * @param p2
     * @return the distance between two points in 3D space.
     */
    public static float get3DPointsDistance(final XYZCoordinate p1, final XYZCoordinate p2){
        return (float) Math.sqrt(
                Math.pow((p2.x() - p1.x()), 2) +
                Math.pow((p2.y() - p1.y()), 2) +
                Math.pow((p2.z() - p1.z()), 2)
        );
    }

    /**
     * add two matrices into a new matrix
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

    /**
     * multiply matrix x value and return the result into a new matrix
     * @param matrix
     * @param val
     * @return
     */
    public static float[] matrixMultiplyWithValue(final float[] matrix, final float val){
        final float[] result = new float[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = matrix[i]*val;
        }
        return result;
    }

    /**
     * This method is multiplying the matrix with the vector (NOT vector with matrix!!!).
     * @param matrix a symmetrical matrix. Do NOT push an AxB matrix!
     * @param vector
     * @return the new resulting vector coordinates into matrix space.
     */
    public static float[] matrixMultiplyWithVector(final float[] matrix, final float[] vector){
        final int matrixRowLength = (int) Math.sqrt(matrix.length);
        //assume a AxA symmetrical matrix and NOT AxB!
        if (BuildConfig.DEBUG &&
                ((Math.sqrt(matrix.length)) * Math.sqrt(matrix.length)) != (double)matrix.length) {
            throw new AssertionError("a symmetrical matrix was expected! length=" + matrix.length);
        }

        final float[] result = new float[matrixRowLength];

        for (int i = 0; i <matrixRowLength; i++) {
            result[i] = 0;
            for (int j = 0; j < matrixRowLength; j++) {
                result[i] += matrix[j + i*matrixRowLength] * vector[j];
            }
        }

        return result;
    }

    /**
     * calculates M1-M2.
     * Consider to have both matrices at the same size. This will be not check inside this method.
     * @param m1 first matrix
     * @param m2 second matrix
     * @return a new matrix with the difference
     */
    public static float[] matrixDifference(final float[] m1, final float[] m2){
        final float[] result = new float[m1.length];
        for (int i = 0; i <m1.length ; i++) {
            result[i] = m1[i] - m2[i];
        }
        return result;
    }

    /**
     * normalize a 3D matrix.
     * The determinant is calculated as follows:
     * <code>d=Math.sqrt(m[0]*m[0] + m[1]*m[1] + ... m[n]*m[n])</code>
     * @param m
     */
    public static void matrixNormalize(float[] m) {
        final int SIZE = m.length;

        double determinant = 0.0;
        for(int i=0; i<SIZE; i++){
            determinant += m[i] * m[i];
        }
        determinant = Math.sqrt(determinant);

        if (determinant == 0) {
            throw new IllegalArgumentException("cannot normalize with 0 determinant!");
        }

        for(int i=0; i<SIZE; i++){
            m[i] = m[i] / (float)determinant;
        }
    }

    /**
     * This method is returning a point that is placed at a specific length from the initial point
     * following a vector from that initial point.
     * So if you set a point A(initialPoint) and a vector V(vector) and you want to know what is
     * the point coordinate at a specific length L(length) from A going the V path then this will be
     * returned here.
     * @param vector
     * @param initialPoint
     * @param length
     * @return
     */
    public static float[] getPointOnVector(float[] vector, final float[] initialPoint, final float length) {
        final float[] pointOnVector = MathGLUtils.matrixMultiplyWithValue(vector, length);
        return MathGLUtils.matrixAddMatrix(initialPoint, pointOnVector);
    }
}
