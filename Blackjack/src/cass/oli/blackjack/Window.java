package cass.oli.blackjack;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import cass.oli.cards.Card;

public class Window extends Canvas{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Blackjack blackjack;
	public static final int WIDTH = 700, HEIGHT = 500;
	
	public int cw = 115, ch = 170;
	
	public Window() {
		JFrame frame = new JFrame("Blackjack v1.0");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		
		blackjack = new Blackjack(4, 0);
		
		this.addMouseListener(new MouseListener() {
		     @Override
		     public void mousePressed(MouseEvent e) {
		        
		     }

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		  });
		
		this.setSize(WIDTH, HEIGHT);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new Window();
	}
	
	public void paint(Graphics g2) {
		Graphics2D g = (Graphics2D) g2;
		g.setColor(new Color(33, 115, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		State state = blackjack.getState();
		
		renderCards(g, blackjack.getCards());
	}
	
	public void renderCards(Graphics2D g, Card[] cards) {
		double segment = WIDTH / cw;
		
	}
}
