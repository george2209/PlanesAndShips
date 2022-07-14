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
import ro.sg.avioane.BuildConfig;

public class GameMapSide extends GameObjectComponent {

    private final short iNoOfTilesX;
    private final short iNoOfTilesZ;

    public GameMapSide(final short tilesX, final short tilesZ, boolean isLeftSide) {
        super("GameMapSide" + (isLeftSide? "Left" : "Right"), tilesX/2 * tilesZ);
        this.iNoOfTilesX = tilesX;
        this.iNoOfTilesZ = tilesZ;
        this.doBuildTiles(isLeftSide);
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


//                System.out.println("tileX =" + tileX + " tileZ=" + tileZ);
//                System.out.println("\t topLeft= " + topLeft.toString());
//                System.out.println("\t topRight= " + topRight.toString());
//                System.out.println("\t lowerLeft= " + lowerLeft.toString());
//                System.out.println("\t lowerRight= " + lowerRight.toString());
            }
        }
    }
}
