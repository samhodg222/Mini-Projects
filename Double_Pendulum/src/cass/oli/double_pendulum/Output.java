package cass.oli.double_pendulum;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Output extends Canvas{
	public int width;
	public int height;
	public JFrame frame;
	
	public BufferedImage bs;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Output(String title, int WIDTH, int HEIGHT) {
		if(WIDTH <= 0 || HEIGHT <= 0){
			width = 1280;
			height = 720;
		}else{
			width = WIDTH;
			height = HEIGHT;
		}
		
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		
		this.setSize(width, height);
		this.setBackground(Color.black);
		
		frame.add(this);
		frame.pack();
		
		bs = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
	
	public void show(){
		frame.setVisible(true);
	}
	
	public void paint(Graphics g){
		g.drawImage(bs, 0, 0, width, height, null);
	}
}
