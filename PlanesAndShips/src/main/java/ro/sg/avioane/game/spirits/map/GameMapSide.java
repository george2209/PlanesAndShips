/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game.spirits.map;

import ro.gdi.canvas.GameObjectComponent;
import ro.gdi.geometry.XYZColor;
import ro.gdi.geometry.XYZCoordinate;
import ro.gdi.geometry.XYZVertex;
import ro.gdi.util.MathGL.MathGLUtils;
import ro.sg.avioane.BuildConfig;

public class GameMapSide extends GameObjectComponent {

    //size of the map side
    private final short iNoOfTilesX;
    private final short iNoOfTilesZ;

    //map side corner coordinates. needed???
    private final XYZCoordinate iTopLeftMapSide = new XYZCoordinate(0,0,0);
    private final XYZCoordinate iTopRightMapSide = new XYZCoordinate(0,0,0);
    private final XYZCoordinate iBottomLeftMapSide = new XYZCoordinate(0,0,0);
    private final XYZCoordinate iBottomRightMapSide = new XYZCoordinate(0,0,0);
    private final boolean isLeftSideMap;

    public GameMapSide(final short tilesX, final short tilesZ, boolean isLeftSide) {
        super("GameMapSide" + (isLeftSide? "Left" : "Right"), tilesX/2 * tilesZ);
        this.iNoOfTilesX = tilesX;
        this.iNoOfTilesZ = tilesZ;
        this.isLeftSideMap = isLeftSide;
        this.doBuildTiles();
    }

    /**
     * paint a tile containing the point of interest
     * The running cost is O(1). No search involved! only calculus ;).
     * I know I can compress all in smaller lines..for the moment for a simple understanding and
     * readability I will keep it like this.
     * @param pointOfInterest the coordinates (X and Z will be processed here) that shall be inside
     *                        the respective tile
     */
    public void paintTile(final XYZCoordinate pointOfInterest){
        System.out.println("paintTile: x=" + pointOfInterest.x() +
                " y=" + pointOfInterest.y() + " z=" + pointOfInterest.z());

        int xAxisIndex = this.isLeftSideMap ? (int) (
                10 * ( 1-( 1 - pointOfInterest.x()) / (iNoOfTilesX/2 * GameTile.TILE_SIZE))
        ) : -1; //TODO: right side map!

        int zAxisIndex =  pointOfInterest.z() < 0 ?
                (int)(10 * (1 - (((iNoOfTilesZ/2 * GameTile.TILE_SIZE) - pointOfInterest.z()) / (iNoOfTilesZ * GameTile.TILE_SIZE)))) :
                (int)(10 * (((iNoOfTilesZ/2* GameTile.TILE_SIZE) + pointOfInterest.z()) / (iNoOfTilesZ * GameTile.TILE_SIZE)));


        //(int)(1 - pointOfInterest.z() / ((iNoOfTilesZ/2) * GameTile.TILE_SIZE));
        //if(zAxisIndex < 0)
        //    zAxisIndex  = 1 - zAxisIndex; //upper negative side correction.

        if(xAxisIndex >= 0 && xAxisIndex <iNoOfTilesX / 2){
            if(zAxisIndex >= 0 && zAxisIndex < iNoOfTilesZ){
                final int cellIndex = xAxisIndex + zAxisIndex * (iNoOfTilesX/2);
                final GameTile gameTile = (GameTile) super.getMesh(cellIndex);
                gameTile.highlightCell();
            }
        } else {
            System.out.println("index out of range!");
        }


    }

    /**
     *
     * @param vector
     * @param startingPoint
     * @return the intersection point of the vector starting from the startingPoint or null if there
     * is no such intersection
     */
    public XYZCoordinate getIntersectionPoint(final XYZCoordinate startingPoint, final XYZCoordinate vector){
        XYZCoordinate intersection = new XYZCoordinate(0,0,0);
        if(MathGLUtils.isVectorIntersectionWithTriangle(startingPoint, vector,
                new XYZCoordinate[]{iTopLeftMapSide, iBottomLeftMapSide, iTopRightMapSide},intersection)){
            return intersection;
        } else if(MathGLUtils.isVectorIntersectionWithTriangle(startingPoint, vector,
                new XYZCoordinate[]{iBottomLeftMapSide, iBottomRightMapSide, iTopRightMapSide},intersection)){
            return intersection;
        } else
            return null;
    }

