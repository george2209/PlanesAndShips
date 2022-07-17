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

    //map side corner coordinates.
    private final XYZCoordinate iTopLeftMapSide = new XYZCoordinate(0,0,0);
    private final XYZCoordinate iTopRightMapSide = new XYZCoordinate(0,0,0);
    private final XYZCoordinate iBottomLeftMapSide = new XYZCoordinate(0,0,0);
    private final XYZCoordinate iBottomRightMapSide = new XYZCoordinate(0,0,0);

    public GameMapSide(final short tilesX, final short tilesZ, boolean isLeftSide) {
        super("GameMapSide" + (isLeftSide? "Left" : "Right"), tilesX/2 * tilesZ);
        this.iNoOfTilesX = tilesX;
        this.iNoOfTilesZ = tilesZ;
        this.doBuildTiles(isLeftSide);
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
    private void doBuildTiles(final boolean isLeftSideMap){
        if(BuildConfig.DEBUG)
            if(this.iNoOfTilesX%2 != 0 || this.iNoOfTilesZ%2 != 0)
                throw new AssertionError("the tiles of the map must be perfectly split in two parts otherwise the map is not balanced." +
                        " use even numbers for tilesX and tilesZ!");
        final short zero = 0;
        //map borders
        final short startIndexX = isLeftSideMap ? (short)(zero-iNoOfTilesX/2) : zero;
        final short endIndexX = isLeftSideMap ? zero : (short)(iNoOfTilesX / 2);
        final short startIndexZ = (short)(zero-iNoOfTilesZ/2);
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

        for(short tileX = startIndexX; tileX<endIndexX; tileX++){

            //tmp
            if(isCe1){
                color = c2;
            } else {
                color = c1;
            }
            isCe1 = !isCe1;


            for(short tileZ = startIndexZ; tileZ<endIndexZ; tileZ++){
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
        System.out.println("**** print margins " + isLeftSideMap);
        System.out.println("\t iTopLeftMapSide= " + iTopLeftMapSide.toString());
        System.out.println("\t iBottomLeftMapSide= " + iBottomLeftMapSide.toString());
        System.out.println("\t iTopRightMapSide= " + iTopRightMapSide.toString());
        System.out.println("\t iBottomRightMapSide= " + iBottomRightMapSide.toString());
    }
}
