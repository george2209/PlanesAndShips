/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.geometry;

import java.util.Optional;

import ro.sg.avioane.util.OpenGLUtils;

public class XYZMaterial {
    /**
     * globalBackgroundColor is only used in case there is no texture nor a color defined per vertex.
     * Consider this parameter as an implicit background color that will be ignored if there is either
     * a material defined for the respective object or, in case the material is also missing however
     * there is a "color per vertex" inside VBO defined.
     * See project documentation "background color" for more information
     */
    public XYZColor globalBackgroundColor = null; //new XYZColor(0.9f, 0.2f, 0.1f, XYZColor.OPAQUE);

    public String materialName = null;

    /**
     * Specular reflection coefficient
     */
    public XYZCoordinate constantKS = null;

    /**
     * Emissivity  reflection coefficient
     */
    public XYZCoordinate constantKE = null;

    public float NS = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public float Ni = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public float d = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public byte ILLUM = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int mapKA_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int mapKD_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int mapKS_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public int mapKE_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;

    /**
     * Specular exponent of the material.
     * During rendering, the map_Ns value is multiplied by the Ns value.
     */
    public int mapNS_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;

    public int mapD_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;

    public int map_Bump_FileNameID = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    public float map_Bump_BM_Param = 1.0f;


    /////////////////////////////AMBIENT LIGHT//////////////////////////////////
    /////////////////////////////constant KA////////////////////////////////////

    /**
     * Ambient reflection coefficient of the material
     */
    private XYZCoordinate constantKA = null;

    public Optional<XYZCoordinate> getConstantKA(){
        return Optional.ofNullable(this.constantKA);
    }

    public void setConstantKA(final XYZCoordinate o){
        this.constantKA = o;
    }

    /////////////////////////////DIFFUSE PROPERTIES/////////////////////////////
    /////////////////////////////constant KD////////////////////////////////////

    /**
     *  Diffuse reflection coefficient (* default)
     */
    private XYZCoordinate constantKD = null;

    public Optional<XYZCoordinate> getConstantKD(){
        return Optional.ofNullable(this.constantKD);
    }

    public void setConstantKD(final XYZCoordinate o){
        this.constantKD = o;
    }
}
