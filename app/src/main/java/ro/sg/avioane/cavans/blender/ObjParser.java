/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.blender;

import android.content.Context;

import androidx.annotation.RawRes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Scanner;

import ro.sg.avioane.BuildConfig;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.geometry.XYZTexture;
import ro.sg.avioane.geometry.XYZVertex;
import ro.sg.avioane.util.TextureUtils;

public class ObjParser extends AbstractObjParser{

    private static final String OBJ_COMMENT = "#";
    private static final String OBJ_IGNORED_ELEMENT_1 = "o ";
    private static final String OBJ_VERTEX_ARRAY = "v ";
    private static final String OBJ_VERTEX_TEXTURE = "vt ";
    private static final String OBJ_VERTEX_NORMAL = "vn ";
    private static final String OBJ_FACE = "f ";
    private static final String OBJ_MTL_FILE = "mtllib";
    private static final String OBJ_MTL_USE = "usemtl";

    private XYZCoordinate[] iCoordinatesArr = null;
    private XYZCoordinate[] iNormalsArr = null;
    private XYZTexture[] iTexturesArr = null;
    private MaterialParser iMaterialParser = null;



    /**
     * get an OBJ file and return an instance of BlenderObjCavan loaded with the data.
     * Warning: costly operation! Do this at the load time!
     * @param context
     * @param objFileName an OBJ file inside the folder assets/obj
     * @return
     */
    public ParsedObjBlender parseOBJ(final Context context, final String objFileName) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.getAssets().open("obj/" + objFileName)), 1024*20);

        final LinkedList<XYZCoordinate> coordinatesList = new LinkedList<XYZCoordinate>();
        final LinkedList<XYZCoordinate> normalsList = new LinkedList<XYZCoordinate>();
        final LinkedList<XYZTexture> texturesList = new LinkedList<XYZTexture>();
        final LinkedList<XYZVertex> verticesList = new LinkedList<XYZVertex>();

        String textLine = reader.readLine();

        while (textLine != null) {
            parseOBJLine(context, textLine, coordinatesList, normalsList, texturesList, verticesList);
            textLine = reader.readLine();
        }
        final ParsedObjBlender result = new ParsedObjBlender();
        result.vertexArray = new XYZVertex[verticesList.size()];
        this.listToArray(result.vertexArray, verticesList);
        result.drawOrderArray = new short[result.vertexArray.length];
        this.fillShortArray(result.drawOrderArray);
        reader.close();
        return result;
    }

    /**
     *
     * @param textLine
     * @param coordinatesList
     * @param normalsList
     * @param texturesList
     * @param verticesList
     */
    private void parseOBJLine(final Context context,
                              String textLine,
                              final LinkedList<XYZCoordinate> coordinatesList,
                              final LinkedList<XYZCoordinate> normalsList,
                              final LinkedList<XYZTexture> texturesList,
                              final LinkedList<XYZVertex> verticesList) throws IOException {
        if(!textLine.startsWith(OBJ_COMMENT) &&
                !textLine.startsWith(OBJ_IGNORED_ELEMENT_1)) {
            if (textLine.startsWith(OBJ_VERTEX_ARRAY)) {
                final int NO_OF_ELEMENTS = 3;
                final float[] tmp = super.textToFloatArray(OBJ_VERTEX_ARRAY, textLine, NO_OF_ELEMENTS);
                coordinatesList.addLast(new XYZCoordinate(tmp));
            } else if (textLine.startsWith(OBJ_VERTEX_TEXTURE)) {
                final int NO_OF_ELEMENTS = 2;
                final float[] tmp = super.textToFloatArray(OBJ_VERTEX_TEXTURE, textLine, NO_OF_ELEMENTS);
                texturesList.addLast(new XYZTexture(tmp));
            } else if (textLine.startsWith(OBJ_VERTEX_NORMAL)) {
                final int NO_OF_ELEMENTS = 3;
                final float[] tmp = super.textToFloatArray(OBJ_VERTEX_NORMAL, textLine, NO_OF_ELEMENTS);
                normalsList.addLast(new XYZCoordinate(tmp));
            } else if(textLine.startsWith(OBJ_MTL_FILE)){
                if(this.iMaterialParser == null)
                    this.iMaterialParser = new MaterialParser();
                final String fileName = textLine.substring(OBJ_MTL_FILE.length() + 1);
                this.iMaterialParser.parseFile(context, fileName);
            } else if(textLine.startsWith(OBJ_MTL_USE)){
                this.iMaterialParser.setCurrentMaterial(textLine.substring(OBJ_MTL_USE.length() + 1));
            } else if (textLine.startsWith(OBJ_FACE)) {
                if(this.iCoordinatesArr == null){
                    this.iCoordinatesArr = new XYZCoordinate[coordinatesList.size()];
                    super.listToArray(this.iCoordinatesArr, coordinatesList);
                    this.iTexturesArr = new XYZTexture[texturesList.size()];
                    super.listToArray(this.iTexturesArr, texturesList);
                    this.iNormalsArr = new XYZCoordinate[normalsList.size()];
                    super.listToArray(this.iNormalsArr, normalsList);

                }
                textLine = textLine.substring(textLine.indexOf(OBJ_FACE) +
                        OBJ_FACE.length());
                final Scanner sc = new Scanner(textLine).useDelimiter(" ");
                while(sc.hasNext()){
                    final String faceText = sc.next();
                    try{
                        //vertex/texture/normal
                        final XYZVertex vertex = this.parseOBJFace(context, faceText);
                        verticesList.addLast(vertex);
                    } catch(ParseException pe){
                        pe.printStackTrace();
                        System.out.println("failed line=" + textLine);
                    }
                }
            } else if (textLine.trim().length() > 0) {
                final int pos = textLine.indexOf(" ");
                if (pos > -1) {
                    System.out.println("WARNING!\nunknown obj token:\n'" +
                            textLine.substring(0, pos) + "'");
                } else {
                    System.out.println("WARNING!\nline not parsed!\n" + textLine);
                }
            } else {
                System.out.println("WARNING!\nline not parsed!\n" + textLine);
            }
        }
    }

    /**
     * @param context
     * @param faceText the face text to be decoded.
     * @return an XYZVertex representing the point of the face.
     * @throws NullPointerException if the line cannot be parsed.
     * @throws IndexOutOfBoundsException if the line parsing is not anymore possible.
     * @throws IOException
     * @throws ParseException
     */
    private XYZVertex parseOBJFace(final Context context, final String faceText) throws ParseException, IOException {

        int vertexIndex = -1;
        int textureIndex = -1;
        int normalIndex = -1;

        final Scanner scTmp = new Scanner(faceText).useDelimiter("/");
        int indexSC = 0;
        while(scTmp.hasNext()) {
            final int val = scTmp.nextInt() - 1; //index start to 1, not from 0
            switch (indexSC) {
                case 0: {//vertex
                    vertexIndex = val;
                    if(BuildConfig.DEBUG && val > Short.MAX_VALUE)
                        throw new AssertionError("This shall never happen! overflow issue:" + val);
                }
                break;
                case 1: {//texture
                    textureIndex = val;
                }
                break;
                case 2: {//normal
                    normalIndex = val;
                }
                break;
                default: {
                    throw new IndexOutOfBoundsException("scanner face index=" + indexSC);
                }
            }
            indexSC++;
        }
        if(indexSC < 3) {
            throw new ParseException("unable to parse the text!", 0);
        } else {
            final XYZVertex vertex = new XYZVertex(
                    new XYZCoordinate(this.iCoordinatesArr[vertexIndex].asArray()));
            if(normalIndex > -1)
                vertex.normal = new XYZCoordinate(this.iNormalsArr[normalIndex].asArray());


            if(textureIndex > -1)
                vertex.texture = new XYZTexture(this.iTexturesArr[textureIndex].u(),
                        this.iTexturesArr[textureIndex].v(),
                        TextureUtils.getInstance().addTextureFromAssets(context,
                        this.iMaterialParser.getCurrentMaterial().map_Kd));


            return vertex;
        }
    }


    private short[] fillShortArray(final short[] arrI){
        for(int i=0; i<arrI.length; i++)
            arrI[i] = (short)i;

        return arrI;
    }
}
