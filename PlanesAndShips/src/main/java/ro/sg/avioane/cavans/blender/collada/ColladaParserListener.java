/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.cavans.blender.collada;

import ro.sg.avioane.cavans.GameObject;

public interface ColladaParserListener {

    /**
     *
     * @param gameObjects the parsed game object.
     */
    void notifyParseFinished(GameObject[] gameObjects);

    /**
     * notification in case of failure.
     */
    void notifyParseFailed();
}
