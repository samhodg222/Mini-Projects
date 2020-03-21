package cass.oli.ants;

public class Vec {
	protected double x, y;
	
	public Vec(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double x() { return this.x; }
	public int X() { return (int) Math.round(this.x); }
	public double y() { return this.y; }
	public int Y() { return (int) Math.round(this.y); }
	
	public double distSqr(double px, double py) { //Should be between 0 and 1
		double dx = this.x - px;
		double dy = this.y - py;
		return dx*dx + dy*dy;
	}
	
	public Vec add(Vec P) {
		return new Vec(this.x + P.x(), this.y + P.y());
	}
	
	public Vec minus(Vec P) {
		return new Vec(this.x - P.x(), this.y - P.y());
	}
	
	public Vec mult(double scale) {
		return new Vec(this.x * scale, this.y * scale);
	}
	
	public static double cross(Vec A, Vec B) {
		return A.x()*B.y() - B.x()*A.y();
	}
	
	public Vec round() {
		return new Vec(this.x(), this.Y());
	}
	
	public void print() {
		System.out.println(String.format("(%d, %d)", this.X(), this.Y()));
	}
}
