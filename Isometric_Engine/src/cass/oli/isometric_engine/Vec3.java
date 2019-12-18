package cass.oli.isometric_engine;

public class Vec3{
	public double x;
	public double y;
	public double z;
	
	public Vec3(double newx, double newy, double newz){
		x = newx;
		y = newy;
		z = newz;
	}
	
	public double dot(Vec3 B){
		double dotProduct = 0;
		
		dotProduct += x * B.x;
		dotProduct += y * B.y;
		dotProduct += z * B.z;
		
		return dotProduct;
	}
	
	public Vec3 cross(Vec3 B){
		Vec3 crossProduct = new Vec3(0, 0, 0);
		
		crossProduct.x = y * B.z - z * B.y;
		crossProduct.y = z * B.x - x * B.z;
		crossProduct.z = x * B.y - y * B.x;
		
		return crossProduct;
	}
	
	public void print(){
		System.out.println(x + ", " + y + ", " + z);
	}
	
	public Vec3 line(Vec3 B){
		Vec3 grad = new Vec3(0, 0, 0);
		
		grad.x = B.x - x;
		grad.y = B.y - y;
		grad.z = B.z - z;
		
		return grad;
	}
	
	public Vec3 unit(){		
		double pyth = getDist();
		
		Vec3 unit = new Vec3(x / pyth, y / pyth, z / pyth);
		
		return unit;
	}
	
	public double distTo(Vec3 point){
		Vec3 diff = this.minus(point);
		return diff.getDist();
	}

	public double getDist(){
		double pyth = Math.sqrt( Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
		
		return pyth;
	}
	public Boolean equals(Vec3 B){
		if(x == B.x && y == B.y && z == B.z) return true;
			else return false;
	}
	
	public Vec3 add(Vec3 b){
		return new Vec3(x + b.x, y + b.y, z + b.z);
	}
	public Vec3 minus(Vec3 b){
		return new Vec3(x - b.x, y - b.y, z - b.z);
	}
	
	public Vec3 multi(double mult){
		return new Vec3(x * mult, y * mult, z * mult);
	}
	public Vec3 divide(double div){
		return new Vec3(x / div, y / div, z / div);
	}
	
	public int[] to2D(double[][] render_mat, double scale, Camera camera){
		double[][] thisVec = {{x}, {y}, {z}};
		
		double[][] finalResult = isometric_engine.multiplicar(render_mat, thisVec);
				
		int[] coordinates = {(int) Math.round(scale * finalResult[0][0] + camera.width * camera.x_offset), (int) Math.round(scale * finalResult[1][0] + camera.height * camera.y_offset), (int) Math.round(finalResult[2][0])};
		return coordinates;
	}
}