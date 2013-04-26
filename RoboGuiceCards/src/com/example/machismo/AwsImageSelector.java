package com.example.machismo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * 
 * @author e300md
 *
 */
public class AwsImageSelector {

	private AmazonS3Client s3Client;
	//For personal development purposes I store these here but make them inactive frequently
	public static final String AWS_ACCESS_ID = "AKIAIUVADSZ7KDDK2SLQ";
	public static final String AWS_SECRET_KEY = "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";
	public static final String AWS_PICTURE_BUCKET = "ENT_CARD_BACK_IMAGES";
	//See http://docs.aws.amazon.com/general/latest/gr/rande.html
	//US standard end point = s3.amazonaws.com
	public static final String AWS_ENDPOINT = "s3.amazonaws.com";
	
	private HashMap <String, Bitmap>amazonImageMap = new HashMap<String, Bitmap>();

	private MainActivity mainActivity;
	
	/**
	 * Constructor
	 */
	public AwsImageSelector(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}
	
	/**
	 * Runs an Async task to get images from Amazon Web S3 
	 */
	public HashMap <String, Bitmap> getImages() {
		
		if(amazonImageMap.size() < 1) {
			AsyncTask.execute(awsRunnable);
		}
		return amazonImageMap;
	}
	
	
	/**
	 * 
	 * StrictMode.ThreadPolicy was introduced since API Level 9 and the default thread policy had been changed since API Level 11, 
	 * which in short, does not allow network operation (include HttpClient and HttpUrlConnection) get executed on UI thread. 
	 * if you do this, you get NetworkOnMainThreadException.
	 * 
	 * 
	 */
	private Runnable awsRunnable = new Runnable() {
	   @Override
	   public void run() {
		   s3Client = new AmazonS3Client( new BasicAWSCredentials( AWS_ACCESS_ID, AWS_SECRET_KEY ) );
		   s3Client.setEndpoint(AWS_ENDPOINT);
	       ObjectListing images = s3Client.listObjects(AWS_PICTURE_BUCKET ); 

			List<S3ObjectSummary> list = images.getObjectSummaries();
			for(S3ObjectSummary image: list) {
			    S3Object s3Object = s3Client.getObject(AWS_PICTURE_BUCKET , image.getKey());
			
			    InputStream is = s3Object.getObjectContent();
				Bitmap bitmapImage = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), 325, 160, true);
				
				try {
					is.close();
				} catch (IOException e) {
					
				}
			    amazonImageMap.put(image.getKey(), bitmapImage);
			}

			Log.i("Machismo", String.format("%d images have been downloaded", list.size() ));
			Intent broadcastIntent = new Intent();
		    broadcastIntent.setAction(AwsBroadcastReceiver.ACTION_RESP);
		    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		    //broadcastIntent.putExtra(PARAM_OUT_MSG, resultTxt);
		    mainActivity.sendBroadcast(broadcastIntent);
		}
	};

	
	
}
