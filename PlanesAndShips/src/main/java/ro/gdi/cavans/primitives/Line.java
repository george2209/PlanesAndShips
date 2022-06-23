/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.gdi.cavans.primitives;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZVertex;

public class Line //extends AbstractGameCavan
{

//    private XYZVertex iStart = null;
//    private XYZVertex iEnd = null;
//    private boolean iIsDirty = false;
//
//    public Line(final XYZVertex start, final XYZVertex end){
//        super(lineVerticesArr,this.buildIndexes());
//        this.buildLine(start, end,  new XYZColor(0.0f, 0.5f, 1.0f, 0.5f));
//    }
//
//    public Line(final XYZVertex start, final XYZVertex end, final XYZColor color){
//        this.buildLine(start, end, color);
//    }
//
//    private void buildLine(final XYZVertex start, final XYZVertex end, final XYZColor color){
//
//        final XYZVertex[] lineVerticesArr =  this.buildCoordinates(start, end);
//        for (int i = 0; i < lineVerticesArr.length ; i++) {
//            lineVerticesArr[i].color = color;
//        }
//
//
//
////        super.compileGLSL(super.getProgramShaderType(lineVerticesArr[0]));
////        super.buildVertexBuffer(lineVerticesArr);
////        super.buildDrawOrderBuffer(this.buildIndexes());
//
//
//
//    }
//
//    /**
//     * update the coordinates of this line.
//     * TODO: synchronization in case this method is kept permanently.
//     * @param start
//     * @param end
//     */
//    public void updateCoordinates(final XYZVertex start, final XYZVertex end){
//        System.out.println("\nupdateCoordinates on line:");
//        System.out.println("\nstart: x=" + start.coordinate.x() + " y=" + start.coordinate.y() + " z=" + start.coordinate.z());
//        System.out.println("\nend: x=" + end.coordinate.x() + " y=" + end.coordinate.y() + " z=" + end.coordinate.z());
//        this.iStart = start;
//        this.iEnd = end;
//        this.iIsDirty = true;
//    }
//
//    private XYZVertex[] buildCoordinates(final XYZVertex start, final XYZVertex end){
//        return new XYZVertex[] {start, end};
//    }
//
//    private short[] buildIndexes(){
//        return new short[]{0,1};
//    }
//
//    @Override
//    public void draw(float[] viewMatrix, float[] projectionMatrix) {
//        if(this.iIsDirty){
//            super.buildVertexBuffer(this.buildCoordinates(iStart, iEnd));
//            this.iIsDirty = false;
//        }
//        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_LINES);
//    }
}
