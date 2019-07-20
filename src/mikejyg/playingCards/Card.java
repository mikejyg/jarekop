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

/**
 * A Card consists of a Suit and a Rank.
 * Note when getting the rank of ACE, it returns 14, instead of 1.
 * It will however recognize both 1 and 14 as ACE when converting an integer to a rank.
 * 
 * For each Suit, Rank and Card, there is a one-to-one matching of an integer value.
 * 
 */
public class Card implements Comparable<Card> {

	public static final int TOTAL_CARDS = 52;
	
	public enum Suit {
		CLUB(0, 'C'),
		DIAMOND(1, 'D'),
		HEART(2, 'H'),
		SPADE(3, 'S');
		
		private final int intValue;
		private final Character symbol;
		
		private static final Suit [] INT_SUIT_MAP = {SPADE, HEART, DIAMOND, CLUB};
		
		Suit(int intValue, Character symbol) {
			this.intValue = intValue;
			this.symbol = symbol;
		}

		public int getIntValue() {
			return intValue;
		}

		public Character getSymbol() {
			return symbol;
		}
		
		static public Suit getSuit(int suit) {
			return INT_SUIT_MAP[suit];
		}
		
	}
	
	public enum Rank {
		TWO(2, '2'),
		THREE(3, '3'),
		FOUR(4, '4'),
		FIVE(5, '5'),
		SIX(6, '6'),
		SEVEN(7, '7'),
		EIGHT(8, '8'),
		NINE(9, '9'),
		TEN(10, 'T'),
		JACK(11, 'J'),
		QUEEN(12, 'Q'),
		KING(13, 'K'),
		ACE(14, 'A');
		
		private final int intValue;
		private final Character symbol;

		Rank(int intValue, Character symbol) {
			this.intValue = intValue;
			this.symbol = symbol;
		}

		public int getIntValue() {
			return intValue;
		}

		public Character getSymbol() {
			return symbol;
		}

		private static final Rank [] INT_RANK_MAP = {null, ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN,
			EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE}; 
		
		// both 1 and 14 are recognized as ACE
		public static Rank getRank(int intValue) {
			return INT_RANK_MAP[intValue];
		}
		
	}
	
	private Rank rank;
	private Suit suit;

	public Card() {
		rank=Rank.ACE;
		suit=Suit.SPADE;
	}

	/**
	 *  rank: 1-14, suite: 0-3
	 * @param rank
	 * @param suit
	 */
	public Card(int rank, int suit) {
		this.rank = Rank.getRank(rank);
		this.suit = Suit.getSuit(suit);
	}

	/**
	 *  construct card from a unique number, [0-51]
	 * @param intValue
	 */
	public Card(int intValue) {
		rank = Rank.getRank( intValue%13 + 1 );
		suit = Suit.getSuit( intValue/13 );
	}
	
	/**
	 * @return a unique value of the card, [0-51]
	 */
	public int getIntValue() {
		int rankInt = rank.intValue - 1;
		if (rankInt==13)
			rankInt = 0;
		return suit.intValue*13+rankInt;
	}

	@Override
	public String toString() {
		return rank.getSymbol().toString() + suit.getSymbol();
	}

	@Override
	public int compareTo(Card o) {
		int c = rank.compareTo(o.rank);
		if (c!=0)
			return c;
		else
			return suit.compareTo(o.suit);
	}

	@Override
	public boolean equals(Object obj) {
		Card card2 = (Card) obj;
		if (rank == card2.rank && suit == card2.suit)
			return true;
		else
			return false;
	}
	
	////////////////////////////
	// getters and setters
	
	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public Suit getSuit() {
		return suit;
	}

	public void setSuit(Suit suit) {
		this.suit = suit;
	}

	
}
