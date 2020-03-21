package cass.oli.cass;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import cass.oli.simulation.Matrix;
import cass.oli.simulation.Vec;

public class Object {
	public Vec loc, vel, acc;
	public float radius;
	public float mass;
	private Color colour;
	public final int id;
	public Shape shape;
	
	Cass cass;
	
	//possibly relate to radius?
	public static final Matrix triangle = new Matrix(new Double[][] {{15.0, -10.0, -10.0}, {0.0, -10.0, 10.0}});
	
	public boolean collision;
	
	private static final int MAX_SPEED = 15;
	private static final int MAX_INIT_SPEED = 7; //initial
	
    private static final int MAX_RADIUS = 60;
    private static final int MIN_RADIUS = 10;
    
	public static final float MAX_FORCE = 0.12f;
	public static final float SEARCH_RADIUS = 150; //TODO INCLUDE OBJECTS OFF SCREEN	
	
	public void update(float time) {
		if(vel.x > MAX_SPEED) vel.x = MAX_SPEED;
		if(vel.y > MAX_SPEED) vel.y = MAX_SPEED;
		
		vel.add(acc.mult1(time));
		vel.limit(MAX_SPEED);
		loc.add(vel.mult1(time));
		acc.mult(0);
		
		if(!cass.border) {
			if(loc.x > cass.width) loc.x = 0;
			if(loc.x < 0) loc.x = cass.width;
			if(loc.y > cass.height) loc.y = 0;
			if(loc.y < 0) loc.y = cass.height;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(colour);
		switch(shape) {
		case Triangle:
			Matrix r = Matrix.rotation(Math.atan2(vel.y, vel.x));
			Matrix points = Matrix.multiply(r, triangle);
			int[][] intPoints = points.round();
			
			int[] xPoints = { (int) (intPoints[0][0] + loc.x), (int) (intPoints[0][1] + loc.x), (int) (intPoints[0][2] + loc.x)};
			int[] yPoints = { (int) (intPoints[1][0] + loc.y), (int) (intPoints[1][1] + loc.y), (int) (intPoints[1][2] + loc.y)};
			g.setColor(colour);
			g.fillPolygon(xPoints, yPoints, 3);	
			break;
		case Circle: //Circle By Default
		default:
			g.fillOval((int)(loc.x - radius), (int)(loc.y - radius), (int)(2 * radius), (int)(2 * radius));
			break;
		}
	}
	
	public Object(int id, Cass cass, float x, float y) {
		Random random = new Random();
		
		this.loc = new Vec(x, y);
		this.vel = new Vec(2*random.nextFloat()*MAX_INIT_SPEED - MAX_INIT_SPEED, 2*random.nextFloat()*MAX_INIT_SPEED - MAX_INIT_SPEED);
		this.acc = new Vec(0, 0);
		
		this.radius = random.nextInt(MAX_RADIUS-MIN_RADIUS)+ MIN_RADIUS;;
		this.mass = 3.14f * radius * radius;
		this.id = id;
		this.cass = cass;
		
		colour = new Color(random.nextInt(128) + 127, random.nextInt(128) + 127, random.nextInt(128) + 127);
	}
}
