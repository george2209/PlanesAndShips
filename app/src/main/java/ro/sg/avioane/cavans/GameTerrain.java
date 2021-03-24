package ro.sg.avioane.cavans;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.cavans.primitives.AbstractGameCavan;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;

public class GameTerrain extends AbstractGameCavan {


    private static final float TILE_LENGTH = 1.0f;
    //private final XYZCoordinate[] iTriangleCoordinates;
    //private final short[] iIndexOrder;

    //Limited for keeping the iArrIndexOrder as short type
    //Note:
    // if you exceed ~180 tiles then the iArrIndexOrder must be (signed)INT
    //as a refactor model this part can be done in C++ with unsigned short so than the
    //~180 tiles can be increased to the ~255 and the 2bytes-short size kept
    public static final short MAX_TILES_NO = 100;

    /**
     *
     * @param width as number of tiles
     * @param length as number of tiles
     */
    public GameTerrain(final int width, final int length) {
        super.iColor = new XYZColor(0.1f, 0.9f, 0.1f, 1.0f);
        final short[] arrTmp = this.buildIndexDrawOrder(width, length);

//        System.out.println("\nindex:");
//        for(int i=0; i<arrTmp.length; i++){
//            System.out.print(arrTmp[i] + ",");
//        }

        super.buildDrawOrderBuffer(arrTmp);
        super.buildVertexBuffer(this.buildCoordinates(width, length));
        super.compileGLSL();
    }

    /**
     *
     * @param width as number of tiles
     * @param length as number of tiles
     * @return an array with all coordinates fixed
     */
    private XYZCoordinate[] buildCoordinates(final int width, final int length){
        if (BuildConfig.DEBUG && (width < 1 || length < 1 || width > MAX_TILES_NO || length > MAX_TILES_NO)) {
            throw new AssertionError(
                    new StringBuilder("Assertion failed width=")
                            .append(width).append(" length")
                            .append(length).toString());
        }

        final XYZCoordinate[] arrVertices = new XYZCoordinate[(width+1) * (length + 1)];

        final XYZColor red = new XYZColor(1.0f,0.0f,0.0f,1.0f);
        final XYZColor green = new XYZColor(0.1f,1.0f,0.3f,1.0f);
        final XYZColor blue = new XYZColor(0.1f,0.2f,1.0f,1.0f);
        XYZColor currColor = red;

        int index = 0;
        final float widthReference = 0-((float)width/2.0f);
        final float lengthReference = ((float)length/2.0f);
        for(int i=length; i>=0; i--){
            for (int j = 0; j < width+1; j++) {
                //System.out.println("\nTile no: " + index);
                arrVertices[index] = new XYZCoordinate();
                arrVertices[index].x = widthReference + j * TILE_LENGTH;
                //if(i==length || i<=1)
                    arrVertices[index].y = 0.0f;
                //else{
                //    final Random r = new Random();
                //    arrVertices[index].y = r.nextFloat();
                //}
                arrVertices[index].z = (i * TILE_LENGTH) - lengthReference;
                if(currColor.equals(red))
                    currColor = green;
                else if(currColor.equals(green))
                    currColor = blue;
                else if(currColor.equals(blue))
                    currColor = red;
                else
                    throw  new RuntimeException("unknown color");
                arrVertices[index].color = currColor;

                /*System.out.println("XYZCoordinate[" + arrVertices[index].x + ", " +
                        arrVertices[index].y + ", " +
                        arrVertices[index].z + "]");*/
                index++;
            }
        }
        return arrVertices;
    }

    /**
     *
     * @return the array of indexes
     */
//    private short[] buildIndexes(){
//        final short[] indexOrder = new short[]{0, 2, 1, 3, /*degeneration*/3, 2, 2, 4, 3, 5};
//        return indexOrder;
//    }

    private short[] buildIndexDrawOrder(final int width, final int length) {
        if (BuildConfig.DEBUG && (width < 1 || length < 1 || width > MAX_TILES_NO || length > MAX_TILES_NO)) {
            throw new AssertionError(
                    new StringBuilder("Assertion failed width=")
                            .append(width).append(" length")
                            .append(length).toString());
        }

        final int size = (4+2*(width-1)) * length + (length-1)*2;
        final short[] indexOrder = new short[size];

        int index = 0;
        for(short le=0; le<length; le++){
            for (short wi=0; wi<=width; wi++){
                indexOrder[index] = (short) (wi + (le*(width+1)));
                index++;
                indexOrder[index] = (short) (wi + ((le+1)*(width+1)));
                index++;
            }
            if(le < length-1){
                indexOrder[index] = (short) (width + ((le+1)*(width+1)));
                index++;
                indexOrder[index] = (short) (0 + ((le+1)*(width+1)));
                index++;
            }
        }
        return indexOrder;
    }

    @Override
    public void draw(final float[] viewMatrix, final float[] projectionMatrix) {
        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_TRIANGLE_STRIP);
    }
}
