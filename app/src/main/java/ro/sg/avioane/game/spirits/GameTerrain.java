
/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game.spirits;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.R;
import ro.sg.avioane.cavans.AbstractGameCavan;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZTexture;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.MathGLUtils;

public class GameTerrain extends AbstractGameCavan {

    private float iLowestYTerrainPoint = 0.0f;


    private static final float TILE_LENGTH = 1.0f;
    //private final XYZVertex[] iTriangleCoordinates;
    //private final short[] iIndexOrder;

    //Limited for keeping the iArrIndexOrder as short type
    //Note:
    // if you exceed ~180 tiles then the iArrIndexOrder must be (signed)INT
    //as a refactor model this part can be done in C++ with unsigned short so than the
    //~180 tiles can be increased to the ~255 and the 2bytes-short size kept
    public static final short MAX_TILES_NO = 100;
    private static final float MAP_DEPTH = 4.0f;

    private final XYZVertex[] iArrVertices;


    public float getLowestYTerrainPoint(){
        return this.iLowestYTerrainPoint;
    }

    private final int iMapWidthX; //tiles number on X
    private final int iMapLengthZ; //tiles number on Z
    private final int NO_OF_MAP_SIDES = 3;

    private boolean iIsDirty = false;

    private int iClickedTileIndex = -1;

    private final Context iContext;



