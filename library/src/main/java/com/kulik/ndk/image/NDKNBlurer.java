package com.kulik.ndk.image;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by kulik on 12/1/13.
 */
public class NDKNBlurer implements IBlurer {
    private static final String TAG = NDKNBlurer.class.getName();

    static {
        System.loadLibrary("bitmaputils");
    }

    private Bitmap mBluredBitmap;
    private ImageView mContainer;
    private View mBlurView;
    private ViewTreeObserver.OnGlobalLayoutListener mOnViewAvailableListener;
    private AsyncTask<Bitmap, Void, Bitmap> mBlurTask;

    public NDKNBlurer() {
        mOnViewAvailableListener = new OnViewAvailableListener();
    }


    @Override
    public void setViewToBlur(View viewToBlur) {
        if (mBlurView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mBlurView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnViewAvailableListener);
            } else {
                mBlurView.getViewTreeObserver().removeGlobalOnLayoutListener(mOnViewAvailableListener);
            }
        }
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
        if (mBlurView.isShown()) {
            Bitmap bitmap = ImageUtils.makeViewScreenshot(mBlurView);
            if (bitmap != null) {
                if (mBlurTask != null) {
                    mBlurTask.cancel(true);
                }
                mBlurTask = new BlurAsyncTask().execute(bitmap);
            }
        }
    }

    @Override
    public void setContainerImageView(ImageView dstImage) {
        mContainer = dstImage;
    }

    private void onBitmapBlured(Bitmap bitmap) {
        Log.v(TAG, "onBitmapBlured");
        if (mContainer != null) {
            // maybe need to check that view has attached
            mContainer.setImageBitmap(bitmap);
        }
        if (mBluredBitmap != null) {
            if (!mBluredBitmap.isRecycled()) {
                mBluredBitmap.recycle();
            }
        }
        mBluredBitmap = bitmap;

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
        private Bitmap mBitmap;
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            mBitmap = params[0];
//            Bitmap dest = Bitmap.createScaledBitmap(params[0], params[0].getWidth() / 10 + 1, params[0].getHeight() / 10 + 1, true);
            if (params[0].getWidth() > 0 && params[0].getHeight() > 0) {
                FilterUtils.blurIt(params[0]);
            }
            return params[0];//res
//            return dest;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            onBitmapBlured(bitmap);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mBitmap != null && !mBitmap.isRecycled()) {
                mBitmap.recycle();
            }
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
