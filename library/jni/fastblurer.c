    // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
    // Ported to C by Nilesh Patel from the source code found here
    // http://incubator.quasimondo.com/processing/stackblur.pde
    // and altered to process each colour channel (r, g, b) separately.

    #include <stdlib.h>
    #include <bitmap.h>
    #include <blur.c>
    #include <android/log.h>
    #include <jni.h>
    #include <android/bitmap.h>
    #include <color_struct.c>

    #define  LOG_TAG    "fastblur"
    #define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
    #define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

//
//int newIntArray(unsigned int size, int** arrayPointer) {
//        unsigned int numBytes = size * sizeof(int);
//        *arrayPointer = (int*) malloc(numBytes);
//        if (arrayPointer == NULL) {
//                return INT_ARRAY_ERROR;
//        }
//
//        memset(*arrayPointer, 0, numBytes);
//        return MEMORY_OK;
//}

Bitmap initBitmap(void* pixels, AndroidBitmapInfo info) {
    Bitmap bitmap;
    initBitmapMemory(&bitmap, info.width, info.height);
    int y,x;
    int offset;
    LOGI("copiing bitmap bitmap");
    for (y=0;y<info.height;y++) {
           argb * line = (argb *) pixels;
           offset=(y*info.width);

           for (x=0;x<info.width;x++) {
                bitmap.red[offset+x]= line[x].red;
                bitmap.green[offset+x]= line[x].green;
                bitmap.blue[offset+x]= line[x].blue;
           }
           pixels = (char *)pixels + info.stride;
       }
       LOGI("finish init bitmap");

    return bitmap;
}

void copyBitmapToTarget(Bitmap* dst,  AndroidBitmapInfo info, void* pixelsblur) {
    int y,x,offset;
//	for (y=0; y < info.height; y++) {
//	    getBitmapRowAsIntegers(dst, y, pixelsblur);
//	    pixelsblur = (char *)pixelsblur + info.stride;
//	}

  for (y=0;y<info.height;y++) {
           argb * line = (argb *) pixelsblur;
           offset=(y*info.width);

           for (x=0;x<info.width;x++) {
                line[x].red = (*dst).red[offset+x];
                line[x].green = (*dst).green[offset+x];
                line[x].blue= (*dst).blue[offset+x];
                line[x].alpha = 10;
           }
           pixelsblur = (char *)pixelsblur + info.stride;
       }
       LOGI("finish copy bitmap");

}


    JNIEXPORT void JNICALL Java_com_kulik_ndk_image_FilterUtils_fastBlurer(JNIEnv
        * env, jobject  obj, int radius, jobject bitmapcolor)
        {
            AndroidBitmapInfo  infocolor;
            void*              pixelscolor;
            int                ret;
            int             y;
            int             x;

            if ((ret = AndroidBitmap_getInfo(env, bitmapcolor, &infocolor)) < 0) {
                LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
                return;
            }

            LOGI("color image :: width is %d; height is %d; stride is %d; format is %d;flags is %d",
            infocolor.width,infocolor.height,infocolor.stride,infocolor.format,infocolor.flags);
            if (infocolor.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
                LOGE("Bitmap format is not RGBA_8888 !");
                return;
            }

            if ((ret = AndroidBitmap_lockPixels(env, bitmapcolor, &pixelscolor)) < 0) {
                LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
            }

            Bitmap b = initBitmap(pixelscolor, infocolor);
                LOGI("initBitmap");
            Bitmap dst;
            initBitmapMemory(&dst, infocolor.width, infocolor.height);
                LOGI("initBitmapMemory");

            stackBlur(radius, b.red, b.green, b.blue, infocolor.width, infocolor.height, dst.red,dst.green, dst.blue);

                LOGI("Stack blur is finished");
            copyBitmapToTarget(&dst, infocolor, pixelscolor);
                LOGI("bitmap has copied from target");
//             freeUnsignedCharArray(dst.red);
//             freeUnsignedCharArray(dst.green);
//             freeUnsignedCharArray(dst.blue);

                AndroidBitmap_unlockPixels(env, bitmapcolor);
            }

