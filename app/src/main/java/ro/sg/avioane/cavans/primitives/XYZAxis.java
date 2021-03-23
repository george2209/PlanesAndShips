package ro.sg.avioane.cavans.primitives;

import android.opengl.GLES20;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;

public class XYZAxis extends AbstractGameCavan{

    public XYZAxis(){
        super.buildDrawOrderBuffer(this.buildIndexes());
        super.buildVertexBuffer(this.buildCoordinates());
        super.compileGLSL();
    }

    /**
     *
     * @return the coordinates array.
     */
    private XYZCoordinate[] buildCoordinates(){
        final XYZCoordinate[] arrCoordinates = new XYZCoordinate[4];
        //Origin
        arrCoordinates[0] = new XYZCoordinate(0.0f, 0.0f, 0.0f);
        arrCoordinates[0].color = new XYZColor(0.8f, 0.8f, 0.8f, 1.0f);
        //X
        arrCoordinates[1] = new XYZCoordinate(10.0f, 0.0f, 0.0f);
        arrCoordinates[1].color = new XYZColor(0.9f, 0.1f, 0.0f, 1.0f);
        //Y
        arrCoordinates[2] = new XYZCoordinate(0.0f, 10.0f, 0.0f);
        arrCoordinates[2].color = new XYZColor(0.0f, 0.9f, 0.1f, 1.0f);
        //Z
        arrCoordinates[3] = new XYZCoordinate(0.0f, 0.0f, 10.0f);
        arrCoordinates[3].color = new XYZColor(0.1f, 0.1f, 0.9f, 1.0f);

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
}
