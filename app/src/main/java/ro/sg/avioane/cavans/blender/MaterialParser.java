/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.blender;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZColor;

public class MaterialParser extends AbstractObjParser {

    private static final String MTL_newmtl = "newmtl";
    private static final String MTL_Ka = "Ka";
    private static final String MTL_Kd = "Kd";
    private static final String MTL_Ks = "Ks";
    private static final String MTL_Ns = "Ns";
    private static final String MTL_Ni = "Ni";
    private static final String MTL_d = "d";
    private static final String MTL_illum = "illum";
    private static final String MTL_map_Kd = "map_Kd";

    private final HashMap<String, MaterialElement> iMaterialList = new HashMap<String, MaterialElement>();
    private MaterialElement iCurrentMaterial = null;

    /**
     * Details on behalf of
     * https://www.loc.gov/preservation/digital/formats/fdd/fdd000508.shtml
     * http://paulbourke.net/dataformats/mtl/
     */
    public static class MaterialElement{
        public String name = null; //material name
        public XYZColor Ka = null; //ambient color
        public XYZColor Kd = null; //diffuse color
        public XYZColor Ks = null; //specular color
        public float Ns = 0.0f; //focus of specular highlight
        public float Ni = 0.0f; //optical density; index of refraction
        public float d = 0.0f; //specifies a factor for dissolve; 1->Opaque, 0=Transparent
        public short illum = 1; //can be a number from 0 to 10. see http://paulbourke.net/dataformats/mtl/
        public String map_Kd = null;
    }

    /**
     *
     * @param materialName
     * @return true if the current material was found inside this parser list of materials
     */
    public boolean setCurrentMaterial(final String materialName){
        this.iCurrentMaterial = this.iMaterialList.get(materialName);
        if(BuildConfig.DEBUG && this.iCurrentMaterial == null)
            throw new AssertionError("material not found! " + materialName);
        return (this.iCurrentMaterial != null);
    }

    /**
     *
     * @return
     */
    public MaterialElement getCurrentMaterial(){
        return this.iCurrentMaterial;
    }

    /**
     * will parse a material file to be used as a part of the OBJ parser.
     * @param fileName
     */
    public void parseFile(final Context context, final String fileName) throws IOException {
        if(this.isFile(context, fileName)){
            final BufferedReader reader = new BufferedReader(new InputStreamReader(
                    context.getAssets().open("obj/" + fileName)));

            String textLine = reader.readLine();
            while(textLine != null){
                if(textLine.length() > 0 && !textLine.startsWith("#"))
                    this.parseLine(textLine);
                textLine = reader.readLine();
            }

            reader.close();
        } else {
            throw new IOException("file not found inside obj:" + fileName);
        }
    }

    /**
     *
     * @param lineText
     */
    private void parseLine(final String lineText){
        if(lineText.startsWith(MTL_newmtl)){
            final String line = lineText.substring(lineText.indexOf(MTL_newmtl) +
                    MTL_newmtl.length() + 1); //1 for space
            this.iCurrentMaterial = new MaterialElement();
            this.iCurrentMaterial.name = line;
            this.iMaterialList.put(this.iCurrentMaterial.name, this.iCurrentMaterial);
        } else if(lineText.startsWith(MTL_Ka)){
            this.iCurrentMaterial.Ka = new XYZColor(super.textToFloatArray(MTL_Ka, lineText, 3));
        } else if(lineText.startsWith(MTL_Kd)){
            this.iCurrentMaterial.Kd = new XYZColor(super.textToFloatArray(MTL_Kd, lineText, 3));
        } else if(lineText.startsWith(MTL_Ks)){
            this.iCurrentMaterial.Ks = new XYZColor(super.textToFloatArray(MTL_Ks, lineText, 3));
        } else if(lineText.startsWith(MTL_Ns)){
            this.iCurrentMaterial.Ns = super.textToFloatValue(MTL_Ns, lineText);
        } else if(lineText.startsWith(MTL_Ni)){
            this.iCurrentMaterial.Ni = super.textToFloatValue(MTL_Ni, lineText);
        } else if(lineText.startsWith(MTL_d)){
            this.iCurrentMaterial.d = super.textToFloatValue(MTL_d, lineText);
        } else if(lineText.startsWith(MTL_illum)){
            this.iCurrentMaterial.illum = super.textToShortValue(MTL_illum, lineText);
        } else if(lineText.startsWith(MTL_map_Kd)){
            String textureFileName = null;
            if(lineText.lastIndexOf("\\") > -1)
                this.iCurrentMaterial.map_Kd = lineText.substring(lineText.lastIndexOf("\\") + 1);
            else
                this.iCurrentMaterial.map_Kd = lineText.substring(MTL_map_Kd.length() + 2);
        } else {
            System.out.println("WARNING: unprocessed MTL tag/line: " + lineText);
        }
    }

    /**
     *
     * @param fileName
     * @return true if the file is inside assets/obj folder
     */
    private boolean isFile(final Context context, final String fileName) throws IOException{
        final String files[] = context.getAssets().list("obj");
        for (String f : files) {
            if (f.equals(fileName))
                return true;
        }
        return false;
    }
}
