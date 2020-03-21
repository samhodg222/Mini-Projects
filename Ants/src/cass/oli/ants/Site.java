package cass.oli.ants;

import java.awt.Color;
import java.util.Random;

public class Site extends Vec{
	public final Color colour;
	
	public Site() {
		super(Math.random(), Math.random());
		Random random = new Random();
		colour = new Color(random.nextInt(128) + 127, random.nextInt(128) + 127, random.nextInt(128) + 127);
	}
	
	public Site(double x, double y) {
		super(x, y);
		Random random = new Random();
		colour = new Color(random.nextInt(128) + 127, random.nextInt(128) + 127, random.nextInt(128) + 127);
	}
	
	public void scale(double i, double j) {
		this.x *= i;
		this.y *= j;
	}
}
