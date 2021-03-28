package PokerHand;

import PokerHand.*;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import java.awt.Color;
import java.awt.Font;


public class UserInterface {
	
	private String[] playerNames = {"Player1", "Player2"};
	
	private static final String fontFamily = "Arial";
	private static final Color handBg = new Color(208,208,255);
	private static final Color winColor = Color.green;
	
	private JButton[] dealButtons = new JButton[52];
	private JPanel topHand;
	private JPanel bottomHand;
	// These are the internal text values, like "KH".
	private String[] cards = new String[10];
	// These are the display areas for the cards that have been dealt.
	private JTextPane[] cardTexts = new JTextPane[10];
	// This is where the next card will go.
	private int nextDealTarget = 0;
	
	private JTextPane resultText;
	private JButton compareButton;
	
	// When a button is clicked, it gets disabled, so you cannot deal it again.
	private void disableDealButton(int n) {
		dealButtons[n].setEnabled(false);
	}
	
	// Start over.
	private void reset() {
		// Enable all the buttons...
		for(int c = 0; c < 52; c++) dealButtons[c].setEnabled(true);
		// ...clear the hands...
		for(int h = 0; h < 10; h++) {
			cardTexts[h].setText("     ...  ");
			cardTexts[h].setForeground(Color.gray);
			cards[h] = "";
		}
		// ...reset the dealt cards...
		topHand.setBackground(handBg);
		bottomHand.setBackground(handBg);
		nextDealTarget = 0;
		// ...disable the "Compare" button...
		compareButton.setEnabled(false);
		// ...and clear the result.
		resultText.setText(" \n \n \n ");
	}
	
	// Compare the two hands, and show who wins!
	private void compareHands() {
		// Build the two "hands"
		String hand1 = "";
		String hand2 = "";
		for(int n = 0; n < 5; n++) {
			if(n > 0) {
				hand1 += ",";
				hand2 += ",";
			}
			hand1 += cards[n];
			hand2 += cards[n+5];
		}
		try {
			// Compare the two hands, and show the winner.
			System.out.println("Comparing " + hand1 + " to " + hand2);
			HandEvaluator.Value v1 = new HandEvaluator().evaluateHand(hand1);
			HandEvaluator.Value v2 = new HandEvaluator().evaluateHand(hand2);
			if(v1.compareTo(v2) > 0) {
				topHand.setBackground(winColor);
				topHand.setOpaque(true);
				System.out.println(playerNames[0] + " WINS!");
				System.out.println(v1.description + " beats " + v2.description);
				resultText.setText(playerNames[0] + " wins!\n" + v1.description + "\n  beats\n" + v2.description);
			}
			else if(v1.compareTo(v2) == 0) {
				System.out.println("TIE!");
				resultText.setText(v1.description + "\n ties\n" + v2.description);
			}
			else {
				bottomHand.setBackground(winColor);
				bottomHand.setOpaque(true);
				System.out.println(playerNames[1] + " WINS!");
				System.out.println(v2.description + " beats " + v1.description);
				resultText.setText(playerNames[1] + " wins!\n" + v2.description + "\n  beats\n" + v1.description);
			}
			
			compareButton.setEnabled(false);
			
		}
		catch(HandEvaluator.InvalidHandException e) {
			System.out.println("ERROR!");
		}
	}
		
	
	
