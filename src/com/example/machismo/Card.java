package com.example.machismo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;


/**
 * Android test class to exercise new functionality
 * @author e300md
 * 
 * 4/3/2013 - initial implementation
 * 4/5/2013 - Added "solved" state so card is neither selected nor not selected, it is no longer part of the game 
 * 			
 *
 */

public class Card extends ImageView {

	public enum CardState {
	    BACK_SHOWN, SELECTED, MATCH_CANDIDATE, MATCHED 
	}
	
	private CardState cardState = CardState.BACK_SHOWN;
	
	private Integer cardType;
	
	//Allows the id to be associated with the card before loading the bitmap in showFront
	private Integer cardFrontId;
	private Bitmap front;
	private Bitmap back;
	
	
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


	public Bitmap getBack() {
		return back;
	}

	public void setBack(Bitmap back) {
		this.back = back;
	}
	
	public void showBack() {
		setImageBitmap(back);
		cardState = CardState.BACK_SHOWN;
	}
	
	public void showFront() {
		if(front == null) {
			this.front  = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), cardFrontId), scaleWidthFactor, scaleHeightFactor, true);
		}
		setImageBitmap(front);
	}


	public void setCardState(CardState cardState) {
		this.cardState = cardState;
	}


	public boolean hasCardState(CardState cardStateIn) {
		return this.cardState == cardStateIn;
	}
	
	public boolean isSameCardType(Card cardIn) {
		
		return ( cardIn.getCardType() == this.getCardType() ) ; 
		
	}

}
