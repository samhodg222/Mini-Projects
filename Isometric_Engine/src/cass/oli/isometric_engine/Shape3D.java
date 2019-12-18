package cass.oli.isometric_engine;

public abstract class Shape3D{
	public Vec3 origin;
	public Vec3 rot;
	public Plane[] planes;
	public double scale;
	
	public Shape3D(double x, double y, double z){
		origin = new Vec3(x, y, z);
		
		rot = new Vec3(0, 0, 0); //Later...
	}
}