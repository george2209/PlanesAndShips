/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.primitives;

import java.util.Optional;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.cavans.AbstractGameCavan;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZMaterial;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.MathGL.MathGLUtils;

/**
 * A class that encapsulate the primitive triangle.
 * The order of building the triangle is: counterclockwise order
 */
public class Triangle extends AbstractGameCavan
{
    //private final XYZMaterial iMaterial = new XYZMaterial();

    public Triangle(){
        super.build(this.buildCoordinates(), this.buildIndexes(), this.getMaterial());
    }

    private Optional<XYZMaterial> getMaterial(){
        final XYZMaterial material = new XYZMaterial();
//        material.setConstantKA(new XYZCoordinate(1.0f, 1.0f, 1.0f));
        material.globalBackgroundColor = new XYZColor(1.0f, 0.0f , 0.0f, XYZColor.OPAQUE);

        return Optional.ofNullable(material);
    }


    /**
     * build a triangle on x,z plane
     * @return
     */
    private XYZVertex[] buildCoordinates(){
        final XYZCoordinate[] arrCoordinates = new XYZCoordinate[3];
        arrCoordinates[0] = new XYZCoordinate(10.0f, 0.0f, 0.0f);
        arrCoordinates[1] = new XYZCoordinate(5.0f, 0.0f, 10.0f);
        arrCoordinates[2] = new XYZCoordinate(15.0f, 0.0f, 10.0f);

        XYZCoordinate normal = MathGLUtils.getTriangleNormal(arrCoordinates[0], arrCoordinates[1],
                arrCoordinates[2]);

        final XYZVertex[] arrVertices = new XYZVertex[3];
        arrVertices[0] = new XYZVertex(arrCoordinates[0], normal);
        //arrVertices[0].backgroundColor = new XYZColor(1.0f, 0.0f , 0.0f, XYZColor.OPAQUE);

        arrVertices[1] = new XYZVertex(arrCoordinates[1], normal);
        //arrVertices[1].backgroundColor = new XYZColor(0.0f, 1.0f , 0.0f, XYZColor.OPAQUE);

        arrVertices[2] = new XYZVertex(arrCoordinates[2], normal);
        //arrVertices[2].backgroundColor = new XYZColor(0.0f, 0.0f , 1.0f, XYZColor.OPAQUE);

        return arrVertices;
    }

    private short[] buildIndexes(){
        return new short[]{0, 1, 2};
    }

    @Override
    public void draw(final float[] viewMatrix, final float[] projectionMatrix) {
        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_TRIANGLES);
    }

    @Override
    public void onRestore() {
        super.build(this.buildCoordinates(), this.buildIndexes(), this.getMaterial());
    }


}
