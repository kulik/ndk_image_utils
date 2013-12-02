LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := bitmaputils
LOCAL_CFLAGS := -DANDROID_NDK \
                -DDISABLE_IMPORTGL
LOCAL_SRC_FILES := color_struct.c fastgray.c fastblurer.c bitmap.c mem_utils.c nanojpeg.c bicubic_resize.c transform.c colour_space.c matrix.c
LOCAL_LDLIBS    := -lm -llog -ljnigraphics
include $(BUILD_SHARED_LIBRARY)
