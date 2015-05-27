package com.mxnavi.platedetection;


public class DetectionPlate
{
	 static{  
	        System.loadLibrary("opencv_java");  
	        System.loadLibrary("plate_locate");  
	    }  
    public static native void nativeSetFaceSize(int size);
    public static native void nativeDetect(long inputImage, long plates);
    public static native int nativeDetect(String imagePath);
    public static native void setResourcePath(String resourcePath);
}
