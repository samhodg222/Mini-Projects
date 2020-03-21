package cass.oli.boids3d;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

import cass.oli.maths3d.Vec;

public class Window_2D extends Canvas implements Runnable{
	public static final Vec size = new Vec(1280, 720, 500);
	public static final int FPS = 60;
	private Boids boids;
	
	float[] p = {
		//limits
		7f,			//max speed
		0.07f,		//max force
		
		//radii
		80f,	//search radius
		3f,		//separation radius
		80f,	//alignment radius
		70f,	//cohesion radius
		
		10f, //edge margin
		
		//influences
		0.5f,		//separation
		3f, 		//alignment
		3f,			//cohesion
		4.5f,		//target
		0.1f,		//edge
	};
	
	public Window_2D(Vec size, int num_boids) {
		boids = new Boids(size, 100, p);
		Mouse mouse = new Mouse(boids);
		
		this.setSize(size.X(), size.Y());
		this.addMouseListener(mouse);
		
		JFrame frame = new JFrame("Boids3D");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setResizable(false);

		frame.add(this);
		frame.pack();
		
		frame.setVisible(true);
		run();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean running = false;

	public void run() {
		running = true;
		final long OPTIMAL_TIME = 1000000000 / FPS;
		long lastTime = System.nanoTime();
		while (running) {
			lastTime = System.nanoTime();
			if (!boids.paused)
				boids.tick();
			render();

			long tick_time = (lastTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
			if (tick_time > 0) {
				try {
					Thread.sleep(tick_time);
				} catch (Exception e) {}
			}
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		boids.render_2d(g);
		g.dispose();
		bs.show();
	}
	
	public static void main(String args[]) {
		new Window_2D(size, 30);
	}
}

class Mouse extends MouseInputAdapter {
	Boids boids;

	public void mousePressed(MouseEvent e) {
		boids.target = new Vec(e.getX(), e.getY(), Window_2D.size.z/2);
	}

	public Mouse(Boids boids) {
		this.boids = boids;
	}

}
