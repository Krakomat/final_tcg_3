package gui2d.geometries;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

import common.utilities.Lock;
import gui2d.GUI2D;
import gui2d.abstracts.SelectableNode;
import gui2d.abstracts.Window2D;

public class WindowGeometry extends Node implements SelectableNode {

	protected Window2D window;
	private BitmapText title;
	private String titleText;
	protected boolean visible;
	protected int level;
	protected float width, height;
	protected float xPos, yPos;
	private Lock lock;

	public WindowGeometry(String name, String text, float width, float height) {
		this.name = name;
		titleText = text;
		visible = true;
		this.level = 0;
		this.width = width;
		this.height = height;
		this.xPos = 0;
		this.yPos = 0;
		this.lock = new Lock();

		window = new Window2D(name, width, height) {
			@Override
			public void mouseSelect() {
				// Do nothing here!
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
		this.attachChild(window);

		// Window text:
		this.title = new BitmapText(GUI2D.getInstance().getGuiFont(), false);
		this.title.setSize(GUI2D.getInstance().getGuiFont().getCharSet().getRenderedSize()); // font size
		this.title.setText(this.titleText); // the text
		this.title.setColor(ColorRGBA.Black); // font color

		// Adjust size:
		while (this.title.getLineHeight() > (height * 0.047) || this.title.getLineWidth() > width * 0.90f)
			this.title.setSize(this.title.getSize() - 0.001f);

		while (this.title.getLineHeight() < (height * 0.047) && this.title.getLineWidth() < width * 0.90f)
			this.title.setSize(this.title.getSize() + 0.001f);

		this.title.setLocalTranslation(width * 0.02f, height * 0.97f + this.title.getLineHeight() / 2, 0.00001f);
		this.attachChild(this.title);
	}

	@Override
	public boolean mouseOver(float xPos, float yPos) {
		return window.mouseOver(xPos, yPos);
	}

	@Override
	public void update() {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (this.isVisible()) {
			if (!this.hasChild(window))
				this.attachChild(window);
			this.title.setText(this.titleText);
		} else {
			if (this.hasChild(window))
				this.detachChild(window);
			this.title.setText("");
		}
		this.lock.unlock();
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
		this.titleText = text;
		this.lock.unlock();
	}

	@Override
	public void mouseEnter() {
		// Do nothing here!
	}

	@Override
	public void mouseExit() {
		// Do nothing here!
	}

	@Override
	public void mouseSelect() {
		// Do nothing here!
	}

	@Override
	public void mouseSelectRightClick() {
		// Do nothing here!
	}

	public void audioSelect() {

	}

	public void mousePressed() {
		// Do nothing here!
	}

	public void mouseReleased() {
		// Do nothing here!
	}

	@Override
	public boolean isGlowing() {
		return false;
	}

	@Override
	public void setGlowing(boolean value) {
		// Do nothing here!
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean value) {
		// Do nothing here!
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

	@Override
	public int getLevel() {
		return level;
	}
}
