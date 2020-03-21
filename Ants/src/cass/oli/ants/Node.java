package cass.oli.ants;

public class Node {
	public int x, y;
	public final int id;
	public int[] connected;
	
	public Node(int x, int y, int id, int[] connected) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.connected = connected;
	}
}
