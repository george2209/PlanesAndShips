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
import ro.sg.avioane.geometry.XYZTexture;
import ro.sg.avioane.util.MathGLUtils;

public class Square extends AbstractGameCavan {

    private final XYZVertex iUpperLeftCoordinate;
    private final float iEdgeSize;

    /**
     * create a square with the edgeSize and starting from upper-left coordinate
     * @param upperLeftCoordinate the x,y,z coordinate
     * @param edgeSize size from (0.0 to 1.0]
     */
    public Square(final XYZVertex upperLeftCoordinate, final float edgeSize){
        this.iUpperLeftCoordinate = upperLeftCoordinate;
        this.iEdgeSize = edgeSize;

        super.build(buildCoordinates(upperLeftCoordinate, edgeSize), buildIndexDrawOrder());

        //rotateYTest();
    }

    private XYZVertex[] buildCoordinates(final XYZVertex upperLeftCoordinate, final float edgeSize){
        final XYZVertex[] coordinatesArray = new XYZVertex[4];
        coordinatesArray[0] = upperLeftCoordinate;
        final XYZTexture squareTexture = upperLeftCoordinate.texture;

        coordinatesArray[1] = new XYZVertex(
                new XYZCoordinate(
                upperLeftCoordinate.coordinate.x(), upperLeftCoordinate.coordinate.y(),
                upperLeftCoordinate.coordinate.z() + edgeSize
                ));
        coordinatesArray[1].backgroundColor = upperLeftCoordinate.backgroundColor;
        coordinatesArray[1].texture = new XYZTexture(0,1,
                squareTexture.getTextureName(), squareTexture.getTextureData());

        coordinatesArray[2] = new XYZVertex(
                new XYZCoordinate(
                upperLeftCoordinate.coordinate.x() + edgeSize, upperLeftCoordinate.coordinate.y(),
                upperLeftCoordinate.coordinate.z()));
        coordinatesArray[2].backgroundColor = upperLeftCoordinate.backgroundColor;
        coordinatesArray[2].texture = new XYZTexture(1,0,
                squareTexture.getTextureName(), squareTexture.getTextureData());

        coordinatesArray[3] = new XYZVertex(
                new XYZCoordinate(
                upperLeftCoordinate.coordinate.x() + edgeSize, upperLeftCoordinate.coordinate.y(),
                upperLeftCoordinate.coordinate.z() + edgeSize));
        coordinatesArray[3].backgroundColor = upperLeftCoordinate.backgroundColor;
        coordinatesArray[3].texture = new XYZTexture(1,1,
                squareTexture.getTextureName(), squareTexture.getTextureData());

        coordinatesArray[0].normal = MathGLUtils.getTriangleNormal(
                coordinatesArray[0].coordinate,
                coordinatesArray[1].coordinate,
                coordinatesArray[2].coordinate);

        coordinatesArray[1].normal = MathGLUtils.getTriangleSharedNormal(
                new XYZCoordinate[] {
                        //triangle 0
                        coordinatesArray[0].coordinate,
                        coordinatesArray[1].coordinate,
                        coordinatesArray[2].coordinate,
                        //triangle 1
                        coordinatesArray[2].coordinate,
                        coordinatesArray[1].coordinate,
                        coordinatesArray[3].coordinate
                });

        coordinatesArray[2].normal = coordinatesArray[1].normal;
        coordinatesArray[3].normal = coordinatesArray[1].normal;


        return coordinatesArray;
    }



    private short[] buildIndexDrawOrder() {
        return new short[] {0,1,2,2,1,3};
    }



    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {



        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_TRIANGLES);
    }

    @Override
    public void onRestore() {
        super.build(buildCoordinates(this.iUpperLeftCoordinate, this.iEdgeSize), buildIndexDrawOrder());
    }

    //https://learnopengl.com/Getting-started/Transformations
    public void rotateYTest(){
        final float[] modelMatrix = super.getModelMatrix();
        float val = 0.707106f; //45 degrees hardcoded for test
        modelMatrix[0] = val;
        modelMatrix[2] = val;
        modelMatrix[8] = -val;
        modelMatrix[10] = val;
    }
}
