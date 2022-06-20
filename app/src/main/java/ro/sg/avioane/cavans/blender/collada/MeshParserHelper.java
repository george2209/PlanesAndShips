/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.blender.collada;

import android.opengl.GLES30;

import ro.sg.avioane.geometry.XYZMaterial;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.OpenGLUtils;

public class MeshParserHelper {

    public XYZVertex[] iVerticesArray = null;
    public int iVertexPositionIndex = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int[] iIndexDrawOrder = null;
    public int iIndexDrawOrderIndex = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int GL_FORM_TYPE = GLES30.GL_TRIANGLES;
    public XYZMaterial iMaterial = null;


    public void reset(){
        this.iVerticesArray = null;
        this.iIndexDrawOrder = null;
        this.GL_FORM_TYPE = GLES30.GL_TRIANGLES;
        this.iMaterial = null;
        this.iVertexPositionIndex = OpenGLUtils.INVALID_UNSIGNED_VALUE;
        iIndexDrawOrderIndex = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    }
}
