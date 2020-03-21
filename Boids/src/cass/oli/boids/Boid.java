package cass.oli.boids;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import cass.oli.simulation.Matrix;
import cass.oli.simulation.Vec;

public class Boid {
	public Vec loc, vel, acc;
	
	public static final int MAX_SPEED = 10;
	public static final float MAX_FORCE = 0.12f;
	public static final float RADIUS = 150;
	
	public static final Matrix shape = new Matrix(new Double[][] {{15.0, -10.0, -10.0}, {0.0, -10.0, 10.0}});
	
	public Color colour;
	public Boids boids;
	
	public Boid(Boids boids) {
		this.boids = boids;
		
		Random rnd = new Random();
		loc = new Vec(rnd.nextFloat()*boids.width, rnd.nextFloat()*boids.height);
		vel = new Vec(2*rnd.nextFloat()*MAX_SPEED - MAX_SPEED, 2*rnd.nextFloat()*MAX_SPEED - MAX_SPEED);
		acc = new Vec(0, 0);
		colour = new Color(rnd.nextInt(128) + 127, rnd.nextInt(128) + 127, rnd.nextInt(128) + 127);
	}
	
	public Boid(float x, float y, Boids boids) {
		this.loc = new Vec(x, y);
		this.boids = boids;
		
		Random rnd = new Random();
		colour = new Color(rnd.nextInt(128) + 127, rnd.nextInt(128) + 127, rnd.nextInt(128) + 127);
		vel = new Vec(2*rnd.nextFloat()*MAX_SPEED - MAX_SPEED, 2*rnd.nextFloat()*MAX_SPEED - MAX_SPEED);
		acc = new Vec(0, 0);
	}
	
	public void tick() {
		ArrayList<Boid> localBoids = getLocalBoids();
		
		Vec rule1 = separation(localBoids);
		Vec rule2 = alignment(localBoids);
		Vec rule3 = cohesion(localBoids);
		Vec rule4 = new Vec(0, 0);
		if(boids.target != null) rule4 = seek(boids.target);
		
		rule1.mult(2.5f);
		rule2.mult(1.5f);
		rule3.mult(3f);
		rule4.mult(4.5f);
		
		acc.add(rule1);		
		acc.add(rule2);		
		acc.add(rule3);	
		acc.add(rule4);	
		//Add Other Directions Here
		
		
		vel.add(acc);
		vel.limit(MAX_SPEED);
		loc.add(vel);
		acc.mult(0);
		
		if(loc.x > boids.width) loc.x = 0;
		if(loc.x < 0) loc.x = boids.width;
		if(loc.y > boids.height) loc.y = 0;
		if(loc.y < 0) loc.y = boids.height;
	}

	private ArrayList<Boid> getLocalBoids() {
		ArrayList<Boid> localBoids = new ArrayList<>();
		for(Boid b : boids.boids) {
			float xDist = b.loc.x - this.loc.x;
			float yDist = b.loc.y - this.loc.y;
			float dist2 = xDist*xDist + yDist*yDist;
			if(dist2 == 0) continue; //self
			if(dist2 < RADIUS*RADIUS) localBoids.add(b);
		}
		return localBoids;
	}

	public void render(Graphics2D g) {		
		int x = (int) Math.round(this.loc.x);
		int y = (int) Math.round(this.loc.y);
				
		Matrix r = Matrix.rotation(Math.atan2(vel.y, vel.x));
		Matrix points = Matrix.multiply(r, shape);
		int[][] intPoints = points.round();
		
		int[] xPoints = { intPoints[0][0] + x, intPoints[0][1] + x, intPoints[0][2] + x};
		int[] yPoints = { intPoints[1][0] + y, intPoints[1][1] + y, intPoints[1][2] + y};
		g.setColor(colour);
		g.fillPolygon(xPoints, yPoints, 3);	
	}
	
	private Vec separation(ArrayList<Boid> boids) { //Separation		
		Vec steer = new Vec(0, 0);
		int count = 0;
		for(Boid b : boids) {
			double d = Vec.dist(loc, b.loc);
			if(d < RADIUS / 3) {
				Vec diff = Vec.sub(loc, b.loc);
				diff.normalize();
				diff.divide(d);
				steer.add(diff);
				count++;
			}
		}
		if(count > 0) steer.divide(count);
		if(steer.mag() > 0) {
			steer.normalize();
			steer.mult(MAX_SPEED);
			steer.sub(vel);
			steer.limit(MAX_FORCE);
			return steer;
		}
		return new Vec(0, 0);
	}
	
	private Vec alignment(ArrayList<Boid> boids) {
		Vec steer = new Vec(0, 0);
		
		int count = 0;
		for(Boid b : boids) {
			double d = Vec.dist(loc, b.loc);
			if(d < RADIUS / 1.8) {
				steer.add(b.vel);
				count++;
			}
		}
		
		if(count > 0) {
			steer.divide(count);
			steer.normalize();
			steer.mult(MAX_SPEED);
			steer.sub(vel);
			steer.limit(MAX_FORCE);
		}
		
		return steer;
	}
	
	private Vec cohesion(ArrayList<Boid> boids) {
		Vec target = new Vec(0, 0);
		int count = 0;
		
		for(Boid b : boids) {
			double d = Vec.dist(loc, b.loc);
			if(d < RADIUS / 1.6) {
				target.add(b.loc);
				count++;
			}
		}
		if(count > 0) {
			target.divide(count);
			return seek(target);
		}
		return target;
	}
	
	private Vec seek(Vec target) {
		Vec steer = Vec.sub(target, loc);
		steer.normalize();
		steer.mult(MAX_SPEED);
		steer.sub(vel);
		steer.limit(MAX_FORCE);
		return steer;
	}
}
