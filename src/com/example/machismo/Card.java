package com.example.machismo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;


public class Card extends ImageView {

	private Integer cardType;
	
	//Allows the id to be associated with the card before loading the bitmap in showFront
	private Integer cardFrontId;
	private Bitmap front;
	private Bitmap back;
	private boolean selected;
	
	public static final int scaleWidthFactor = 120;
	public static final int scaleHeightFactor = 160;
	
	
	public Card(Context context) {
		super(context);
		
		
	}
	
	
	private Rect rect = new Rect();
	private Paint paint = new Paint();
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		//Adding a border to see size of TextView
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(3);
		getLocalVisibleRect(rect);
		canvas.drawRect(rect, paint);
	}

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public Bitmap getBitmap() {
		return front;
	}

	public void setFront(Integer cardId) {
		cardFrontId = cardId;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Bitmap getBack() {
		return back;
	}

	public void setBack(Bitmap back) {
		this.back = back;
	}
	
	public void showBack() {
		setImageBitmap(back);
	}
	
	public void showFront() {
		if(front == null) {
			this.front  = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), cardFrontId), scaleWidthFactor, scaleHeightFactor, true);
		}
		setImageBitmap(front);
	}

}
