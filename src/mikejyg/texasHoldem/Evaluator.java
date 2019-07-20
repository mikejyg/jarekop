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

import org.apache.log4j.Logger;


import mikejyg.playingCards.Card;
import mikejyg.playingCards.Deck;
import mikejyg.playingCards.Card.Rank;
import mikejyg.texasHoldem.Score.Pattern;
import mikejyg.utilities.ArrayListUtils;

/**
 * a class that evaluates a hand and produces a score
 *  
 */
public class Evaluator {

	static Logger logger = Logger.getLogger(Evaluator.class);
	
	Score score;

	private ArrayList<Card> remainingCards = new ArrayList<Card>();
	
	private ArrayList<Card> straightCards = new ArrayList<Card>();
	
	/////////////////////////////
	// working variables
	private ArrayList<Card> cards;
	private int totalCards;
	private ArrayList<Card> highCards;
	
	public Score evalulate(Card [] cards) {
		
		// init fields
		this.cards = new ArrayList<Card>(Arrays.asList(cards));
		totalCards = cards.length;
		Collections.sort(this.cards);
		logger.debug("evaluate: " + this.cards.toString());
		
		score = new Score();
		
		highCards = score.getHighCards();

		remainingCards.clear();
		remainingCards.addAll(this.cards);
		
		// count cards in suits, look for flush, straight flush
		if ( ! searchBySuit() ) {

			// flush and four of a kind, full house are mutually exclusive
			// so it is safe to search flush before four of a kind and full house
			searchByRank();
		}
		
		logger.debug("score: " + score);
		logger.debug("not used: " + remainingCards.toString());
		
		sanityCheck();
		
		return score;
	}

	private void sanityCheck() {
		// sanity check
		// high cards + remaining == cards
		
		if ( highCards.size() !=5 || remainingCards.size() != 2 ) {
			throw new Error("sanity check failed!");
		}
		
		ArrayList<Card> check = new ArrayList<Card>(highCards);
		check.addAll(remainingCards);
		Collections.sort(check);
		
		if ( !check.equals(cards) ) {
			throw new Error("sanity check failed!");
		}
	}

	/**
	 * count cards in suits, look for flush, straight in suits (straight flush)
	 * @return
	 */
	private boolean searchBySuit() {
		
		// arrange cards in suits
		
		@SuppressWarnings("unchecked")
		ArrayList<Card> [] cardsInSuits = new ArrayList[4];
		for (int i=0; i<4; i++) {
			cardsInSuits[i] = new ArrayList<Card>();
		}
		
		for (Card card : cards) {
			cardsInSuits[card.getSuit().getIntValue()].add(card);
		}

		for (ArrayList<Card> cardList : cardsInSuits) {
			if (cardList.size() >=5) {
				
				// flush found, look for straight
				Collections.sort(cardList);
				logger.debug("searchBySuit: " + cardList.toString());
				
				if (searchStraight(cardList)) {
					// found straight flush
					score.setPattern(Pattern.STRAIGHT_FLUSH);
					highCards.addAll(straightCards);
					remainingCards.removeAll(highCards);
					
					if (highCards.get(0).getRank() == Rank.ACE)
						score.setPattern(Pattern.ROYAL_FLUSH);
					
					return true;
				}
				
				score.setPattern(Pattern.FLUSH);
				
				// fill pattern
				for (int j=0; j<5; j++) {
					highCards.add(ArrayListUtils.removeTail(cardList));
				}
				remainingCards.removeAll(highCards);
				
				return true;		
			}
		}
		return false;
	}

	/**
	 * cardRanks is sorted
	 * @param a list of cards
	 * @return highest ranking, 14 if ace, 0 not found
	 */
	private boolean searchStraight(ArrayList<Card> cardList0) {
		
		// duplicates it to avoid mutability
		ArrayList<Card> cardList = new ArrayList<Card>(cardList0);
		
		if (cardList.size() < 5) {
			logger.warn("searchStraight: asked to search straight on " + cardList.toString());
			return false;
		}
		
		// check to see if there is a ACE
		if ( ArrayListUtils.getTail(cardList).getRank() == Rank.ACE) {
			// duplicate the ACE at the head
			cardList.add(0, ArrayListUtils.getTail(cardList));
		}
		
		int span=0;
		Card prevCard = null;

		straightCards.clear();
		
		for (int cardIdx = cardList.size()-1; cardIdx>=0; cardIdx-- ) {
			
			Card currentCard = cardList.get(cardIdx);
			
			if ( prevCard ==null || 
					currentCard.getRank().getIntValue() == prevCard.getRank().getIntValue()-1
					|| currentCard.getRank()==Rank.ACE && prevCard.getRank()==Rank.TWO ) {

				span++;
				
				straightCards.add(currentCard);

				if (span==5)
					return true;

				prevCard = currentCard;

			} else if ( currentCard.getRank().getIntValue() == prevCard.getRank().getIntValue() ) {
				continue;
				
			} else {
				straightCards.clear();
				straightCards.add(currentCard);

				prevCard=currentCard;
				span=1;
			}
		}
		
		straightCards.clear();
		
		return false;
	}

