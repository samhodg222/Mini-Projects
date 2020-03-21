package cass.oli.blackjack;

import java.util.ArrayList;

public class Player {
	protected ArrayList<Integer> cards = new ArrayList<Integer>();
	
	public void addCard(int card) {
		cards.add(card);
	}
	
	public int getCard(int index) {
		return cards.get(index);
	}
	
	public int numCards() {
		return cards.size();
	}
	
	public int[] cards() {
		return cards.stream().mapToInt(i -> i).toArray();
	}
	
	public boolean hit(int hand) {
		return hand < 17; //bit basic..
	}
}