    /**
     *
     * @param width as number of tiles. It must be an even number!
     * @param length as number of tiles. It must be an even number!
     */
    public GameTerrain(final int width, final int length, final Context context) {
        if (BuildConfig.DEBUG &&
                (width < 1 || length < 1 || width > MAX_TILES_NO || length > MAX_TILES_NO
                || width%2 != 0 || length%2 != 0  )) {
            throw new AssertionError(
                    new StringBuilder("Assertion failed width=")
                            .append(width).append(" length")
                            .append(length).toString());
        } else {
            this.iContext = context;
            this.iMapWidthX = width;
            this.iMapLengthZ = length;

            //width * length * 4 coordinates per tile +
            //add the square lower front [bottomLeft to bottomRight] => 4 * NO_OF_MAP_SIDES (large square)
            this.iArrVertices = new XYZVertex[(width * length * 4) + (4 * NO_OF_MAP_SIDES)];


            //super.iColor = new XYZColor(0.1f, 0.9f, 0.1f, 1.0f);
            super.build(this.buildCoordinates(), this.buildIndexDrawOrder());
        }
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
    private XYZVertex[] buildCoordinates(){
//        final XYZColor red = new XYZColor(1.0f,0.0f,0.0f,1.0f);
//        final XYZColor green = new XYZColor(0.1f,1.0f,0.3f,1.0f);
//        final XYZColor blue = new XYZColor(0.1f,0.2f,1.0f,1.0f);
        int index = 0;


        //build the map upper surface
        for (int j = -this.iMapLengthZ/2; j < this.iMapLengthZ/2; j++) {
            for (int i = -this.iMapWidthX/2; i < this.iMapWidthX/2; i++) {
//                XYZColor tmpColor = null;
//                if(i<0) {
//                    //water side.
//
//
//                    final int colorCode = (i + (j + 1));
//                    if (colorCode % 2 == 0)
//                        tmpColor = green;
//                    else /*if (colorCode % 3 == 0)*/
//                        tmpColor = blue;
//                } else {
//                    tmpColor = red;
//                }

                final Bitmap waterBitmap = BitmapFactory.decodeResource(this.iContext.getResources(), R.drawable.me);
                if (waterBitmap == null)
                    throw new NullPointerException("null picture");

                final XYZVertex leftUp = new XYZVertex(new XYZCoordinate(i*TILE_LENGTH, 0.0f, j*TILE_LENGTH));
                this.fillSquareCoordinates(leftUp, waterBitmap, "water", index);
                index+=4;
            }
        }


        //final XYZColor colorDirt = new XYZColor(.79f, 0.3f, 0.06f,1);
        //build the map front side lower
        {
            final Bitmap waterBitmap = BitmapFactory.decodeResource(this.iContext.getResources(), R.drawable.me);
            if (waterBitmap == null)
                throw new NullPointerException("null picture");
            //front side
            int leftBottom = (this.iMapLengthZ-1) * this.iMapWidthX * 4 + 1;
            int rightBottom = index -1; // this.iArrVertices.length-8 + 3; //-8 not -4 as the next 4 will be used for front square
            //+1 to take the right hand side of the square.
            index = makeSideMap(index, waterBitmap, "water", leftBottom, rightBottom, 1, -1);

            //left hand side
            rightBottom = leftBottom;
            leftBottom = 0;
            index = makeSideMap(index, waterBitmap, "water", leftBottom, rightBottom, -1, -1);

            //right hand side
            leftBottom = index - 1 - 5;
            rightBottom = this.iMapWidthX * 4 - 2;
            index = makeSideMap(index, waterBitmap, "water", leftBottom, rightBottom, 1, 1);
        }


        return this.iArrVertices;
    }

    /**
     *
     * @param index
     * @param textureData
     * @param textureName
     * @param leftBottom
     * @param rightBottom
     * @param rightSideMultiplier
     * @param leftSideMultiplier
     * @return
     */
    private int makeSideMap(int index,
                            final Bitmap textureData,
                            final String textureName,
                            int leftBottom,
                            int rightBottom,
                            int rightSideMultiplier ,
                            int leftSideMultiplier) {
        final float deltaTrapeze = 2.0f;
        //1. upper left vertex is already added inside  buildIndexDrawOrder leftBottom
        this.iArrVertices[index] = new XYZVertex(
                new XYZCoordinate(this.iArrVertices[leftBottom].coordinate.asArray())
        );
        this.iArrVertices[index].texture = new XYZTexture(0,0,textureName, textureData);
        index++;

        //2. low left
        this.iArrVertices[index] = new XYZVertex(
                new XYZCoordinate(this.iArrVertices[leftBottom].coordinate.asArray())
        );
        this.iArrVertices[index].coordinate.setX(
                this.iArrVertices[index].coordinate.x() + deltaTrapeze * leftSideMultiplier);
        this.iArrVertices[index].coordinate.setY(
                this.iArrVertices[index].coordinate.y() - MAP_DEPTH);
        this.iArrVertices[index].coordinate.setZ(
                this.iArrVertices[index].coordinate.z() + deltaTrapeze);
        this.iArrVertices[index].texture = new XYZTexture(0,1,textureName, textureData);
        index++;
        //3. right bottom
        this.iArrVertices[index] = new XYZVertex(
                new XYZCoordinate(this.iArrVertices[rightBottom].coordinate.asArray())
        );
        this.iArrVertices[index].texture = new XYZTexture(1,0,textureName, textureData);
        index++;

        //4. low right
        this.iArrVertices[index] = new XYZVertex(
                new XYZCoordinate(this.iArrVertices[rightBottom].coordinate.asArray())
        );
        this.iArrVertices[index].coordinate.setX(
                this.iArrVertices[index].coordinate.x() + deltaTrapeze * rightSideMultiplier);
        this.iArrVertices[index].coordinate.setY(
                this.iArrVertices[index].coordinate.y() - MAP_DEPTH);
        this.iArrVertices[index].coordinate.setZ(
                this.iArrVertices[index].coordinate.z() + deltaTrapeze);
        this.iArrVertices[index].texture = new XYZTexture(1,1,textureName, textureData);
        index++;


        final XYZCoordinate normal = MathGLUtils.getTriangleSharedNormal(
                new XYZCoordinate[]{
                        this.iArrVertices[index - 4].coordinate,
                        this.iArrVertices[index - 3].coordinate,
                        this.iArrVertices[index - 2].coordinate
                });
        this.iArrVertices[index-4].normal = normal;
        this.iArrVertices[index-3].normal = normal;
        this.iArrVertices[index-2].normal = normal;
        this.iArrVertices[index-1].normal = normal;

        return index;
    }


    /**
     * @param leftUp
     * @param textureData
     * @param textureName
     */
    private void fillSquareCoordinates(XYZVertex leftUp,
                                       final Bitmap textureData,
                                       final String textureName,
                                       int index){

        this.iArrVertices[index] = leftUp;
        this.iArrVertices[index++].texture = new XYZTexture(0,0,textureName, textureData);


        this.iArrVertices[index] = new XYZVertex(
                new XYZCoordinate(
                leftUp.coordinate.x(), leftUp.coordinate.y(), leftUp.coordinate.z() + TILE_LENGTH
                ));
        this.iArrVertices[index++].texture = new XYZTexture(0,1,textureName, textureData);


        this.iArrVertices[index] = new XYZVertex(
                new XYZCoordinate(
                leftUp.coordinate.x() + TILE_LENGTH, leftUp.coordinate.y(), leftUp.coordinate.z()
                ));
        this.iArrVertices[index++].texture = new XYZTexture(1,0,textureName, textureData);
        this.iArrVertices[index] = new XYZVertex(
                new XYZCoordinate(
                leftUp.coordinate.x() + TILE_LENGTH, leftUp.coordinate.y(), leftUp.coordinate.z() + TILE_LENGTH
                ));
        this.iArrVertices[index++].texture = new XYZTexture(1,1,textureName, textureData);

        {
            //normal triangle 1
            final XYZCoordinate normal1 = MathGLUtils.getTriangleSharedNormal(
                    new XYZCoordinate[]{
                            this.iArrVertices[index - 4].coordinate,
                            this.iArrVertices[index - 3].coordinate,
                            this.iArrVertices[index - 2].coordinate
                    });
            this.iArrVertices[index - 4].normal = normal1;
            this.iArrVertices[index - 3].normal = normal1;
            this.iArrVertices[index - 2].normal = normal1;
        }

        {
            //normal triangle 2
            final XYZCoordinate normal2 = MathGLUtils.getTriangleSharedNormal(
                    new XYZCoordinate[]{
                            this.iArrVertices[index - 2].coordinate,
                            this.iArrVertices[index - 3].coordinate,
                            this.iArrVertices[index - 1].coordinate
                    });
            this.iArrVertices[index - 2].normal = normal2;
            this.iArrVertices[index - 3].normal = normal2;
            this.iArrVertices[index - 1].normal = normal2;
        }
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
        //adding 6 = front side square
        final int coordinates = this.iMapWidthX * this.iMapLengthZ * indexPerSquareNo + 6*NO_OF_MAP_SIDES;
        final short[] arr = new short[coordinates];
        int index = 0;

        //map index
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
        //add front side big square
        //int leftBottom = (this.iMapLengthZ-1) * this.iMapWidthX * 4 + 1;
        //arr[index++] = (short)((this.iMapLengthZ-1) * this.iMapWidthX * 4 + 1);
        for (int sides = 0; sides < NO_OF_MAP_SIDES; sides++) {
            final int startIndex = this.iMapLengthZ * this.iMapWidthX * 4 + 4*sides;
            for (int i = 0; i < 3; i++) {
                arr[index++] = (short) (startIndex + i);
            }
            arr[index++] = (short) (startIndex + 2);
            arr[index++] = (short) (startIndex + 1);
            arr[index++] = (short) (startIndex + 3);
        }

        return arr;
    }



    @Override
    public void draw(final float[] viewMatrix, final float[] projectionMatrix) {
        if(this.iIsDirty){
            super.buildVertexBuffer(this.iArrVertices);
            this.iIsDirty = false;
        }

        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_TRIANGLES);
    }

