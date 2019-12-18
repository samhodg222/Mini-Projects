package cass.oli.isometric_engine;

import java.util.ArrayList;
import java.awt.Color;

public class STL extends Shape3D{
	public STL(String ascii, int width, double scale){
		super(0, 0, 0);
		
		planes = ascii2planes(ascii);
		
		double x_min = Double.POSITIVE_INFINITY;
		double y_min = Double.POSITIVE_INFINITY;
		double z_min = Double.POSITIVE_INFINITY;
		
		double x_max = Double.NEGATIVE_INFINITY;
		double y_max = Double.NEGATIVE_INFINITY;
		double z_max = Double.NEGATIVE_INFINITY;
		
		
		for(Plane p : planes){
			if(p.A.x < x_min) x_min = p.A.x;
			if(p.A.y < y_min) y_min = p.A.y;
			if(p.A.z < z_min) z_min = p.A.z;
			
			if(p.A.x > x_max) x_max = p.A.x;
			if(p.A.y > y_max) y_max = p.A.y;
			if(p.A.z > z_max) z_max = p.A.z;
			
			if(p.B.x < x_min) x_min = p.B.x;
			if(p.B.y < y_min) y_min = p.B.y;
			if(p.B.z < z_min) z_min = p.B.z;
			
			if(p.B.x > x_max) x_max = p.B.x;
			if(p.B.y > y_max) y_max = p.B.y;
			if(p.B.z > z_max) z_max = p.B.z;
			
			if(p.C.x < x_min) x_min = p.C.x;
			if(p.C.y < y_min) y_min = p.C.y;
			if(p.C.z < z_min) z_min = p.C.z;
			
			if(p.C.x > x_max) x_max = p.C.x;
			if(p.C.y > y_max) y_max = p.C.y;
			if(p.C.z > z_max) z_max = p.C.z;
		}
		
		this.scale = width / ( (x_max - x_min) + (y_max - y_min) + (z_max - z_min)) * scale;
	}
	
	Plane[] ascii2planes(String ascii){
		ArrayList<Plane> strp = new ArrayList<Plane>();
		
		String lines[] = ascii.split("\\r?\\n");
		int num_of_planes = (lines.length - 2) / 7;
				
		int line = 2;
		for(int i = 0; i < num_of_planes; i++){
			String[] split = lines[line - 1].split(" ");
			Vec3 normal = new Vec3(Double.valueOf(split[2]), Double.valueOf(split[3]), Double.valueOf(split[4]));
			
			line += 2;
			
			split = lines[line - 1].split(" ");
			Vec3 A = new Vec3(Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]));
			
			line++;
			split = lines[line - 1].split(" ");
			Vec3 B = new Vec3(Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]));
			
			line++;
			split = lines[line - 1].split(" ");
			Vec3 C = new Vec3(Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]));
			
			strp.add(new Plane(A, B, C, normal, new Color((int)(Math.random() * 0x1000000))));
			
			line += 3;
		}
		
		
		return strp.toArray(new Plane[strp.size()]);
	}
}