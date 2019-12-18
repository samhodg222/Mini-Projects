package cass.oli.isometric_engine;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

//Y is up,
//export as negative y, since y is inversed (might change)

//Shaders (pixel by pixel) 
//Proper Scaling
//video and image outputs

public class isometric_engine {
	static int output_width, output_height, refresh_time;
	static double refresh_alpha_displacement, refresh_beta_displacement, start_alpha, start_beta, scale, x_offset, y_offset;
	static double light_strength, light_x, light_y, light_z, light_delta_x, light_delta_y, light_delta_z;
	
	public static void main(String[] args) {
		System.out.println("Isometric Engine");
		System.out.println("Designed By Oliver Cass");
		
		
		JFrame gui = new JFrame("Isometric Engine");
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setSize(700, 500);
		
		JTextField txt_file_loc = new JTextField("cube.stl", 20);
		JTextField txt_width = new JTextField("1280", 20);
		JTextField txt_height = new JTextField("720", 20);
		JTextField txt_refresh = new JTextField("40", 20);
		JTextField txt_refresh_alpha = new JTextField("0", 20);
		JTextField txt_refresh_beta = new JTextField("5", 20);
		JTextField txt_start_alpha = new JTextField("35", 20);
		JTextField txt_start_beta = new JTextField("45", 20);
		JTextField txt_scale = new JTextField("0.5", 20);
		JTextField txt_offset_x = new JTextField("0.5", 20);
		JTextField txt_offset_y = new JTextField("0.5", 20);
		
		//Light Settings, Allow more in future
		JTextField txt_light_strength = new JTextField("1000", 20);
		JTextField txt_light_x = new JTextField("100", 20);
		JTextField txt_light_y = new JTextField("10", 20);
		JTextField txt_light_z = new JTextField("10", 20);
		JTextField txt_light_delta_x = new JTextField("-0.1", 20);
		JTextField txt_light_delta_y = new JTextField("0", 20);
		JTextField txt_light_delta_z = new JTextField("0", 20);
		
		
		JButton render_button = new JButton("Render");
		render_button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				output_width = Integer.parseInt(txt_width.getText());
				output_height = Integer.parseInt(txt_height.getText());
				refresh_time = Integer.parseInt(txt_refresh.getText());
				refresh_alpha_displacement = Double.parseDouble(txt_refresh_alpha.getText());
				refresh_beta_displacement = Double.parseDouble(txt_refresh_beta.getText());
				start_alpha = Double.parseDouble(txt_start_alpha.getText());
				start_beta = Double.parseDouble(txt_start_beta.getText());
				scale = Double.parseDouble(txt_scale.getText());
				x_offset = Double.parseDouble(txt_offset_x.getText());
				y_offset = Double.parseDouble(txt_offset_y.getText());
				
				light_strength = Double.parseDouble(txt_scale.getText());
				
				String fileName = txt_file_loc.getText();
				
				String file_type = "";

				int i = fileName.lastIndexOf('.');
				if (i > 0) {
					file_type = fileName.substring(i+1);
				}
				if(file_type.equals("stl"))	render_stl(fileName);
				else System.out.println("Unrecognised File Extension");
			}
		});
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 4));
		panel.add(new JLabel("File Location: "));
		panel.add(txt_file_loc);
		
		panel.add(new JLabel());
		panel.add(new JLabel());

		panel.add(new JLabel("Window Information:"));
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JLabel());

		panel.add(new JLabel("Window Width: "));
		panel.add(txt_width);
		panel.add(new JLabel("Window Height: "));
		panel.add(txt_height);
		panel.add(new JLabel("FPS: "));
		panel.add(txt_refresh);
		
		panel.add(new JLabel());
		panel.add(new JLabel());
		
		panel.add(new JLabel("Camera Information: "));
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JLabel());
		
		panel.add(new JLabel("Alpha Rotation (x): "));
		panel.add(txt_refresh_alpha);
		panel.add(new JLabel("Beta Rotation (z): "));
		panel.add(txt_refresh_beta);
		panel.add(new JLabel("Alpha Start (x): "));
		panel.add(txt_start_alpha);
		panel.add(new JLabel("Beta Start (z): "));
		panel.add(txt_start_beta);
		panel.add(new JLabel("Scale: "));
		panel.add(txt_scale);
		panel.add(new JLabel("X-Offset: "));
		panel.add(txt_offset_x);
		panel.add(new JLabel("Y-Offset: "));
		panel.add(txt_offset_y);
		
		panel.add(new JLabel());
		panel.add(new JLabel());
		
		panel.add(new JLabel("Light Information:"));
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JLabel());
		
		panel.add(new JLabel("Light Strength: "));
		panel.add(txt_light_strength);
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JLabel("Light Location: "));
		panel.add(txt_light_x);
		panel.add(txt_light_y);
		panel.add(txt_light_z);
		panel.add(new JLabel("Light Delta: "));
		panel.add(txt_light_delta_x);
		panel.add(txt_light_delta_y);
		panel.add(txt_light_delta_z);

		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JLabel());
		
		panel.add(render_button);
		
		gui.getContentPane().add(BorderLayout.NORTH, panel);
		gui.pack();
		gui.setVisible(true);
		gui.setResizable(false);
	}
		
	public static void render_stl(String fileName){
		String content = "";
		try{
			
			Scanner scan = new Scanner(new File("src/stl/" + fileName));
			content = scan.useDelimiter("\\Z").next();
			scan.close();
		}catch(Exception e){
			System.out.println("Invalid File Name");
			return;
		}	
		
		//Define STL and turn into array of planes
		Shape3D shape = new STL(content, output_width, scale);
		render(shape);
	}

	public static void render(Shape3D shape){
		Camera camera = new Camera(start_alpha, start_beta, refresh_alpha_displacement, refresh_beta_displacement, x_offset, y_offset, output_width, output_height);
		Light light = new Light(light_x, light_y, light_z, light_strength, light_delta_x, light_delta_y, light_delta_z);
		
		if(refresh_time > 0){
			Thread new_render = new Thread(new isometric_render(shape, refresh_time, camera, light));
			new_render.start();
		}else{
			isometric_render new_render = new isometric_render(shape, refresh_time, camera, light);
			new_render.draw();
		}
		System.out.println("Shape contains " + shape.planes.length + " Planes");
	}
	
	public static double[][] multiplicar(double[][] A, double[][] B) {
        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] C = new double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                C[i][j] = 0.00000;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }
}