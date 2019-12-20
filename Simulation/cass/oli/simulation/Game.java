package cass.oli.simulation;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Game {
	public BufferedImage[] sprites;

	public String name = "Simulation";
	public int FPS = 60;
	public int width = 1280, height = 720;
	public int mouseX, mouseY;
	public int dragX, dragY;
	public boolean dragging = false;
	public boolean paused = false;
	public boolean resizable = true;
	public boolean init_extended = true;

	public Game() {
		try{
			loadImages();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract void loadImages() throws Exception;

	public void resize(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public abstract void leftClick(int x, int y);

	public abstract void rightClick(int x, int y);

	public abstract void dragTo(int x, int y);

	protected abstract void tick();

	protected abstract void render(Graphics2D g);
}
