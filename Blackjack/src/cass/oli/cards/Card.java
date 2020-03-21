package cass.oli.cards;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Card {
	private Suit suit;
	private int num; //10 ,11, 12 for Jack Queen King (0 - 12)
	
	public Card(Suit suit, int num) {
		this.suit = suit;
		this.num = num;
	}
	
	public void print() {
		System.out.println(">" + card());
	}
	
	public String card() {
		return number() + " of " + suitName();
	}
	
	public Suit suit() {
		return this.suit;
	}
	
	public String suitName() {
		switch(this.suit) {
			case Clubs:
				return "Clubs";
			case Diamonds:
				return "Diamonds";
			case Hearts:
				return "Hearts";
			case Spades:
				return "Spades";
			default:
				return "Unknown";
		}
	}
	
	public String suitSymbol() {
		switch(this.suit) {
			case Clubs:
				return "♣";
			case Diamonds:
				return "♦";
			case Hearts:
				return "♥";
			case Spades:
				return "♠";
			default:
				return "?";
		}
	}
	
	public boolean red() {
		switch(this.suit) {
			case Diamonds:
			case Hearts:
				return true;
			case Clubs:
			case Spades:
			default:
				return false;
	}
	}
	
	public String number() {
		String number;
		switch(num) {
			case 0:
				number = "Ace";
				break;
			case 10:
				number = "Jack";
				break;
			case 11:
				number = "Queen";
				break;
			case 12:
				number = "King";
				break;
			default:
				number = Integer.toString(num + 1);
				break;
		}
		return number;
	}
	
	public String abbriv() {
		String number;
		switch(num) {
			case 0:
				number = "A";
				break;
			case 10:
				number = "J";
				break;
			case 11:
				number = "Q";
				break;
			case 12:
				number = "K";
				break;
			default:
				number = Integer.toString(num + 1);
				break;
		}
		return number;
	}
	
	public int num() {
		return this.num + 1;
	}
	
	public void render(Graphics2D g, int x, int y, int width, int height) {
		g.setColor(Color.white);
		g.fillRect(x, y, width, height);
		
		g.setFont(new Font("TimesRoman", Font.PLAIN, height/8)); 
		if(red()) g.setColor(Color.red); else g.setColor(Color.black);
		
		g.drawString(abbriv(), x + 5, y + height/8);		
		g.drawString(suitSymbol(), x + 5, y + 2*height/8);		
		
		AffineTransform orig = g.getTransform();
		g.rotate(Math.PI, x + width/2, y + height/2);
		
		g.drawString(abbriv(), x + 5, y + height/8);		
		g.drawString(suitSymbol(), x + 5, y + 2*height/8);
		g.setTransform(orig);
	}
	
}
