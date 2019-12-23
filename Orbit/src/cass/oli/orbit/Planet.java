package cass.oli.orbit;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Planet {
	public float x, y, velX, velY;
	public final int id;
	public float radius, mass;
	public boolean stationairy = false;
	public Color colour;
	public Orbit orbit;
	
	private static final int MAX_INIT_SPEED = 30; //initial
	private static final int MAX_RADIUS = 60;
    private static final int MIN_RADIUS = 10;
	public static float G = 1000f;
	public static float W = 200f;
	public static float edge = 0.9f;
	
	public void tick(float delta) {
		if(stationairy) return;
		//Resolve Forces
		float Fx = 0, Fy = 0;
		//Gravitational Planets
		for(Planet planet : orbit.planets) {
			if(planet.id == this.id) continue;
			float dx = planet.x - this.x;
			float dy = planet.y - this.y;
			float r2 = dx*dx + dy*dy;
			float r = (float) Math.sqrt(r2);
			if(r < this.radius + planet.radius) continue;
			float F = G*this.mass*planet.mass/r2;
			Fx += F*(dx/r);
			Fy += F*(dy/r);
		}
		//Away from wall
		if(x > edge*orbit.width) Fx -= W*(x - edge*orbit.width);
		if(x < (1-edge)*orbit.width) Fx -= W*(x - (1-edge)*orbit.width);
		if(y > edge*orbit.height) Fy -= W*(y - edge*orbit.height);
		if(y < (1-edge)*orbit.height) Fy -= W*(y - (1-edge)*orbit.height);
		
		//Resolve Velocity
		velX += (Fx*delta)/mass;
		velY += (Fy*delta)/mass;
		
		//Resolve Location
		x += velX*delta;
		y += velY*delta;
	}
	
	public void render(Graphics g) {
		g.setColor(colour);
		g.fillOval((int)(x - radius), (int)(y - radius), (int)(2 * radius), (int)(2 * radius));
	}
	
	public Planet(int id, Orbit orbit, float x, float y) {
		Random random = new Random();
		//Bright Colours (possible better system)
		colour = new Color(random.nextInt(128) + 127, random.nextInt(128) + 127, random.nextInt(128) + 127);
		this.id = id;
		this.orbit = orbit;
		this.x = x;
		this.y = y;
		radius = random.nextInt(MAX_RADIUS-MIN_RADIUS)+ MIN_RADIUS;
		mass = radius * radius; //*PI (Constant so doesn't matter)
		velX = random.nextInt(MAX_INIT_SPEED*2) - MAX_INIT_SPEED;
		velY = random.nextInt(MAX_INIT_SPEED) - MAX_INIT_SPEED;
	}
	public Planet(int id, Orbit orbit, float x, float y, boolean stationairy) {
		Random random = new Random();
		//Bright Colours (possible better system)
		colour = new Color(random.nextInt(128) + 127, random.nextInt(128) + 127, random.nextInt(128) + 127);
		this.id = id;
		this.orbit = orbit;
		this.x = x;
		this.y = y;
		radius = random.nextInt(MAX_RADIUS-MIN_RADIUS)+ MIN_RADIUS;
		mass = radius * radius; //*PI (Constant so doesn't matter)
		velX = 0;
		velY = 0;
		this.stationairy = stationairy;
	}
}
