package gui2d.geometries;

import model.enums.Sounds;
import src.gui2D.particleSystem.GlowingBorder;

import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

import common.utilities.Degree;
import common.utilities.Lock;
import gui2d.GUI2D;
import gui2d.abstracts.Panel2D;
import gui2d.abstracts.SelectableNode;

/**
 * Selectable node that represents an image.
 * 
 * @author Michael
 *
 */
public abstract class Image2D extends Node implements SelectableNode {
	/** Actual image element */
	private Panel2D imagePanel;
	/** Level for the starting zPosition */
	private float level;
	private boolean visible;
	private String cardId;
	private Node selectedNode;
	private Node glowingNode;
	private Lock lock;
	private BlendMode blendMode;
	private AudioNode clickSoundNode;

	public Image2D(String name, TextureKey texture, float width, float height, BlendMode blendMode) {
		this.level = 0;
		this.name = name;
		this.visible = true;
		this.cardId = null;
		this.lock = new Lock();
		this.blendMode = blendMode;
		
		this.imagePanel = new Panel2D(name, texture, width, height) {
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
		this.attachChild(imagePanel);
		selectedNode = this.createRect(ColorRGBA.Red);
		this.attachChild(selectedNode);
		glowingNode = this.createRect(ColorRGBA.Green);
		this.attachChild(glowingNode);

		// Init audio:
		this.clickSoundNode = new AudioNode(GUI2D.getInstance().getAssetManager(), Sounds.BUTTON_CLICKED, false);
		this.clickSoundNode.setPositional(false);
		this.clickSoundNode.setLooping(false);
		this.clickSoundNode.setVolume(2);
		this.attachChild(this.clickSoundNode);
	}

	@Override
	public void update() {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (this.isVisible()) {
			if (!this.hasChild(imagePanel))
				this.attachChild(imagePanel);

			if (this.isGlowing() && !this.hasChild(glowingNode) && !this.isSelected())
				this.attachChild(glowingNode);
			else if (!this.isGlowing() || this.isSelected())
				this.detachChild(glowingNode);

			if (this.isSelected() && !this.hasChild(selectedNode))
				this.attachChild(selectedNode);
			else if (!this.isSelected())
				this.detachChild(selectedNode);
		} else {
			this.detachChild(imagePanel);
			this.detachChild(selectedNode);
			this.detachChild(glowingNode);
		}
		this.lock.unlock();
	}

	private Node createRect(ColorRGBA color) {
		float width = imagePanel.getWidth();
		float height = imagePanel.getHeight();
		float xPos = imagePanel.getxPos();
		float yPos = imagePanel.getyPos();
		float zPos = level + 0.00001f;
		Node selectedNode = new Node();

		GlowingBorder lineUpRight = new GlowingBorder((xPos + width) / 2, yPos + height, zPos, (xPos + width) / 4, 2, color, blendMode);
		selectedNode.attachChild(lineUpRight);

		GlowingBorder lineRightLeft = new GlowingBorder((xPos + width) / 2, yPos, zPos, (xPos + width) / 4, 2, color, blendMode);
		lineRightLeft.rotate(0, 0, Degree.degreeToRadiant(180));
		selectedNode.attachChild(lineRightLeft);

		GlowingBorder lineLeftUp = new GlowingBorder(xPos, (yPos + height) / 2, zPos, (yPos + height) / 4, 3, color, blendMode);
		lineLeftUp.rotate(0, 0, Degree.degreeToRadiant(90));
		selectedNode.attachChild(lineLeftUp);

		GlowingBorder lineRightDown = new GlowingBorder(xPos + width, (yPos + height) / 2, zPos, (yPos + height) / 4, 3, color, blendMode);
		lineRightDown.rotate(0, 0, Degree.degreeToRadiant(-90));
		selectedNode.attachChild(lineRightDown);

		return selectedNode;
	}

	@Override
	public void mouseEnter() {
		// Do nothing here!
	}

	@Override
	public void mouseExit() {
		// Do nothing here!
	}

	public abstract void mouseSelect();

	public abstract void mouseSelectRightClick();

	public void audioSelect() {
		this.clickSoundNode.playInstance();
	}

	public void mousePressed() {

	}

	public void mouseReleased() {

	}

	@Override
	public boolean isGlowing() {
		return this.imagePanel.isGlowing();
	}

	@Override
	public synchronized void setGlowing(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.imagePanel.setGlowing(value);
		lock.unlock();
	}

	@Override
	public boolean isSelected() {
		return this.imagePanel.isSelected();
	}

	@Override
	public synchronized void setSelected(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.imagePanel.setSelected(value);
		lock.unlock();
	}

	@Override
	public boolean mouseOver(float xPos, float yPos) {
		float x = this.getWorldTranslation().x;
		float y = this.getWorldTranslation().y;

		if (xPos >= x && xPos <= (x + this.imagePanel.getWidth()) && yPos >= y && yPos <= (y + this.imagePanel.getHeight()))
			return true;
		return false;
	}

	@Override
	public int getLevel() {
		return (int) this.level;
	}

	public synchronized void setTexture(TextureKey texture) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.imagePanel.setTexture(texture);
		lock.unlock();
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public synchronized void setVisible(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.visible = value;
		lock.unlock();
	}

	public Vector2f getSize() {
		return new Vector2f(this.imagePanel.getWidth(), this.imagePanel.getHeight());
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
}
