
package ro.sg.avioane.cavans;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.cavans.primitives.AbstractGameCavan;
import ro.sg.avioane.game.TouchScreenListener;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.util.MathGLUtils;

public class GameTerrain extends AbstractGameCavan {

    private float iLowestYTerrainPoint = 0.0f;


    private static final float TILE_LENGTH = 1.0f;
    //private final XYZCoordinate[] iTriangleCoordinates;
    //private final short[] iIndexOrder;

    //Limited for keeping the iArrIndexOrder as short type
    //Note:
    // if you exceed ~180 tiles then the iArrIndexOrder must be (signed)INT
    //as a refactor model this part can be done in C++ with unsigned short so than the
    //~180 tiles can be increased to the ~255 and the 2bytes-short size kept
    public static final short MAX_TILES_NO = 100;

    private final XYZCoordinate[] iArrVertices;

    public float getLowestYTerrainPoint(){
        return this.iLowestYTerrainPoint;
    }

    private final int iMapWidthX; //tiles number on X
    private final int iMapLengthZ; //tiles number on Z

    /**
     *
     * @param width as number of tiles
     * @param length as number of tiles
     */
    public GameTerrain(final int width, final int length) {
        if (BuildConfig.DEBUG && (width < 1 || length < 1 || width > MAX_TILES_NO || length > MAX_TILES_NO)) {
            throw new AssertionError(
                    new StringBuilder("Assertion failed width=")
                            .append(width).append(" length")
                            .append(length).toString());
        } else {
            this.iMapWidthX = width;
            this.iMapLengthZ = length;

//            this.iArrVertices = new XYZCoordinate[(width + 1) * (length + 1)];
            this.iArrVertices = new XYZCoordinate[(width * length * 4)]; //4 coordinates per tile
        }

        super.iColor = new XYZColor(0.1f, 0.9f, 0.1f, 1.0f);
        super.buildDrawOrderBuffer(this.buildIndexDrawOrder());
        super.buildVertexBuffer(this.buildCoordinates());
        super.compileGLSL();
    }

    /**
     * The way the map is arranged is from top-down and left to the right.
     * This method was refactored to accommodate from drawing GL_TRIANGLE_STRIP into now
     * drawing the more flexible GL_TRIANGLES.
     * Even if GL_TRIANGLES takes some more vertices to build two or more squares it still has
     * an advantage of separating the colors per triangle thus it is possible to draw adiacent
     * squares with different colors.
     *
     * This is the first tile looking like:
     * 0 --- 2  ...
     * |     |
     * |     |
     * 1 --- 3
     *
     * @return an array with all coordinates fixed
     */
    private XYZCoordinate[] buildCoordinates(){
        final XYZColor red = new XYZColor(1.0f,0.0f,0.0f,0.0f);
        final XYZColor green = new XYZColor(0.1f,1.0f,0.3f,0.0f);
        final XYZColor blue = new XYZColor(0.1f,0.2f,1.0f,0.0f);
        int index = 0;

        int xxx = 0;

        for (int j = this.iMapLengthZ/2; j > -this.iMapLengthZ/2; j--) {
            for (int i = -this.iMapWidthX/2; i < this.iMapWidthX/2; i++) {
                XYZColor tmpColor = null;
                if(i%2==0)
                    tmpColor = green;
                else
                    tmpColor = red;


                final XYZCoordinate leftUp = new XYZCoordinate(i*TILE_LENGTH, 0.0f, j*TILE_LENGTH);
                final XYZCoordinate[] arr = getSquareCoordinates(leftUp, tmpColor);
                System.out.println("array:" + xxx); xxx++;
                for(int k=0; k<arr.length; k++) {
                    System.out.println("\tx=" + arr[k].x() + "\tz=" + arr[k].z() );
                    this.iArrVertices[index++] = arr[k];
                }
            }
        }
        return this.iArrVertices;
    }

