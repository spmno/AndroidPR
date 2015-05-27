#include <jni.h>
#include <vector>
#include <string>
#include <sstream>
#include <opencv2/core/core.hpp>
#include "include/plate_locate.h"
#include "include/plate_judge.h"
#include "include/util.h"
#include "com_mxnavi_platedetection_DetectionPlate.h"

using namespace std;
using namespace cv;
using namespace easypr;

/*
 * Class:     com_mxnavi_platedetection_DetectionPlate
 * Method:    nativeSetFaceSize
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_com_mxnavi_platedetection_DetectionPlate_nativeSetFaceSize
  (JNIEnv *, jlong, jint)
{

}

/*
 * Class:     com_mxnavi_platedetection_DetectionPlate
 * Method:    nativeDetect
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_com_mxnavi_platedetection_DetectionPlate_nativeDetect
  (JNIEnv *, jclass, jlong src, jlong resultImage)
{
	vector<Mat> resultVec;
#if 0
	CPlateLocate plate;
	plate.setLifemode(true);
	int result = plate.plateLocate(*((Mat*)src), resultVec);
	Mat* plateMat = (Mat*)resultImage;
	resultVec[0].copyTo(*plateMat);
#else
	Mat srcImage = imread("/sdcard/test.jpg");
	CPlateLocate plate;
	plate.setLifemode(true);
	int result = plate.plateLocate(srcImage, resultVec);
	LOGD("plateLocate over!");
	Mat* plateMat = (Mat*)resultImage;
	resultVec[0].copyTo(*plateMat);
	LOGD("copyTo over!");
	imwrite("/sdcard/plate_image.jpg", resultVec[0]);
#endif
}

#define MAX_COLS 600

JNIEXPORT jint JNICALL Java_com_mxnavi_platedetection_DetectionPlate_nativeDetect__Ljava_lang_String_2
  (JNIEnv *env, jclass, jstring imageName)
{
	vector<Mat> locateResultVec;
	const char* str = env->GetStringUTFChars(imageName, JNI_FALSE);
	string baseName(str);
	size_t dotPosition = baseName.find_last_of(".");
	size_t dirPosition = baseName.find_last_of("/");
	string justFileName = baseName.substr(0, dotPosition);
	string saveDirPath = baseName.substr(0, dirPosition);
	LOGD("save dir path = %s", saveDirPath.c_str());
	Utils::setSaveDir(saveDirPath);
	//Mat srcImage = imread("/sdcard/test.jpg");
	Mat srcImage = imread(baseName);
	Mat readyImage;
	LOGD("image rows = %d, cols = %d", srcImage.rows, srcImage.cols);

	if (srcImage.cols > MAX_COLS) {
		int height = srcImage.rows * MAX_COLS * 1.0/srcImage.cols;
		resize(srcImage, readyImage, Size(MAX_COLS, height));
	}

	CPlateLocate plate;
	plate.setLifemode(true);
	int result = plate.plateLocate(readyImage, locateResultVec);
	LOGD("plateLocate over!");
	result = locateResultVec.size();
	for (int i = 0; i < locateResultVec.size(); ++i) {
		stringstream fileNameStream;
		fileNameStream << justFileName << "_plate_" << i << ".jpg";
		string fileName;
		fileNameStream >> fileName;
		LOGD("filename %d, %s", i, fileName.c_str());
		imwrite(fileName, locateResultVec[i]);
	}

	CPlateJudge ju;
	vector<Mat> judgeResultVec;
	int resultJu = ju.plateJudge(locateResultVec, judgeResultVec);
	result = judgeResultVec.size();
	for (int i = 0; i < judgeResultVec.size(); ++i) {
		stringstream fileNameStream;
		fileNameStream << justFileName << "_judge_" << i << ".jpg";
		string fileName;
		fileNameStream >> fileName;
		LOGD("filename %d, %s", i, fileName.c_str());
		imwrite(fileName, judgeResultVec[i]);
	}
	LOGD("judge over, judge %d plates!", result);
	return result;
}

JNIEXPORT void JNICALL Java_com_mxnavi_platedetection_DetectionPlate_setResourcePath(JNIEnv *env, jclass, jstring pathName)
{
	const char* str = env->GetStringUTFChars(pathName, JNI_FALSE);
	string resourcePath(str);
	Utils::setResourceDir(resourcePath);
	LOGD("set ndk path = %s", resourcePath.c_str());
}
