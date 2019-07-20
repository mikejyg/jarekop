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

import mikejyg.playingCards.Card;

/**
 * the score of a hand.
 * consists of a pattern and the 5 cards of the highest value.
 */
public class Score implements Comparable<Score> {

	public enum Pattern {
		
		HIGH_CARD(0, "high card"),
		PAIR(1, "pair"),
		TWO_PAIR(2, "two pair"),
		THREE_OF_A_KIND(3, "three of a kind"),
		STRAIGHT(4, "straight"),
		FLUSH(5, "flush"),
		FULL_HOUSE(6, "full house"),
		FOUR_OF_A_KIND(7, "four of a kind"),
		STRAIGHT_FLUSH(8, "straight flush"),
		ROYAL_FLUSH(9, "royal flush");
		
		static public final int TOTAL_PATTERNS = 10;
		
		private int intValue;
		private String string;
	
		static private final Pattern [] PATTERN_MAP = { HIGH_CARD, PAIR, TWO_PAIR, THREE_OF_A_KIND, 
			STRAIGHT, FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH, ROYAL_FLUSH };
		
		private Pattern(int intValue, String string) {
			this.intValue = intValue;
			this.string = string;
		}
		
		public int getIntValue() {
			return intValue;
		}

		@Override
		public String toString() {
			return string;
		}
	
		static public Pattern getPattern(int intValue) {
			return PATTERN_MAP[intValue];
		}
		
	}
	
	private Pattern pattern = Pattern.HIGH_CARD;
	
	private ArrayList<Card> highCards = new ArrayList<Card>();

	@Override
	public String toString() {
		return pattern.toString() + ", " + highCards.toString();
	}
	
	@Override
	public int compareTo(Score o) {
		int c = pattern.compareTo(o.pattern);
		if (c!=0)
			return c;
		else
			return compareHighCards(o);
	}

	@Override
	public boolean equals(Object obj) {
		Score o = (Score) obj;
		if ( pattern == o.pattern && compareHighCards(o)==0 )
			return true;
		else
			return false;
	}
	
	private int compareHighCards(Score o) {
		for (int i=0; i<highCards.size(); i++) {
			if ( highCards.get(i).getRank().getIntValue() < o.highCards.get(i).getRank().getIntValue() )
				return -1;
			else if ( highCards.get(i).getRank().getIntValue() > o.highCards.get(i).getRank().getIntValue() )
				return 0xffff;
		}
		return 0;
	}

	/////////////////////////////
	// getters and setters
	
	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	/**
	 *  get organized highest pattern cards.
	 *  the order of cards shall be from high to low
	 */
	public ArrayList<Card> getHighCards() {
		return highCards;
	}

	public void setHighCards(ArrayList<Card> highCards) {
		this.highCards = highCards;
	}

	
}
