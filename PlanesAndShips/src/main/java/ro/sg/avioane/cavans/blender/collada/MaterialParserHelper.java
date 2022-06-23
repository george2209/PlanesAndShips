/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.blender.collada;

import android.content.Context;

import java.io.IOException;
import java.util.HashSet;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZMaterial;
import ro.sg.avioane.util.OpenGLUtils;
import ro.sg.avioane.util.TextureUtils;

public class MaterialParserHelper {

    private XYZMaterial[] iMaterialsArray = null;
    private XYZMaterial iCurrMaterial = null;
    private int iArrayIndex = OpenGLUtils.INVALID_UNSIGNED_VALUE;
    private HashSet<String> iMaterialNames = null;

    /**
     *
     * @param materialNo
     */
    public void setNoOfMaterials(final int materialNo){
        if(BuildConfig.DEBUG && (iMaterialsArray != null) || (materialNo < 1))
            throw new AssertionError("materials container already build?");

        this.iMaterialNames = new HashSet<>(materialNo);
        this.iMaterialsArray = new XYZMaterial[materialNo];
        this.iArrayIndex = 0;
    }

    /***
     * add a new material into the materials array
     * @param materialName the name of the new material
     */
    public void addNewMaterial(final String materialName){
        assert (iMaterialNames.contains(materialName) == false);
        iMaterialNames.add(materialName);
        iCurrMaterial = new XYZMaterial(materialName);
        this.iMaterialsArray[this.iArrayIndex] = this.iCurrMaterial;
        this.iArrayIndex++;
    }

    /**
     *
     * @return true if the capacity is fully used.
     */
    public boolean isMaterialsFullLoaded(){
        assert (this.iArrayIndex <= this.iMaterialsArray.length);
        return (this.iArrayIndex == this.iMaterialsArray.length);
    }

    /**
     *
     * @param ambientColor the RGBA color
     */
    public void setMaterialAmbientColor(final XYZColor ambientColor){
        this.iCurrMaterial.iAmbientMaterialColor = ambientColor;
    }

    /**
     *
     * @param diffuseColor the RGBA color
     */
    public  void setMaterialDiffuseColor(final XYZColor diffuseColor){
        this.iCurrMaterial.iDiffuseMaterialColor = diffuseColor;
    }

    /**
     *
     * @param fileName
     * @param context
     * @throws IOException
     */
    public void setMaterialAmbientTexture(final String fileName, final Context context) throws IOException {
        this.iCurrMaterial.iAmbientFileNameID =
                TextureUtils.getInstance().addTextureFromAssets(context, fileName);
    }

    /**
     *
     * @param fileName
     * @param context
     * @throws IOException
     */
    public void setMaterialDiffuseTexture(final String fileName, final Context context) throws IOException {
        this.iCurrMaterial.iDiffuseFileNameID =
                TextureUtils.getInstance().addTextureFromAssets(context, fileName);
    }

    /***
     *
     * @param materialIndex
     * @return the material from the respective index
     */
    public XYZMaterial getMaterial(final int materialIndex){
        assert (materialIndex >=0 && materialIndex<this.iArrayIndex);
        return this.iMaterialsArray[materialIndex];
    }
}
