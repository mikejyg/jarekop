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

package mikejyg.texasHoldem.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

import mikejyg.playingCards.Card;
import mikejyg.playingCards.gui.CardImages;
import mikejyg.texasHoldem.Dealer;
import mikejyg.texasHoldem.Score;

/**
 * A panel for drawing cards and scores
 * 
 * use model-view-controller architecture
 * 
 * this is the view
 * 
 */
public class TableDrawPanel extends JPanel implements Observer {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Dealer dealer;
    
	CardImages cardImages;
	
	public TableDrawPanel() throws IOException {
		// init images
		
		cardImages = new CardImages();
		
		// margin: 10
		// width for 9 hands
		
		setPreferredSize(new Dimension( 10 + 125*8 + 25 + cardImages.getWidth() + 10, 
				10 + 150 + cardImages.getHeight() + 10 + 10));
		
	}
	
	@Override
    public void paint(Graphics g) {

		super.paint(g);
    	
    	if (dealer != null) {
    		// draw the cards
    		
    		Card [] cards = dealer.getTableCards();
    		for (int i=0; i<5; i++) {
    			if ( cards[i] != null )
    				g.drawImage( cardImages.getCardImage(cards[i]), 100 + 100*i, 10, null);
    		}
    		
    		Card [][] handCards = dealer.getHandCards();
    		int xpos = 10;
    		for (int i=0; i<dealer.getHandsTotal(); i++) {
    			if (handCards[i][0] != null)
    				g.drawImage( cardImages.getCardImage(handCards[i][0]), xpos, 150, null);
    			xpos+=25;
    			if (handCards[i][1] != null)
    				g.drawImage( cardImages.getCardImage(handCards[i][1]), xpos, 150, null);
    			xpos+=100;
    		}
    		
    		if (dealer.isScored()) {

    			// draw scores and ranks

    			Font font = new Font("Dialog", Font.BOLD, 12); 
    			g.setFont(font);
    			
        		xpos = 10;
    			for (int i=0; i<dealer.getHandsTotal(); i++) {
    				Score score = dealer.getScoreByHand(i);
    				int rank = dealer.getRankByScore(score);
    				
    				if (rank==1)
    					g.setColor(Color.RED);
    				else
    					g.setColor(Color.BLACK);
    				
    				String str = Integer.toString(rank) + ", " + score.getPattern().toString().toUpperCase();
    				g.drawString(str, xpos, 275);
    				
    				xpos+=125;
    			}
    		}
    	}
    	
    }

	@Override
	public void update(Observable o, Object arg) {
		dealer = (Dealer) o;
		repaint();
	}
	
	////////////////////////////////
	// getters and setters
	
	public Dealer getDealer() {
		return dealer;
	}


}
