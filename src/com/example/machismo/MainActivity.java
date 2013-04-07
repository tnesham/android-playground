package com.example.machismo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.example.machismo.Card.CardState;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
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
 * 4/3/2013 - initial implementation
 * 4/5/2013 - Add ability to maintain "state" when device rotated - see onConfigurationChanged
 * 			- Added handler.postDelayed(runnable) to hide card(s) that did not match
 * 			- Added CardState enum to make it simpler to track when to flip a card 
 *
 */
public class MainActivity extends Activity {

	//The number of cards in subset of total cards
	//These are used for matching game
	int numberOfCardsInSubset=10;
	
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
		Log.i("Machismo", "Called onCreate");
		
		setContentView(R.layout.activity_main);

		initCardsIds();
		back = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.back), Card.scaleWidthFactor, Card.scaleHeightFactor, true);

		
		generateRandomCardIds(cardIds, randomCardIds, numberOfCardsInSubset);
		
		
		buildTable(randomCardIds, getResources().getConfiguration().orientation, numberOfCardsInSubset, true);

		setActionBar();
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    Log.i("Machismo", "Called onConfigurationChanged");
	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	Log.i("Machismo", "onConfigurationChanged to ORIENTATION_LANDSCAPE");
	    	clearTableLayoutViews();
	    	buildTable(randomCardIds, getResources().getConfiguration().orientation, numberOfCardsInSubset, false);
	        
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    	Log.i("Machismo", "onConfigurationChanged to ORIENTATION_PORTRAIT");
	    	clearTableLayoutViews();
	    	buildTable(randomCardIds, getResources().getConfiguration().orientation, numberOfCardsInSubset, false);
	    }
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
			
			if(field.getName().startsWith("back") || field.getName().startsWith("cards")) {
				continue;
			}
			drawableId = getResources().getIdentifier(field.getName(), "drawable", "com.example.machismo");
			
			cardIds.add(drawableId);
		}
	}

	/**
	 * Builds the table of cards
	 * @param randomCardIdList - holds cardIds used 
	 * @param orientation - portrait or landscape
	 * @param numberOfCardsInSubset - qty of cards selected to match in game 
	 * @param newCards - if this is new game, then need new Card references
	 */
	private void buildTable(ArrayList <Integer>randomCardIdList, int orientation, int numberOfCardsInSubset, boolean newCards) {

		//Variable to hold card reference
		Card card=null;
		
		//Based on orientation, this value controls when a new row is created
		int newRowValue=10;
		
		//Number cards placed on the table
		int tableQty = 30;
		
		if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
			newRowValue=10;
		} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			newRowValue=5;
		}
		TableLayout tableLayout = (TableLayout) findViewById(R.id.cardTable);
		/* Create a new row to be added. */
		TableRow tr = null;
		/* Create Cards to be the row-content. */
		for (int i = 0, useCardId = 0; i < tableQty; i++, useCardId++) {

			//new row every time newRowValue is equaled  
			if (i % newRowValue == 0) {
				if(useCardId == numberOfCardsInSubset) {
					if( newCards ) {
						useCardId = 0; //reuse ids 
					}
					Collections.shuffle(randomCardIdList, new Random(i));
				}
				tr = new TableRow(this);
				tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
				/* Add row to TableLayout. */
				tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
			}
			if( newCards ) {
				card = new Card(this.getApplicationContext());
				cardsOnTable.add(card);
				card.setCardType(randomCardIdList.get(useCardId));
				card.setFront(randomCardIdList.get(useCardId));
				card.setBack(back);
				card.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
				card.setOnTouchListener(mClickListener);
				card.showBack();
			} else {
				
				card = cardsOnTable.get(useCardId);
				if( !card.hasCardState(CardState.BACK_SHOWN ) ) {
					card.showFront();
				} else {
					card.showBack();
				}
			}
			
			/* Add Imageto row. */
			tr.addView(card);
		}

	}

	/**
	 * Finds first card that matches card user touched, and is not in a solution set 
	 * @param cardTouched
	 * @return true if match found
	 */
	private boolean cardsMatched(Card cardTouched) {

		int matchCounter = 0;
		
		for (Card cardOnTable : cardsOnTable) {

			if (cardTouched == cardOnTable) {
				continue; // Don't compare cardTouched to itself
			}
			if ( cardOnTable.isSameCardType(cardTouched) && 
					( cardOnTable.hasCardState(CardState.SELECTED) || cardOnTable.hasCardState(CardState.MATCH_CANDIDATE ))) 
			{
				matchCounter++;
				cardOnTable.setCardState(CardState.MATCH_CANDIDATE);
				cardTouched.setCardState(CardState.MATCH_CANDIDATE);
			}
		}

		if (matchCounter > 0) {
			return true;
		}

		return false;
	}
	
	
	/**
	 * Returns the qty of cards selected that have not been "matched" out of play 
	 * @return
	 */
	private int cardsSelectedStateQty(CardState cardState) {

		int matchCounter = 0;
		
		for (Card card : cardsOnTable) {

			if ( card.hasCardState(cardState) ) {
				matchCounter++;
			}
		}

		return matchCounter;
	}

	
	/**
	 * This will select 10 cards randomly from a deck of 54 cards (2 Jokers)
	 * 
	 */
	private void generateRandomCardIds(ArrayList <Integer>allCardIds, ArrayList <Integer>randomCardIdList, int numberOfCardsInSubset) {
		long start = 1;
	    long end = 54;
	    long range = end - start + 1;
	    long fraction;
	    int randomNumber;
	    try {
		    Random random = new Random();
		    for (int idx = 0; idx < numberOfCardsInSubset; ++idx){
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
	 * This method gets card's parent and calls parent's removeView passing in child.
	 * This approach is more accurate, and relying on removeAllViewsInLayout alone
	 * did not work as the error "specified child already has a parent" occurs.
	 * http://stackoverflow.com/questions/6526874/call-removeview-on-the-childs-parent-first
	 * 
	 */
	private void clearTableLayoutViews() {
		
		TableLayout tableLayout = (TableLayout) findViewById(R.id.cardTable);
		
		for (Card card : cardsOnTable) {
			TableRow tr = (TableRow) card.getParent();
			if(tr != null) {
				tr.removeView(card);
			}
		}
		//Remove other objects like rows themselves
		tableLayout.removeAllViewsInLayout();
		
	}
	
	/**
	 * Indicates if this card makes a set based on the matchQtyForPoints being met
	 * @param cardIn
	 * @return
	 */
	private boolean hasCompleteMatchBeenReached(Card cardIn) {
		int cardCounter=0;
		for (Card card : cardsOnTable) {
			
			if (cardIn.getCardType() == card.getCardType() && card.hasCardState(CardState.MATCH_CANDIDATE)) {
				
				if( ++cardCounter == matchQtyForPoints ) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * Sets cards of this type to be in MATCHED state
	 * @param cardIn
	 */
	private void setMatchedState(Card cardIn) {
		for (Card card : cardsOnTable) {
			
			if (cardIn.getCardType() == card.getCardType() && card.hasCardState(CardState.MATCH_CANDIDATE)) {
				card.setCardState(CardState.MATCHED);
			}
		}
		return;
	}
	
	
	private void turnOverSelectedCards() {
		
		for (Card card : cardsOnTable) {
			
			if ( card.hasCardState(CardState.SELECTED) ) {
				card.showBack();
			}
		}
		return;
	}
	
	
	/**
	 * Called for a new game
	 */
	private void resetCards() {

		for (Card card : cardsOnTable) {
			card.showBack();
			card.setCardState(CardState.BACK_SHOWN);
			
		}
		
		clearTableLayoutViews();
		
		generateRandomCardIds(cardIds, randomCardIds, numberOfCardsInSubset);
		buildTable(randomCardIds, getResources().getConfiguration().orientation, numberOfCardsInSubset, true);

		positiveMatchPoints=0; 
		negativeMatchPoints=0;
		setActionBar();
	}

	
	/**
	 * 
	 */
	private OnTouchListener mClickListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent mev) {

			int action = mev.getAction();
			if (action != MotionEvent.ACTION_DOWN) {
				return true;
			}

			final Card card = (Card) v;
			if (card.hasCardState(CardState.BACK_SHOWN)) {
				card.showFront();
				card.setCardState(CardState.SELECTED);
			} else {
				return true;
			}

			if (cardsMatched(card)) {
				if(hasCompleteMatchBeenReached(card)) {
					setMatchedState(card);
					positiveMatchPoints++;
					Toast.makeText(getApplicationContext(), "Great - a match!", Toast.LENGTH_SHORT).show();
				}
				
			} else {
				//Card selected has no match that was previously selected
				if( (cardsSelectedStateQty(CardState.SELECTED) == 2) || 
						(cardsSelectedStateQty(CardState.SELECTED)==1 && cardsSelectedStateQty(CardState.MATCH_CANDIDATE)==2) ) {
					
					Toast.makeText(getApplicationContext(), "Sorry - not a match!", Toast.LENGTH_SHORT).show();
					negativeMatchPoints++;
					
					final Handler handler = new Handler();
					
					Runnable runnable = new Runnable() {
						   @Override
						   public void run() {
							   card.showBack();
							   turnOverSelectedCards();
						   }
						};
					
					
					handler.postDelayed(runnable, 2000);
					
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
				setActionBar();
				
			case MENU_GUESS_TWO:
				matchQtyForPoints=3;
				setActionBar();
				
		}
		return true;
	}
}