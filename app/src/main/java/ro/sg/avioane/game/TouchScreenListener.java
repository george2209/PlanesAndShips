/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.game;

public interface TouchScreenListener {
    public void fireMovement(final float xPercent, final float zPercent);
    public void fireTouchClick(final float x, final float y);
}
