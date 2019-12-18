package cass.oli.isometric_engine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class isometric_render implements Runnable{
	Shape3D shape;
	double[][] render_mat;
	int refresh;
	final long OPTIMAL_TIME;
	Camera camera;
	output_render output;
	ArrayList<Light> lights;
	
	public isometric_render(Shape3D shape, int refresh, Camera camera, Light light){
		this.shape = shape;
		this.refresh = refresh;
		if(refresh != 0) OPTIMAL_TIME = 1000000000 / refresh; else OPTIMAL_TIME = 0;
		this.camera = camera;
		
		lights = new ArrayList<Light>();
		lights.add(light);
		
		output = new output_render("Isometric Render", camera.width, camera.height);
		output.show();
	}
	
	public void run(){
		while (output.frame.isVisible()) {
			long lastTime = System.nanoTime();
			camera.update();
			
			for(Light light : lights){
				light.update();
			}
			
			draw();
			long tick_time = (lastTime - System.nanoTime() + OPTIMAL_TIME)/1000000;
			if(tick_time > 0){
				try{
					Thread.sleep(tick_time);
				}catch(Exception e) {}
			}
		}
	}
	
	public void draw(){
		//Multiply x_mat by z_mat
		render_mat = isometric_engine.multiplicar(x_mat(), z_mat());
		
		 Arrays.sort(shape.planes, new SortByDepth(render_mat, shape.scale, camera));
		 
		 BufferedImage bs = new BufferedImage(camera.width, camera.height, BufferedImage.TYPE_INT_RGB);
		
		//Draw Each Plane
		for(Plane p : shape.planes){
			if(!p.facing(camera)) continue;
			
			ArrayList<ArrayList<Integer>> pixels = p.render(render_mat, shape.scale, camera, lights, shape);
			for(ArrayList<Integer> pixel : pixels){
				if(pixel.get(0) >= camera.width || pixel.get(0) <= 0) continue;
				if(pixel.get(1) >= camera.height || pixel.get(1) <= 0) continue;
				if(pixel.get(2) == 0 && pixel.get(3) == 0 && pixel.get(4) == 0) continue;
				bs.setRGB(pixel.get(0), pixel.get(1), new Color(pixel.get(2), pixel.get(3), pixel.get(4)).getRGB());
			}
		}
		//Reset Canvas
		
		output.bs = bs;
		output.repaint();
	}
	
	public double[][] x_mat(){
		double a = Math.toRadians(camera.alpha);
		double[][] result = { {1, 0, 0}, {0, Math.cos(a), Math.sin(a)}, {0, -Math.sin(a), Math.cos(a)} };
		return result;
	}
	
	public double[][] z_mat(){
		double b = Math.toRadians(camera.beta);
		double result[][] = { {Math.cos(b), 0, -Math.sin(b)}, {0, 1, 0}, {Math.sin(b), 0, Math.cos(b)} };
		return result;
	}
}