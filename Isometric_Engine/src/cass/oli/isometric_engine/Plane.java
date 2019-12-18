package cass.oli.isometric_engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

public class Plane{
	public Vec3 A;
	public Vec3 B;
	public Vec3 C;
	public Vec3 normal;
	public Color color;
	
	public Plane(Vec3 newA, Vec3 newB, Vec3 newC, Vec3 newNormal, Color COLOR){
		A = newA;
		B = newB;
		C = newC;
		normal = newNormal;
		color = COLOR;
	}
	
	public boolean facing(Camera camera){
		//direction of camera
		double alpha = Math.toRadians(camera.alpha);
		double beta = Math.toRadians(camera.beta);
		Vec3 cameraNormal = new Vec3(Math.sin(beta) * Math.cos(alpha), Math.sin(-alpha), Math.cos(alpha) * Math.cos(beta));
		
		double result = normal.dot(cameraNormal);
		if(result < 0) {
			return false;
		}
		return true;
	}
	
	public boolean equals(Vec3 A1, Vec3 B1, Vec3 C1){
		if(A.equals(A1) && B.equals(B1) && C.equals(C1)) return true;
		return false;
	}
	
	public ArrayList<ArrayList<Integer>> render (double[][] render_mat, double scale, Camera camera, ArrayList<Light> lights, Shape3D shape){
		ArrayList<ArrayList<Integer>> pixels = new ArrayList<ArrayList<Integer>>();
		/*
			x y r g b
			etc..
		*/
		
		int[] pointA = A.to2D(render_mat, scale, camera);
		int[] pointB = B.to2D(render_mat, scale, camera);
		int[] pointC = C.to2D(render_mat, scale, camera);
		
		//Vec3 av = A.add(B).add(C).divide(3);
		double influence = 1;
		/*
		for(Light light : lights){
			influence += light.influence(av, shape, this);
		}
		influence = influence / lights.size();
		*/
		
		Color render_color = new Color((int) Math.round(color.getRed() * influence), (int) Math.round(color.getGreen() * influence), (int) Math.round(color.getBlue() * influence));
		
		//Determine Area of Trinagle
		double Area = ( pointA[0] * (pointB[1] - pointC[1]) + pointB[0] * (pointC[1] - pointA[1]) + pointC[0] * (pointA[1] - pointB[1]))/2;
		
		//Draw pixels for lines that exist in plane
		for (int x = 0; x <= camera.width; x++){
			if(pointA[0] < x && pointB[0] < x && pointC[0] < x) continue;
			if(pointA[0] > x && pointB[0] > x && pointC[0] > x) continue;
            for (int y = 0; y <= camera.height; y++){
				if(pointA[1] < y && pointB[1] < y && pointC[1] < y) continue;
				if(pointA[1] > y && pointB[1] > y && pointC[1] > y) continue;
				
				double s = 1/(2*Area)*(pointA[1]*pointC[0] - pointA[0]*pointC[1] + (pointC[1] - pointA[1])*x + (pointA[0] - pointC[0])*y);
				double t = 1/(2*Area)*(pointA[0]*pointB[1] - pointA[1]*pointB[0] + (pointA[1] - pointB[1])*x + (pointB[0] - pointA[0])*y);
				
				
				if(s>0 && t>0 && 1-s-t>0) {
					ArrayList<Integer> pixel = new ArrayList<Integer>();
					pixel.add(x);
					pixel.add(y);
					pixel.add(render_color.getRed());
					pixel.add(render_color.getGreen());
					pixel.add(render_color.getBlue());
					
					pixels.add(pixel);
				}
            }
        }
		return pixels;
	}
}

class SortByDepth implements Comparator<Plane> { 
    // Used for sorting in ascending order of 
    // roll number
	double[][] render_mat;
	double scale;
	Camera camera;
	public SortByDepth(double[][] render_mat, double scale, Camera camera){
		this.render_mat = render_mat;
		this.scale = scale;
		this.camera = camera;
	}
	
    public int compare(Plane a, Plane b) {
		int[] a_A = a.A.to2D(render_mat, scale, camera); //[2] = depth (distance from camera)
		int[] a_B = a.B.to2D(render_mat, scale, camera);
		int[] a_C = a.C.to2D(render_mat, scale, camera);
		int[] b_A = b.A.to2D(render_mat, scale, camera);
		int[] b_B = b.B.to2D(render_mat, scale, camera);
		int[] b_C = b.C.to2D(render_mat, scale, camera);
		
		
        return Double.compare((a_A[2] + a_B[2] + a_C[2]), (b_A[2] + b_B[2] + b_C[2])); 
    } 
}
class SortByDistance implements Comparator<Plane> { 
	Vec3 point;
	public SortByDistance(Vec3 point){
		this.point = point;
	}
	
    public int compare(Plane a, Plane b) {
		double a_A  = a.A.distTo(point);
		double a_B  = a.B.distTo(point);
		double a_C  = a.C.distTo(point);
		
		double b_A  = b.A.distTo(point);
		double b_B  = b.B.distTo(point);
		double b_C  = b.C.distTo(point);
		
        return Double.compare((b_A + b_B + b_C), (a_A + a_B + a_C)); 
    } 
}