/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.blender;

import java.util.LinkedList;
import java.util.Scanner;

import ro.sg.avioane.BuildConfig;

public class AbstractObjParser {

    /**
     * transform a text line like <code>1.000000 1.000000 1.000000</code>
     * into a float array with the respective values inside by keeping the order of the elements.
     * @param strElement
     * @param textLine
     * @param arraySize
     * @return an array with the elements or null in case the parsing failed.
     */
    protected float[] textToFloatArray(final String strElement,
                                       String textLine,
                                       final int arraySize) {
        textLine = textLine.substring(textLine.indexOf(strElement) +
                strElement.length());
        final Scanner sc = new Scanner(textLine).useDelimiter(" ");
        final float[] tmp = new float[arraySize];
        int i=0;
        while(sc.hasNext()) {
            tmp[i++] = Float.parseFloat(sc.next());
        }
        if(arraySize != tmp.length) {
            if(BuildConfig.DEBUG)
                throw new AssertionError("parsing failed!");
            else
                return null;
        }
        return tmp;
    }

    /**
     * extract the float value that is present after the strElement
     * @param strElement
     * @param textLine
     * @return
     */
    protected float textToFloatValue(final String strElement,
                                     String textLine){
        textLine = textLine.substring(textLine.indexOf(strElement) +
                strElement.length() + 1);
        return Float.parseFloat(textLine);
    }

    /**
     * extract the short value that is present after the strElement
     * @param strElement
     * @param textLine
     * @return
     */
    protected short textToShortValue(final String strElement,
                                     String textLine){
        textLine = textLine.substring(textLine.indexOf(strElement) +
                strElement.length() + 1);
        return Short.parseShort(textLine);
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
    protected Object[] listToArray(final Object[] vertexArr, final LinkedList<?> theList) {
        int lstIndex = 0;
        while(theList.size() > 0){
            vertexArr[lstIndex++] = theList.pollFirst();
        }
        return vertexArr;
    }
}
