package com.kulik.ndk.image;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.kulik.ndk.image.FilterUtils;

/**
 * Created by kulik on 12/1/13.
 */
public class NDKBlurer implements IBlurer {
    private static final String TAG = NDKBlurer.class.getName();

    static {
        System.loadLibrary("bitmaputils");
    }

    private Bitmap mBluredBitmap;
    private ImageView mContainer;
    private View mBlurView;
    private ViewTreeObserver.OnGlobalLayoutListener mOnViewAvailableListener;
    private AsyncTask<Bitmap, Void, Bitmap> mBlurTask;

    public NDKBlurer() {
        mOnViewAvailableListener = new OnViewAvailableListener();
    }

    @Override
    public void setViewToBlur(View viewToBlur) {
        mBlurView = viewToBlur;
        if (mBlurView != null) {
            if (mBlurView.isShown()) {
                startBluring();
            } else {
                mBlurView.getViewTreeObserver().addOnGlobalLayoutListener(mOnViewAvailableListener);
            }
        }
    }

    @Override
    public void startBluring() {
        Bitmap bitmap = ImageUtils.makeViewScreenshot(mBlurView);
        if (mBlurTask != null) {
            mBlurTask.cancel(true);
        }
        mBlurTask = new BlurAsyncTask().execute(bitmap);
    }

    @Override
    public void setContainerImageView(ImageView dstImage) {
        mContainer = dstImage;
    }

    private void onBitmapBlured(Bitmap bitmap) {
        Log.v(TAG, "onBitmapBlured");
        if (mBluredBitmap != null) {
            mContainer.setImageBitmap(null);
            if (!mBluredBitmap.isRecycled()) {
                mBluredBitmap.recycle();
            }
        }
        mBluredBitmap = bitmap;
        if (mContainer != null) {
            // maybe need to check that view has attached
            mContainer.setImageBitmap(mBluredBitmap);
        }
    }


    private class OnViewAvailableListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @SuppressWarnings("deprecation")
        @Override
        public void onGlobalLayout() {
            if (mBlurView != null) {
                startBluring();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mBlurView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mBlurView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        }
    }


    protected class BlurAsyncTask extends AsyncTask<Bitmap, Void, Bitmap> implements CancelableCallback {
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
//            Bitmap dest = Bitmap.createScaledBitmap(params[0], params[0].getWidth(), params[0].getHeight(), true);

//            Bitmap res = FilterUtils.convertToGray(dest);
            Bitmap res = FilterUtils.blurIt(params[0]);

            return params[0];//res;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            onBitmapBlured(bitmap);
        }

        @Override
        public boolean isCancel() {
            return isCancelled();
        }
    }

    public static interface CancelableCallback {
        boolean isCancel();
    }

}
