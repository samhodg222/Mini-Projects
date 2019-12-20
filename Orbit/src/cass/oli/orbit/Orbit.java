package cass.oli.orbit;

import java.awt.Canvas;

public class Orbit extends Canvas implements Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 1280, HEIGHT = 720;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		Planet[] planets = new Planet[2];
		planets[0] = new Planet(WIDTH/2, HEIGHT/2, 50, 100, true);
		planets[1] = new Planet(100, 100, 10, 10);
		new Orbit(planets);
	}
}
