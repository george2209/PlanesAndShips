/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.gdi.cavans;

import ro.sg.avioane.geometry.XYZMaterial;
import ro.sg.avioane.geometry.XYZVertex;

/**
 * the reason behind this class is to have a collection of meshes inside each object that extends
 * AbstractGameCavan that will contain <i>separated by material</i> each characteristics for a
 * part of a graphical object that has an unique material attached.
 * Often we have a graphical object having more sub-parts, each sub-part with its own material.
 * Imagine a tank that has a turret that has its own material distinct from the tank main body.
 * In this case we know exactly what are the vertices and normals associated with that sub-part and
 * what kind of material will be used for this sub-part.
 *
 * Reason for creating this class:
 * 1. To be able to separate parts of a graphical object on sub-part, each sub-part with its
 * own unique material.
 * 2. To be able to manipulate parts of the objects in a separate way from the <i>main body</i>.
 */
public class GameCavanMesh extends AbstractGameCavan{
    private final XYZVertex[] iVerticesArray;
    private final int[] iIndexDrawOrder;
    private final int GL_FORM_TYPE;
    private final XYZMaterial iMaterial;


    /**
     *
     * @param verticesArray the vertices array
     * @param indexDrawOrder the draw order array
     * @param material the material used for this mesh
     * @param glFormType a GL_* type. Example: GLES30.GL_TRIANGLES
     */
    public GameCavanMesh(final XYZVertex[] verticesArray,
                         final int[] indexDrawOrder,
                         final XYZMaterial material,
                         final int glFormType){
        this.iMaterial = material;
        this.iVerticesArray = verticesArray;
        this.iIndexDrawOrder = indexDrawOrder;
        this.GL_FORM_TYPE = glFormType;
    }

    /**
     * Because this object is created not on the main OpenGL thread but in the background
     * we cannot build it as the "build" operation requires to be done inside the  OpenGL thread.
     * This means that this method must be called before the first draw operation. The call must be
     * done inside the OpenGL thread.
     */
    public void build(){
        this.onRestore();
    }

    public XYZVertex[] getVerticesArray() {
        return iVerticesArray;
    }

    public int[] getIndexDrawOrder() {
        return iIndexDrawOrder;
    }

    /**
     *
     * @param viewMatrix the view matrix
     * @param projectionMatrix the projection matrix
     */
    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        super.doDraw(viewMatrix, projectionMatrix, this.GL_FORM_TYPE);
    }

    @Override
    public void onRestore() {
        super.build(this.iVerticesArray, this.iIndexDrawOrder, this.iMaterial);
    }
}
