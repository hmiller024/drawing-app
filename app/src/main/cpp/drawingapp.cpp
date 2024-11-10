#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>

#define  LOG_TAG    "libdrawingapp"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


static int rgb_clamp(int value) {
    if(value > 255) {
        return 255;
    }
    if(value < 0) {
        return 0;
    }
    return value;
}

static void invertHighlightedSection(AndroidBitmapInfo* info, void* pixels, AndroidBitmapInfo* highlightInfo, void* highlightPixels) {

    uint32_t* pixelData = (uint32_t*)pixels;
    uint32_t* highlightPixelData = (uint32_t*)highlightPixels;

    int width = info->width;
    int height = info->height;

    int highlightWidth = highlightInfo->width;
    int highlightHeight = highlightInfo->height;

    // Iterate through each pixel in the highlight bitmap
    for (int i = 0; i < highlightHeight; i++) {
        for (int j = 0; j < highlightWidth; j++) {
            // Check if the pixel is drawn on the highlight bitmap
            uint32_t highlightColor = highlightPixelData[i * highlightWidth + j];
            if (highlightColor != 0) {
                int mainX = j * width / highlightWidth;
                int mainY = i * height / highlightHeight;

                int mainIndex = mainY * width + mainX;

                // Invert the color of the pixel
                uint32_t mainColor = pixelData[mainIndex];

                uint32_t red = 255 - ((mainColor >> 16) & 0xFF);
                uint32_t green = 255 - ((mainColor >> 8) & 0xFF);
                uint32_t blue = 255 - (mainColor & 0xFF);

                uint32_t invertedColor = (mainColor & 0xFF000000) | (red << 16) | (green << 8) | blue;

                pixelData[mainIndex] = invertedColor;
            }
        }
    }
}

// Uses gaussian blur
static void blurHighlightedSection(AndroidBitmapInfo* info, void* pixels, AndroidBitmapInfo* highlightInfo, void* highlightPixels) {

    uint32_t* pixelData = (uint32_t*)pixels;
    uint32_t* highlightPixelData = (uint32_t*)highlightPixels;

    int width = info->width;
    int height = info->height;

    int highlightWidth = highlightInfo->width;
    int highlightHeight = highlightInfo->height;

    const int radius = 3;

    // Iterate through each pixel in the highlight bitmap
    for (int i = 0; i < highlightHeight; i++) {
        for (int j = 0; j < highlightWidth; j++) {
            // Check if the pixel is drawn on the highlight bitmap
            uint32_t highlightColor = highlightPixelData[i * highlightWidth + j];
            if (highlightColor != 0) {
                int mainX = j * width / highlightWidth;
                int mainY = i * height / highlightHeight;

                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        int x = mainX + dx;
                        int y = mainY + dy;

                        if (x >= 0 && x < width && y >= 0 && y < height) {
                            int mainIndex = y * width + x;

                            uint32_t mainColor = pixelData[mainIndex];
                            uint32_t red = (mainColor >> 16) & 0xFF;
                            uint32_t green = (mainColor >> 8) & 0xFF;
                            uint32_t blue = mainColor & 0xFF;

                            red = green = blue = 0;

                            for (int oy = -1; oy <= 1; oy++) {
                                for (int ox = -1; ox <= 1; ox++) {
                                    int sx = x + ox;
                                    int sy = y + oy;

                                    if (sx >= 0 && sx < width && sy >= 0 && sy < height) {
                                        int surroundIndex = sy * width + sx;
                                        uint32_t surroundColor = pixelData[surroundIndex];
                                        red += (surroundColor >> 16) & 0xFF;
                                        green += (surroundColor >> 8) & 0xFF;
                                        blue += surroundColor & 0xFF;
                                    }
                                }
                            }

                            red /= 9;
                            green /= 9;
                            blue /= 9;

                            red = rgb_clamp(red);
                            green = rgb_clamp(green);
                            blue = rgb_clamp(blue);

                            uint32_t blurredColor = (mainColor & 0xFF000000) | (red << 16) | (green << 8) | blue;

                            pixelData[mainIndex] = blurredColor;
                        }
                    }
                }
            }
        }
    }
    LOGI("BLURRING");
}

