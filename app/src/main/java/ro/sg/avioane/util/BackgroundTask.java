/*
 * Copyright (c) 2021.
 * By using this source code from this project/file you agree with the therms listed at
 * https://github.com/george2209/PlanesAndShips/blob/main/LICENSE
 */

package ro.sg.avioane.util;

import android.app.Activity;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BackgroundTask {
    private final Activity iParentActivity;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public BackgroundTask(final Activity activity){
        this.iParentActivity = activity;
    }

    public void start(){
        this.preloadData();
        this.isRunning.set(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean keepMoving = true;
                while(keepMoving && isRunning.get()){
                    keepMoving = runInBackground();
                    try {
                        Thread.sleep(5);
                    }catch(InterruptedException iex){
                        iex.printStackTrace();
                    }
                }
                iParentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyThreadFinished();
                    }
                });
            }
        }).start();
    }

    public void stop(){
        this.isRunning.set(false);
    }

    /**
     * here you will load all data BEFORE calling <code>my.BackgroundThread.start()</code> method
     */
    public abstract void preloadData();

    /**
     * This method is called inside the thread loop as long as you will return true.
     * @return if you return true this method will be keep the thread running.
     */
    public abstract boolean runInBackground();

    /**
     * the method is signaling on the GUI Thread that the work was finished.
     */
    public abstract void notifyThreadFinished();
}
