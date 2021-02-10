package ro.sg.avioane.geometry;

/**
 * A structure that encapsulate the X,Y,Z coordinates as Float.
 */
public class XYZCoordinate {
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;

    public XYZCoordinate(){

    }

    public XYZCoordinate(final float x, final float y, final float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
