package gui2d.geometries;

import model.enums.Sounds;
import gui2d.GUI2D;
import gui2d.abstracts.Button2D;
import gui2d.abstracts.SelectableNode;
import gui2d.controller.EffectController;

import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapFont;
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
public abstract class TextButton2D extends Node implements SelectableNode {

	private String text;
	/** The actual button text */
	private BitmapText textBitmap;
	/** Actual button element */
	private Button2D button;
	/** Level for the starting zPosition */
	private float level;
	private boolean visible;
	private Lock lock;
	private float width, height;

	private AudioNode clickSoundNode;

	public TextButton2D(String name, String text, float width, float height) {
		this.name = name;
		this.text = text;
		this.visible = true;
		this.lock = new Lock();
		this.width = width;
		this.height = height;

		button = new Button2D(name, width, height) {
			@Override
			public void mouseSelect() {
				// Do nothing here!!!
			}

		};
		this.attachChild(button);

		// Button text:
		BitmapFont myFont = GUI2D.getInstance().getAssetManager().loadFont("assets/Fonts/DejaVuSans.fnt");
		this.textBitmap = new BitmapText(myFont, false);
		this.textBitmap.setSize(myFont.getCharSet().getRenderedSize()); // font size
		this.textBitmap.setText(this.text); // the text
		this.textBitmap.setColor(ColorRGBA.Black); // font color

		this.adjustTextSize();

		this.textBitmap.setLocalTranslation(width * 0.50f - this.textBitmap.getLineWidth() / 2, height * 0.50f + this.textBitmap.getLineHeight() / 2,
				this.level + 0.00001f);
		this.attachChild(this.textBitmap);

		// Init audio:
		this.clickSoundNode = EffectController.createEffectAudioNode(Sounds.BUTTON_CLICKED);
		this.attachChild(this.clickSoundNode);
	}

	private void adjustTextSize() {
		// Adjust size:
		while (this.textBitmap.getLineHeight() > height * 0.5f && this.textBitmap.getLineWidth() > width * 0.3f)
			this.textBitmap.setSize(this.textBitmap.getSize() - 0.001f);

		while (this.textBitmap.getLineHeight() < height * 0.5f && this.textBitmap.getLineWidth() < width * 0.3f)
			this.textBitmap.setSize(this.textBitmap.getSize() + 0.001f);
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
			if (!this.hasChild(button))
				this.attachChild(button);

			textBitmap.setText(text);

			this.adjustTextSize();

			this.textBitmap.setLocalTranslation(width * 0.50f - this.textBitmap.getLineWidth() / 2, height * 0.50f + this.textBitmap.getLineHeight() / 2,
					this.level + 0.00001f);

			if (!this.hasChild(textBitmap))
				this.attachChild(textBitmap);

			GUI2D.getInstance().getIOController().addShootable(this);
		} else {
			if (this.hasChild(button))
				this.detachChild(button);

			if (this.hasChild(textBitmap))
				this.detachChild(textBitmap);

			GUI2D.getInstance().getIOController().removeShootable(this);
		}
		this.lock.unlock();
	}

	@Override
	public void mouseEnter() {
		this.button.mouseEnter();
	}

	@Override
	public void mouseExit() {
		this.button.mouseExit();
	}

	public abstract void mouseSelect();

	public abstract void mouseSelectRightClick();

	public void audioSelect() {
		this.clickSoundNode.playInstance();
	}

	public void mousePressed() {
		this.button.mousePressed();
	}

	public void mouseReleased() {
		this.button.mouseReleased();
	}

	@Override
	public boolean isGlowing() {
		return this.button.isGlowing();
	}

	@Override
	public void setGlowing(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.button.setGlowing(value);
		this.lock.unlock();
	}

	@Override
	public boolean isSelected() {
		return this.button.isSelected();
	}

	@Override
	public void setSelected(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.button.setSelected(value);
		this.lock.unlock();
	}

	@Override
	public boolean mouseOver(float xPos, float yPos) {
		float x = this.getWorldTranslation().x;
		float y = this.getWorldTranslation().y;

		if (xPos >= x && xPos <= (x + this.button.getWidth()) && yPos >= y && yPos <= (y + this.button.getHeight()))
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
		return new Vector2f(this.width, this.height);
	}
}
