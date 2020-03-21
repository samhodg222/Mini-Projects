package cass.oli.cards;

import java.util.concurrent.ThreadLocalRandom;

public class Deck {
	public static int CARDS = 52;
	private Card[] cards = new Card[52];
	public int top = 0;
	
	public Deck() {
		int i = 0;
		for(Suit suit : Suit.values()) {
			for(int num = 0; num < 13; num++) {
				cards[i] = new Card(suit, num);
				i++;
			}
		}
	}
	
	public Card get(int index) {
		return cards[index];
	}
	
	public int index(Card card) {
		for(int index = 0; index < CARDS; index++) {
			if(card.suit() == cards[index].suit() && card.num() == cards[index].num()) return index;
		}
		return -1;
	}
	
	private void swap(int index_1, int index_2) {
		Card temp = cards[index_1];
		cards[index_1] = cards[index_2];
		cards[index_2] = temp;
	}
	
	public void shuffle() { //Fisher-Yates Shuffle
		for(int i = CARDS - 1; i > 1; i--) {
			swap(i, ThreadLocalRandom.current().nextInt(0, i));
		}
		top = 0;
	}
	
	public void print(int index) {
		cards[index].print();
	}
	
	public int topCard() {
		int topIndex = top++;
		if(top > 52) return -1;
		return topIndex;			
	}
}
