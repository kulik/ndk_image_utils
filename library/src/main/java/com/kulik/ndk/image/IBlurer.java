package com.kulik.ndk.image;

import android.view.View;
import android.widget.ImageView;

/**
 * Created by kulik on 12/1/13.
 */
public interface IBlurer {
    void setViewToBlur(View viewToBlur);

    void startBluring();

    void setContainerImageView(ImageView dstImage);

}