    /**
     * TODO: will be refactored and optimised later....
     * @param leftUp
     * @param color
     * @return
     */
    private XYZCoordinate[] getSquareCoordinates(XYZCoordinate leftUp,  final XYZColor color){
        final XYZCoordinate[] arr = new XYZCoordinate[4];
        arr[0] = leftUp;
        arr[1] = new XYZCoordinate(leftUp.x(), leftUp.y(), leftUp.z() - TILE_LENGTH);
        arr[2] = new XYZCoordinate(leftUp.x() + TILE_LENGTH, leftUp.y(), leftUp.z());
        arr[3] = new XYZCoordinate(leftUp.x() + TILE_LENGTH, leftUp.y(), leftUp.z() - TILE_LENGTH);

        arr[0].color = color;
        arr[1].color = color;
        arr[2].color = color;
        arr[3].color = color;

        return arr;
    }

    /**
     * return the tiles indexes as follows
     *  0 --- 2  ...
     *  |     |
     *  |     |
     *  1 --- 3
     *
     *  the triangles per square are having the indexes as follows (counter-clockwise):
     *  0,1,2
     *  2,1,3
     *  TOTAL = 6 indexes per square.
     * @return the array of indexes
     */
    private short[] buildIndexDrawOrder() {
        final int indexPerSquareNo = 6;
        final int coordinates = this.iMapWidthX * this.iMapLengthZ * indexPerSquareNo;
        final short[] arr = new short[coordinates];
        int index = 0;

        for (short i = 0; i < this.iMapLengthZ ; i++) {
            for (short j = 0; j < this.iMapWidthX; j++) {
                final short index1 = (short)(this.iMapWidthX * i * 4 + j * 4);
                arr[index++] = index1;
                arr[index++] = (short )(index1 + 1);
                arr[index++] = (short )(index1 + 2);
                arr[index++] = (short )(index1 + 2);
                arr[index++] = (short )(index1 + 1);
                arr[index++] = (short )(index1 + 3);
            }
        }

        System.out.println("INDEX ARRAY:\n\n");
        index = 0;
        for (short i = 0; i < this.iMapLengthZ; i++) {
            System.out.println("\n----\n");
            for (short j = 0; j < this.iMapWidthX * 6; j++) {
                    System.out.print(arr[index++] + ", ");
            }
        }

        System.out.println("\n\nINDEX ARRAY: arr=" + arr.length + "=" + (index));

        return arr;
    }
    

    /**
     * the way the map is arranged is from top-down and left to the right.
     * @param width as number of tiles
     * @param length as number of tiles
     * @return an array with all coordinates fixed
     */
//    private XYZCoordinate[] buildCoordinates(final int width, final int length){
//
//        final XYZColor red = new XYZColor(1.0f,0.0f,0.0f,0.0f);
//        final XYZColor green = new XYZColor(0.1f,1.0f,0.3f,0.0f);
//        final XYZColor blue = new XYZColor(0.1f,0.2f,1.0f,0.0f);
//        XYZColor currColor = red;
//
//        int index = 0;
//        final float widthReference = 0-((float)width/2.0f);
//        final float lengthReference = ((float)length/2.0f);
//
//        this.iLowestYTerrainPoint = 0.0f;
//
//        System.out.println("\n");
//        for(int i=length; i>=0; i--){
//            for (int j = 0; j < width+1; j++) {
//                System.out.println("\nTile no: " + index);
//                iArrVertices[index] = new XYZCoordinate();
//                iArrVertices[index].setX(widthReference + j * TILE_LENGTH);
//                //if(i==length || i<=1)
//                    iArrVertices[index].setY(0.0f);
//                //else{
//                //    final Random r = new Random();
//                //    iArrVertices[index].y = r.nextFloat();
//                //}
//                //update iLowestYTerrainPoint = min(..)!!!!
//                iArrVertices[index].setZ((i * TILE_LENGTH) - lengthReference);
//
////                if(currColor.equals(red))
////                    currColor = green;
////                else if(currColor.equals(green))
////                    currColor = blue;
////                else if(currColor.equals(blue))
////                    currColor = red;
////                else
////                    throw  new RuntimeException("unknown color");
//
//                iArrVertices[index].color = currColor;
//
//                System.out.println("XYZCoordinate[" + iArrVertices[index].x() + ", " +
//                        iArrVertices[index].y() + ", " +
//                        iArrVertices[index].z() + "]");
//                index++;
//            }
//        }
//        return iArrVertices;
//    }

