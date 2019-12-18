package cass.oli.isometric_engine;


//TODO SOME VECTOR MATHS
public class Light{
	public Vec3 loc;
	public Vec3 displacement;
	public double strength;
	public Light(double x, double y, double z, double strength, double dx, double dy, double dz){
		this.loc = new Vec3(x, y, z);
		displacement = new Vec3(dx, dy, dz);
		this.strength = strength;
	}
	
	public double influence(Vec3 point, Shape3D shape, Plane plane){
		Vec3 dir = point.minus(loc);
		double dist = dir.getDist();
		double inv = Math.pow(dist, 2);
		if(inv < strength) inv = strength;
		double inf = strength / inv;
		if(collision(point, dir, shape, plane)) inf = inf / 5;
		return inf;
	}
	
	public void update(){
		loc = loc.add(displacement);
	}
	
	public boolean collision(Vec3 origin, Vec3 dir, Shape3D shape, Plane plane){
		for(Plane p : shape.planes){
			if(p.equals(plane)) continue;
			Vec3 u = p.B.minus(p.A);
			Vec3 v = p.C.minus(p.A);
			Vec3 w0 = p.A.minus(loc);
			
			double a = -p.normal.dot(w0);
			double b = p.normal.dot(dir);
			
			if(b == 0) continue;
			
			double r = a / b;
			if(Math.abs(r) > 1) continue;
			//if(r < 0) continue; //travels away from plane
			
			Vec3 I = origin.add(dir.multi(r));
			
			double uu, uv, vv, wu, wv, D;
			uu = u.dot(u);
			uv = u.dot(v);
			vv = v.dot(v);
			Vec3 w = I.minus(p.A);
			wu = w.dot(u);
			wv = w.dot(v);
			
			D = uv * uv - uu * vv;
			
			double s, t;
			
			s = (uv * wv - vv * wu) / D;
			if(s < 0 || s > 1) continue;
			t = (uv * wu - uu * wv) / D;
			if (t < 0 || (s + t) > 1) continue;
			return true;
		}
		return false;
	}
}