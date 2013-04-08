package com.example.machismo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AwsImageSelector {

	private AmazonS3Client s3Client;
	public static final String AWS_ACCESS_ID = "AKIAJJJBR3T3ZGZ73BAA";
	public static final String AWS_SECRET_KEY = "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";
	public static final String AWS_PICTURE_BUCKET = "ENT_CARD_BACK_IMAGES";
	//See http://docs.aws.amazon.com/general/latest/gr/rande.html
	//US standard end point = s3.amazonaws.com
	public static final String AWS_ENDPOINT = "s3.amazonaws.com";
	
	private HashMap <String, Bitmap>amazonImageMap = new HashMap<String, Bitmap>();

	
	/**
	 * Constructor
	 */
	public AwsImageSelector() {

	}
	
	/**
	 * Runs an Async task to get images from Amazon Web S3 
	 */
	public void getImages() {
		
		if(amazonImageMap.size() < 1) {
			AsyncTask.execute(awsRunnable);
		}
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
				Bitmap bitmapImage = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), Card.scaleWidthFactor, Card.scaleHeightFactor, true);
				
				try {
					is.close();
				} catch (IOException e) {
					
				}
			    amazonImageMap.put(image.getKey(), bitmapImage);
			}

		}
	};

	
	
}
