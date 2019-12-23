package cass.oli.boids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import cass.oli.simulation.Game;
import cass.oli.simulation.Launcher;
import cass.oli.simulation.Vec;

public class Boids extends Game{
	/**
	 * https://rosettacode.org/wiki/Boids/Java
	 */
	public ArrayList<Boid> boids = new ArrayList<>();
	
	
	private final int num_boids = 30;
	
	public Vec target = null;
	private long target_timeout;
	
	public Boids() {
		name = "Boids";
		for(int i = 0; i < num_boids; i++) boids.add(new Boid(this));
	}
	
	@Override
	protected void tick(float delta) {
		for(Boid boid : boids) boid.tick();
		if(target != null & (System.currentTimeMillis() - target_timeout) > 2000) target = null;
	}
	
	@Override
	protected void render(Graphics2D g){
		//Background
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		for(Boid boid : boids) boid.render(g);
		
		//Pause Button
		g.setColor(Color.white);
		if(!paused) {
			g.fillRect(width - 60, 10, 20, 60);
			g.fillRect(width - 30, 10, 20, 60);
		}else {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, width, height);
			g.setColor(Color.white);
			
			int[] xTri = {width- 60, width - 10, width - 60};
			int[] yTri = {10, 40, 70};
			g.fillPolygon(xTri, yTri, 3);
		}
	}
	
	public void newTarget(int x, int y) {
		target = new Vec(x, y);
		target_timeout = System.currentTimeMillis();
	}

	@Override
	public void leftClick(int x, int y) {
		if(x > width - 60 && x < width - 10 && y > 10 && y < 70) {
			paused = !paused;
			return;
		}
		
		if(!paused) {
			newTarget(x, y);	
		}
	}

	@Override
	public void rightClick(int x, int y) {
		target = null;
	}

	@Override
	public void dragTo(int x, int y) {
		leftClick(x, y);
	}

	public static void main(String[] args) {
		new Launcher(new Boids());
	}
	
	@Override
	protected void loadImages() throws Exception{
		//No Images
	}
}
