package gui2d.abstracts;

import model.database.Database;

/**
 * A class representing a button.
 * 
 * @author Michael
 *
 */
public abstract class Button2D extends Panel2D {
	private static final String BUTTON_BACKGROUND = "button_normal";
	private static final String BUTTON_BACKGROUND_OVER = "button_over";
	private static final String BUTTON_BACKGROUND_PRESSED = "button_pressed";

	public Button2D(String name, float width, float height) {
		super(name, Database.getAssetKey(BUTTON_BACKGROUND), width, height);
	}

	@Override
	public void mouseEnter() {
		this.setTexture(Database.getAssetKey(BUTTON_BACKGROUND_OVER));
	}

	@Override
	public void mouseExit() {
		this.setTexture(Database.getAssetKey(BUTTON_BACKGROUND));
	}

	/**
	 * When the mouse is pressed on this panel.
	 */
	public void mousePressed() {
		this.setTexture(Database.getAssetKey(BUTTON_BACKGROUND_PRESSED));
	}

	/**
	 * When the mouse is released on this panel.
	 */
	public void mouseReleased() {
		this.setTexture(Database.getAssetKey(BUTTON_BACKGROUND_OVER));
	}
}
