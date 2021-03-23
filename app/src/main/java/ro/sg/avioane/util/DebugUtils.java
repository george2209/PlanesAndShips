/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import ro.sg.avioane.BuildConfig;

public class DebugUtils {

    public static void printSymmetricalMatrix(final float[] matrix, final String matrixName){
        System.out.println(matrixName + System.getProperty("line.separator"));

        final int matrixRowLength = (int) Math.sqrt(matrix.length);

        //assume a AxA symmetrical matrix and NOT AxB!
        if (BuildConfig.DEBUG &&
                ((Math.sqrt(matrix.length)) * Math.sqrt(matrix.length)) != (double)matrix.length) {
            throw new AssertionError("a symmetrical matrix was expected! length=" + matrix.length);
        }

        for(int i=0; i<matrixRowLength; i++){
            for(int j=0; j<matrixRowLength; j++){
                String separator = ", ";
                if(j==matrixRowLength-1)
                    separator = "\n";
                System.out.print(matrix[i*matrixRowLength + j] + separator);
            }
        }
    }

    public static void printVector(final float[] vector, final String vectorName){
        System.out.println(vectorName + System.getProperty("line.separator"));

        for(int j=0; j<vector.length; j++){
            String separator = ", ";
            if(j==vector.length-1)
                separator = "\n";
            System.out.print(vector[j] + separator);
        }
    }
}
