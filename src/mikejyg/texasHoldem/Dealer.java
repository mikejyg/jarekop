/*
	Copyright 2012 Junyang Gu

	This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package mikejyg.texasHoldem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;

import mikejyg.playingCards.Card;
import mikejyg.playingCards.Deck;

/**
 * this class deals cards to table and hands; It also scores them;
 * 
 * It uses model-view-controller architecture
 * this is the model (dealt cards)
 * 
 */
public class Dealer extends Observable {

	private Deck deck;
	
	private int handsTotal;
	
	private Card [][] handCards;  
	
	private Card [] tableCards = new Card[5];
	
	/**
	 * The state of the dealer.
	 * When in RIVER, the card dealing process is complete.
	 */
	enum DealerState {
		CLEAR,
		PREFLOP,
		FLOP,
		TURN,
		RIVER;
	}
	
	private DealerState dealerState = DealerState.CLEAR; 
	
	private boolean scored = false;
	
	public Dealer() {
		// default deck
		deck = new Deck();
		setHandsTotal(9);
	}
	
	public Dealer(long randSeed) {
		// default deck
		deck = new Deck(randSeed);
		setHandsTotal(9);
	}	

	/**
	 * performs one step of the card dealing process
	 */
	public void deal() {
		switch (dealerState) {
		
		case CLEAR:
			newDeal();
			break;
			
		case PREFLOP:
			flop();
			break;
			
		case FLOP:
			turn();
			break;
			
		case TURN:
			river();
			break;
			
		case RIVER:
			clear();
			break;
		}
	}
	
	private void newDeal() {
		deck.shuffle();
		dealHands();
		dealerState = DealerState.PREFLOP;
		setChanged();
	}
	
	private void dealHands() {
		for (int i=0; i<2; i++) {
			for (int j=0; j<handsTotal; j++) {
				handCards[j][i] = deck.removeTailCard();
			}
		}
		setChanged();
	}

	private void flop() {
		for (int i=0; i<3; i++) {
			tableCards[i] = deck.removeTailCard();
		}
		dealerState = DealerState.FLOP;
		setChanged();
	}
	
	private void turn() {
		tableCards[3] = deck.removeTailCard();
		dealerState = DealerState.TURN;
		setChanged();
	}
	
	private void river() {
		tableCards[4] = deck.removeTailCard();
		dealerState = DealerState.RIVER;
		setChanged();
	}
	
	public void clear() {
		for (int i=0; i<getHandsTotal(); i++) {
			Arrays.fill(handCards[i], null);
		}

		Arrays.fill(tableCards, null);
		
		dealerState = DealerState.CLEAR;
		scored = false;
		
		setChanged();
	}
	
	@Override
	public String toString() {
		
		String str="";
		
		for (int i=0; i<5; i++) {
			if (tableCards[i]!=null)
				str += tableCards[i].toString() + ' ';
		}
		
		String NL = System.getProperty("line.separator");
		str += NL;
		
		for (int i=0; i<handsTotal; i++) {
			if (handCards[i][0]!=null) {
				str += (i+1) + ": " + handCards[i][0].toString() + ' ';
				str += handCards[i][1].toString() + "  ";
			}
		}
		
		return str;
	}
	
	public void setHandsTotal(int hands) {
		this.handsTotal = hands;
		handCards = new Card[getHandsTotal()][2];
	}
	
	public boolean isCompleted() {
		return dealerState == DealerState.RIVER;
	}
	
	private ArrayList<Score> scoreList = new ArrayList<Score>();

	private HashMap<Integer, Score> handToScoreMap = new HashMap<Integer, Score>();
	private HashMap<Score, Integer> scoreToHandMap = new HashMap<Score, Integer>();

	private HashMap<Score, Integer> scoreToRankMap = new HashMap<Score, Integer>();
	private HashMap<Score, Boolean> scoreToTiedMap = new HashMap<Score, Boolean>();
	
	public void scoreHands() {
		scoreList.clear();
		Evaluator evaluator = new Evaluator();
		for (int i=0; i<getHandsTotal(); i++) {
			
			Card [] cards = getCombinedCards(i);

			Score score = evaluator.evalulate(cards);
			scoreList.add( score );
			
			handToScoreMap.put(i, score);
			scoreToHandMap.put(score, i);
		}
		
		// sort the scores
		Collections.sort(scoreList);
		
		// rank the scores
		
		boolean equalPrevious = false;
		boolean equalNext = false;
		int equalRank = 1;
		Score score;
		Score scoreNext;
		
		for (int i=0; i<getHandsTotal(); i++) {
			
			int rank = i+1;
			score = scoreList.get(getHandsTotal()-i-1);
			
			equalPrevious = equalNext;
			
			// compare with next score to detect tie
			if (getHandsTotal()-i-1 != 0) {
				
				scoreNext = scoreList.get(getHandsTotal()-i-2);
				
				if (score.equals(scoreNext)) {
					// tied with next hand

					// set equal rank, if the starting rank of the tie
					if (!equalPrevious)
						equalRank = rank;

					equalNext = true;
					
				} else {
					equalNext = false;
					
				}
				
			} else
				equalNext = false;
			
			if (equalPrevious)	{
				// mark from previous hand
				rank = equalRank;
			}
			
			scoreToRankMap.put(score, rank);
			scoreToTiedMap.put(score, equalPrevious || equalNext);
		}
		
		scored = true;
		
		setChanged();
	}
	
	private Card [] getCombinedCards(int hand) {
		Card [] cards = new Card[7];
		for (int j=0; j<5; j++) {
			cards[j] = getTableCards()[j];
		}
		for (int j=0; j<2; j++) {
			cards[5+j] = getHandCards()[hand][j];
		}
		return cards;
	}
	
	////////////////////////////////
	// getters and setters
	
	public Deck getDeck() {
		return deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	public int getHandsTotal() {
		return handsTotal;
	}

	public Card[][] getHandCards() {
		return handCards;
	}

	public void setHandCards(Card[][] handCards) {
		this.handCards = handCards;
	}

	public Card[] getTableCards() {
		return tableCards;
	}

	public void setTableCards(Card[] tableCards) {
		this.tableCards = tableCards;
	}

	public DealerState getDealerState() {
		return dealerState;
	}

	// scoreList is sorted according to natural order
	public ArrayList<Score> getScoreList() {
		return scoreList;
	}
	
	public Score getScoreByHand(int hand) {
		return handToScoreMap.get(hand);
	}
	
	public int getHandByScore(Score score) {
		return scoreToHandMap.get(score);
	}
	
	public int getRankByScore(Score score) {
		return scoreToRankMap.get(score);
	}
	
	public boolean isTied(Score score) {
		return scoreToTiedMap.get(score);
	}
	
	public boolean isScored() {
		return scored;
	}

	/////////////////////////////////////////////////
	// test
	
	public static void main(String[] args) {

		Dealer dealer = new Dealer();
		
		while (!dealer.isCompleted())
			dealer.deal();
		
		System.out.println(dealer.toString());
		
		// evaluate and rank each hand
		dealer.scoreHands();
		
		ArrayList<Score> scoreList = dealer.getScoreList();

		for (int i=0; i<dealer.getHandsTotal(); i++) {
			
			Score score = scoreList.get(dealer.getHandsTotal()-i-1);
			
			int hand = dealer.getHandByScore(score);
			int rank = dealer.getRankByScore(score);
			boolean isTied = dealer.isTied(score);
			
			System.out.println("rank: " + rank + ", hand: " + (hand+1) + 
					", score: " + score.toString() + ( isTied? " (tied)" : "") );
		}
		
	}

	
}
