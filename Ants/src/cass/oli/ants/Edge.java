package cass.oli.ants;

public class Edge {
	public Vec A, B;
	public Edge(Vec A, Vec B){
		this.A = A;
		this.B = B;
	}
	
	public void print() {
		System.out.println("(" + A.X() + ", " + A.Y() + ") (" + B.X() + ", " + B.Y() + ")");
	}
}
