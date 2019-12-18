package cass.oli.double_pendulum;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Double_Pendulum implements Runnable{
	private final double theta1, theta2, l1, l2, g; //final while individual simulations are changeable
	static final int WIDTH = 1280, HEIGHT = 720;
	private Output output;
	
	public static final int TARGET_FPS = 60;
	final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

	public Double_Pendulum(double theta1, double theta2, double l1, double l2, double g) {
		this.theta1 = theta1;
		this.theta2 = theta2;
		this.l1 = l1;
		this.l2 = l2;
		this.g = g;
		
		output = new Output("Double Pendulum Simulation", Double_Pendulum.WIDTH, Double_Pendulum.HEIGHT);
		output.show();
	}
	
	@Override
	public void run() {
		while (output.frame.isVisible()) {
			long lastTime = System.nanoTime();
			//update
			//...
			//render
			draw();

			long tick_time = (lastTime - System.nanoTime() + OPTIMAL_TIME)/1000000;
			if(tick_time > 0){
				try{
					Thread.sleep(tick_time);
				}catch(Exception e) {}
			}
		}
	}
	
	public void draw() {
		
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Double Pendulum");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 500);
		
		JTextField theta1 = new JTextField("30", 20);
		JTextField theta2 = new JTextField("30", 20);
		JTextField l1     = new JTextField("100", 20);
		JTextField l2     = new JTextField("100", 20);
		JTextField g	  = new JTextField("9.8", 20);
		
		JButton run_button = new JButton("Run");
		run_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double theta_1 = Double.parseDouble(theta1.getText());
				double theta_2 = Double.parseDouble(theta2.getText());
				double l_1 = Double.parseDouble(l1.getText());
				double l_2 = Double.parseDouble(l2.getText());
				double G = Double.parseDouble(g.getText());

				Thread new_double_pendulum = new Thread(new Double_Pendulum(theta_1, theta_2, l_1, l_2, G));
				new_double_pendulum.start();
			}
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 4));;
		
		panel.add(new JLabel("Pendulum 1"));
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JLabel());
		
		panel.add(new JLabel("Theta:"));
		panel.add(theta1);
		panel.add(new JLabel("Length:"));
		panel.add(l1);
		
		panel.add(new JLabel("Pendulum 2"));
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(new JLabel());
		
		panel.add(new JLabel("Theta:"));
		panel.add(theta2);
		panel.add(new JLabel("Length:"));
		panel.add(l2);
		
		panel.add(new JLabel("g:"));
		panel.add(g);
		panel.add(new JLabel());
		panel.add(run_button);
		
		frame.getContentPane().add(BorderLayout.NORTH, panel);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
	}


}
