package ro.sg.avioane.geometry;

/**
 * A structure that encapsulate the X,Y,Z coordinates as Float.
 */
public class XYZCoordinate {
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;
    public XYZColor color = null; //opens the possibility to set the color per vertex

    public XYZCoordinate(){

    }

    public XYZCoordinate(final float[] arr){
        this.x = arr[0];
        this.y = arr[1];
        this.z = arr[2];
    }

    public XYZCoordinate(final float x, final float y, final float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     *
     * @return a float array with {x,y,z} position
     */
    public float[] asArray(){
        return new float[] {x,y,z};
    }
}
