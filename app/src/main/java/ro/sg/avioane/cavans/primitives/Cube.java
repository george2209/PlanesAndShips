/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.primitives;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZVertex;

import static android.opengl.GLES20.GL_TRIANGLES;

public class Cube extends AbstractGameCavan {

    private static final int NO_OF_VERTICES = 16; //f=4 * c=4
    private XYZVertex iUpperLeftCoordinate;
    private float iEdgeSize = -1;

    /**
     * Builds a cube starting with one coordinate and one length
     * @param upperLeftFrontCoordinate the upper-front-left coordinate
     * @param edgeSize the size of the cube`s edge
     */
    public Cube(final XYZVertex upperLeftFrontCoordinate, final float edgeSize){
        this.iUpperLeftCoordinate = upperLeftFrontCoordinate;
        this.iEdgeSize = edgeSize;
        super.build(buildCoordinates(upperLeftFrontCoordinate, edgeSize), buildIndexDrawOrder());
    }

    private XYZVertex[] buildCoordinates(final XYZVertex upperLeftCoordinate, final float edgeSize){
        final XYZVertex[] cubeVertices = new XYZVertex[NO_OF_VERTICES]; //6 faces each having 4 vertices

        final XYZColor red = new XYZColor(1, 0,0, XYZColor.OPAQUE);
        final XYZColor blue = new XYZColor(0, 0,1,XYZColor.OPAQUE);
        final XYZColor green = new XYZColor(0, 1,0,XYZColor.OPAQUE);
        final XYZColor purple = new XYZColor(1, 1,0,XYZColor.OPAQUE);

        int index = 0;
        //right side x = upperLeftCoordinate.x() + edgeSize remaining constant
        {
            final XYZCoordinate s = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
            s.setX(s.x() + edgeSize);
            s.setZ(s.z() - edgeSize);
            index = buildCubeSideFace(new XYZVertex(s), edgeSize, cubeVertices, index, blue);
        }

        //left side x = upperLeftCoordinate.x() remaining constant
        {
            final XYZCoordinate u = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
            u.setZ(u.z() - edgeSize);
            index = buildCubeSideFace(new XYZVertex(u), edgeSize, cubeVertices, index, purple);
        }

        //back face with z=upperLeftCoordinate.z()-edgeSize
        {
            final XYZCoordinate c = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
            c.setZ(c.z() - edgeSize);
            index = buildCubeBackFace(new XYZVertex(c), edgeSize, cubeVertices, index, green);
        }

        //front face with z=upperLeftCoordinate.z() remaining constant
        index = buildCubeBackFace(upperLeftCoordinate, edgeSize, cubeVertices, index, red);



        return cubeVertices;
    }

    private int buildCubeFrontFace(final XYZVertex upperLeftCoordinate,
                                  final float edgeSize,
                                  final XYZVertex[] cubeVertices,
                                  int index,
                                  XYZColor color) {
        final int startIndex = index;
        //0
        final XYZCoordinate upRight = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
        upRight.setX(upRight.x() - edgeSize);
        cubeVertices[index++] = new XYZVertex(upRight);
        //1
        final XYZCoordinate lowLeft = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
        lowLeft.setY(lowLeft.y() - edgeSize);
        lowLeft.setY(lowLeft.x() - edgeSize);
        cubeVertices[index++] = new XYZVertex(lowLeft);
        //2
        cubeVertices[index++] = new XYZVertex(new XYZCoordinate(upperLeftCoordinate.coordinate.asArray()));
        //3
        final XYZCoordinate lowRight = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
        lowRight.setY(lowRight.y() - edgeSize);
        cubeVertices[index++] = new XYZVertex(lowRight);


        for (int i = startIndex; i < index; i++) {
            cubeVertices[i].color = color;
        }

        return index;
    }

