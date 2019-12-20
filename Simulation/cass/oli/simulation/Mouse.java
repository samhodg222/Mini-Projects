package cass.oli.simulation;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

public class Mouse extends MouseInputAdapter {
	Game game;

	public void mousePressed(MouseEvent e) {
		game.mouseX = e.getX();
		game.mouseY = e.getY();
	}

	public void mouseDragged(MouseEvent e) {
		game.dragging = true;
		game.dragX = e.getX();
		game.dragY = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
		game.mouseX = e.getX();
		game.mouseY = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			game.dragTo(e.getX(), e.getY());
			game.dragging = false;
		}
	}

	public Mouse(Game game) {
		this.game = game;
	}

}