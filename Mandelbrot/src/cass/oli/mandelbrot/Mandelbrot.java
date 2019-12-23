package cass.oli.mandelbrot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import cass.oli.simulation.Game;
import cass.oli.simulation.Launcher;

public class Mandelbrot extends Game {
	private BufferedImage image = null;
	/**
	 * see http://java.rubikscube.info/
	 */
	private final int default_max = 6;
	private final int absolute_max = 12;
	private int current_max = 6;
	private Color[][] colors;
	private long lastRender = 0;
	private static final int[][][] colpal = {
		{ {0, 10, 20}, {50, 100, 240}, {20, 3, 26}, {230, 60, 20},
		{25, 10, 9}, {230, 170, 0}, {20, 40, 10}, {0, 100, 0},
		{5, 10, 10}, {210, 70, 30}, {90, 0, 50}, {180, 90, 120},
		{0, 20, 40}, {30, 70, 200} },
		{ {70, 0, 20}, {100, 0, 100}, {255, 0, 0}, {255, 200, 0} },
		{ {40, 70, 10}, {40, 170, 10}, {100, 255, 70}, {255, 255, 255} },
		{ {0, 0, 0}, {0, 0, 255}, {0, 255, 255}, {255, 255, 255}, {0, 128, 255} },
		{ {0, 0, 0}, {255, 255, 255}, {128, 128, 128} },
	};
	private int pal = 0;
	private static final int[][] rows = {
		{ 0, 16,  8}, { 8, 16,  8}, 
		{ 4, 16,  4}, {12, 16,  4},
		{ 2, 16,  2}, {10, 16,  2}, 
		{ 6, 16,  2}, {14, 16,  2}, 
		{ 1, 16,  1}, { 9, 16,  1},
		{ 5, 16,  1}, {13, 16,  1},
		{ 3, 16,  1}, {11, 16,  1},
		{ 7, 16,  1}, {15, 16,  1}};

	private boolean smooth = true;
	private boolean antialias = true;
	private double viewX = 0.0;
	private double viewY = 0.0;
	private double zoom = 1.0;
	Thread improve;
	boolean improving = false;

	public Mandelbrot() {
		name = "Mandelbrot";
		lastRender = System.currentTimeMillis();
		colors = new Color[colpal.length][];
		for (int p = 0; p < colpal.length; p++) {
			colors[p] = new Color[colpal[p].length * 12];
			for (int i = 0; i < colpal[p].length; i++) {
				int[] c1 = colpal[p][i];
				int[] c2 = colpal[p][(i + 1) % colpal[p].length];
				for (int j = 0; j < 12; j++)
					colors[p][i * 12 + j] = new Color(
							(c1[0] * (11 - j) + c2[0] * j) / 11,
							(c1[1] * (11 - j) + c2[1] * j) / 11,
							(c1[2] * (11 - j) + c2[2] * j) / 11);
			}
		}
	}
	
	public void draw() {
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = newImage.getGraphics();
		
		boolean completed = false;

		// fractal image drawing
		for (int row = 0; row < rows.length; row++) {
			if(Thread.interrupted()) break;
			for (int y = rows[row][0]; y < height; y += rows[row][1]) {
				for (int x = 0; x < width; x++) {
					double r = zoom / Math.min(width, height);
					double dx = 2.5 * (x * r + viewX) - 2;
					double dy = 1.25 - 2.5 * (y * r + viewY);
					Color color = color(dx, dy);
					if (antialias) {
						Color c1 = color(dx - 0.25 * r, dy - 0.25 * r);
						Color c2 = color(dx + 0.25 * r, dy - 0.25 * r);
						Color c3 = color(dx + 0.25 * r, dy + 0.25 * r);
						Color c4 = color(dx - 0.25 * r, dy + 0.25 * r);
						int red = (color.getRed() + c1.getRed() + c2.getRed() + c3.getRed() + c4.getRed()) / 5;
						int green = (color.getGreen() + c1.getGreen() + c2.getGreen() + c3.getGreen() + c4.getGreen()) / 5;
						int blue = (color.getBlue() + c1.getBlue() + c2.getBlue() + c3.getBlue() + c4.getBlue()) / 5;
						color = new Color(red, green, blue);
					}
					g.setColor(color);
					g.fillRect(x, y - rows[row][2] / 2, 1, rows[row][2]);
					if(		row == rows.length - 1 &&
							x == width - 1) completed = true;
				}
			}
		}
		g.dispose();
		if(!Thread.interrupted() && completed) image = newImage;
	}
	
	public void update() {
		if(improving) {
			improving = false;
			improve.interrupt();
		}

		current_max = default_max;
		lastRender = System.currentTimeMillis();
		draw();
	}
	
