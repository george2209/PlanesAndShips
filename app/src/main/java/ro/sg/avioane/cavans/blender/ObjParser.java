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

public class ObjParser {

    private static final String OBJ_COMMENT = "#";
    private static final String OBJ_IGNORED_ELEMENT_1 = "o ";
    private static final String OBJ_VERTEX_ARRAY = "v ";
    private static final String OBJ_VERTEX_TEXTURE = "vt ";
    private static final String OBJ_VERTEX_NORMAL = "vn ";
    private static final String OBJ_FACE = "f ";

    private XYZCoordinate[] iCoordinatesArr = null;
    private XYZCoordinate[] iNormalsArr = null;
    private XYZTexture[] iTexturesArr = null;



    /**
     * get an OBJ file and return an instance of BlenderObjCavan loaded with the data.
     * Warning: costly operation! Do this at the load time!
     * @param context
     * @param id
     * @return
     */
    public ParsedObjBlender parseOBJ(Context context, @RawRes int id){
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(id)));

        try {
            final LinkedList<XYZCoordinate> coordinatesList = new LinkedList<XYZCoordinate>();
            final LinkedList<XYZCoordinate> normalsList = new LinkedList<XYZCoordinate>();
            final LinkedList<XYZTexture> texturesList = new LinkedList<XYZTexture>();
            final LinkedList<XYZVertex> verticesList = new LinkedList<XYZVertex>();

            String textLine = reader.readLine();

            while (textLine != null) {
                parseOBJLine(textLine, coordinatesList, normalsList, texturesList, verticesList);
                textLine = reader.readLine();
            }
            final ParsedObjBlender result = new ParsedObjBlender();
            result.vertexArray = new XYZVertex[verticesList.size()];
            this.listToArray(result.vertexArray, verticesList);
            result.drawOrderArray = new short[result.vertexArray.length];
            this.fillShortArray(result.drawOrderArray);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param textLine
     * @param coordinatesList
     * @param normalsList
     * @param texturesList
     * @param verticesList
     */
    private void parseOBJLine(String textLine,
                              final LinkedList<XYZCoordinate> coordinatesList,
                              final LinkedList<XYZCoordinate> normalsList,
                              final LinkedList<XYZTexture> texturesList,
                              final LinkedList<XYZVertex> verticesList) {
        if(!textLine.startsWith(OBJ_COMMENT) &&
                !textLine.startsWith(OBJ_IGNORED_ELEMENT_1)) {
            if (textLine.startsWith(OBJ_VERTEX_ARRAY)) {
                final int NO_OF_ELEMENTS = 3;
                final float[] tmp = tokenizeLine(OBJ_VERTEX_ARRAY, textLine, NO_OF_ELEMENTS);
                coordinatesList.addLast(new XYZCoordinate(tmp));
            } else if (textLine.startsWith(OBJ_VERTEX_TEXTURE)) {
                final int NO_OF_ELEMENTS = 2;
                final float[] tmp = tokenizeLine(OBJ_VERTEX_TEXTURE, textLine, NO_OF_ELEMENTS);
                //texturesList.addLast(new XYZTexture(tmp));
                if(BuildConfig.DEBUG)
                    throw new AssertionError("texture name and registration to be implemented!");
            } else if (textLine.startsWith(OBJ_VERTEX_NORMAL)) {
                final int NO_OF_ELEMENTS = 3;
                final float[] tmp = tokenizeLine(OBJ_VERTEX_NORMAL, textLine, NO_OF_ELEMENTS);
                normalsList.addLast(new XYZCoordinate(tmp));
            } else if (textLine.startsWith(OBJ_FACE)) {
                if(this.iCoordinatesArr == null){
                    this.iCoordinatesArr = new XYZCoordinate[coordinatesList.size()];
                    this.listToArray(this.iCoordinatesArr, coordinatesList);
                    this.iTexturesArr = new XYZTexture[texturesList.size()];
                    this.listToArray(this.iTexturesArr, texturesList);
                    this.iNormalsArr = new XYZCoordinate[normalsList.size()];
                    this.listToArray(this.iNormalsArr, normalsList);

                }
                textLine = textLine.substring(textLine.indexOf(OBJ_FACE) +
                        OBJ_FACE.length());
                final Scanner sc = new Scanner(textLine).useDelimiter(" ");
                while(sc.hasNext()){
                    final String faceText = sc.next();
                    try{
                        //vertex/texture/normal
                        final XYZVertex vertex = this.parseOBJFace(faceText);
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
     *
     * @param faceText the face text to be decoded.
     * @return an XYZVertex representing the point of the face.
     * @throws NullPointerException if the line cannot be parsed.
     * @throws IndexOutOfBoundsException if the line parsing is not anymore possible.
     */
    private XYZVertex parseOBJFace(final String faceText) throws ParseException {

        int vertexIndex = -1;
        int textureIndex = -1;
        int normalIndex = -1;

        final Scanner scTmp = new Scanner(faceText).useDelimiter("/");
        int indexSC = 0;
        while(scTmp.hasNext()) {
            final int val = scTmp.nextInt() - 1; //index start to 1, not to 0
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

            ////////////////////////////////////////////////////////////
//            if(textureIndex > -1)
//                vertex.texture = new XYZTexture(this.iTexturesArr[textureIndex].asArray());

            ////////////////////////////////////////////////////////////
            return vertex;
        }
    }

    /**
     *
     * @param strElement this is the starting element of an OBJ test line. It can be
     *                   OBJ_VERTEX_ARRAY, OBJ_VERTEX_TEXTURE or OBJ_VERTEX_NORMAL
     * @param textLine Example "-1.000000 1.000000 -1.000000"
     * @param NO_OF_ELEMENTS the no of elements expected to have the returned array.
     * @return from a line of floats separated by " " an array of floats with those elements.
     * @throws IndexOutOfBoundsException if the elements are more than NO_OF_ELEMENTS
     */
    private float[] tokenizeLine(String strElement, String textLine, int NO_OF_ELEMENTS) {
        textLine = textLine.substring(textLine.indexOf(strElement) +
                strElement.length());
        final Scanner sc = new Scanner(textLine).useDelimiter(" ");
        final float[] tmp = new float[NO_OF_ELEMENTS];
        int i=0;
        while(sc.hasNext()) {
            tmp[i++] = Float.parseFloat(sc.next());
        }
        return tmp;
    }

        /**
     * take the content of a LinkedList and transferring it into an array.
     * After this call the list will be of size=0 and the array will be of the initial
     * size of the list.
     * The array must be not null!
     * @param vertexArr a non null array
     * @param theList a non null list
     * @return the vertexArr with the populated elements inside starting from 0 index.
     */
    private Object[] listToArray(final Object[] vertexArr, final LinkedList<?> theList) {
        int lstIndex = 0;
        while(theList.size() > 0){
            vertexArr[lstIndex++] = theList.pollFirst();
        }
        return vertexArr;
    }

    private short[] fillShortArray(final short[] arrI){
        for(int i=0; i<arrI.length; i++)
            arrI[i] = (short)i;

        return arrI;
    }
}
