LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)


#OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include E:\code\opencv\OpenCV-2.4.10-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_SRC_FILES  := DetectionPlateTracker_jni.cpp \
					core\chars_identify.cpp \
					core\chars_recognise.cpp \
					core\chars_segment.cpp \
					core\core_func.cpp \
					core\feature.cpp \
					core\plate_detect.cpp \
					core\plate_judge.cpp \
					core\plate_locate.cpp \
					core\plate_recognize.cpp \
					core\plate.cpp
					
LOCAL_C_INCLUDES += $(LOCAL_PATH) \
					E:\code\opencv\OpenCV-2.4.10-android-sdk\sdk\native\jni\include
					
LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := detection_based_tracker

include $(BUILD_SHARED_LIBRARY)
