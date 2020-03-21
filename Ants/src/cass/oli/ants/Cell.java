package cass.oli.ants;

import java.util.ArrayList;
import java.util.Collections;

public class Cell {
	Site site;
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	
	public Cell(Site site, Edge[] edges) {
		this.site = site;
		Collections.addAll(this.edges, edges);
	}
	
	public Cell(Site site) {
		this.site = site;
	}
}
