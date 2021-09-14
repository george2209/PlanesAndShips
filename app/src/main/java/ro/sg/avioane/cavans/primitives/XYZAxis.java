/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.primitives;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.cavans.AbstractGameCavan;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZVertex;

public class XYZAxis extends AbstractGameCavan {

    public XYZAxis(){
        super.build(this.buildCoordinates(), this.buildIndexes(), null);
    }

    /**
     *
     * @return the coordinates array.
     */
    private XYZVertex[] buildCoordinates(){
        final XYZVertex[] arrCoordinates = new XYZVertex[4];
        //Origin
        arrCoordinates[0] = new XYZVertex(new XYZCoordinate(0.0f, 0.0f, 0.0f));
        //arrCoordinates[0].color = new XYZColor(0.8f, 0.8f, 0.8f, 1.0f);
        //arrCoordinates[0].normal = new XYZCoordinate(1.0f, 1.0f, 1.0f);
        //X
        arrCoordinates[1] = new XYZVertex(new XYZCoordinate(10.0f, 0.0f, 0.0f));
        //arrCoordinates[1].color = new XYZColor(0.9f, 0.1f, 0.0f, 1.0f);
        //arrCoordinates[1].normal = new XYZCoordinate(0.0f, 1.0f, 0.0f);
        //Y
        arrCoordinates[2] = new XYZVertex(new XYZCoordinate(0.0f, 10.0f, 0.0f));
        //arrCoordinates[2].color = new XYZColor(0.0f, 0.9f, 0.1f, 1.0f);
        //arrCoordinates[2].normal = new XYZCoordinate(0.0f, 0.0f, 1.0f);
        //Z
        arrCoordinates[3] = new XYZVertex(new XYZCoordinate(0.0f, 0.0f, 10.0f));
        //arrCoordinates[3].color = new XYZColor(0.1f, 0.1f, 0.9f, 1.0f);
        //arrCoordinates[3].normal = new XYZCoordinate(1.0f, 0.0f, 0.0f);

        return  arrCoordinates;
    }

    /**
     *
     * @return the indexes as short array
     */
    private final short[] buildIndexes(){
        final short[] indexOrder = new short[]{0,1,0,2,0,3};
        return indexOrder;
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_LINES);
    }

    @Override
    public void onRestore() {
        //no persistence in this case is needed.
        super.build(this.buildCoordinates(), this.buildIndexes(), null);
    }
}