    /**
     * doBuildTiles is building the map with the tiles by initializing each tile
     */
    private void doBuildTiles(){
        if(BuildConfig.DEBUG)
            if(this.iNoOfTilesX%2 != 0 || this.iNoOfTilesZ%2 != 0)
                throw new AssertionError("the tiles of the map must be perfectly split in two parts otherwise the map is not balanced." +
                        " use even numbers for tilesX and tilesZ!");
        //map borders
        final short startIndexX = isLeftSideMap ? (short)(-iNoOfTilesX/2) : 0;
        final short endIndexX = isLeftSideMap ? 0 : (short)(iNoOfTilesX / 2);
        final short startIndexZ = (short)(-iNoOfTilesZ/2);
        final short endIndexZ = (short)(iNoOfTilesZ / 2);


        final XYZColor greenLight = new XYZColor(0, 0.5f,0.1f,1);
        final XYZColor greenDark = new XYZColor(0, 0.3f,0.2f,1);

        final XYZColor blueLight = new XYZColor(0, 0.1f,0.5f,1);
        final XYZColor blueDark = new XYZColor(0, 0.2f,0.3f,1);

        final XYZColor c1 = isLeftSideMap ? greenLight : blueLight;
        final XYZColor c2 = isLeftSideMap ? greenDark : blueDark;
        boolean isCe1 = isLeftSideMap ? true : false;
        XYZColor color = isLeftSideMap ? c1 : c2;


        //System.out.println(" ");
        //System.out.println(isLeftSideMap? "LEFT SIDE ********* " : "RIGHT SIDE ********* ");


        for(short tileZ = startIndexZ; tileZ<endIndexZ; tileZ++){
            //tmp
            if(isCe1){
                color = c2;
            } else {
                color = c1;
            }
            isCe1 = !isCe1;

            for(short tileX = startIndexX; tileX<endIndexX; tileX++){

                final XYZCoordinate topLeft = new XYZCoordinate(
                        tileX*GameTile.TILE_SIZE  , 0, tileZ * GameTile.TILE_SIZE);
                final XYZCoordinate lowerLeft = new XYZCoordinate(
                        tileX*GameTile.TILE_SIZE  , 0, (tileZ + 1) * GameTile.TILE_SIZE);

                final XYZCoordinate topRight = new XYZCoordinate(
                        (tileX+1)*GameTile.TILE_SIZE , 0, tileZ * GameTile.TILE_SIZE);
                final XYZCoordinate lowerRight = new XYZCoordinate(
                        (tileX+1)*GameTile.TILE_SIZE , 0, (tileZ+1)*GameTile.TILE_SIZE);

                final XYZVertex[] verticesArray = new XYZVertex[4];
                verticesArray[0] = new XYZVertex(topLeft, color);//0
                verticesArray[1] = new XYZVertex(topRight, color);//1
                verticesArray[2] = new XYZVertex(lowerLeft, color);//2
                verticesArray[3] = new XYZVertex(lowerRight, color);//3
                super.addMesh(new GameTile(verticesArray));


                //tmp
                if(isCe1){
                    color = c2;
                } else {
                    color = c1;
                }
                isCe1 = !isCe1;

                //set map border coordinates
                if(tileX == startIndexX){
                    if(tileZ == startIndexZ){
                        this.iTopLeftMapSide.setX(topLeft.x());
                        this.iTopLeftMapSide.setY(topLeft.y());
                        this.iTopLeftMapSide.setZ(topLeft.z());
                    } else if(tileZ == endIndexZ - 1) {
                        this.iBottomLeftMapSide.setX(lowerLeft.x());
                        this.iBottomLeftMapSide.setY(lowerLeft.y());
                        this.iBottomLeftMapSide.setZ(lowerLeft.z());
                    }
                } else if(tileX == endIndexX - 1){
                    if(tileZ == startIndexZ){
                        this.iTopRightMapSide.setX(topRight.x());
                        this.iTopRightMapSide.setY(topRight.y());
                        this.iTopRightMapSide.setZ(topRight.z());
                    } else if(tileZ == endIndexZ - 1) {
                        this.iBottomRightMapSide.setX(lowerRight.x());
                        this.iBottomRightMapSide.setY(lowerRight.y());
                        this.iBottomRightMapSide.setZ(lowerRight.z());
                    }
                }
            }
        }

        //tmp
//        System.out.println("**** print margins " + isLeftSideMap);
//        System.out.println("\t iTopLeftMapSide= " + iTopLeftMapSide.toString());
//        System.out.println("\t iBottomLeftMapSide= " + iBottomLeftMapSide.toString());
//        System.out.println("\t iTopRightMapSide= " + iTopRightMapSide.toString());
//        System.out.println("\t iBottomRightMapSide= " + iBottomRightMapSide.toString());
    }
}
