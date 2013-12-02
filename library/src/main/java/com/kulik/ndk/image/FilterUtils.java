package com.kulik.ndk.image;

import android.graphics.Bitmap;

/**
 * Created by kulik on 12/1/13.
 */
public class FilterUtils {

    private static native void convertToGray(Bitmap color, Bitmap gray);
    private static native void fastBlurer(int radius, Bitmap color);

    public static Bitmap convertToGray(Bitmap color) {
        Bitmap gray = Bitmap.createBitmap(color.getWidth(), color.getHeight(), Bitmap.Config.ALPHA_8);
        convertToGray(color, gray);
        return gray;
    }

    public static Bitmap blurIt(Bitmap color) {
        fastBlurer(20, color);
        return color;
    }
}
