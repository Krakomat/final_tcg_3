package gui2d.geometries.messages;

import model.database.Database;
import gui2d.GUI2D;
import gui2d.abstracts.Panel2D;
import gui2d.abstracts.SelectableNode;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

import common.utilities.Lock;

/**
 * A texted Button.
 * 
 * @author Michael
 *
 */
public abstract class TextPanel2D extends Node implements SelectableNode {

	private String text;
	/** The actual button text */
	private BitmapText textBitmap;
	/** Actual panel element */
	private Panel2D panel;
	/** Level for the starting zPosition */
	private float level;
	private float width, height;
	private boolean visible;
	private Lock lock;

	public TextPanel2D(String name, String text, float width, float height) {
		this.name = name;
		this.text = text;
		this.visible = true;
		this.lock = new Lock();
		this.width = width;
		this.height = height;

		panel = new Panel2D(name, Database.getAssetKey("panel_bg"), width, height) {
			@Override
			public void mouseEnter() {
				// Do nothing here!!!
			}

			@Override
			public void mouseExit() {
				// Do nothing here!!!
			}

			@Override
			public void mouseSelect() {
				// Do nothing here!!!
			}

			@Override
			public void mousePressed() {
				// Do nothing here!
			}

			@Override
			public void mouseReleased() {
				// Do nothing here!
			}
		};
		this.attachChild(panel);

		// Button text:
		this.textBitmap = new BitmapText(GUI2D.getInstance().getGuiFont(), false);
		this.textBitmap.setSize(GUI2D.getInstance().getGuiFont().getCharSet().getRenderedSize()); // font size
		this.textBitmap.setText(this.text); // the text
		this.textBitmap.setColor(ColorRGBA.Black); // font color

		// Adjust size:
		while (this.textBitmap.getLineHeight() > height || this.textBitmap.getLineWidth() > width * 0.5f)
			this.textBitmap.setSize(this.textBitmap.getSize() - 0.001f);

		while (this.textBitmap.getLineHeight() < height && this.textBitmap.getLineWidth() < width * 0.5f)
			this.textBitmap.setSize(this.textBitmap.getSize() + 0.001f);

		this.textBitmap.setLocalTranslation(width * 0.5f - this.textBitmap.getLineWidth() / 2, height * 0.50f + this.textBitmap.getLineHeight() / 2, 0.00001f);
		this.attachChild(this.textBitmap);
	}

	/**
	 * Sets the text for this button.
	 * 
	 * @param text
	 */
	public void setText(String text) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.text = text;
		this.lock.unlock();
	}

	@Override
	public void update() {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (this.isVisible()) {
			if (!this.hasChild(panel))
				this.attachChild(panel);

			textBitmap.setText(text);

			// Adjust size:
			while (this.textBitmap.getLineHeight() > height || this.textBitmap.getLineWidth() > width * 0.5f)
				this.textBitmap.setSize(this.textBitmap.getSize() - 0.001f);

			while (this.textBitmap.getLineHeight() < height && this.textBitmap.getLineWidth() < width * 0.5f)
				this.textBitmap.setSize(this.textBitmap.getSize() + 0.001f);

			this.textBitmap.setLocalTranslation(width * 0.5f - this.textBitmap.getLineWidth() / 2, height * 0.50f + this.textBitmap.getLineHeight() / 2, 0.00001f);

			if (!this.hasChild(textBitmap))
				this.attachChild(textBitmap);
		} else {
			if (this.hasChild(panel))
				this.detachChild(panel);

			if (this.hasChild(textBitmap))
				this.detachChild(textBitmap);
		}
		this.lock.unlock();
	}

	@Override
	public void mouseEnter() {
		this.panel.mouseEnter();
	}

	@Override
	public void mouseExit() {
		this.panel.mouseExit();
	}

	public abstract void mouseSelect();

	public abstract void mouseSelectRightClick();

	public void audioSelect() {

	}

	public void mousePressed() {

	}

	public void mouseReleased() {

	}

	@Override
	public boolean isGlowing() {
		return this.panel.isGlowing();
	}

	@Override
	public void setGlowing(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.panel.setGlowing(value);
		this.lock.unlock();
	}

	@Override
	public boolean isSelected() {
		return this.panel.isSelected();
	}

	@Override
	public void setSelected(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.panel.setSelected(value);
		this.lock.unlock();
	}

	@Override
	public boolean mouseOver(float xPos, float yPos) {
		float x = this.getWorldTranslation().x;
		float y = this.getWorldTranslation().y;

		if (xPos >= x && xPos <= (x + this.panel.getWidth()) && yPos >= y && yPos <= (y + this.panel.getHeight()))
			return true;
		return false;
	}

	@Override
	public int getLevel() {
		return (int) this.level;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void setVisible(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.visible = value;
		this.lock.unlock();
	}
	
	public Vector2f getSize() {
		return new Vector2f(this.panel.getWidth(), this.panel.getHeight());
	}
}
