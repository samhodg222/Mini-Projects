package cass.oli.simulation;

public class Vec {
	public float x, y;

	public Vec(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void add(Vec v) {
		this.x += v.x;
		this.y += v.y;
	}

	public void add(float factor) {
		this.x += factor;
		this.y += factor;
	}

	public void sub(Vec v) {
		this.x -= v.x;
		this.y -= v.y;
	}

	public void mult(double factor) {
		this.x *= factor;
		this.y *= factor;
	}
	
	public Vec mult1(float factor) {
		return new Vec(this.x * factor, this.y * factor);
	}

	public void divide(double factor) {
		this.x /= factor;
		this.y /= factor;
	}

	public void limit(float factor) {
		if (x > factor)
			x = factor;
		if (x < -factor)
			x = -factor;
		if (y > factor)
			y = factor;
		if (y < -factor)
			y = -factor;
	}

	public void min(float factor) {
		if (x < factor && x > 0)
			x = factor;
		if (x > -factor && x < 0)
			x = -factor;
		if (y < factor && y > 0)
			y = factor;
		if (y > -factor && y < 0)
			y = -factor;
	}

	public void normalize() {
		double factor = Math.sqrt(x * x + y * y);
		divide((float) factor);
	}

	public double mag() {
		return x * x + y * y;
	}

	public static Vec add(Vec v1, Vec v2) {
		return new Vec(v1.x + v2.x, v1.y + v2.y);
	}

	public static Vec sub(Vec v1, Vec v2) {
		return new Vec(v1.x - v2.x, v1.y - v2.y);
	}

	public static double dist(Vec v1, Vec v2) {
		double xDist = v1.x - v2.x;
		double yDist = v1.y - v2.y;
		double dist2 = xDist * xDist + yDist * yDist;
		return Math.sqrt(dist2);
	}
}
