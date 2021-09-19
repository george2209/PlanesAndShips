/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.geometry;

import ro.sg.avioane.util.OpenGLUtils;

public class XYZMaterial {
    public String materialName = null;
    public XYZCoordinate materialKA = null;
    public XYZCoordinate materialKD = null;
    public XYZCoordinate materialKS = null;
    public XYZCoordinate materialKE = null;
    public float NS = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public float Ni = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public float d = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public byte ILLUM = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int mapKA_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int mapKD_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int mapKS_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int mapKE_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int mapNS_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int mapD_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
}
