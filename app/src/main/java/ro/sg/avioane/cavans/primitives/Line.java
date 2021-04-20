/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.primitives;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;

public class Line extends AbstractGameCavan{

    private XYZCoordinate iStart = null;
    private XYZCoordinate iEnd = null;
    private boolean iIsDirty = false;

    public Line(final XYZCoordinate start, final XYZCoordinate end){
        this.buildLine(start, end,  new XYZColor(0.0f, 0.5f, 1.0f, 0.5f));
    }

    public Line(final XYZCoordinate start, final XYZCoordinate end, final XYZColor color){
        this.buildLine(start, end, color);
    }

    private void buildLine(final XYZCoordinate start, final XYZCoordinate end, final XYZColor color){
        super.buildDrawOrderBuffer(this.buildIndexes());
        final XYZCoordinate[] lineCoordinatesArr =  this.buildCoordinates(start, end);
        for (int i = 0; i < lineCoordinatesArr.length ; i++) {
            lineCoordinatesArr[i].color = color;
        }
        super.buildVertexBuffer(lineCoordinatesArr);
        super.compileGLSL();
    }

    /**
     * update the coordinates of this line.
     * TODO: synchronization in case this method is kept permanently.
     * @param start
     * @param end
     */
    public void updateCoordinates(final XYZCoordinate start, final XYZCoordinate end){
        System.out.println("\nupdateCoordinates on line:");
        System.out.println("\nstart: x=" + start.x() + " y=" + start.y() + " z=" + start.z());
        System.out.println("\nend: x=" + end.x() + " y=" + end.y() + " z=" + end.z());
        this.iStart = start;
        this.iEnd = end;
        this.iIsDirty = true;
    }

    private XYZCoordinate[] buildCoordinates(final XYZCoordinate start, final XYZCoordinate end){
        return new XYZCoordinate[] {start, end};
    }

    private short[] buildIndexes(){
        return new short[]{0,1};
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        if(this.iIsDirty){
            super.buildVertexBuffer(this.buildCoordinates(iStart, iEnd));
            this.iIsDirty = false;
        }
        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_LINES);
    }
}
