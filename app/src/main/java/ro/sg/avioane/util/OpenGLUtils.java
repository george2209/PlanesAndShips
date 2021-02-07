package ro.sg.avioane.util;

import android.content.Context;

import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

import java.io.InputStream;

public class OpenGLUtils {

    /**
     *
     * @param context the application context
     * @param objID the object resource
     * @param mtlID the object material file. It can be null.
     * @return null in case the Object3D cannot be build
     */
    public static Object3D loadModel(Context context, int objID, int mtlID) {
        InputStream inObj;
        InputStream inMtl;
        try {
            inObj = context.getResources().openRawResource(objID);//(R.raw.pine_tree_obj);
            inMtl = context.getResources().openRawResource(mtlID);//(R.raw.pine_tree_mtl);
            return getModel(inObj, inMtl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object3D loadModel(Context context, int objID) {
        InputStream inObj;
        try {
            inObj = context.getResources().openRawResource(objID);//(R.raw.pine_tree_obj);
            return getModel(inObj, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object3D getModel(InputStream inObj, InputStream inMtl) {
        Object3D[] model = Loader.loadOBJ(inObj, inMtl, 1.0f);
        Object3D o3d = new Object3D(0);
        for (final Object3D temp:model) {
            temp.setCenter(SimpleVector.ORIGIN);
            temp.rotateX((float) (-Math.PI));
            temp.rotateMesh();
            temp.setRotationMatrix(new Matrix());
            o3d = Object3D.mergeObjects(o3d, temp);
            o3d.build();
        }
        return o3d;
    }
}
