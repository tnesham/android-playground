/**
 * 
 */
package com.example.machismo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;


/**
 * @author e300md
 *
 */
public class ImageSwitcherActivity extends View implements ViewFactory {

		public ImageSwitcherActivity(Context context) {
			super(context);
			createLayout();
		}
	
	

		public ImageSwitcherActivity(Context context, AttributeSet attrs) {
		super(context, attrs);
		createLayout();
	}
	
	

		public ImageSwitcherActivity(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		createLayout();
	}

		Integer pics[]; 

		ImageSwitcher iSwitcher;
		
		
		AwsImageSelector awsImageSelector = new AwsImageSelector();
		

		/** Called when the activity is first created. */
		public void createLayout() {
			//setContentView(R.layout.image_switcher);

			
			iSwitcher = (ImageSwitcher) LayoutInflater.from(getContext()).inflate(R.id.ImageSwitcher01, null);
			iSwitcher.setFactory(this);
			iSwitcher.setInAnimation(AnimationUtils.loadAnimation(getContext(),
					android.R.anim.fade_in));
			iSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
					android.R.anim.fade_out));

			Gallery gallery = (Gallery) findViewById(R.id.Gallery01);
			gallery.setAdapter(new ImageAdapter(getContext()));
			gallery.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					iSwitcher.setImageResource(pics[arg2]);
				}
			});
			
			awsImageSelector.getImages();
		}

		public class ImageAdapter extends BaseAdapter {

			private Context ctx;

			public ImageAdapter(Context c) {
				ctx = c; 
			}

			@Override
			public int getCount() {

				return pics.length;
			}

			@Override
			public Object getItem(int arg0) {

				return arg0;
			}

			@Override
			public long getItemId(int arg0) {

				return arg0;
			}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {

				ImageView iView = new ImageView(ctx);
				iView.setImageResource(pics[arg0]);
				iView.setScaleType(ImageView.ScaleType.FIT_XY);
				iView.setLayoutParams(new Gallery.LayoutParams(150, 150));
				return iView;
			}

		}

		@Override
		public View makeView() {
			ImageView iView = new ImageView(getContext());
			iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			iView.setLayoutParams(new 
					ImageSwitcher.LayoutParams(
							LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			iView.setBackgroundColor(0xFF000000);
			return iView;
		}
	}
	
	

