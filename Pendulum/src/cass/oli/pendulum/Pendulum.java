package cass.oli.pendulum;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import cass.oli.simulation.Game;
import cass.oli.simulation.Launcher;

public class Pendulum extends Game{
	
	public static float g = (float) 35;
	
	ArrayList<Node> nodes = new ArrayList<Node>();
	
	private long lastTime;
	
	public Pendulum() {
		name = "Pendulum";
		nodes.add(new Node(100, 100, false));
		lastTime = System.currentTimeMillis();
	}

	@Override
	protected void loadImages() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void leftClick(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rightClick(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragTo(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void tick(float delta) {
		for(Node node : nodes) node.tick(delta);
	}

	@Override
	protected void render(Graphics2D g) {
		//Background
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		for(Node node : nodes) node.render(g);
	}
	
	public static void main(String[] args) {
		new Launcher(new Pendulum());
	}
}
