/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZVertex;

public class MathGLUtils {

    public static class Vector{

        /**
         *
         * @param vector the vector (not position!) array coordinates
         * @return the length/magnitude of a vector into float (low precision).
         */
        public static float getVectorLength(final float[] vector){
            float sum = 0;
            for (float u: vector ) {
                sum += u*u;
            }
            return (float)Math.sqrt(sum);
        }

        public static float[] getVectorDirectionFromTwoPoints(final XYZCoordinate p1, final XYZCoordinate p2){
            return new float[]{
                    p2.x()-p1.x(),
                    p2.y()-p1.y(),
                    p2.z()-p1.z()
            };
        }

        /**
         * normalize a 3D vector.
         * The determinant is calculated as follows:
         * <code>d=Math.sqrt(m[0]*m[0] + m[1]*m[1] + ... m[n]*m[n])</code>
         * @param v the vector to be normalized
         * @return v normalized
         */
        public static float[] normalize(float[] v) {
            final int SIZE = v.length;
            final float determinant = Vector.getVectorLength(v);

            for(int i=0; i<SIZE; i++){
                v[i] = v[i] / (float)determinant;
            }

            return v;
        }

        /**
         *
         * @param vector
         * @param val
         * @return a new arrays having the results (V x val)
         */
        public static float[] multiplyByValue(final float[] vector, final float val){
            final float[] res = new float[vector.length];
            for (int i = 0; i < vector.length; i++) {
                res[i] = vector[i] * val;
            }
            return res;
        }

    }

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

//    /**
//     * normalize a 3D matrix.
//     * The determinant is calculated as follows:
//     * <code>d=Math.sqrt(m[0]*m[0] + m[1]*m[1] + ... m[n]*m[n])</code>
//     * that is the vector's magnitude
//     * @param m the matrix or vector to be normalized
//     * @return m normalized
//     */
//    public static float[] matrixNormalizeX(float[] m) {
//        final int SIZE = m.length;
//        final float determinant = Vector.getVectorLength(m);
//
//        for(int i=0; i<SIZE; i++){
//            m[i] = m[i] / (float)determinant;
//        }
//
//        return m;
//    }

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

    /**
     * calculates the cross product (NOT dot!) between two points
     * @param a
     * @param b
     * @return
     */
    public static XYZCoordinate crossProduct(final XYZCoordinate a, final XYZCoordinate b){
        return new XYZCoordinate(
                a.y() * b.z() - b.y()*a.z(),
                a.z()*b.x() - b.z()*a.x(),
                a.x()*b.y() - b.x()*a.y());
    }

    /**
     * calculate the normal of a triangle represented by its vertices a,b,c.
     * Calculus:
     * triangle ( v1, v2, v3 )
     * edge1 = v2-v1
     * edge2 = v3-v1
     * triangle.normal = cross(edge1, edge2).normalize()
     *
     * Important:
     * - set the a,b,c parameters in the order they will be draw via the index array.
     * - if the vertex is shared by more than one triangle then you must use getTriangleSharedNormal
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public static XYZCoordinate getTriangleNormal(final XYZCoordinate a, final XYZCoordinate b,
                                              final XYZCoordinate c){
        final XYZCoordinate edge1 = subtract(b, a);
        final XYZCoordinate edge2 = subtract(c,a);
        return new XYZCoordinate( Vector.normalize(crossProduct(edge1, edge2).asArray()));
    }

    /**
     * in case of a vertex shared by more triangles we need to calculate the value against all
     * triangles that shares that vertex.
     * Calculus:
     * vertex v1, v2, v3, ....
     * triangle tr1, tr2, tr3 // all share vertex v1
     * v1.normal = normalize( tr1.normal + tr2.normal + tr3.normal )
     *
     * @param arrTriangles having arrTriangles[0] the common vertex that must be calculated
     * @return
     */
    public static XYZCoordinate getTriangleSharedNormal(final XYZCoordinate[] arrTriangles){
        XYZCoordinate normalSum = null;
        for (int i = 0; i < arrTriangles.length; i+=3) {
            final XYZCoordinate a = arrTriangles[i];
            final XYZCoordinate b = arrTriangles[i+1];
            final XYZCoordinate c = arrTriangles[i+2];
            if(normalSum==null){
                normalSum = getTriangleNormal(a,b,c);
            } else {
                normalSum = add(normalSum, getTriangleNormal(a,b,c));
            }
        }

        return new XYZCoordinate(
                Vector.normalize(normalSum.asArray())
        );
    }

    /**
     * calculate the difference:
     *  a - b
     * @param a
     * @param b
     * @return the result in a new XYZVertex object
     */
    public static XYZCoordinate subtract(final XYZCoordinate a, final XYZCoordinate b){
        return new XYZCoordinate(a.x() - b.x(),
                a.y() - b.y(),
                a.z() - b.z());
    }

    /**
     * calculate a sum of two coordinates
     * a+b
     * @param a
     * @param b
     * @return a new object with the result
     */
    public static XYZCoordinate add(final XYZCoordinate a, final XYZCoordinate b){
        return new XYZCoordinate(a.x() + b.x(),
                a.y() + b.y(),
                a.z() + b.z());
    }
}