    /**
     * build a square starting from up-left and building in order
     * low-left, up-right, low-right
     * Then the index will be 0,1,2,2,1,3 (using GL_TRIANGLES counter clockwise)
     * @param upperLeftCoordinate
     * @param edgeSize
     * @param cubeVertices
     * @param index
     * @return the next index available inside cubeVertices
     */
    private int buildCubeBackFace(final XYZVertex upperLeftCoordinate,
                                  final float edgeSize,
                                  final XYZVertex[] cubeVertices,
                                  int index,
                                  XYZColor color) {
        final int startIndex = index;
        //0
        cubeVertices[index++] = new XYZVertex(new XYZCoordinate(upperLeftCoordinate.coordinate.asArray()));//upLeft
        //1
        final XYZCoordinate lowLeft = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
        lowLeft.setY(lowLeft.y() - edgeSize);
        cubeVertices[index++] = new XYZVertex(lowLeft);
        //2
        final XYZCoordinate upRight = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
        upRight.setX(upRight.x() + edgeSize);
        cubeVertices[index++] = new XYZVertex(upRight);
        //3
        final XYZCoordinate lowRight = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
        lowRight.setX(lowRight.x() + edgeSize);
        lowRight.setY(lowRight.y() - edgeSize);
        cubeVertices[index++] = new XYZVertex(lowRight);


        for (int i = startIndex; i < index; i++) {
            cubeVertices[i].color = color;
        }

        return index;
    }

//    private void printVertex(XYZCoordinate v, int index){
//        final StringBuilder sb = new StringBuilder("print index =").append(index).append("\n");
//        sb.append("x=").append(v.x()).append(" ");
//        sb.append("y=").append(v.y()).append(" ");
//        sb.append("z=").append(v.z()).append("\n");
//        System.out.println(sb.toString());
//    }

    private int buildCubeSideFace(final XYZVertex upperLeftCoordinate,
                                   final float edgeSize,
                                   final XYZVertex[] cubeVertices,
                                   int index, final XYZColor color) {
        final int startIndex = index;

        cubeVertices[index++] = new XYZVertex(new XYZCoordinate(upperLeftCoordinate.coordinate.asArray()));;//upLeft

        final XYZCoordinate lowLeft = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
        lowLeft.setY(lowLeft.y() - edgeSize);
        cubeVertices[index++] = new XYZVertex(lowLeft);

        final XYZCoordinate upRight = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
        upRight.setZ(upRight.z() + edgeSize);
        cubeVertices[index++] = new XYZVertex(upRight);

        final XYZCoordinate lowRight = new XYZCoordinate(upperLeftCoordinate.coordinate.asArray());
        lowRight.setZ(lowRight.z() + edgeSize);
        lowRight.setY(lowRight.y() - edgeSize);
        cubeVertices[index++] = new XYZVertex(lowRight);



        for (int i = startIndex; i < index; i++) {
            cubeVertices[i].color = color;
        }

        return index;
    }

    private short[] buildIndexDrawOrder() {
        final int SIZE = (NO_OF_VERTICES/4); //0,1,2,2,1,3
        final short[] ret = new short[SIZE*6];
        for (short i = 0; i <SIZE ; i++) {
            ret[6*i] = (short)(i*4); //0
            ret[6*i+1] = (short)(i*4+1); //1
            ret[6*i+2] = (short)(i*4+2);//2
            ret[6*i+3] = (short)(i*4+2);//2
            ret[6*i+4] = (short)(i*4+1); //1
            ret[6*i+5] = (short)(i*4+3); //3
        }

//        System.out.println("\n");
//        for (int i = 0; i < ret.length; i++) {
//            System.out.print(ret[i] + ",");
//        }
//        System.out.println("\n");
        return ret;
    }


    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        super.doDraw(viewMatrix, projectionMatrix, GL_TRIANGLES);
    }

    @Override
    public void onRestore() {
        super.build(buildCoordinates(this.iUpperLeftCoordinate, this.iEdgeSize), buildIndexDrawOrder());
    }
}
