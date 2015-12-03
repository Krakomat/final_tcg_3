package gui2d.geometries;

import gui2d.GUI2D;
import gui2d.abstracts.SelectableNode;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

import common.utilities.Lock;

/**
 * Just a BitmapText as a selectable node.
 * 
 * @author Michael
 *
 */
public abstract class Text2D extends Node implements SelectableNode {

	private String text;
	/** The actual button text */
	private BitmapText textBitmap;
	/** Level for the starting zPosition */
	private float level;
	private float width, height;
	private boolean visible;
	private Lock lock;

	public Text2D(String name, String text, float width, float height) {
		this.name = name;
		this.text = text;
		this.visible = true;
		this.lock = new Lock();
		this.width = width;
		this.height = height;

		// Button text:
		BitmapFont myFont = GUI2D.getInstance().getAssetManager().loadFont("assets/Fonts/DejaVuSans.fnt");
		this.textBitmap = new BitmapText(myFont, false);
		this.textBitmap.setSize(myFont.getCharSet().getRenderedSize()); // font
																		// size
		this.textBitmap.setText(this.text); // the text
		this.textBitmap.setColor(ColorRGBA.White); // font color

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
			textBitmap.setText(text);

			this.textBitmap.setLocalTranslation(width * 0.5f - this.textBitmap.getLineWidth() / 2, height * 0.50f + this.textBitmap.getLineHeight() / 2, 0.00001f);

			if (!this.hasChild(textBitmap))
				this.attachChild(textBitmap);
		} else {
			if (this.hasChild(textBitmap))
				this.detachChild(textBitmap);
		}
		this.lock.unlock();
	}

	@Override
	public void mouseEnter() {

	}

	@Override
	public void mouseExit() {

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
		return false;
	}

	@Override
	public void setGlowing(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.lock.unlock();
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.lock.unlock();
	}

	@Override
	public boolean mouseOver(float xPos, float yPos) {
		float x = this.getWorldTranslation().x;
		float y = this.getWorldTranslation().y;

		if (xPos >= x && xPos <= (x + width) && yPos >= y && yPos <= (y + height))
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
		return new Vector2f(width, height);
	}
}
