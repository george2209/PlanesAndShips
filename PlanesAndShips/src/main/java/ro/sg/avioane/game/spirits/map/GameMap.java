
/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game.spirits.map;

import ro.gdi.canvas.GameObject;
import ro.gdi.geometry.XYZCoordinate;

public class GameMap extends GameObject {

    private final short iNoOfTilesX;
    private final short iNoOfTilesZ;
    private final GameMapSide iLeftMapSide;
    private final GameMapSide iRightMapSide;
    private static enum STATE_ENGINE_t {
        E_NONE,
        E_STATE_SELECT_MAP_TILE
    };

    private STATE_ENGINE_t iStateEngine = STATE_ENGINE_t.E_STATE_SELECT_MAP_TILE; //.E_NONE;


    /***
     *
     * @param tilesX the number of tiles the map will have on X axis
     * @param tilesZ the number of tiles the map will have on Z axis
     */
    public GameMap(final short tilesX, final short tilesZ) {
        super("GameMap", 2);
        this.iNoOfTilesX = tilesX;
        this.iNoOfTilesZ = tilesZ;
        this.iLeftMapSide = new GameMapSide((short)(tilesX), (short)(tilesZ),true);
        this.iRightMapSide = new GameMapSide((short)(tilesX), (short)(tilesZ),false);
        super.addComponent(this.iLeftMapSide);
        super.addComponent(this.iRightMapSide);
    }

    /**
     *
     * @param startingPoint a starting point of the vector. In "click" case it is usually the camera
     *                      position.
     * @param vector a vector where the "click" is pointing to
     * @return true if the click was on the map, false if not
     */
    public boolean processMapClick(final XYZCoordinate startingPoint, final XYZCoordinate vector){
        boolean result = false;
        switch(this.iStateEngine){
            case E_STATE_SELECT_MAP_TILE:
            {
                final XYZCoordinate intersectionPoint = this.getIntersectionPoint(startingPoint, vector);
                if(intersectionPoint != null)
                    this.iLeftMapSide.paintTile(intersectionPoint);
                else
                    throw new AssertionError("TODO: check right side? depends on the state engine");
            } break;
            case E_NONE:
            default:
                System.out.println("***warning*** click on map was not processed at all!");
        }
        return result;
    }

    /**
     *
     * @param vector
     * @param startingPoint
     * @return the intersection point of the vector starting from the startingPoint or null if there
     * is no such intersection
     */
    public XYZCoordinate getIntersectionPoint(final XYZCoordinate startingPoint, final XYZCoordinate vector){
        return this.iLeftMapSide.getIntersectionPoint(startingPoint, vector);
    }

}
