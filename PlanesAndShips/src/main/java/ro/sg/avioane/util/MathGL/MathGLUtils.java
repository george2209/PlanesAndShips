/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util.MathGL;

import androidx.annotation.NonNull;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZCoordinate;

public class MathGLUtils {

    /**
     * get3DPointsDistance
     * If you need more details I am recommending you this page:
     * https://en.wikipedia.org/wiki/Dot_product
     *
     * Used formula:
     * D(P1, P2) = √ [(Px2 − Px1)² + (Py2 − Py1)² + (Pz2 − Pz1)²]
     * @param p1 origin of the vector
     * @param p2 "destination" or "direction pointer"
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
     * This method is multiplying the matrix with the vector (NOT vector with matrix!!!).
     * @param matrix a symmetrical matrix. Do NOT push an AxB matrix! Only AxA.
     * @param vector a A size vector.
     * @return the new resulting vector coordinates into matrix space.
     */
    public static float[] matrixMultiplyWithVector(final float[] matrix, final float[] vector){
        final int matrixRowLength = (int) Math.sqrt(matrix.length);
        //assume a AxA symmetrical matrix and NOT AxB!
        if (BuildConfig.DEBUG &&
                (matrixRowLength * matrixRowLength) != (double)matrix.length) { //some "basic" check
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
        final float[] pointOnVector = Matrix.matrixMultiplyWithValue(vector, length);
        return Matrix.matrixAddMatrix(initialPoint, pointOnVector);
    }

    /**
     * calculates the cross product (NOT dot!) between two points
     * used formula is:
     * | i, j, k    |
     * | Ax, Ay, Az |
     * | Bx, By, Bz |
     *
     * <code>iX - jY + kZ</code>
     * @param a first coordinate
     * @param b second coordinate (starting point of the vector)
     * @return the coordinates of the normal <code>iX - jY + kZ</code>
     */
    public static XYZCoordinate crossProduct(final XYZCoordinate a, final XYZCoordinate b){
        return new XYZCoordinate(
                a.y() * b.z() - b.y()*a.z(),
                a.z()*b.x() - b.z()*a.x(),
                a.x()*b.y() - b.x()*a.y());
    }

    /**
     * @param a
     * @param b
     * @return the normal to the edge/vector that starts from "a" and ends in "b"
     */
    public static XYZCoordinate getEdgeNormal(final XYZCoordinate a, final XYZCoordinate b){
        return new XYZCoordinate(Vector.normalize(MathGLUtils.crossProduct(a, b).asArray()));
    }

    /**
     * calculate the normal of a triangle represented by its vertices a,b,c.
     * Calculus:
     * triangle ( a, b, c )
     * edge1 = b-a
     * edge2 = c-a
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
        final XYZCoordinate edge1 = new XYZCoordinate(b.asArray()).subtract(a);
        final XYZCoordinate edge2 = new XYZCoordinate(c.asArray()).subtract(a);
        return new XYZCoordinate( Vector.normalize(crossProduct(edge1, edge2).asArray()));
    }

    /**
     * calculate the normal of a triangle represented by its vertices arr.
     * Info: The normal will be calculated at the point arr[0].
     * @param arr a length = 3 elements array that are forming a triangle.
     * @return <code>return getTriangleNormal(arr[0], arr[1], arr[2]);</code>
     */
    public static XYZCoordinate getTriangleNormal(@NonNull final XYZCoordinate[] arr){
        if(BuildConfig.DEBUG && arr.length != 3) {
            throw new AssertionError("arr out of range with length=" + arr.length);
        }

        return getTriangleNormal(arr[0], arr[1], arr[2]);
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

//    /**
//     * calculate the difference:
//     *  a - b
//     * @param a
//     * @param b
//     * @return the result in a new XYZVertex object
//     */
//    public static XYZCoordinate subtract(final XYZCoordinate a, final XYZCoordinate b){
//        return new XYZCoordinate(a.x() - b.x(),
//                a.y() - b.y(),
//                a.z() - b.z());
//    }

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
