package ro.sg.avioane.cavans;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.cavans.primitives.AbstractGameCavan;
import ro.sg.avioane.geometry.XYZCoordinate;

public class GameTerrain extends AbstractGameCavan {
    private final short[] iArrIndexOrder;
    private final XYZCoordinate[] iArrVertices;
    private static final float TILE_LENGTH = 0.2f;

    //Limited for keeping the iArrIndexOrder as short type
    //Note:
    // if you exceed ~180 tiles then the iArrIndexOrder must be (signed)INT
    //as a refactor model this part can be done in C++ with unsigned short so than the
    //~180 tiles can be increased to the ~255 and the 2bytes-short size kept
    public static final short MAX_TILES_NO = 100;

    public GameTerrain(short width, short length){
        this.iArrIndexOrder = buildIndexDrawOrder(width, length);
        this.iArrVertices = buildCoordinates(width, length);
    }

    public XYZCoordinate[] buildCoordinates(int width, int length) {
        final XYZCoordinate[] arrVertices = new XYZCoordinate[width * length];
        int index = 0;
        for(int i=length-1; i>=0; i--){
            for (int j = 0; j < width; j++) {
                arrVertices[index].x = j * TILE_LENGTH;
                arrVertices[index].y = 0;
                arrVertices[index].z = i * TILE_LENGTH;
                index++;
            }
        }
        return arrVertices;
    }

    /**
     * build the order the vertices will be draw (CCW) as triangle strip
     * @param width the number of tiles this map will have on width
     * @param length the number of tiles this map will have on length
     * @return
     */
    public short[] buildIndexDrawOrder(short width, short length) {
        if (BuildConfig.DEBUG && (width < 1 || length < 1 || width > MAX_TILES_NO || length > MAX_TILES_NO)) {
            throw new AssertionError(
                    new StringBuilder("Assertion failed width=")
                            .append(width).append(" length")
                            .append(length).toString());
        }

        final short[] indexOrder = new short[(width-1)*length*2 + (width-2)*2];
        short degenerationFactor = 0;

        for( short i=0; i<width-1; i++){
            if(i>0){
                short j=0;
                indexOrder[degenerationFactor + i*(2*length) + 2*j] = (short) ((i*length)+j);
                degenerationFactor++;
            }

            for(short j=0; j<length; j++){
                indexOrder[degenerationFactor + i*(2*length) + 2*j] = (short) ((i*length)+j);
                indexOrder[degenerationFactor + i*(2*length) + 2*j +1] = (short) (((i+1)*length)+j);
            }
            if(i<width-2){
                short j= (short) (length-1);
                degenerationFactor++;
                indexOrder[degenerationFactor + i*(2*length) + 2*j +1] = (short) (((i+1)*length)+j);
            }
        }
        return indexOrder;
    }

    @Override
    public void draw(final float[] viewProjectionMatrix) {

    }
}
