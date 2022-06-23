/*
 * Copyright (c) 2022.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.gdi.cavans.util;


/**
 * this class is using the Prototype Pattern for storing objects into an array with an known size
 * at the creation time of its instance.
 */
public class GameObjectArray<T> {
    private T[] iArray = null;
    private int iNextFreeIndex = 0;

    /**
     *
     * @param arr the initial array to be encapsulated here
     */
    public void initiateObjectsArray(T[] arr){
        assert (arr != null);
        this.iArray = arr;
    }

    /**
     * @param element
     */
    public void addGameObjectComponent(final T element){
        this.iArray[iNextFreeIndex++] = element;
    }

    /**
     *
     * @param index
     * @return
     */
    public T getComponentAt(final int index){
        assert (index >= 0 && index < iNextFreeIndex);
        return this.iArray[index];
    }

    /**
     *
     * @return last inserted component
     */
    public T getLastComponent(){
        return this.iArray[iNextFreeIndex-1];
    }

    /**
     *
     * @return
     */
    public int size(){
        return this.iNextFreeIndex;
    }

    /**
     *
     * @return true if all elements are filled
     */
    public boolean isFull(){
        return this.iNextFreeIndex == this.iArray.length;
    }


}