	public void zoomIn(double scale) {
		viewX += (double) zoom*(scale - 1)/2;
		viewY += (double) zoom*(scale - 1)/2;
		zoom /= scale;
		
		update();
	}
	public void zoomOut(double scale) {
		viewX -= (double) zoom*(scale - 1)/2;
		viewY -= (double) zoom*(scale - 1)/2;
		zoom *= scale;
		update();
	}

	public void dragTo(int x, int y) {
		int dx = Math.abs(x - mouseX);
		int dy = Math.abs(y - mouseY);
		if(dx + dy < 15 || dx == 0 || dy == 0) {
			leftClick(x, y);
			return;
		}
		if(!dragging) return;
		double scale;
		if(dx < dy) scale = width / dx;
		else scale = height / dy;
		
		viewX += 2*zoom*mouseX/width;
		viewY += zoom*mouseY/height;
		
		zoom /= scale;
		update();
	}
	public void newPallette() {
		pal = (pal + 1) % colpal.length;
		update();
	}
	public void reset() {
		viewX = 0;
		viewY = 0;
		zoom = 1.0;
		update();
	}
	
	// Computes a colour for a given point
	private Color color(double x, double y) {
		int count = mandel(x, y);
		int palSize = colors[pal].length;
		Color color = colors[pal][count / 256 % palSize];
		if (smooth) {
			Color color2 = colors[pal][(count / 256 + palSize - 1) % palSize];
			int k1 = count % 256;
			int k2 = 255 - k1;
			int red = (k1 * color.getRed() + k2 * color2.getRed()) / 255;
			int green = (k1 * color.getGreen() + k2 * color2.getGreen()) / 255;
			int blue = (k1 * color.getBlue() + k2 * color2.getBlue()) / 255;
			color = new Color(red, green, blue);
		}
		return color;
	}
	
	private int mandel(double pRe, double pIm) {
		double zRe = 0;
		double zIm = 0;
		double zRe2 = zRe * zRe;
		double zIm2 = zIm * zIm;
		double zM2 = 0.0;
		int count = 0;
		int max = (int) Math.pow(2, current_max);
		while (zRe2 + zIm2 < 4.0 && count < max) {
			zM2 = zRe2 + zIm2;
			zIm = 2.0 * zRe * zIm + pIm;
			zRe = zRe2 - zIm2 + pRe;
			zRe2 = zRe * zRe;
			zIm2 = zIm * zIm;
			count++;
		}
		if (count == 0 || count == max) return 0;
		// transition smoothing
		zM2 += 0.000000001;
		return 256 * count + (int)(255.0 * Math.log(4 / zM2) / Math.log((zRe2 + zIm2) / zM2));
	}

	@Override
	public void leftClick(int x, int y) {
		int imageSize = Math.round(width / 20);
		if(y < 20 || y > 20 + imageSize) return;
		if(x > width - 20 - imageSize && x < width - 20) reset();
		if(x > width - 40 - 2*imageSize && x < width - 40 - imageSize) newPallette();
	}

	@Override
	public void rightClick(int x, int y) {
		if(dragging) dragging = false;
		else reset();
	}

	@Override
	protected void tick(float delta) {
		long timeSinceLastRender = System.currentTimeMillis() - lastRender;
		if(timeSinceLastRender > 500 && !improving && current_max <= absolute_max) {
			improve = new Thread() {
				public void run() {
					improving = true;
					while(improving && current_max <= absolute_max) {
						current_max++;
						draw();
					}
					improving = false;
				}
			};
			improve.start();
		}
	}

	@Override
	protected void render(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		if(image == null) g.fillRect(0, 0, width, height);
		else g.drawImage(image, null, 0, 0);
		
		g.setColor(Color.white);
		if(dragging) {
			double dx = Math.abs(dragX - mouseX);
			double dy = Math.abs(dragY - mouseY);
			if(dx != 0 && dy != 0) {
				double scale;
				if(dx < dy) scale = width / dx;
				else scale = height / dy;
				g.drawRect(mouseX, mouseY, (int) Math.round(width/scale), (int) Math.round(height/scale));
			}
		}
		
		int imageSize = Math.round(width / 20);
		g.drawImage(sprites[1], width - 40 - 2*imageSize, 20, imageSize, imageSize, null);
		g.drawImage(sprites[0], width - 20 - imageSize, 20, imageSize, imageSize, null);

	}
	
	@Override
	public void resize(int w, int h) {
		if(width == w && height == h) return;
		width = w;
		height = h;
		update();
	}
	
	public static void main(String[] args) {
		new Launcher(new Mandelbrot());
	}

	@Override
	protected void loadImages() throws Exception{
		sprites = new BufferedImage[2];
		try {
			sprites[0] = ImageIO.read(getClass().getResource("/res/Reload.png"));
			sprites[1] = ImageIO.read(getClass().getResource("/res/Art.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
