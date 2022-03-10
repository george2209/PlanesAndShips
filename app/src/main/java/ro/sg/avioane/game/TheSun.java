/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game;

import ro.sg.avioane.geometry.XYZColor;
import ro.sg.avioane.geometry.XYZCoordinate;
import ro.sg.avioane.lighting.AmbientLight;
import ro.sg.avioane.lighting.DiffuseLight;
import ro.sg.avioane.util.MathGL.MathGLUtils;
import ro.sg.avioane.util.MathGL.Vector;

/**
 * a Singleton implementation of the Sun.
 * There will be only one Sun (or at least at this moment) into the game`s engine.
 * The Sun will have:
 *      1. a position (that shall be far away from the game`s plane).
 *      This is needed to calculate the Sun light vector direction for the Diffuse light
 *          Initial position of the Sun is .... TBD..
 *          TODO: find initial sun position
 *          TODO: implement Sun "movement" along X (linear or better a quadratic function) to simulate
 *          a day-night time.
 *      2. a light color (used by both Ambient and Diffuse light)
 *      3. a light strength from 0.0f .. 1.0f. Default is 1.0f;
 *
 */
public class TheSun {

    private static final TheSun instance = new TheSun();
    private final DiffuseLight iDiffuseLight = DiffuseLight.getStaticInstance();
    private final AmbientLight iAmbientLight = AmbientLight.getStaticInstance();
    private final XYZColor iSunColor = new XYZColor(0.97f, 0.95f, 0.90f, 1.0f);
    private final XYZCoordinate iSunPosition = new XYZCoordinate(0.0f,0.0f, 50.0f);



    private TheSun(){
        //a se seta parametrii
        iAmbientLight.setAmbientLightColor(this.iSunColor);
        iAmbientLight.setAmbientColorStrength(1.0f);
        iDiffuseLight.setDiffuseLightColor(this.iSunColor);
        this.doCalculateSunDirection();
    }

    public static synchronized TheSun getStaticInstance(){
        return instance;
    }

    /**
     * The access to the method is NOT synchronized.
     * @return a reference to the Ambient Light object.
     */
    public AmbientLight getAmbientLight(){
        return this.iAmbientLight;
    }

    /**
     * The access to the method is NOT synchronized.
     * @return a reference to the Diffuse Light object.
     */
    public DiffuseLight getDiffuseLight(){
        return this.iDiffuseLight;
    }

    /**
     * call this method to change at runtime the sun color and alpha.
     * Note:
     * this method must be called from the main OpenGL thread as the parameters are not thread safe!
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public void setSunColor(final float red, final float green, final float blue, final float alpha){
        this.iSunColor.setRed(red);
        this.iSunColor.setGreen(green);
        this.iSunColor.setBlue(blue);
        this.iSunColor.setAlpha(alpha);
    }

    /**
     * @param sunPosition as world-coordinate
     */
    public void setSunPosition(final XYZCoordinate sunPosition){
        this.iSunPosition.setY(sunPosition.x());
        this.iSunPosition.setY(sunPosition.y());
        this.iSunPosition.setZ(sunPosition.z());
        this.doCalculateSunDirection();
    }

    /**
     *
     * @return the light direction as XYZCoordinate
     */
    public XYZCoordinate getSunLightDirection(){
        return this.iDiffuseLight.getDiffuseLightDirection();
    }

    /**
     *
     * @return sun position as world-coordinate
     */
    public final XYZCoordinate getSunPosition(){
        return this.iSunPosition;
    }

    /**
     * calculate the sun light direction against the world coordinate 0,0,0
     * The idea is that the sun is far away so we can approximate the direction to be relatively
     * constant over the complete game map.
     * Warning!
     * In case the sun is <I>too close</I> then this method will not properly work!
     *
     * Note: normally the calculus is to be done like this:
     * <code>vec3 lightDir = normalize(light.position - FragPos);</code>
     * As the "FragPos" is considered into this method the origin of the world then it will be:
     * <code>vec3 lightDir = normalize(light.position);</code>
     */
    private void doCalculateSunDirection(){
        this.iDiffuseLight.setDiffuseLightDirection(
                new XYZCoordinate(Vector.normalize(this.iSunPosition.asArray())
        ));
    }

}
