package cass.oli.ants;

public class Line {
	public Vec O, t;
	
	public Vec A, B; //Not necessary
	
	public Line(Edge edge) {
		this.A = edge.A;
		this.B = edge.B;
		
		this.O = edge.A;
		this.t = edge.B.minus(edge.A);
	}
	
	public Line(double x1, double y1, double x2, double y2) { //Creates Line		
		this.A = new Vec(x1, y1);
		this.B = new Vec(x2, y2);
		
		this.O = A;
		this.t = B.minus(A);
	}
	
	public Line(Vec A, Vec B) { //Finds perpendicular bisector
		this.O = new Vec((A.x() + B.x())/2, (A.y() + B.y())/2); //Midpoint
		this.t = new Vec(A.y() - B.y(), B.x() - A.x()); //http://mathworld.wolfram.com/PerpendicularVector.html
		
		this.A = O;
		this.B = O.add(t);
	}
	
	public double sr(Vec P) {
		Vec Cross1 = P.minus(A);
		Vec Cross2 = B.minus(A);
		return Vec.cross(Cross1, Cross2);
	}
	
	public Vec r(double lambda) {
		return O.add(t.mult(lambda));
	}
	
	public static Vec intersection(Line A, Line B) {
		double Anom  = B.t.x()*(B.O.y() - A.O.y()) - B.t.y()*(B.O.x() - A.O.x());
		double Bnom  = A.t.x()*(B.O.y() - A.O.y()) - A.t.y()*(B.O.x() - A.O.x());
		double denom = B.t.x()*A.t.y() - A.t.x()*B.t.y();
		Vec A1 = A.r(Anom/denom);
		Vec B1 = B.r(Bnom/denom);		
		if(A1.X() != B1.X() || A1.Y() != B1.Y()) System.out.println("Shit");
		return A1.round();
	}
}
