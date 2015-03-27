package gui2d.abstracts;

import model.database.Database;

public abstract class Window2D extends Panel2D {

	private static final String WINDOW_BACKGORUND = "window_bg";

	public Window2D(String name, float width, float height) {
		super(name, Database.getAssetKey(WINDOW_BACKGORUND), width, height);
	}

	@Override
	public void mouseEnter() {
		this.setTexture(Database.getAssetKey(WINDOW_BACKGORUND));
	}

	@Override
	public void mouseExit() {
		this.setTexture(Database.getAssetKey(WINDOW_BACKGORUND));
	}
}
