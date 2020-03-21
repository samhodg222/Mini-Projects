package cass.oli.boids3d;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import cass.oli.maths3d.Vec;
import cass.oli.maths3d.Matrix;

public class Boid {
	public Vec loc, vel, acc;
	public Color colour;
	public final Vec size;
	
	public static final Matrix shape_2d = new Matrix(new Double[][] {{8.0, -5.0, -5.0}, {0.0, -5.0, 5.0}});
	
	public final float MAX_SPEED, MAX_FORCE, RADIUS, 
		separation_distsqr, alignment_distsqr, cohesion_distsqr,
		edge_margin, 
		separation_f, alignment_f, cohesion_f, target_f, edge_f;
	
	public Boid(Vec size, float[] p) {
		MAX_SPEED          = p[0];
		MAX_FORCE          = p[1];
		RADIUS             = p[2];
		separation_distsqr = p[3]*p[3];
		alignment_distsqr  = p[4]*p[4];
		cohesion_distsqr   = p[5]*p[5];
		edge_margin        = p[6];
		separation_f       = p[7];
		alignment_f		   = p[8];
		cohesion_f         = p[9];
		target_f           = p[10];
		edge_f             = p[11];
		
		this.size = size;
		Random rnd = new Random();
		loc = new Vec(rnd.nextFloat()*size.x, rnd.nextFloat()*size.y, rnd.nextFloat()*size.z);
		vel = new Vec(2*rnd.nextFloat()*MAX_SPEED - MAX_SPEED, 2*rnd.nextFloat()*MAX_SPEED - MAX_SPEED, 2*rnd.nextFloat()*MAX_SPEED - MAX_SPEED);
		acc = new Vec(0, 0, 0);
		colour = new Color(rnd.nextInt(128) + 127, rnd.nextInt(128) + 127, rnd.nextInt(128) + 127);
	}
	
	public void tick(Boid[] boids, Vec target) {
		Vec rule1 = separation(boids);
		Vec rule2 = alignment(boids);
		Vec rule3 = cohesion(boids);
		Vec rule4 = new Vec(0, 0, 0);
		Vec rule5 = edge();
		
		if(target != null) rule4 = seek(target);
		
		rule1.mult(separation_f);
		rule2.mult(alignment_f);
		rule3.mult(cohesion_f);
		rule4.mult(target_f);
		rule5.mult(edge_f);
		
		acc.add(rule1);		
		acc.add(rule2);		
		acc.add(rule3);	
		acc.add(rule4);
		acc.add(rule5);
		
		vel.add(acc);
		vel.limit(MAX_SPEED);
		loc.add(vel);
		acc.mult(0);
	}
	
	public void render_2d(Graphics g) { //Don't consider z-axis (for now)
		int x = this.loc.X();
		int y = this.loc.Y();
				
		Matrix r = Matrix.rotation(Math.atan2(vel.y, vel.x));
		Matrix points = Matrix.multiply(r, shape_2d);
		int[][] intPoints = points.round();
		
		int[] xPoints = { intPoints[0][0] + x, intPoints[0][1] + x, intPoints[0][2] + x};
		int[] yPoints = { intPoints[1][0] + y, intPoints[1][1] + y, intPoints[1][2] + y};
		g.setColor(colour);
		g.fillPolygon(xPoints, yPoints, 3);	
	}
	
	private Vec separation(Boid[] boids) {
		Vec steer = new Vec();
		int count = 0;
		for(Boid b : boids) {
			float d = loc.sqrdist(b.loc);
			if(d < separation_distsqr) {
				Vec diff = Vec.diff(loc, b.loc);
				diff.normalize();
				diff.divide(d);
				steer.add(diff);
				count++;
			}
		}
		if(count > 0) steer.divide(count);
		if(steer.sqrdist(new Vec()) > 0) {
			steer.normalize();
			steer.mult(MAX_SPEED);
			steer.sub(vel);
			steer.limit(MAX_FORCE);
			return steer;
		}
		return new Vec();
	}
	
	private Vec alignment(Boid[] boids) {
		Vec steer = new Vec();
		
		int count = 0;
		for(Boid b : boids) {
			float d = loc.sqrdist(b.loc);
			if(d < alignment_distsqr) {
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
	
	private Vec cohesion(Boid[] boids) {
		Vec target = new Vec();
		int count = 0;
		
		for(Boid b : boids) {
			float d = loc.sqrdist(b.loc);
			if(d < cohesion_distsqr) {
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
		Vec steer = Vec.diff(target, loc);
		steer.normalize();
		steer.mult(MAX_SPEED);
		steer.sub(vel);
		steer.limit(MAX_FORCE);
		return steer;
	}
	
	private Vec edge() {
		Vec edge = new Vec();
		
		if(loc.x > size.x + edge_margin) edge.x += size.x + edge_margin - loc.x;
		if(loc.x < -edge_margin) edge.x -= (loc.x - edge_margin);
		if(loc.y > size.y + edge_margin) edge.y += size.y + edge_margin- loc.y;
		if(loc.y < -edge_margin) edge.y -= (loc.y - edge_margin);
		if(loc.z > size.z + edge_margin) edge.z += size.z + edge_margin - loc.z;
		if(loc.z < -edge_margin) edge.z -= (loc.z - edge_margin);
		
		return edge;
	}
}
