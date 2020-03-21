package cass.oli.blackjack;
//https://en.wikipedia.org/wiki/Blackjack

import java.util.Scanner;

import cass.oli.cards.Card;
import cass.oli.cards.Deck;

public class Blackjack {
	private Deck deck;
	private Player dealer;
	private Player[] players;
	private int position;
	private State state;
	
	private boolean playing = true;
	
	public Blackjack(int other_players, int position) {
		state = State.Start;
		deck = new Deck();
		deck.shuffle();
		
		dealer = new Player();
		players = new Player[other_players + 1];
		if(position > other_players) return;
		this.position = position;
		for(int i = 0; i < players.length; i++) players[i] = new Player();
		
		for(int i = 0; i < 2*(players.length + 1); i++) {
			int index = i % (players.length + 1);
			if(index == players.length) {
				dealer.addCard(deck.topCard());
				continue;
			}
			players[index].addCard(deck.topCard());
		}
		
		print();
		pre_player();
	}
	
	private void pre_player() {
		state = State.Before;
		for(int i = 0; i < position; i++) while(players[i].hit(hand(players[i]))) hit(players[i]);
		state = State.Turn;
	}
	
	public void user_hit() {
		if(hit(players[position])) print();
		else {
			System.out.println("Bust!");
			print();
			post_player();
		}
		
	}
	
	public void post_player() {
		state = State.After;
		for(int i = position + 1; i < players.length; i++) while(players[i].hit(hand(players[i]))) hit(players[i]);
		dealers_turn();
	}
	
	public boolean hit(Player player) {
		player.addCard(deck.topCard());
		
		if(hand(player) > 21) return false;
		else return true;
	}
	
	public void dealers_turn() {
		int[] win = new int[players.length];
		int[] hands = new int[players.length];
		for(int i = 0; i < hands.length; i++) hands[i] = hand(players[i]);

		for(int i = 0; i < players.length; i++) {
			if(blackjack(players[i])) win[i] = 3;
			if(hands[i] > 21) win[i] = 1; 
		}
		
		while(hand(dealer) < 17) dealer.addCard(deck.topCard());
		
		int dealer_hand = hand(dealer);
		
		if(dealer_hand > 21) {
			for(int w : win) if(w != 1) w = 3;
		}
		
		for(int i = 0; i < players.length; i++) {
			if(win[i] != 0) continue;
			if(dealer_hand == hands[i]) win[i] = 2;
			else if(dealer_hand > hands[i]) win[i] = 1;
			else win[i] = 3;
		}
		
		state = State.End;
		printAll(win);
	}
	
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		System.out.println("Blackjack v1.0");
		System.out.println("Oliver Cass 2020");
		System.out.println();
		System.out.println("----------------------");
		
		Blackjack blackjack = new Blackjack(4, 0);
		
		while(blackjack.playing) {
			String in = input.nextLine();
			switch(in) {			
			case("hit"):
				blackjack.user_hit();
			break;
			case("stand"):
				blackjack.post_player();
			break;
			case("quit"):
				blackjack.stop();
			break;
			default:
				System.out.println("Unrecognised Command");
			}
		}
		
		input.close();
	}
	
	public int hand(Player player) {
		int hand = 0;
		int num_aces = 0;
		for(int card : player.cards()) {
			int value = deck.get(card).num();
			if(value > 10) value = 10;
			if(value == 1) {
				++num_aces;
				value = 11;
			}
			hand += value;
		}
		
		while(num_aces > 0 && hand > 21) {
			num_aces--;
			hand -= 10;
		}
		return hand;
	}
	
	private void print() {
		for(int i = 0; i < players.length; i++) {
			if(i == position) System.out.println("Your Cards:");
			else System.out.println(i + "'s Cards:");
			for(int index : players[i].cards()) deck.print(index);
			System.out.println("Total: " + hand(players[i]));
			System.out.println();
		}
		System.out.println("Dealers Card:");
		deck.print(dealer.getCard(0));
		System.out.println("----------------------");
	}
	
	private void printAll(int[] win) {
		for(int i = 0; i < players.length; i++) {
			String winner;
			switch(win[i]) {
			case 1:
				winner = "(Lose)";
				break;
			case 2:
				winner = "(Draw)";
				break;
			case 3:
				winner = "(Win)";
				break;
			default:
				winner = "(Error)";
			}
			
			if(i == position) System.out.println("Your Cards: " + winner);
			else System.out.println(i + "'s Cards: " + winner);
			for(int index : players[i].cards()) deck.print(index);
			System.out.println("Total: " + hand(players[i]));
			System.out.println();
		}
		
		System.out.println("Dealers Cards:");
		for(int index : dealer.cards()) deck.print(index);
		System.out.println("Total: " + hand(dealer));
		System.out.println("----------------------");
	}
	
	public void stop() {
		playing = false;
	}
	
	public State getState() {
		return this.state;
	}
	
	public boolean blackjack(Player player) {
		if(player.numCards() == 2) {
			Card A = deck.get(player.getCard(0));
			Card B = deck.get(player.getCard(1));
			if(A.num() == 0 && B.num() > 9) return true;
			if(B.num() == 0 && A.num() > 9) return true;
		}
		return false;
	}
	
	public Card[] getCards() {
		int[] myCards = players[position].cards();
		Card[] cards = new Card[myCards.length];
		
		for(int i = 0; i < myCards.length; i++) cards[i] = deck.get(myCards[i]);
		return cards;
	}
}