    @Override
    public void onRestore() {
        this.iIsDirty = false;
        super.build(this.buildCoordinates(), this.buildIndexDrawOrder());
    }

//    @Override
//    public void onResume() {
//        if(super.isOnPause()){
//            //we must rebuild the indexes
//            super.buildDrawOrderBuffer(this.buildIndexDrawOrder());
//            super.buildVertexBuffer(this.buildCoordinates());
//        }
//        super.onResume();
//    }






//    /***
//     *  We'll use the theorem of Thales to find the startingPoint A on the plane GameTerrain.
//     *  Known points:
//     *  C = <code>startingPoint</code> parameter (normally it shall be the camera position)
//     *  A1 = will be calculated at a height of CB/2
//     *  B1 = will be calculated once A1 is calculated by B1[Cx, A1y, Cz]
//     *  B is actually like this: B[Cx, 0, Cz]
//     *  A = will be calculated once we know the distance CA by using Thales theorem.
//     *               /C
//     *              / |
//     *             /  |
//     *            /   |
//     *           A1   B1
//     *          /     |
//     *         /      |
//     *        A-------B
//     * @param startingPoint usually this is the point where camera is located.
//     * @param vector this is the vector for calculating the resulting point on the map starting from
//     *               startingPoint and following the vector.
//     */
//    public void processClickOnObject(final XYZVertex startingPoint, final XYZVertex vector){
//        final XYZVertex a1 = new XYZVertex(MathGLUtils.getPointOnVector(vector.asArray(), startingPoint.asArray(), startingPoint.y()/3.0f));
//        final XYZVertex b1 = new XYZVertex();
//        b1.setX(startingPoint.x());
//        b1.setY(a1.y());
//        b1.setZ(startingPoint.z());
//
//        final XYZVertex b = new XYZVertex();
//        b.setX(startingPoint.x());
//        b.setY(0.0f);
//        b.setZ(startingPoint.z());
//
//        //calculate CA = (BC x A1C) / B1C
//        final float bc = startingPoint.y(); //MathGLUtils.get3DPointsDistance(b, startingPoint);
//        final float a1c = MathGLUtils.get3DPointsDistance(a1, startingPoint);
//        final float b1c = MathGLUtils.get3DPointsDistance(b1, startingPoint);
//        final float ca = bc*a1c/b1c;
//
//        final float a[] = MathGLUtils.getPointOnVector(vector.asArray(), startingPoint.asArray(), ca);
//
//        System.out.printf("CLICK ON MAP x=%.2f y=%.2f z=%.2f\n\n", a[0], a[1], a[2]);
//        //System.out.println("searching tile on map: ");
//        this.searchTile(new XYZVertex(a));
//    }

