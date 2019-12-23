package cass.oli.pendulum;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

public class Node {
	public float x, y, velX, velY;
	public boolean stationairy;
	public float mass, radius; //TODO
	private Color colour;
	
	public ArrayList<Node> connections = new ArrayList<Node>();
	
	public Node(float x, float y, boolean stationairy) {
		Random random = new Random();
		this.colour = new Color(random.nextInt(128) + 127, random.nextInt(128) + 127, random.nextInt(128) + 127);
		
		this.x = x;
		this.y = y;
		this.stationairy = stationairy;
		velX = 0;
		velY = 0;
		radius = 20;
		mass = 400;
	}
	
	public void tick(float time) {
		if(stationairy) return;
		//Resolve Forces
		float Fx = 0;
		float Fy = 0;
		
		Fy += Pendulum.g*mass;
		
		//Resolve Velocity
		velX += (Fx*time)/mass;
		velY += (Fy*time)/mass;
		
		//Resolve Position
		x += velX*time;
		y += velY*time;
	}
	
	public void render(Graphics g) {
		g.setColor(colour);
		g.fillOval((int)(x - radius), (int)(y - radius), (int)(2 * radius), (int)(2 * radius));
	}
}
