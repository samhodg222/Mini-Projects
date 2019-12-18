package cass.oli.isometric_engine;

public class Camera{
	public double alpha, beta;
	double d_alpha, d_beta;
	public double x_offset, y_offset;
	public int width, height;
	public Camera(double alpha, double beta, double d_alpha, double d_beta, double x_offset, double y_offset, int width, int height){
		this.alpha = alpha;
		this.beta = beta;
		this.d_alpha = d_alpha;
		this.d_beta = d_beta;
		this.x_offset = x_offset;
		this.y_offset = y_offset;
		this.width = width;
		this.height = height;
	}
	
	public void update(){
		alpha += d_alpha;
		beta += d_beta;
	}
}