	private void searchByRank() {
		
		// lists of a pile of cards of the same rank
		ArrayList< ArrayList<Card> > duplicatesLists = new ArrayList<ArrayList<Card>>(); 
		
		// seperate cards into piles

		ArrayList<Card> tempList = new ArrayList<Card>();

		int highestSameCardCnt = 1;
		int highestSameCardListIdx = 0;
		
		boolean hasPair = false;
		int pairIdx = 0;
		
		boolean hasSecondPair = false;
		int secondPairIdx = 0;
		
		boolean streak = false;
		
		for (int cardIdx = totalCards-1; cardIdx >=0; cardIdx--) {
			
			if (tempList.size()==0 
					|| cards.get(cardIdx).getRank() == tempList.get(0).getRank() ) {
				tempList.add(cards.get(cardIdx));
				streak = true;
			} else
				streak = false;
			
			if ( !streak || cardIdx == 0 ) {
				duplicatesLists.add(tempList);

				if ( !hasPair && tempList.size()==2 ) {
					hasPair=true;
					pairIdx = duplicatesLists.size()-1;
				} else if ( !hasSecondPair && tempList.size()==2 ) {
					hasSecondPair = true;
					secondPairIdx = duplicatesLists.size()-1;
				}
				
				if (tempList.size() >highestSameCardCnt) {
					highestSameCardCnt = tempList.size();
					highestSameCardListIdx = duplicatesLists.size()-1;
				}
				
				tempList=new ArrayList<Card>();
				tempList.add(cards.get(cardIdx));
				
				streak = true;
			}
		}
		
		if (highestSameCardCnt ==4) {
			score.setPattern(Pattern.FOUR_OF_A_KIND);
			highCards.addAll(duplicatesLists.get(highestSameCardListIdx));
			remainingCards.removeAll(duplicatesLists.get(highestSameCardListIdx));
			highCards.add(ArrayListUtils.removeTail(remainingCards));
			return;
		}
		
		if (highestSameCardCnt==3 && hasPair) {
			score.setPattern(Pattern.FULL_HOUSE);
			highCards.addAll(duplicatesLists.get(highestSameCardListIdx));
			highCards.addAll(duplicatesLists.get(pairIdx));
			remainingCards.removeAll(highCards);
			return;
		}

		if (searchStraight(cards)) {
			score.setPattern(Pattern.STRAIGHT);
			highCards.addAll(straightCards);
			remainingCards.removeAll(highCards);
			return;
		}
						
		if (highestSameCardCnt==3) {
			highCards.addAll(duplicatesLists.get(highestSameCardListIdx));
			
			// three of a kind
			score.setPattern(Pattern.THREE_OF_A_KIND);
			remainingCards.removeAll(highCards);
			highCards.add(ArrayListUtils.removeTail(remainingCards));
			highCards.add(ArrayListUtils.removeTail(remainingCards));
			return;
		}

		if (hasPair) {
			highCards.addAll(duplicatesLists.get(pairIdx));

			// search for pair
			if (hasSecondPair) {
				score.setPattern(Pattern.TWO_PAIR);
				highCards.addAll(duplicatesLists.get(secondPairIdx));
				remainingCards.removeAll(highCards);
				highCards.add(ArrayListUtils.removeTail(remainingCards));
				return;
			}

			// pair
			score.setPattern(Pattern.PAIR);
			remainingCards.removeAll(highCards);
			for (int j=0; j<3; j++)
				highCards.add(ArrayListUtils.removeTail(remainingCards));
			return;
		}

		// high hard
		
		for (int i=0; i<5; i++) {
			highCards.add(ArrayListUtils.removeTail(remainingCards));
		}
		
	}

	public ArrayList<Card> getRemainingCards() {
		return remainingCards;
	}

	public ArrayList<Card> getStraightCards() {
		return straightCards;
	}

	public Score getScore() {
		return score;
	}

	///////////////////////////////////////////////
	// test
	
	public static void main(String[] args) {
		Deck deck;
		long seed = 1;
		int loopCount = 1000000;
		
		if (args.length>=1) {
			seed = Long.parseLong(args[0]);
		}
		
		if (args.length>=2) {
			loopCount = Integer.parseInt(args[1]);
		}
		
		// accountings
		int [] patternCnt = new int[Pattern.TOTAL_PATTERNS];
		
		for (int cnt=0; cnt<loopCount; cnt++) {
			System.out.println(Long.toString(seed));
			
			deck = new Deck(seed++);
			deck.shuffle();
			Card [] cards = new Card[7];
			for (int i=0; i<7; i++) {
				cards[i] = deck.removeTailCard();
				System.out.print(cards[i].toString() + " ");
			}
			System.out.println();

			Evaluator evaluator = new Evaluator();
			evaluator.evalulate(cards);
			patternCnt[evaluator.getScore().getPattern().getIntValue()]++;
		}
		
		for (int i=0; i<Pattern.TOTAL_PATTERNS; i++) {
			float probability = (float) patternCnt[i] / loopCount; 
			System.out.println( Pattern.getPattern(i).toString() + ": " + patternCnt[i] + 
					", " +  probability*100 + "%" );
		}
		
	}

}
