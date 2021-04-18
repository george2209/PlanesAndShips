package ro.sg.avioane.geometry;

/**
 * A structure that encapsulate the X,Y,Z coordinates as Float.
 */
public class XYZCoordinate {
    private final float iCoordinateArray[] = new float[3];

    public XYZColor color = null; //opens the possibility to set the color per vertex

    public XYZCoordinate(){
        this.iCoordinateArray[0] = 0;
        this.iCoordinateArray[1] = 0;
        this.iCoordinateArray[2] = 0;
    }

    public XYZCoordinate(final float[] arr){
        for(int i=0; i<3;i++)
            this.iCoordinateArray[i] = arr[i];
    }

    public XYZCoordinate(final float x, final float y, final float z){
        this.iCoordinateArray[0] = x;
        this.iCoordinateArray[1] = y;
        this.iCoordinateArray[2] = z;
    }

    public void setX(final float x){
        this.iCoordinateArray[0] = x;
    }

    public void setY(final float y){
        this.iCoordinateArray[1] = y;
    }

    public void setZ(final float z){
        this.iCoordinateArray[2] = z;
    }

    public float x(){
        return this.iCoordinateArray[0];
    }

    public float y(){
        return this.iCoordinateArray[1];
    }

    public float z(){
        return this.iCoordinateArray[2];
    }

    /**
     *
     * @return a float array with {x,y,z} position
     */
    public float[] asArray(){
        return this.iCoordinateArray;
    }
}
