package ro.sg.avioane.geometry;

public class XYZColor {
    public float red = 0.0f;
    public float green = 0.0f;
    public float blue = 0.0f;
    public float alpha = 0.0f;

    public XYZColor(){

    }

    public XYZColor(final float red, final float green, final float blue, final float alpha){
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public float[] asFloatArray(){
        return new float[]{this.red, this.green, this.blue, this.alpha};
    }
}
