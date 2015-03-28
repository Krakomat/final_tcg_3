package gui2d.geometries;

import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;

import common.utilities.Lock;
import gui2d.GUI2D;
import gui2d.abstracts.Panel2D;
import gui2d.abstracts.SelectableNode;
import gui2d.controller.EffectController;

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

	private AudioNode clickSoundNode;

	public Image2D(String name, TextureKey texture, float width, float height) {
		this.level = 0;
		this.name = name;
		this.visible = true;
		this.cardId = null;
		this.lock = new Lock();

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
		this.clickSoundNode = new AudioNode(GUI2D.getInstance().getAssetManager(), EffectController.BUTTON_CLICKED, false);
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

			if (this.isGlowing() && !this.hasChild(glowingNode))
				this.attachChild(glowingNode);
			else if (!this.isGlowing())
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
		Material mark_mat = new Material(GUI2D.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mark_mat.setColor("Color", color);

		Line lineLeftUp = new Line(new Vector3f(xPos, yPos, zPos), new Vector3f(xPos, yPos + height, zPos));
		Line lineUpRight = new Line(new Vector3f(xPos, yPos + height, zPos), new Vector3f(xPos + width, yPos + height, zPos));
		Line lineRightDown = new Line(new Vector3f(xPos + width, yPos + height, zPos), new Vector3f(xPos + width, yPos, zPos));
		Line lineRightLeft = new Line(new Vector3f(xPos + width, yPos, zPos), new Vector3f(xPos, yPos, zPos));
		lineLeftUp.setLineWidth(5);
		lineUpRight.setLineWidth(5);
		lineRightDown.setLineWidth(5);
		lineRightLeft.setLineWidth(5);

		Geometry line1 = new Geometry(this.name, lineLeftUp);
		line1.setMaterial(mark_mat);
		line1.setLocalTranslation(0, 0, level + 0.00001f);
		selectedNode.attachChild(line1);

		Geometry line2 = new Geometry(this.name, lineUpRight);
		line2.setMaterial(mark_mat);
		line2.setLocalTranslation(0, 0, level + 0.00001f);
		selectedNode.attachChild(line2);

		Geometry line3 = new Geometry(this.name, lineRightDown);
		line3.setMaterial(mark_mat);
		line3.setLocalTranslation(0, 0, level + 0.00001f);
		selectedNode.attachChild(line3);

		Geometry line4 = new Geometry(this.name, lineRightLeft);
		line4.setMaterial(mark_mat);
		line4.setLocalTranslation(0, 0, level + 0.00001f);
		selectedNode.attachChild(line4);

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

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
}
