package cass.oli.pendulum;

public class Connection {
	public Node A;
	public Node B;
	public ConnectionType type;
	
	public Connection(Node A, Node B, ConnectionType type) {
		this.A = A;
		this.B = B;
		this.type = type;
	}
}
