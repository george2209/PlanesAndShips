package ro.sg.avioane.cavans.primitives;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;

public class XYZPoint extends AbstractGameCavan{

    public XYZPoint(XYZCoordinate coordinate){
        final XYZCoordinate[] arrCoordinates = new XYZCoordinate[1];
        arrCoordinates[0] = coordinate;
        super.buildDrawOrderBuffer(new short[]{0});
        super.buildVertexBuffer(arrCoordinates);
        super.compileGLSL();
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_POINTS);
    }
}