    private void searchTile(final XYZVertex tileCoordinate){
        //check if x is inside the map range.
        if(this.iArrVertices[0].coordinate.x() > tileCoordinate.coordinate.x() ||
                this.iArrVertices[this.iArrVertices.length-1].coordinate.x() < tileCoordinate.coordinate.x()){
            System.out.println("IGNORED: X is out of the map range:" + tileCoordinate.coordinate.x());
        } else if(this.iArrVertices[0].coordinate.z() > tileCoordinate.coordinate.z() ||
                this.iArrVertices[this.iArrVertices.length-1].coordinate.z() < tileCoordinate.coordinate.z()) {
            System.out.println(
                    "IGNORED: Z is out of the map range:" + tileCoordinate.coordinate.z() +
                            "  this.iArrVertices[0].z()=" + this.iArrVertices[0].coordinate.z() +
                            "  this.iArrVertices[this.iArrVertices.length-1].z()=" +
                            this.iArrVertices[this.iArrVertices.length-1].coordinate.z());
        } else {
            //do a binary search on X
            int xTile = -1;
            int left = 0;
            int right = this.iMapWidthX - 1;
            int pivot = -1;

            while (left <= right) {
                pivot = left + (right - left) / 2;
                final int index = pivot * 4;
                if(this.iArrVertices[index].coordinate.x() <= tileCoordinate.coordinate.x() &&
                        this.iArrVertices[index + 2].coordinate.x() >= tileCoordinate.coordinate.x()){
                    break;
                } else if (this.iArrVertices[index + 2].coordinate.x() < tileCoordinate.coordinate.x()) {
                    left = pivot + 1;
                } else {
                    right = pivot - 1;
                }
            }
            xTile = pivot;



            //do a binary search on Z
            left = 0;
            right = this.iMapLengthZ - 1;
            int finalIndex = -1;

            while (left <= right) {
                pivot = left + (right - left) / 2;
                final int index = (pivot * this.iMapWidthX + xTile)*4;

                if(this.iArrVertices[index].coordinate.z() <= tileCoordinate.coordinate.z() &&
                        this.iArrVertices[index + 1].coordinate.z() >= tileCoordinate.coordinate.z()){


                    System.out.println("Z TILE POSITION FOUND at square no="
                            + (pivot * this.iMapWidthX + xTile) + " " +
                            "at index=" + index);

                    finalIndex = index;
                    break;
                } else if (this.iArrVertices[index].coordinate.z() < tileCoordinate.coordinate.z()) {
                    left = pivot + 1;
                } else {
                    right = pivot - 1;
                }
            }

            for (int i = 0; i < 4; i++) {
                this.iArrVertices[i+finalIndex].color = new XYZColor(0.5f, 0.5f, 0.5f, 1.0f);
            }

            this.iIsDirty = true;
        }
    }
}
