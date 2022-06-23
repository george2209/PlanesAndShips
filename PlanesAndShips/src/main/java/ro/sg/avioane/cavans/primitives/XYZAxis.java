/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.primitives;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.cavans.AbstractGameCavan;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZMaterial;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.MathGL.MathGLUtils;

public class XYZAxis extends AbstractGameCavan {


    public XYZAxis(){
        super.build(this.buildCoordinates(), this.buildIndexes(), this.getMaterial());
    }

    private XYZMaterial getMaterial(){
        final XYZMaterial material = new XYZMaterial("XYZAxisMaterial");

        return material;
    }

    /**
     *
     * @return the coordinates array.
     */
    private XYZVertex[] buildCoordinates(){
        final XYZVertex[] arrVertices = new XYZVertex[4];
        final XYZCoordinate originCoordinate = new XYZCoordinate(0.0f, 0.0f, 0.0f);
        final XYZCoordinate xCoordinate = new XYZCoordinate(10.0f, 0.0f, 0.0f);
        final XYZCoordinate yCoordinate = new XYZCoordinate(0.0f, 10.0f, 0.0f);
        final XYZCoordinate zCoordinate = new XYZCoordinate(0.0f, 0.0f, 10.0f);

        final XYZCoordinate oxNormal = MathGLUtils.getEdgeNormal(originCoordinate, xCoordinate);
        final XYZCoordinate oyNormal = MathGLUtils.getEdgeNormal(originCoordinate, xCoordinate);
        final XYZCoordinate ozNormal = MathGLUtils.getEdgeNormal(originCoordinate, xCoordinate);
        final XYZCoordinate oNormal = new XYZCoordinate(oxNormal.asArray()).add(oyNormal).add(ozNormal);

        //Origin
        arrVertices[0] = new XYZVertex(originCoordinate, oNormal);
        arrVertices[0].vertexColor = new XYZColor(1.0f, 0.0f, 0.0f, XYZColor.OPAQUE);
        //arrVertices[0].normal = new XYZCoordinate(Vector.normalize(MathGLUtils.crossProduct(edge1, edge2).asArray()));
        //X
        arrVertices[1] = new XYZVertex(xCoordinate, oxNormal);
        arrVertices[1].vertexColor = new XYZColor(0.0f, 1.0f, 0.0f, XYZColor.OPAQUE);
        //arrVertices[1].normal = new XYZCoordinate(0.0f, 1.0f, 0.0f);
        //Y
        arrVertices[2] = new XYZVertex(yCoordinate, oyNormal);
        arrVertices[2].vertexColor = new XYZColor(0.0f, 0.0f, 1.0f, XYZColor.OPAQUE);
        //arrVertices[2].normal = new XYZCoordinate(0.0f, 1.0f, 0.0f);
        //Z
        arrVertices[3] = new XYZVertex(zCoordinate, ozNormal);
        arrVertices[3].vertexColor = new XYZColor(0.1f, 0.1f, 0.9f, 1.0f);
        //arrVertices[3].normal = new XYZCoordinate(1.0f, 1.0f, 0.0f);

        return  arrVertices;
    }

    /**
     *
     * @return the indexes as short array
     */
    private final int[] buildIndexes(){
        final int[] indexOrder = new int[]{0,1,0,2,0,3};
        return indexOrder;
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_LINES);
    }

    @Override
    public void onRestore() {
        super.build(this.buildCoordinates(), this.buildIndexes(), this.getMaterial());
    }
}
