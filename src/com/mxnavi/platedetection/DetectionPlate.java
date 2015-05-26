package com.mxnavi.platedetection;


public class DetectionPlate
{
    public static native void nativeSetFaceSize(int size);
    public static native void nativeDetect(long inputImage, long plates);
    public static native int nativeDetect(String imagePath);
}