	private void executeUserInterface() {

		JFrame frame = new JFrame("Deal Some Poker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridBagLayout());
		
		
		// Put a little help text at the top. Why not?
		JTextArea helpText = 
				new JTextArea("Click on the cards to deal the hands,\nthen click 'Compare'",
				3,1);
		helpText.setFont(new Font(fontFamily, Font.BOLD, 16));
		helpText.setOpaque(false);
		helpText.setEditable(false);
		GridBagConstraints helpTextConstraints = new GridBagConstraints();
		helpTextConstraints.gridx = 0;
		helpTextConstraints.gridy = 0;
		helpTextConstraints.anchor = GridBagConstraints.WEST;
		helpTextConstraints.gridwidth = 2;
		frame.add(helpText, helpTextConstraints);
		
		
		// There are 52 buttons, used to deal the cards.
		// You can click each one only once (until you reset).
		
		final String[] Nums = {" 2"," 3"," 4"," 5"," 6"," 7"," 8"," 9"," 10"," J"," Q"," K"," A"};
		final String[] Suits = {"C","D","H","S"};
		// Unicode values for the suits.
		final String[] SuitSymbols = {"\u2663", "\u2666", "\u2665", "\u2660"};

		// Make a little grid to hold the buttons.
		JPanel dealPanel = new JPanel();
		dealPanel.setLayout(new GridLayout(13, 9));

		int nButton = 0;
		for(int n = 0; n < Nums.length; n++) {
			for(int s = 0; s < Suits.length; s++) {
				
				final String display = Nums[n] + SuitSymbols[s];
				final String action = Nums[n] + Suits[s];
				final int nb = nButton;
				final Color color = 
					(Suits[s].matches("H") || Suits[s].matches("D")) ? Color.red : Color.black;
				
				dealButtons[nButton] = new JButton(new AbstractAction(display) {
					public void actionPerformed(ActionEvent e) {
						System.out.println(action);
						if(nextDealTarget < 10) {
							// disable myself...
							disableDealButton(nb);
							// ... and deal the card into the next slot.
							cardTexts[nextDealTarget].setText("   " + display);
							cardTexts[nextDealTarget].setForeground(color);
							cards[nextDealTarget] = action;
							nextDealTarget++;
						}
						if(nextDealTarget == 10) {
							// Now that all 10 cards have been dealt, 
							// we can compare the hands.
							compareButton.setEnabled(true);
						}
					}
				});
				dealButtons[nButton].setForeground(color);
				dealButtons[nButton].setFont(new Font(fontFamily, Font.PLAIN, 20));
				dealPanel.add(dealButtons[nButton]);
				nButton++;
			}
			
		}

		// On the right are the two dealt hands.
	
		JPanel handsPanel = new JPanel();
		handsPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints constraintsTopHand = new GridBagConstraints();
		constraintsTopHand.gridx = 0;
		constraintsTopHand.gridy = 0;
		constraintsTopHand.weightx = 1.0;
		constraintsTopHand.weighty = 1.0;
		constraintsTopHand.ipady = 20;
		constraintsTopHand.ipadx = 20;
		constraintsTopHand.anchor = GridBagConstraints.NORTH;
		constraintsTopHand.fill = GridBagConstraints.HORIZONTAL;
		
		topHand = new JPanel();
		topHand.setLayout(new GridLayout(5,1));
		topHand.setBackground(new Color(128,128,255));
		for(int n = 0; n < 5; n++) {
			cardTexts[n] = new JTextPane();
			cardTexts[n].setOpaque(false);
			cardTexts[n].setForeground(Color.gray);
			cardTexts[n].setFont(new Font(fontFamily, Font.BOLD, 20));
			cardTexts[n].setEditable(false);
			topHand.add(cardTexts[n]);
		}
		handsPanel.add(topHand, constraintsTopHand);

		
		
		GridBagConstraints constraintsBottomHand = new GridBagConstraints();
		constraintsBottomHand.gridx = 0;
		constraintsBottomHand.gridy = 1;
		constraintsBottomHand.weightx = 1.0;
		constraintsBottomHand.weighty = 1.0;
		constraintsBottomHand.ipady = 20;
		constraintsBottomHand.ipadx = 20;
		constraintsBottomHand.anchor = GridBagConstraints.SOUTH;
		constraintsBottomHand.fill = GridBagConstraints.HORIZONTAL;

		
		bottomHand = new JPanel();
		bottomHand.setLayout(new GridLayout(5,1));
		bottomHand.setBackground(new Color(128,128,255));
		for(int n = 0; n < 5; n++) {
			cardTexts[n+5] = new JTextPane();	// JTextArea("", 1, 1);
			cardTexts[n+5].setOpaque(false);
			cardTexts[n+5].setForeground(Color.gray);
			cardTexts[n+5].setFont(new Font(fontFamily, Font.BOLD, 20));
			cardTexts[n+5].setEditable(false);
			bottomHand.add(cardTexts[n+5]);
		}
		handsPanel.add(bottomHand, constraintsBottomHand);


		GridBagConstraints dealPanelConstraints = new GridBagConstraints();
		dealPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		dealPanelConstraints.gridx = 0;
		dealPanelConstraints.gridy = 1;
		dealPanelConstraints.weightx = 0.5;
		frame.add(dealPanel, dealPanelConstraints);
		
		
		GridBagConstraints handsConstraints = new GridBagConstraints();
		handsConstraints.fill = GridBagConstraints.BOTH;
		handsConstraints.gridx = 1;
		handsConstraints.gridy = 1;
		handsConstraints.ipadx = 40;
		handsConstraints.weightx = 0.5;
		//handsConstraints.insets = new Insets(0,30,0,0);
		frame.add(handsPanel, handsConstraints);


		// At the bottom, there's a reset and compare button.
        JButton resetButton = new JButton(new AbstractAction("Reset") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("RESET!");
				reset();
            }
        });
		resetButton.setFont(new Font(fontFamily, Font.BOLD, 20));
		
        compareButton = new JButton(new AbstractAction("Compare!") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("COMPARING!");
				compareHands();
            }
        });
		compareButton.setFont(new Font(fontFamily, Font.BOLD, 20));
		compareButton.setEnabled(false);

		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.gridx = 0;
		buttonConstraints.gridy = 2;
		buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
		frame.add(resetButton, buttonConstraints);
		
		buttonConstraints.gridx = 1;
		frame.add(compareButton, buttonConstraints);
		
		// Below the buttons, a field to tell you who wins!
		resultText = new JTextPane();
		resultText.setFont(new Font(fontFamily, Font.BOLD, 16));
		resultText.setText(" \n \n \n ");
		resultText.setEditable(false);
		GridBagConstraints resultConstraints = new GridBagConstraints();
		resultConstraints.gridx = 0;
		resultConstraints.gridy = 3;
		resultConstraints.gridwidth = 2;
		resultConstraints.fill = GridBagConstraints.HORIZONTAL;
		frame.add(resultText, resultConstraints);

	
        frame.pack();
        frame.setVisible(true);
		
		reset();
	}
	
	
	
	public static void main(String[] args) {
		new UserInterface().executeUserInterface();
	}
		
	
}
