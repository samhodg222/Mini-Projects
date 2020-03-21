package cass.oli.ants;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;

public class Voronoi extends Canvas{
	//https://courses.cs.washington.edu/courses/cse326/00wi/projects/voronoi.html
	//https://leatherbee.org/index.php/2018/10/06/terrain-generation-3-voronoi-diagrams/
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int WIDTH = 1000, HEIGHT = 500;
	public static int numSites = 2;
	public static int siteRadius = 3; 
	
	private JFrame frame;
	
	final int width, height;
	final Site[] sites;
	
	ArrayList<Edge> edges;
	ArrayList<Cell> cells;
	
	public Voronoi(int width, int height, int siteCount) {
		this.width = width;
		this.height = height;
		
		Site[] sites = new Site[siteCount];
		for(int i = 0; i < siteCount; i++) sites[i] = new Site();
		for(Site site : sites) site.scale(width,  height);
		this.sites = sites;
		
		frame = new JFrame("Voronoi");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		
		this.setSize(width, height);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}
	
	public void paint(Graphics g) {
		//[1]
		cells = new ArrayList<Cell>();
		edges = new ArrayList<Edge>();
		generateBounds(); //[2]
		
		for(Site site : sites) { //[3]
			System.out.println("------------------------");
			site.print();
			System.out.println(":");
			
			Cell cell = new Cell(site); //[4]
			
			for(Cell c : cells) { //[5]
				Line pb = new Line(cell.site, c.site); //[6]
				ArrayList<Edge> delete = new ArrayList<Edge>();
				ArrayList<Vec> X = new ArrayList<Vec>(); //[7]
				
				for(Edge e : c.edges) {//[8]
					double srA = pb.sr(e.A);
					double srB = pb.sr(e.B);
					
					//[9]
					if(srA < 0 && srB < 0) {
						delete.add(e); // e is on the near side of pb (closer to site than to c's site) [10]
						//System.out.println("Edge on inside of pb");
						continue;
					}else if(srA < 0 && srB > 0) { //[11]
						Vec intersection = Line.intersection(pb, new Line(e));
						e.B = intersection;
						//intersection.print();
						X.add(intersection);
						//System.out.println("A on Inside, B on Outside");
					}else if(srA > 0 && srB < 0) { //[11]
						Vec intersection = Line.intersection(pb, new Line(e));
						e.A = intersection;
						//intersection.print();
						X.add(intersection);
						//System.out.println("B on Inside, A on Outside");
					}
				}
				
				//System.out.println(X.size());
				if(X.size() == 2) { //[12]
					Edge e = new Edge(X.get(0), X.get(1));
					e.print();
					c.edges.add(e);
					cell.edges.add(e);
					edges.add(e);
				}
				//[13]
				for(Edge e : delete) c.edges.remove(e);
				for(Edge e : delete) edges.remove(e);
			}
			cells.add(cell); //[14]
		}
		
		/*
		ArrayList<Line> rectangle = new ArrayList<Line>();
		rectangle.add(new Line(    0, height,     0, 0)); //left
		rectangle.add(new Line(    0,      0, width,      0)); //top
		rectangle.add(new Line(width, 0, width, height)); //right
		rectangle.add(new Line(width, height, 0, height)); //bottom
		
		for(Line border : rectangle) {//[15]
			ArrayList<Vec> P = new ArrayList<Vec>(); //[16]
			//P.add(border.A);
			//P.add(border.B);
			
			ArrayList<Edge> delete = new ArrayList<Edge>();
			
			for(Edge e : edges) { //[17]
				//[18]
				double srA = border.sr(e.A);
				double srB = border.sr(e.B);
				
				//System.out.println(String.format("%f, %f", srA, srB));
				
				if(srA < 0 && srB < 0) delete.add(e); //[19]
				else if(srA > 0 && srB < 0) { //[20]
					Vec intersection = Line.intersection(new Line(e), border);
					e.A = intersection;
					P.add(intersection);
				}else if(srA < 0 && srB > 0) { //[20]
					Vec intersection = Line.intersection(new Line(e), border);
					e.B = intersection;
					P.add(intersection);
				}
			}
			
			//[21]
			List<Vec> sortedP = null;
			if(border.t.y() == 0) {
				sortedP = P.stream()
						  .sorted(Comparator.comparing(Vec::x))
						  .collect(Collectors.toList());
			}else if(border.t.x() == 0){
				sortedP = P.stream()
						  .sorted(Comparator.comparing(Vec::y))
						  .collect(Collectors.toList());
			}
			
			for(int i = 0; i < sortedP.size() - 1; i++) {
				edges.add(new Edge(sortedP.get(i), sortedP.get(i+1))); 
			}
			
			for(Edge e : delete) edges.remove(e); //[22]
		}
		*/
		/*System.out.println("------------------");
		for(int i = 0; i < cells.size(); i++) {
			System.out.println(i + ": ");
			for(Edge e : cells.get(i).edges) {
				e.print();
			}
		}
		System.out.println("------------------");*/
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				Site closest = null;
				double dist = Double.POSITIVE_INFINITY;
				
				for(Cell c : cells) {
					if(c.site.distSqr(x, y) < dist) {
						dist = c.site.distSqr(x, y);
						closest = c.site;
					}
				}				
				
				image.setRGB(x, y, closest.colour.getRGB());
			}
		}
		
		g.drawImage(image, 0, 0, null);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(4));
		g2.setColor(Color.black);
		System.out.println("----------------------------");
		System.out.println("Clipped Edges");
		for(Edge e : edges) {
			e.print();
			g2.drawLine(e.A.X(), e.A.Y(), e.B.X(), e.B.Y());
		}
		
		g.setColor(Color.red);
		for(Cell c : cells) {
			g.fillOval(c.site.X() - siteRadius,  c.site.Y() - siteRadius, siteRadius*2, siteRadius*2);
		}
		
	}
	
	public void generateBounds() {
		Site topLeftSite = new Site(-width, -height);
		Edge[] topLeftEdges =  {
				new Edge(new Vec(width/2, height/2), new Vec(width/2, -10*height)),
				new Edge(new Vec(width/2, -10*height), new Vec(-10*width, height/2)),
				new Edge(new Vec(-10*width, height/2), new Vec(width/2, height/2))};
		Cell topLeftCell = new Cell(topLeftSite, topLeftEdges);
		
		Site topRightSite = new Site(2*width, -height);
		Edge[] topRightEdges =  {
				new Edge(new Vec(width/2, height/2), new Vec(width/2, -10*height)),
				new Edge(new Vec(width/2, -10*height), new Vec(10*width, height/2)),
				new Edge(new Vec(10*width, height/2), new Vec(width/2, height/2))};
		Cell topRightCell = new Cell(topRightSite, topRightEdges);
		
		Site bottomLeftSite = new Site(-width, 2*height);
		Edge[] bottomLeftEdges =  {
				new Edge(new Vec(width/2, height/2), new Vec(width/2, 10*height)),
				new Edge(new Vec(width/2, 10*height), new Vec(-10*width, height/2)),
				new Edge(new Vec(-10*width, height/2), new Vec(width/2, height/2))};
		Cell bottomLeftCell = new Cell(bottomLeftSite, bottomLeftEdges);
		
		Site bottomRightSite = new Site(2*width, 2*height);
		Edge[] bottomRightEdges =  {
				new Edge(new Vec(width/2, height/2), new Vec(width/2, 10*height)),
				new Edge(new Vec(width/2, 10*height), new Vec(10*width, height/2)),
				new Edge(new Vec(10*width, height/2), new Vec(width/2, height/2))};
		Cell bottomRightCell = new Cell(bottomRightSite, bottomRightEdges);
		
		cells.add(topLeftCell);
		cells.add(topRightCell);
		cells.add(bottomLeftCell);
		cells.add(bottomRightCell);
	}
	
	public static void main(String[] args) {
		new Voronoi(WIDTH, HEIGHT, numSites);
	}
}
