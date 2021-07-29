/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.blender;

import javax.microedition.khronos.opengles.GL10;

import ro.sg.avioane.cavans.AbstractGameCavan;
import ro.sg.avioane.geometry.XYZMaterial;
import ro.sg.avioane.geometry.XYZVertex;

public class BlenderObjCavan extends AbstractGameCavan {

    private XYZVertex[] iVerticesArray;
    //private final short[] iIndexDrawOrder;
    //
    // private short iVerticexIndex = 0;

    public XYZMaterial iOBJMaterial = null;
    public String iOBJName = null;

    public BlenderObjCavan(){

    }

    public void initializeVertices(short verticesNo){
        this.iVerticesArray = new XYZVertex[verticesNo];
        //this.iVerticexIndex = 0;
    }

//    public short verticesSize(){
//        return this.iVerticexIndex;
//    }

    public void addVertexAt(final XYZVertex vertex, short index){
        this.iVerticesArray[index] = vertex;
        //iVerticexIndex++; //needed??
    }

    public XYZVertex getVertexAt(short index){
        return this.iVerticesArray[index];
    }

//    public BlenderObjCavan(final XYZVertex[] arr, short[] indexDrawOrder){
//        this.iVerticesArray = arr;
//        this.iIndexDrawOrder = indexDrawOrder;
//        super.build(this.iVerticesArray, this.iIndexDrawOrder);
//    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        super.doDraw(viewMatrix, projectionMatrix, GL10.GL_TRIANGLES);
    }

    @Override
    public void onRestore() {
        ///////////////////////////////////super.build(this.iVerticesArray, this.iIndexDrawOrder);
    }

}
