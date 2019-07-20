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

package mikejyg.playingCards.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import mikejyg.playingCards.Card;
import mikejyg.playingCards.Card.Suit;
import mikejyg.texasHoldem.gui.TableDrawPanel;

/**
 * This class Initializes and provides images for Cards.
 * Currently the SVG Deck 75 is used.
 */
public class CardImages {

	/**
	 * uses same integer mapping as class Card 
	 */
	BufferedImage [] cardImages;
	
	public CardImages() throws IOException {
		loadSvgDeck75Images();
	}
	
	private void loadSvgDeck75Images() throws IOException {
		cardImages = new BufferedImage[Card.TOTAL_CARDS];
		
		for (int i=0; i<Card.TOTAL_CARDS; i++) {
			Card card = new Card(i);
			
			// compose file name
			
			Suit suit = card.getSuit();
			
			String suffix = suit==Suit.SPADE ? "spades" : suit==Suit.HEART ? "hearts" : 
				suit==Suit.DIAMOND ? "diamonds" : "clubs";
			
			int rank = card.getRank().getIntValue();
			
			String rankStr;
			if (rank==14)
				rankStr="a";
			else if (rank==11)
				rankStr="j";
			else if (rank==12)
				rankStr="q";
			else if (rank==13)
				rankStr="k";
			else
				rankStr = Integer.toString(rank);

			String fileName;
			fileName="mikejyg/playingCards/gui/svgdeck/75/" + suffix + "-" + rankStr + "-75.png";
				
			URL fileUrl = TableDrawPanel.class.getClassLoader().getResource(fileName);
				
			cardImages[card.getIntValue()] = ImageIO.read(fileUrl);
		}
		
	}

	public BufferedImage getCardImage(Card card) {
		return cardImages[card.getIntValue()];
	}
	
	/**
	 * assuming all width are the same
	 * @return width of card image
	 */
	public int getWidth() {
		return cardImages[0].getWidth();
	}
	
	/**
	 * assuming all height are the same
	 * @return height of card image
	 */
	public int getHeight() {
		return cardImages[0].getHeight();
	}
	
	
}
