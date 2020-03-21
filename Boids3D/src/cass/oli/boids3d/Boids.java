package cass.oli.boids3d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import cass.oli.maths3d.Vec;

public class Boids {
	private Boid[] boids;
	
	public Vec target = null;
	public boolean paused = false;
	private long target_timeout;
	
	public final Vec size;
		
	public Boids(Vec size, int num_boids, float[] parameters) {
		this.size = size;
		boids = new Boid[num_boids];
		for(int i = 0; i < num_boids; i++) boids[i] = new Boid(size, parameters);
	}
	
	public void tick() {
		//TODO send distsqr info to boid, with local boid data to save it being done twice
		for(int i = 0; i < boids.length; i++) {
			Boid boid = boids[i];
			ArrayList<Boid> local_boids = new ArrayList<Boid>();
			for(int j = 0; j < boids.length; j++) {
				if(i == j) continue;				
				if(boid.loc.sqrdist(boids[j].loc) < boid.RADIUS*boid.RADIUS) local_boids.add(boids[j]);
			}
			
			boid.tick((Boid[]) local_boids.toArray(new Boid[local_boids.size()]), target);
		}
		if(target != null & (System.currentTimeMillis() - target_timeout) > 2000) target = null;
	}
	
	public void render_2d(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, size.X(), size.Y());
		
		for(Boid b : boids) b.render_2d(g);
	}
}
