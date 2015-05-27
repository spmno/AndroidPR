package com.mxnavi.platedetection;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static final int MEDIA_TYPE_IMAGE = 1; 
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100; 
    private Uri fileUri    = null; 
    private ImageView srcImageView;
    private ImageView plateImageView;
    private Button plateButton;
    static String currentImagePath;
    private ProgressDialog progressDialog; 
    
    private Handler handler = new Handler(){  
    	  
        @Override  
        public void handleMessage(Message msg) {  
              
            //关闭ProgressDialog  
            progressDialog.dismiss();  
            if (msg.what == 0) {
            	new AlertDialog.Builder(MainActivity.this).setMessage("没有检测到车牌").setPositiveButton("确定",null).show();
            } else {
					int dotPosition = currentImagePath.lastIndexOf('.');
					String path = currentImagePath.substring(0, dotPosition);
					path += "_judge_0.jpg";	
					Bitmap bitmap = BitmapFactory.decodeFile(path);
					plateImageView.setImageBitmap(bitmap);
            	new AlertDialog.Builder(MainActivity.this).setMessage("检测到车牌").setPositiveButton("确定",null).show();
            }
              
        }};  
    /*
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    //Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("plate_locate");

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		srcImageView = (ImageView)findViewById(R.id.imageView1);
		plateImageView = (ImageView)findViewById(R.id.imageView2);
		plateButton = (Button)findViewById(R.id.button1);
		plateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				fileUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));  // create a file to save the video
			    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name

			    // start the Video Capture Intent
			    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}});
		setResource2NDK();
		//create new Intent
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

	    fileUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));  // create a file to save the video
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name

	    // start the Video Capture Intent
	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	/*
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
    }
	*/
	private static File getOutputMediaFile(int type){ 
		// To be safe, you should check that the SDCard is mounted  
	    // using Environment.getExternalStorageState() before doing this.  
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory( 
	              Environment.DIRECTORY_PICTURES), "MyCameraApp"); 
	    // This location works best if you want the created images to be shared  
	    // between applications and persist after your app has been uninstalled.  
	 
	    if (! mediaStorageDir.exists()){ 
	        if (! mediaStorageDir.mkdirs()){ 
	            Log.d("MyCameraApp", "failed to create directory"); 
	            return null; 
	        } 
	    } 
	 
	        // Create a media file name  
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); 
	    File mediaFile = null; 
	    if (type == MEDIA_TYPE_IMAGE){ 
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + 
	        "IMG_"+ timeStamp + ".jpg"); 
	        currentImagePath = mediaFile.getPath();
	    } 
	 
	    return mediaFile; 
	} 

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	  
		if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
	        // Image captured and saved to fileUri specified in the Intent
				//Uri uri = data.getData();
				//String path = uri.getPath();
				Bitmap srcBitmap = BitmapFactory.decodeFile(currentImagePath);
				Bitmap smallBitmap = ThumbnailUtils.extractThumbnail(srcBitmap, 480, 800); 
				srcImageView.setImageBitmap(smallBitmap);
				Toast.makeText(this, "Image saved to:\n" +
						currentImagePath,
	                  Toast.LENGTH_LONG).show();
				//Mat mat = new Mat();
				//Utils.bitmapToMat(bitmap, mat);
				//Mat plate = new Mat();
				progressDialog = ProgressDialog.show(MainActivity.this, "Loading...", "Please wait...", true, false);
				new Thread(){  
					  
                    @Override  
                    public void run() {  
                        //需要花时间计算的方法  
                    	   
                          
                        //向handler发消息  
                    	int judgeCount = DetectionPlate.nativeDetect(currentImagePath);
         				if (judgeCount > 0) {
         					handler.sendEmptyMessage(judgeCount);  
         				} else {
                            handler.sendEmptyMessage(0);  
         				}


                    }}.start();  

				
	        } else if (resultCode == RESULT_CANCELED) {
	             // User cancelled the image capture
	        } else {
	             // Image capture failed, advise user
	        }
		}
	}
	
	public void setResource2NDK(){
	       File sdDir = null;
	       boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);  
	                          
	       if (sdCardExist) {                              
	         sdDir = Environment.getExternalStorageDirectory();//获取跟目录
	       }  
	       DetectionPlate.setResourcePath(sdDir.toString());
	} 
}
