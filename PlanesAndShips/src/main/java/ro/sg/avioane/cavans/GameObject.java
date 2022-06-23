/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans;

import ro.sg.avioane.cavans.util.GameObjectArray;

public class GameObject extends GameObjectArray<GameObjectComponent> {
    protected final String iObjName;

    /**
     *
     * @param objName the name of this object. Not sure if it has any practicability in production
     */
    public GameObject(final String objName) {
        this.iObjName = objName;
    }

    /**
     * @return the name of the object as it comes from Blender or as it was set at the initialize
     * time (in case of objects not imported from Blender)
     */
    public String getObjectName() {
        return this.iObjName;
    }

    /**
     *
     * @param viewMatrix the view matrix
     * @param projectionMatrix the projection matrix
     */
    public void draw(float[] viewMatrix, float[] projectionMatrix) {
        for(int i=0; i<super.size(); i++)
        {
            super.getComponentAt(i).draw(viewMatrix, projectionMatrix);
        }

    }

    /**
     * call this method on the OpenGL thread to restore the objects
     */
    public void onRestore() {
        for(int i=0; i<super.size(); i++)
        {
            super.getComponentAt(i).onRestore();
        }
    }

    /**
     * call this method on the OpenGL thread to restore the objects
     */
    public void destroy(){
        for(int i=0; i<super.size(); i++)
        {
            super.getComponentAt(i).destroy();
        }
    }
}
