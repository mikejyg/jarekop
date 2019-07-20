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

package mikejyg.playingCards;

import java.util.ArrayList;
import java.util.Random;

import mikejyg.utilities.ArrayListUtils;

import org.apache.log4j.Logger;

/**
 * A Deck of cards, with shuffling, dispensing, adding, removing, and etc, capabilities.
 * 
 */
public class Deck {
	
	static Logger logger = Logger.getLogger(Deck.class);
	
	private ArrayList<Card> cards = new ArrayList<Card>();
	
	private Random random;
	
	private boolean useGuShuffle = false;
	
	public Deck() {
		random = new Random();
		init();
	}

	/**
	 *  initialize deck with a specific random seed
	 * @param randomSeed
	 */
	public Deck(long randomSeed) {
		random = new Random(randomSeed);
		init();
	}
	
	/**
	 * fill the deck with the 52 cards, in native order
	 */
	private void init() {
		clear();
		for (int i=0; i<Card.TOTAL_CARDS; i++) {
			addCard(new Card(i));
		}
		logger.debug("cards: " + toString());
	}

	/**
	 * shuffle the deck, using the default method.
	 */
	public void shuffle() {
		if (useGuShuffle)
			shuffleGu();
		else
			shuffle2();
		logger.debug("shuffle: " + toString());
	}
	
	/**
	 *  my own algorithm, seems to produce somewhat biased outcome
	 */
	public void shuffleGu() {
		for (int i=0; i<cards.size(); i++) {
			int subIdx = random.nextInt(cards.size());
			// switch places of a pair of cards
			swapCards(i, subIdx);
		}
	}

	/**
	 *  Durstenfeld's version of Fisher–Yates shuffle
	 */
	public void shuffle2() {
		for (int i=cards.size()-1; i>0; i--) {
			int subIdx = random.nextInt(i+1);
			// switch places of a pair of cards
			swapCards(i, subIdx);
		}
	}
	
	public void swapCards(int idx1, int idx2) {
		if (idx1==idx2)
			return;
		
		Card tmp = cards.get(idx1);
		cards.set( idx1, cards.get(idx2) );
		cards.set(idx2, tmp);
	}
	
	public Card removeHeadCard() {
		return cards.remove(0);
	}
	
	/**
	 *  get and remove a card from the end of the deck
	 */
	public Card removeTailCard() {
		return ArrayListUtils.removeTail(cards);
	}
	
	/**
	 * clear all cards from the deck
	 */
	public void clear() {
		cards.clear();
	}
	
	/**
	 * add a card
	 */
	public void addCard(Card card) {
		cards.add(card);
	}
	
	/**
	 * remove a card 
	 */
	public void removeCard(Card card) {
		cards.remove(card);
	}
	
	/**
	 * add all cards from another deck
	 */
	public void addDeck(Deck o) {
		cards.addAll(o.cards);
	}
	
	@Override
	public String toString() {
		String str="";
		for (int i=0; i<cards.size(); i++)
			str += cards.get(i) + " ";
		return str;
	}
	
	
}
