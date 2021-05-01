/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.primitives;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.geometry.XYZVertex;

public class XYZPoint extends AbstractGameCavan{

    private XYZVertex iCoordinate;

    public XYZPoint(XYZVertex coordinate){
        iCoordinate = coordinate;
        super.build(new XYZVertex[]{this.iCoordinate}, new short[]{0});
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_POINTS);
    }

    @Override
    public void onRestore() {
        super.build(new XYZVertex[]{this.iCoordinate}, new short[]{0});
    }
}
