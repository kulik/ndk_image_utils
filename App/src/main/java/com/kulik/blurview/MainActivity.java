package com.kulik.blurview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kulik.ndk.image.IBlurer;
import com.kulik.ndk.image.NDKBlurer;

public class MainActivity extends Activity {

    private IBlurer mBlurer;

    private ImageView mSrcImage;
    private ImageView mDstImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSrcImage = (ImageView) findViewById(R.id.src);
        mSrcImage.setOnClickListener(new View.OnClickListener() {
            boolean flag;

            @Override
            public void onClick(View v) {
                mSrcImage.setImageDrawable((flag) ? getResources().getDrawable(R.drawable.rainbow1) :
                        getResources().getDrawable(R.drawable.rainbow2));
                mBlurer.setViewToBlur(mSrcImage);
                flag = !flag;
//                mBlurer.startBluring();
            }
        });
        mDstImage = (ImageView) findViewById(R.id.dst);

        mBlurer = new NDKBlurer();
        mBlurer.setContainerImageView(mDstImage);
    }

}