static inline void diffuseError(uint32_t& pixel, int errorRed, int errorGreen, int errorBlue, int weight) {
    int red = (pixel >> 16) & 0xFF;
    int green = (pixel >> 8) & 0xFF;
    int blue = pixel & 0xFF;

    red = rgb_clamp(red + errorRed * weight / 16);
    green = rgb_clamp(green + errorGreen * weight / 16);
    blue = rgb_clamp(blue + errorBlue * weight / 16);

    pixel = (pixel & 0xFF000000) | (red << 16) | (green << 8) | blue;
}

// Uses floyd steinberg dithering
static void ditherHighlightedSection(AndroidBitmapInfo* info, void* pixels, AndroidBitmapInfo* highlightInfo, void* highlightPixels) {
    uint32_t* pixelData = (uint32_t*)pixels;
    uint32_t* highlightPixelData = (uint32_t*)highlightPixels;

    int width = info->width;
    int height = info->height;

    int highlightWidth = highlightInfo->width;
    int highlightHeight = highlightInfo->height;

    // Iterate through each pixel in the highlight bitmap
    for (int i = 0; i < highlightHeight; i++) {
        for (int j = 0; j < highlightWidth; j++) {
            // Check if the pixel is drawn on the highlight bitmap
            uint32_t highlightColor = highlightPixelData[i * highlightWidth + j];
            if (highlightColor != 0) { // If the pixel is not transparent

                int mainX = j * width / highlightWidth;
                int mainY = i * height / highlightHeight;

                int mainIndex = mainY * width + mainX;

                uint32_t mainColor = pixelData[mainIndex];
                uint32_t red = (mainColor >> 16) & 0xFF;
                uint32_t green = (mainColor >> 8) & 0xFF;
                uint32_t blue = mainColor & 0xFF;

                int oldRed = red;
                int oldGreen = green;
                int oldBlue = blue;

                red = red > 127 ? 255 : 0;
                green = green > 127 ? 255 : 0;
                blue = blue > 127 ? 255 : 0;
                pixelData[mainIndex] = (mainColor & 0xFF000000) | (red << 16) | (green << 8) | blue;

                int errorRed = oldRed - red;
                int errorGreen = oldGreen - green;
                int errorBlue = oldBlue - blue;

                if (mainX + 1 < width) {
                    diffuseError(pixelData[mainIndex + 1], errorRed, errorGreen, errorBlue, 7);
                }
                if (mainY + 1 < height) {
                    if (mainX > 0) {
                        diffuseError(pixelData[mainIndex + width - 1], errorRed, errorGreen, errorBlue, 3);
                    }
                    diffuseError(pixelData[mainIndex + width], errorRed, errorGreen, errorBlue, 5);
                    if (mainX + 1 < width) {
                        diffuseError(pixelData[mainIndex + width + 1], errorRed, errorGreen, errorBlue, 1);
                    }
                }
            }
        }
    }
}



extern "C"
JNIEXPORT void JNICALL
Java_com_example_drawingapp_CanvasViewKt_invert(JNIEnv *env, jclass clazz, jobject bitmap, jobject highlightBitmap, jint tool) {

    AndroidBitmapInfo info;
    AndroidBitmapInfo highlightInfo;
    void* pixels;
    void* highlightPixels;

    int ret;


    // Get information about the main bitmap
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed for main bitmap! error=%d", ret);
        return;
    }
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Main bitmap format is not RGBA_8888 !");
        return;
    }

    // Get information about the highlight bitmap
    if ((ret = AndroidBitmap_getInfo(env, highlightBitmap, &highlightInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed for highlight bitmap! error=%d", ret);
        return;
    }
    if (highlightInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Highlight bitmap format is not RGBA_8888 !");
        return;
    }

    // Lock the pixels of the main bitmap
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed for main bitmap! error=%d", ret);
        return;
    }

    // Lock the pixels of the highlight bitmap
    if ((ret = AndroidBitmap_lockPixels(env, highlightBitmap, &highlightPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed for highlight bitmap! error=%d", ret);
        // Unlock the pixels of the main bitmap
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    }
    if(tool == 1)
        invertHighlightedSection(&info, pixels, &highlightInfo, highlightPixels);

    if(tool == 2)
        blurHighlightedSection(&info, pixels, &highlightInfo, highlightPixels);

    if(tool == 3)
        ditherHighlightedSection(&info, pixels, &highlightInfo, highlightPixels);

    AndroidBitmap_unlockPixels(env, bitmap);
    AndroidBitmap_unlockPixels(env, highlightBitmap);
}
