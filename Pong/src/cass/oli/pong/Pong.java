package cass.oli.pong;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import cass.oli.simulation.Game;
import cass.oli.simulation.Launcher;

public class Pong extends Game{
	
	public static final float EPSILON_TIME = 1e-2f; //Threshold for zero time
	public BoxContainer box;
	public ArrayList<GameObject> objects = new ArrayList<GameObject>();
	public ArrayList<GameObject> addObjects = new ArrayList<GameObject>();
	public ArrayList<GameObject> removeObjects = new ArrayList<GameObject>();
	private int objID = 0;
	private final int spriteRatio = 10;
	
	public Pong() {
		name = "Pong";
		box = new BoxContainer(0, 0, width, height);
	}
	
	public void tap(int x, int y) {
		//Organise top down in terms of layer height
		//return after each function
		
		//Pause Button
		if(x > width - 60 && x < width - 10 && y > 10 && y < 70) {
			paused = !paused;
			return;
		}
		if(paused) {
			int nButtons = 1;
			int spriteWidth = (int) Math.round(width / spriteRatio); 
			int offset = (int) Math.round((height - spriteWidth * nButtons)/(nButtons+1));
			
			//Reset
			if(		x > (width - spriteWidth)/2 && x < (width + spriteWidth)/2 &&
					y > offset && y < offset + spriteWidth) {
				removeObjects.addAll(objects);
				paused = false;
				return;
			}
		}else {
			//Remove Ball
			for(GameObject object : objects){
				if(object.shape == Shape.Circle) {
					Ball ball = (Ball) object;
					double xDist = x - ball.x;
		            double yDist = y - ball.y;
		            double dist = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
		            if(dist < ball.radius) {
		    			//ball.print();
		                removeBall(ball);
		                return;
		            }
				}
			}
			
			//Add Ball
			addBall(new Ball(x, y, objID));
			objID++;	
		}
	}
	
	public void tick(float delta) {
		float timeLeft = 1.0f;
		
		//Synchronises adding and removing objects
		for(int i = 0; i < addObjects.size(); i++) {
			GameObject object = addObjects.get(i);
			boolean add = true;
			
			switch(object.shape) {
			case Circle:
				Ball nBall = (Ball) object;
				if(        nBall.x - nBall.radius < 0
						|| nBall.x + nBall.radius > width
						|| nBall.y - nBall.radius < 0
						|| nBall.y + nBall.radius > height) {
					add = false;
					break;
				}
				for(GameObject exist : objects) {
					switch(exist.shape) {
					case Circle:
						Ball ball = (Ball) exist;
						if(ball.ballCollision(nBall)) add = false;
						break;
					}
				}
				break;
			default:
				break;
			
			}
			
			if(add) objects.add(object);			
		}
		addObjects.clear();
		
		for(GameObject object : removeObjects) {
			objects.remove(object);
		}
		removeObjects.clear();
		
		do {
			float tMin = timeLeft;
			
			//Object-Object Collision
			for(int i = 0; i < objects.size(); i++) {
				for(int j = 0; j < objects.size(); j++) {
					if(i < j) {
						objects.get(i).intersect(objects.get(j), tMin);
						if(objects.get(i).earliestCollisionResponse.t < tMin) 
							tMin = objects.get(i).earliestCollisionResponse.t;
					}
				}
			}
			
			//Object-Wall Collision
			for(GameObject object : objects) {
				object.intersect(box, timeLeft);
				if(object.earliestCollisionResponse.t < tMin) {
					tMin = object.earliestCollisionResponse.t;
				}
			}
			//Move
			for(GameObject object : objects) object.update(tMin);
			
			timeLeft -= tMin;
		}while(timeLeft > EPSILON_TIME);
	}
	
	public void render(Graphics2D g){
		//Background
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
				
		//Game Objects
		for(GameObject object : objects) {
			object.render(g);
		}
		
		//Pause Button
		g.setColor(Color.white);
		if(!paused) {
			g.fillRect(width - 60, 10, 20, 60);
			g.fillRect(width - 30, 10, 20, 60);
		}else {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, width, height);
			g.setColor(Color.white);
			
			int nButtons = 1;
			int spriteWidth = (int) Math.round(width / spriteRatio); 
			int offset = (int) Math.round((height - spriteWidth * nButtons)/(nButtons+1));
			
			g.drawImage(sprites[0], (width - spriteWidth)/2, offset, spriteWidth, spriteWidth, null);
			
			int[] xTri = {width- 60, width - 10, width - 60};
			int[] yTri = {10, 40, 70};
			g.fillPolygon(xTri, yTri, 3);
		}
	}
	
	@Override
	public void resize(int w, int h) {
		width = w;
		height = h;
		this.box = new BoxContainer(0, 0, width, height);
	}
	
	public void addBall(Ball ball) {
		addObjects.add(ball);
	}
	public void removeBall(Ball ball) {
		removeObjects.add(ball);
	}

	@Override
	public void leftClick(int x, int y) {
		tap(x, y);		
	}

	@Override
	public void rightClick(int x, int y) {
		//Do Nothing
	}
	@Override
	public void dragTo(int x, int y) {
		leftClick(x, y);
	}
	
	public static void main(String[] args) {
		new Launcher(new Pong());
	}
	
	@Override
	protected void loadImages() throws Exception{
		sprites = new BufferedImage[1];
		try {
			sprites[0] = ImageIO.read(getClass().getResource("/res/Reload.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
