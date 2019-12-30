package cass.oli.glasswash;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class GUI {
	public GUI() {
		JFrame frame = new JFrame("Glass Wash");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 700);
		
		JTextArea area = new JTextArea(42, 60);
		JButton submit = new JButton("Submit");
		
		
		Render canvas = new Render();
		
		submit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String text = area.getText();
				
				String[] lines = text.split("\\r?\\n");;
				canvas.nodes.clear();
				
				for(int i = 0; i < lines.length; i++) {
					String[] data = lines[i].split("\\s+");
					int x = Integer.parseInt(data[0]);
					int y = Integer.parseInt(data[1]);
					Color color = Color.black;
					switch(data[2]) {
						case("red"):
							color = Color.red;
							break;
					}
					canvas.nodes.add(new Node(x, y, color));
				}
				
				canvas.repaint();
			}
		});
		
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
		panel.add(area, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(canvas, gbc);
		
		gbc.gridx = 0;
	    gbc.gridy = 2;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.gridwidth = 2;
		panel.add(submit, gbc);
		
		frame.add(panel);
		
		
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
	}
}

class Render extends Canvas{
	final int grid_size = 50;
	final int num_grids;
	
	ArrayList<Node> nodes = new ArrayList<Node>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Render() {
		setBackground(Color.BLACK);  
        setSize(750, 750); 
        num_grids = this.getWidth() / grid_size;
	}
	
	public void paint(Graphics g) {
		grid(g);
		for(Node node : nodes) {
			g.setColor(node.color);
			g.fillRect(node.x*grid_size, node.y*grid_size, grid_size, grid_size);
		}
	}
	
	public void grid(Graphics g) {
		boolean alternate = false;
		for(int y = 0; y < this.getHeight(); y += grid_size) {
			for(int x = 0; x < this.getWidth(); x += grid_size) {
				if(alternate) g.setColor(Color.GRAY); else g.setColor(Color.DARK_GRAY);
				g.fillRect(x, y, grid_size, grid_size);
				alternate = !alternate;
			}
		}
	}
}
