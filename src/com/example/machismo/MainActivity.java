package com.example.machismo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;


/**
 * Android test class to exercise new functionality
 * @author e300md
 *
 */
public class MainActivity extends Activity {

	//All cards have same image for the back
	private Bitmap back;
	
	//References to all cards on table
	private ArrayList<Card> cardsOnTable = new ArrayList<Card>();

	//Holds all possible Card Ids
	private ArrayList<Integer> cardIds = new ArrayList<Integer>();
	
	//Holds randomly chosen Card Ids
	private ArrayList<Integer> randomCardIds = new ArrayList<Integer>();
	
	//Total match points
	private int positiveMatchPoints=0;
	private int negativeMatchPoints=0;
	
	//difficulty level
	private int matchQtyForPoints=2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initCardsIds();
		back = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.back), Card.scaleWidthFactor, Card.scaleHeightFactor, true);

		
		generateRandomCardIds(cardIds, randomCardIds);
		buildTable(randomCardIds);

		setActionBar();
	}
	
	/**
	 * Updates the action bar with consistent format
	 */
	private void setActionBar() {
		getActionBar().setTitle(String.format("%s  Need to match %d to win points. Total matches: %d, total misses: %d", 
				getResources().getString(R.string.app_name), matchQtyForPoints, positiveMatchPoints, negativeMatchPoints));
	}
	
	/**
	 * Use reflection to load image ids rather than hardcoding them in Java as well as XML
	 * See res/drawable folder. Android generates a class R with ids for each image
	 */
	@SuppressWarnings("rawtypes")
	private void initCardsIds() {
		Class resources = R.drawable.class;
		Field[] fields = resources.getFields();
		int drawableId;
		for (Field field : fields) {
			
			if(field.getName().contains("back")) {
				continue;
			}
			drawableId = getResources().getIdentifier(field.getName(), "drawable", "com.example.machismo");
			
			cardIds.add(drawableId);
		}
	}

	/**
	 * Builds the table of cards
	 */
	private void buildTable(ArrayList <Integer>randomCardIdList) {

		TableLayout tableLayout = (TableLayout) findViewById(R.id.cardTable);
		/* Create a new row to be added. */
		TableRow tr = null;
		/* Create Cards to be the row-content. */
		for (int i = 0, useCardId = 0; i < 30; i++, useCardId++) {

			//new row every 10 cards 
			if (i % 10 == 0) {
				useCardId = 0;
				Collections.shuffle(randomCardIdList, new Random(i));
				tr = new TableRow(this);
				tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
				/* Add row to TableLayout. */
				tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
			}
			Card card = new Card(this.getApplicationContext());
			cardsOnTable.add(card);
			card.setCardType(randomCardIdList.get(useCardId));
			card.setFront(randomCardIdList.get(useCardId));
			card.setBack(back);
			card.showBack();
			card.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
			card.setOnTouchListener(mClickListener);
			/* Add Imageto row. */
			tr.addView(card);
		}

	}

	/**
	 * 
	 * @param cardIn
	 * @return
	 */
	private boolean cardsMatch(Card cardIn) {

		int matchCounter = 0;
		
		for (Card card : cardsOnTable) {

			if (cardIn == card) {
				continue; // Don't compare cardIn to itself
			}
			if (cardIn.getCardType() == card.getCardType() && card.isSelected()) {
				matchCounter++;
			}
		}

		if (matchCounter > matchQtyForPoints) {
			return true;
		}

		return false;
	}
	
	
	private int cardsSelectedQty() {

		int matchCounter = 0;
		
		for (Card card : cardsOnTable) {

			if (card.isSelected()) {
				matchCounter++;
			}
		}

		return matchCounter;
	}

	
	/**
	 * This will select 10 cards randomly from a deck of 54 cards (2 Jokers)
	 * 
	 */
	private void generateRandomCardIds(ArrayList <Integer>allCardIds, ArrayList <Integer>randomCardIdList) {
		long start = 1;
	    long end = 54;
	    long range = end - start + 1;
	    long fraction;
	    int randomNumber;
	    try {
		    Random random = new Random();
		    for (int idx = 0; idx < 10; ++idx){
		        // compute a fraction of the range, 0 <= frac < range
		        fraction = (long)(range * random.nextDouble());
		        randomNumber =  (int)(fraction + start);
		        randomCardIdList.add(allCardIds.get(randomNumber));
		    }
	    } catch (Exception e) {
	    	Log.e("Machismo", "generateRandomCards: "+ e.getMessage());
	    }
	}
	
	
	/**
	 * 
	 */
	private void resetCards() {

		for (Card card : cardsOnTable) {
			if (card.isSelected()) {
				card.showBack();
				card.setSelected(false);
			}
		}
		
		TableLayout tableLayout = (TableLayout) findViewById(R.id.cardTable);
		tableLayout.removeAllViewsInLayout();
		
		generateRandomCardIds(cardIds, randomCardIds);
		buildTable(randomCardIds);

		positiveMatchPoints=0; 
		negativeMatchPoints=0;
		setActionBar();
	}

	private OnTouchListener mClickListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent mev) {

			int action = mev.getAction();
			if (action != MotionEvent.ACTION_DOWN) {
				return true;
			}

			Card card = (Card) v;
			if (!card.isSelected()) {
				card.showFront();
				card.setSelected(true);
			}

			if (cardsMatch(card)) {
				
				Toast.makeText(getApplicationContext(), "Great - a match!", Toast.LENGTH_SHORT).show();
				positiveMatchPoints++;
				
			} else {
				if(cardsSelectedQty() > 1) {
					negativeMatchPoints++;
				}
			}

			setActionBar();
			return true;
		}

	};

	public static final int MENU_NEW_GAME = 1;
	public static final int MENU_GUESS_ONE = 2;
	public static final int MENU_GUESS_TWO = 3;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_NEW_GAME, 0, "New Game");
		menu.add(0, MENU_GUESS_ONE, 1, "Match 2");
		menu.add(0, MENU_GUESS_TWO, 2, "Match 3");
		return true;
	}

	@Override
	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case MENU_NEW_GAME:
				resetCards();
				
			case MENU_GUESS_ONE:
				matchQtyForPoints=2;
				
			case MENU_GUESS_TWO:
				matchQtyForPoints=3;
				
		}
		return true;
	}
}