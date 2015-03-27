package gui2d.geometries;

import gui2d.GUI2D;
import gui2d.abstracts.Button2D;
import gui2d.abstracts.Panel2D;
import gui2d.abstracts.SelectableNode;

import com.jme3.asset.TextureKey;
import com.jme3.scene.Node;

import common.utilities.Lock;

/**
 * A button with an image onto it.
 * 
 * @author Michael
 *
 */
public abstract class ImageButton2D extends Node implements SelectableNode {
	/** Actual button element */
	private Button2D button;
	/** Image that is put onto the button */
	private Panel2D imagePanel;
	/** Level for the starting zPosition */
	private int level;
	private boolean visible;
	private Lock lock;

	public ImageButton2D(String name, TextureKey texture, float width, float height) {
		this.name = name;
		this.visible = true;
		this.lock = new Lock();

		button = new Button2D(name, width, height) {
			@Override
			public void mouseSelect() {
				// Do nothing here!!!
			}

		};
		this.attachChild(button);

		this.imagePanel = new Panel2D(name, texture, width * 0.8f, height * 0.8f) {
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
			public void mousePressed() {
				// Do nothing here!
			}

			@Override
			public void mouseReleased() {
				// Do nothing here!
			}
		};
		this.imagePanel.setLocalTranslation(width * 0.1f, height * 0.1f, 0);
		this.attachChild(imagePanel);
	}

	public void setTexture(TextureKey texture) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.imagePanel.setTexture(texture);
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
			if (!this.hasChild(imagePanel))
				this.attachChild(imagePanel);

			GUI2D.getInstance().getIOController().addShootable(this);
		} else {
			if (this.hasChild(button))
				this.detachChild(button);
			if (this.hasChild(imagePanel))
				this.detachChild(imagePanel);

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
		return this.button.mouseOver(xPos, yPos);
	}

	@Override
	public int getLevel() {
		return this.level;
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
}
