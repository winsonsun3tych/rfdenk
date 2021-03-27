package PokerHand;

import java.util.*;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.Rule;
import org.junit.rules.ExpectedException;


public class HandEvaluator {
	
	public static class InvalidHandException extends Exception {
	}

	private static final String[] faceNames = {"-", "-", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace" };
	private static final String[] faces = { "-", "-", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };
	private static final String[] suits = {"C", "D", "H", "S"};
	
	// The Card class parses the string representation to generate a "number" and a "suit"
	private static class Card {
		
		public int number;
		public int suit;
		
		Card(String card) throws InvalidHandException {
			number = -1;
			suit = -1;
			String suitString = "";
			
			// remove leading whitespace.
			String trimmedCard = card.replaceAll("^\\s+","");
			
			for(int f = 2; f < faces.length; f++) {
				//System.out.println("Testing '" + card.substring(0, faces[f].length()) + "' against '" + faces[f] + "'");
				// "10" has two characters. Fortunately, there's no "1" card!
				if(trimmedCard.substring(0, faces[f].length()).matches(faces[f])) {
					number = f;
					suitString = trimmedCard.substring(faces[f].length());
					break;
				}
			}
			if(number < 2) {
				//System.out.println(card + " unknown face number");
				throw new InvalidHandException();
			}
			
			for(int s = 0; s < suits.length; s++) {
				if(suitString.substring(0,1).matches(suits[s])) {
					suit = s;
					break;
				}
			}
			if(suit < 0) {
				//System.out.println(card + " invalid suit");
				throw new InvalidHandException();
			}
		}
	}

	// This class computes and holds the value of a hand.
	public static class Value {
		
		public static final int STRAIGHT_FLUSH = 10;
		public static final int FOUR_OF_A_KIND = 9;
		public static final int FULL_HOUSE = 8;
		public static final int FLUSH = 7;
		public static final int STRAIGHT = 6;
		public static final int THREE_OF_A_KIND = 5;
		public static final int TWO_PAIR = 4;
		public static final int PAIR = 3;
		public static final int HIGH = 2;
		
		// The "value" of a hand consists of a "handType" (like FLUSH or PAIR)...
		public int handType;
		// ...and, within the handType, a "tiebreaker" value, which is specific
		// to the handType.
		public int tiebreaker;
		// For fun, hold a description of the hand.
		public String description;
		
		// We sort the cards before examining the hand.
		// It makes everything a lot easier.
		private class CardSorter implements Comparator<Card> {
			public int compare(Card card1, Card card2) {
				if(card1.number > card2.number) return 1;
				if(card1.number < card2.number) return -1;
				return 0;
			}
		}
		
		// Compare this hand to other hand.
		public int compareTo(Value other) {
			if(this.handType > other.handType) return 1;
			if(this.handType < other.handType) return -1;
			if(this.tiebreaker > other.tiebreaker) return 1;
			if(this.tiebreaker < other.tiebreaker) return -1;
			return 0;
		}

		// Used for invalid hands
		Value() {
			handType = 0;
			tiebreaker = 0;
		}
		
		Value(String handString) throws InvalidHandException {
			
			// The "handString" looks like "KH,AS,10C,9D,3D".
			// There might be some whitespace, too.
			String[] cardStrings = handString.split(",");
			if(cardStrings.length != 5) {
				//System.out.println("Hand has " + cardStrings.length + " cards, expected 5");
				throw new InvalidHandException();
			}
			
			Card[] hand = new Card[5];
			for(int c = 0; c < 5; c++) {
				hand[c] = new Card(cardStrings[c]);
			}
			// TODO: check: no more than 4 of any number.
			// TODO: check: no duplicate cards (will actually include previous test).
			
			// Sort the hand, low number to high number. This makes it easier
			// to detect the type of hand, and to compute its tiebreaker.
			Arrays.sort(hand, new CardSorter());
			
			// These "checkXyz" functions will fill in the handType, tiebreaker,
			// and description, and return true if the hand matches the filter.
			// If the hand does not match the pattern, then a "checkXyz" function
			// returns false.
			if(checkStraightFlush(hand)) {
			}
			else if(checkFourOfAKind(hand)) {
			}
			else if(checkFullHouse(hand)) {
			}
			else if(checkFlush(hand)) {
			}
			else if(checkStraight(hand)) {
			}
			else if(checkThreeOfAKind(hand)) {
			}
			else if(checkTwoPairs(hand)) {
			}
			else if(checkPair(hand)) {
			}
			else {
				// Fall back to "High card".
				handType = HIGH;
				tiebreaker = 0;
				for(int n = 4; n >= 0; n--) {
					tiebreaker = tiebreaker * 64;
					tiebreaker += hand[n].number;
				}
				description = "" + faceNames[hand[4].number] + " high";
			}
		}

		
		private boolean checkStraightFlush(Card[] hand) {
			if(isStraight(hand) && isFlush(hand)) {
				handType = STRAIGHT_FLUSH;
				// The requirements in the task says that the hands are ranked
				// by the highest card, but there should be a tiebreaker between
				// 10C,JC,QC,KC,AC" and "10D,JD,QD,KD,AD".
				// TODO: tiebreak between suits of a Straight Flush.
				tiebreaker = hand[4].number;
				description = "Straight Flush to the " + faceNames[hand[4].number];
				return true;
			}
			return false;
		}

		// Because the hand is sorted, four-of-a-kind will all appear together.
		// A FOAK hand will be XXXX_, or _XXXX
		private boolean checkFourOfAKind(Card[] hand) {
			for(int s = 0; s < 2; s++) {
				int v = hand[s].number;
				boolean is = true;
				for(int e = s+1; e < s+4; e++) {
					if(hand[e].number != v) {
						is = false;
					}
				}
				if(is) {
					handType = FOUR_OF_A_KIND;
					tiebreaker = v;	// this is enough; there are only 4 of any number.
					description = "Four " + faceNames[v] + "s";
					return true;
				}
			}
			return false;
		}		
		
		private boolean isFullHouseLow(Card[] hand) {
			// A "low" full house is XXXYY
			if(hand[0].number != hand[1].number) return false;
			if(hand[0].number != hand[2].number) return false;
			if(hand[3].number != hand[4].number) return false;
			return true;
		}
		
		private boolean isFullHouseHigh(Card[] hand) {
			// A "high" full house is XXYYY
			if(hand[0].number != hand[1].number) return false;
			if(hand[2].number != hand[3].number) return false;
			if(hand[2].number != hand[4].number) return false;
			return true;
		}
		
		boolean checkFullHouse(Card[] hand) {
			if(isFullHouseLow(hand)) {
				handType = FULL_HOUSE;
				tiebreaker = hand[0].number;
				description = "Full House " + faceNames[hand[0].number] + "s over " + faceNames[hand[4].number] + "s";
				return true;
			}
			else if(isFullHouseHigh(hand)) {
				handType = FULL_HOUSE;
				tiebreaker = hand[4].number;
				description = "Full House " + faceNames[hand[4].number] + "s over "+ faceNames[hand[0].number] + "s";
				return true;
			}
			return false;
		}

		private boolean isFlush(Card[] hand) {
			int firstSuit = hand[0].suit;
			for(int n = 1; n < 5; n++) {
				if(hand[n].suit != firstSuit) return false;
			}
			return true;
		}
		
		private boolean checkFlush(Card[] hand) {
			if(!isFlush(hand)) return false;
			handType = FLUSH;
			// TODO: tiebreak two flushes that differ only by suit.
			tiebreaker = 0;
			for(int n = 4; n >= 0; n--) {
				tiebreaker = (tiebreaker * 64) + hand[n].number;
			}
			description = "Flush to the " + faceNames[hand[4].number];
			return true;
		}
		
		private boolean isStraight(Card[] hand) {
			// note that the hand is already sorted!
			int firstNum = hand[0].number;
			for(int n = 1; n < 5; n++) {
				if(hand[n].number != firstNum + n) return false;
			}
			return true;
		}
		
		private boolean checkStraight(Card[] hand) {
			if(!isStraight(hand)) return false;
			handType = STRAIGHT;
			// TODO: tiebreak two straights that differ only by suits.
			tiebreaker = hand[4].number;
			description = "Straight to the " + faceNames[hand[4].number];
			return true;
		}
		
		// because the hand is sorted, three-of-a-kind will all appear together.
		// a TOAK hand will be XXX__, _XXX_, or __XXX.
		private boolean checkThreeOfAKind(Card[] hand) {
			for(int s = 0; s < 3; s++) {
				int v = hand[s].number;
				boolean is = true;
				for(int e = s+1; e < s+3; e++) {
					if(hand[e].number != v) {
						is = false;
					}
				}
				if(is) {
					handType = THREE_OF_A_KIND;
					tiebreaker = v;
					description = "Three " + faceNames[v] + "s";
					return true;
				}
			}
			return false;
		}
		
		// The two pairs will appear as XXYY_, XX_YY, or _XXYY.
		private boolean checkTwoPairs(Card[] hand) {
			for(int s1 = 0; s1 < 2; s1++) {
				if(hand[s1].number == hand[s1+1].number) {
					for(int s2 = s1+2; s2 < 4; s2++) {
						if(hand[s2].number == hand[s2+1].number)
						{
							handType = TWO_PAIR;
							tiebreaker = hand[s2].number * 64 * 64;
							tiebreaker += hand[s1].number * 64;
							for(int n = 0; n < 5; n++) {
								if(
									(hand[n].number != hand[s1].number) && 
									(hand[n].number != hand[s2].number)
									){
									tiebreaker += hand[n].number;
								}
							}
							description = "Two pairs " + faceNames[hand[s2].number] + "s and " + faceNames[hand[s1].number] + "s";
							return true;
						}
					}
				}
			}
					
			return false;
		}

		// because the hand is sorted, pairs will appear together.
		// a PAIR hand will be XX___, _XX__, __XX_, ___XX.
		private boolean checkPair(Card[] hand) {
			for(int s = 0; s < 4; s++) {
				int v = hand[s].number;
				boolean is = true;
				for(int e = s+1; e < s+2; e++) {
					if(hand[e].number != v) {
						is = false;
					}
				}
				if(is) {
					handType = PAIR;
					// Generate the kicker value from the 
					// cards not in the pair, from highest (at 4) to lowest (at 0).
					tiebreaker = v;
					for(int n = 4; n >= 0; n--) {
						//System.out.println("" + hand[n].number + ": ", tiebreaker);
						if(hand[n].number != v) {
							tiebreaker = tiebreaker * 64;
							tiebreaker += hand[n].number;
						}
					}
					description = "Pair of " + faceNames[v] + "s";
					return true;
				}
			}
			return false;
		}
	}

	public Value evaluateHand(String hand) throws InvalidHandException {
		return new Value(hand);
	}

	public static class ValueComparator implements Comparator<Value> {
		public int compare(Value value1, Value value2) {
			if(value1.handType > value2.handType) return 1;
			if(value1.handType < value2.handType) return -1;
			if(value1.tiebreaker > value2.tiebreaker) return 1;
			if(value1.tiebreaker < value2.tiebreaker) return -1;
			return 0;
		}
	}
	
	
	
	//
	//
	//
	// Test harness for HandEvaluator.
	//
	//
	//	
	
	private String[] invalidHands = {
		"AH,3Q,2H,4H,5H",		// Q is not a suit.
		"AH",					// Too few cards.
		"AH,3H",
		"AH,3H,2H",
		"AH,3H,2H,4H",
		"AH,3H,2H,4H,5H,7H",	// Too many cards.
		"AH,3H,2H,4H,5K",		// K is not a suit.
		"AH,3H,19H,4H,5C",		// 19 is not a number.
		"AH,3H,29H,4H,5C",		// 9H is not a suit.
		"AH,3H,2H,4H,5K",
	};
		
	public void testInvalidHand(int n) {
		String hand = invalidHands[n];
		boolean didThrow = false;
		try {
			HandEvaluator.Value v = new HandEvaluator().evaluateHand(hand);
		} catch(HandEvaluator.InvalidHandException e) {
			System.out.println("Expected invalid hand");
			didThrow = true;
		}
		assertEquals(didThrow, true);
	}
	
	@Test
	public void testInvalidHands() {
		for(int n = 0; n < invalidHands.length; n++) {
			testInvalidHand(n);
		}
	}

	private class ValidHand {
		private String hand;
		private int expectedType;
		private int expectedTiebreaker;
		
		ValidHand(String hand, int expectedType, int expectedTiebreaker) {
			this.hand = hand;
			this.expectedType = expectedType;
			this.expectedTiebreaker = expectedTiebreaker;
		}
		public void testHand() {
			System.out.println("Testing " + hand);
			try {
				HandEvaluator.Value v = new HandEvaluator().evaluateHand(hand);
				System.out.println("  --> " + v.description + " (" + v.handType + "," + v.tiebreaker + ")");
				assertEquals(v.handType, expectedType);
				assertEquals(v.tiebreaker, expectedTiebreaker);
			}
			catch(HandEvaluator.InvalidHandException e) {
				fail();
			}
		}
		public HandEvaluator.Value evaluateHand() {
			try {
				return new HandEvaluator().evaluateHand(hand);
			}
			catch(HandEvaluator.InvalidHandException e) {
				fail();
			}
			return new HandEvaluator.Value();
		}
	}
	
	// The vals vararg passed into this needs to be constructed
	// per the hand type. For example, for two pairs, the first number
	// needs to be the high pair, the second number the low pair, and the
	// third number the remaining card.
	static int calculateKicker(int... vals) {
		int val = 0;
		for(int n = 0; n < vals.length; n++) {
			val = (val*64) + vals[n];
		}
		return val;
	}

	// NOTA BENE: These hands must be ordered by highest to lowest,
	// so that the testComparison test will work!
	// Yeah, and there's also the weirdness that arises if you compare
	// two hands in which there are duplicates between them.
	private ValidHand[] validHands = {
		new ValidHand("6H,3H,2H,4H,5H", HandEvaluator.Value.STRAIGHT_FLUSH, 6),
		new ValidHand("6H,6C,6D,6S,3H", HandEvaluator.Value.FOUR_OF_A_KIND, 6),
		new ValidHand(" 4H, 4C,4D,4S,5H", HandEvaluator.Value.FOUR_OF_A_KIND, 4),
		new ValidHand("QH,QC,QD,5S,5H", HandEvaluator.Value.FULL_HOUSE, 12),
		new ValidHand("JH,5S,5H,JC,JD", HandEvaluator.Value.FULL_HOUSE, 11),
		new ValidHand("QH,QC,7D,  7S,7H", HandEvaluator.Value.FULL_HOUSE, 7),
		new ValidHand("QH,5D,QC,5S,5H", HandEvaluator.Value.FULL_HOUSE, 5),
		new ValidHand("AH,3H, 2H, 4H,5H", HandEvaluator.Value.FLUSH, calculateKicker(14, 5, 4, 3, 2)),
		new ValidHand("JH,8C,10D,9S,7H", HandEvaluator.Value.STRAIGHT, 11),
		new ValidHand("10H,6C,9D,7S,8H", HandEvaluator.Value.STRAIGHT, 10),
		// TOAK form _XXX_
		new ValidHand("10H,10S,10C,AH,4C", HandEvaluator.Value.THREE_OF_A_KIND, 10),
		// TOAK form XXX__
		new ValidHand("9H,9S,9C,8H,4C", HandEvaluator.Value.THREE_OF_A_KIND, 9),
		// TOAK form __XXX
		new ValidHand("8H,8S,8C,4H,3C", HandEvaluator.Value.THREE_OF_A_KIND, 8),
		new ValidHand("7H,AH,7S,10D,7C", HandEvaluator.Value.THREE_OF_A_KIND, 7),
		new ValidHand("AS,AH,10S,4C,4D", HandEvaluator.Value.TWO_PAIR, calculateKicker(14, 4, 10)),
		new ValidHand("10H,10S,7C,3D,8H", HandEvaluator.Value.PAIR, calculateKicker(10, 8, 7, 3)),
		new ValidHand("9H,7C,9S,3D,8H", HandEvaluator.Value.PAIR, calculateKicker(9, 8, 7, 3)),
		new ValidHand("9H,3D,8H,9S,6C", HandEvaluator.Value.PAIR, calculateKicker(9, 8, 6, 3)),
		new ValidHand("10H,QS,9C,4S,8D", HandEvaluator.Value.HIGH, calculateKicker(12, 10, 9, 8, 4)),
		new ValidHand("10H,JS,9C,4S,8D", HandEvaluator.Value.HIGH, calculateKicker(11, 10, 9, 8, 4)),
		new ValidHand("10H,JS,9C,3S,8D", HandEvaluator.Value.HIGH, calculateKicker(11, 10, 9, 8, 3))
	};

	@Test
	public void testValidHands() {
		for(int n = 0; n < validHands.length; n++) {
			validHands[n].testHand();
		}
	}
	
	@Test
	public void testComparison() {
		for(int n = 0; n < validHands.length -1; n++) {
			for(int m = n+1; m < validHands.length; m++) {
				HandEvaluator.Value v1 = validHands[n].evaluateHand();
				HandEvaluator.Value v2 = validHands[m].evaluateHand();
				System.out.println("Comparing " + validHands[n].hand + " to " + validHands[m].hand);
				assertTrue(v1.compareTo(v2)>0);
			}
		}
	}	
}

		