    /**
     *
     * @return the array of indexes
     */
//    private short[] buildIndexes(){
//        final short[] indexOrder = new short[]{0, 2, 1, 3, /*degeneration*/3, 2, 2, 4, 3, 5};
//        return indexOrder;
//    }

//    private short[] buildIndexDrawOrder(final int width, final int length) {
//        if (BuildConfig.DEBUG && (width < 1 || length < 1 || width > MAX_TILES_NO || length > MAX_TILES_NO)) {
//            throw new AssertionError(
//                    new StringBuilder("Assertion failed width=")
//                            .append(width).append(" length")
//                            .append(length).toString());
//        }
//
//        final int size = (4+2*(width-1)) * length + (length-1)*2;
//        final short[] indexOrder = new short[size];
//
//        int index = 0;
//        for(short le=0; le<length; le++){
//            for (short wi=0; wi<=width; wi++){
//                indexOrder[index] = (short) (wi + (le*(width+1)));
//                index++;
//                indexOrder[index] = (short) (wi + ((le+1)*(width+1)));
//                index++;
//            }
//            if(le < length-1){
//                indexOrder[index] = (short) (width + ((le+1)*(width+1)));
//                index++;
//                indexOrder[index] = (short) (0 + ((le+1)*(width+1)));
//                index++;
//            }
//        }
//
//
//        System.out.println("\nbuildIndexDrawOrder:\n");
//        for(short le=0; le<indexOrder.length; le++){
//            if(le<indexOrder.length-1){
//                System.out.print( indexOrder[le] + ", ");
//            } else {
//                System.out.print( indexOrder[le] + "\n");
//            }
//        }
//
//        return indexOrder;
//    }

    @Override
    public void draw(final float[] viewMatrix, final float[] projectionMatrix) {
        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_TRIANGLES);
    }


    /***
     *  We'll use the theorem of Thales to find the startingPoint A on the plane GameTerrain.
     *  Known points:
     *  C = <code>startingPoint</code> parameter (normally it shall be the camera position)
     *  A1 = will be calculated at a height of CB/2
     *  B1 = will be calculated once A1 is calculated by B1[Cx, A1y, Cz]
     *  B is actually like this: B[Cx, 0, Cz]
     *  A = will be calculated once we know the distance CA by using Thales theorem.
     *               /C
     *              / |
     *             /  |
     *            /   |
     *           A1   B1
     *          /     |
     *         /      |
     *        A-------B
     * @param startingPoint usually this is the point where camera is located.
     * @param vector this is the vector for calculating the resulting point on the map starting from
     *               startingPoint and following the vector.
     */
    public void processClickOnObject(final XYZCoordinate startingPoint, final XYZCoordinate vector){
        final XYZCoordinate a1 = new XYZCoordinate(MathGLUtils.getPointOnVector(vector.asArray(), startingPoint.asArray(), startingPoint.y()/3.0f));
        final XYZCoordinate b1 = new XYZCoordinate();
        b1.setX(startingPoint.x());
        b1.setY(a1.y());
        b1.setZ(startingPoint.z());

        final XYZCoordinate b = new XYZCoordinate();
        b.setX(startingPoint.x());
        b.setY(0.0f);
        b.setZ(startingPoint.z());

        //calculate CA = (BC x A1C) / B1C
        final float bc = startingPoint.y(); //MathGLUtils.get3DPointsDistance(b, startingPoint);
        final float a1c = MathGLUtils.get3DPointsDistance(a1, startingPoint);
        final float b1c = MathGLUtils.get3DPointsDistance(b1, startingPoint);
        final float ca = bc*a1c/b1c;

        final float a[] = MathGLUtils.getPointOnVector(vector.asArray(), startingPoint.asArray(), ca);

        System.out.printf("CLICK ON MAP x=%.2f y=%.2f z=%.2f", a[0], a[1], a[2]);
    }
}
