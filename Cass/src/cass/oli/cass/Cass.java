package cass.oli.cass;

import java.awt.Graphics2D;

import cass.oli.simulation.Game;
import cass.oli.simulation.Launcher;

public class Cass extends Game{
	
	public boolean border = false;

	public static void main(String[] args) {
		new Launcher(new Cass());
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
		leftClick(x, y);
		
	}

	@Override
	protected void tick(float time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void render(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

}
