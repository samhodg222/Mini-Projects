package cass.oli.simulation;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Launcher extends Canvas implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int width, height;

	private JFrame frame;
	private final Game game;
	private Mouse mouse;

	final long OPTIMAL_TIME;
	private boolean running;

	public Launcher(Game game) {
		this.game = game;
		this.width = game.width;
		this.height = game.height;
		this.setSize(width, height);

		if (game.FPS > 0)
			OPTIMAL_TIME = 1000000000 / game.FPS;
		else
			OPTIMAL_TIME = 1000000000 / 60;
		mouse = new Mouse(game);
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);

		frame = new JFrame(game.name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setResizable(game.resizable);

		frame.add(this);
		frame.pack();

		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				if (game.resizable)
					resize();
			}
		});

		if (game.init_extended)
			frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		run();
	}

	public void resize() {
		this.width = this.getWidth();
		this.height = this.getHeight();
		game.resize(width, height);
	}

	public void render() {
		// Render
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g1 = bs.getDrawGraphics();
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		game.render(g);
		g.dispose();
		bs.show();
	}

	@Override
	public void run() {
		running = true;
		game.paused = false;
		while (running) {
			long lastTime = System.nanoTime();

			if (!game.paused)
				game.tick();
			render();

			long tick_time = (lastTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
			if (tick_time > 0) {
				try {
					Thread.sleep(tick_time);
				} catch (Exception e) {
				}
			}
		}
	}
}
