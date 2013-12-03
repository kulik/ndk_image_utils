package com.kulik.ndk.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

/**
 * Created by konstantin on 11/4/13.
 */
public class ImageUtils {
    public static Bitmap makeViewScreenshot(View view) {
        Bitmap viewBmp = null;
        if (view.getWidth() > 0 && view.getHeight() >0) {
            viewBmp = Bitmap.createBitmap(
                    view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(viewBmp);
            view.draw(canvas);
        }
        return viewBmp;
//        return view.getDrawingCache();
    }
}
