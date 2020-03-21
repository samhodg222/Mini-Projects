package cass.oli.maths3d;

public class Vec {
	public float x, y, z;
	
	public Vec(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public int X() { return Math.round(x); }
	public int Y() { return Math.round(y); }
	public int Z() { return Math.round(z); }
	
	public void add(Vec B) {
		x += B.x;
		y += B.y;
		z += B.z;
	}
	
	public void limit(float factor) {
		if(x > factor) x = factor;
		if(x < -factor) x = -factor;
		if(y > factor) y = factor;
		if(y < -factor) y = -factor;
		if(z > factor) z = factor;
		if(z < -factor) z = -factor;
	}
	
	public void add(float factor) {
		x += factor;
		y += factor;
		z += factor;
	}
	
	public void mult(float factor) {
		x *= factor;
		y *= factor;
		z *= factor;
	}
	
	public float sqrdist(Vec B) {
		float dx = B.x - this.x;
		float dy = B.y - this.y;
		float dz = B.z - this.z;
		return dx*dx + dy*dy + dz*dz;
	}
	
	public static Vec diff(Vec A, Vec B) {
		Vec diff = new Vec();
		diff.x = B.x - A.x;
		diff.y = B.y - A.y;
		diff.z = B.z - A.z;
		return diff;
	}
	
	public void normalize() {
		double factor = Math.sqrt(sqrdist(new Vec()));
		divide(factor);
	}
	
	public void divide(double factor) {
		this.x /= factor;
		this.y /= factor;
		this.z /= factor;
	}
	
	public void sub(Vec b) {
		this.x -= b.x;
		this.y -= b.y;
		this.z -= b.z;
	}
}
