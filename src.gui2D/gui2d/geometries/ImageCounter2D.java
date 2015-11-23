package gui2d.geometries;

import java.util.List;

import gui2d.GUI2D;
import gui2d.abstracts.Panel2D;
import gui2d.abstracts.SelectableNode;
import gui2d.geometries.messages.TextPanel2D;

import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;

import common.utilities.Lock;

/**
 * Represents a selectable element that is represented by an image and a number. This is used for arena elements like the card deck or the discardpile.
 * 
 * @author Michael
 *
 */
public abstract class ImageCounter2D extends Node implements SelectableNode {
	/** Actual image element */
	private Panel2D imagePanel;
	/** Panel for the text that is put over the image */
	private TextPanel2D textPanel;
	/** Level for the starting zPosition */
	private int level;

	private boolean visible;
	private List<String> cardIds; // the ids of cards on this component

	private Node selectedNode;
	private Node glowingNode;
	private Lock lock;

	public ImageCounter2D(String name, TextureKey texture, float width, float height, int counter) {
		this.level = 0;
		this.name = name;
		this.visible = true;
		this.cardIds = null;
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

		this.textPanel = new TextPanel2D(name, String.valueOf(counter), width * 0.3f, height * 0.2f) {
			@Override
			public void mouseSelect() {
				// Do nothing here!
			}

			@Override
			public void mouseSelectRightClick() {
				// Do nothing here!
			}
		};
		this.textPanel.setLocalTranslation(0, 0, 0.00001f);
		this.attachChild(textPanel);

		selectedNode = this.createRect(ColorRGBA.Red);
		glowingNode = this.createRect(ColorRGBA.Green);
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

			this.textPanel.update();
		} else {
			this.detachChild(imagePanel);
			this.detachChild(selectedNode);
			this.detachChild(glowingNode);
			this.textPanel.update();
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
		return this.imagePanel.isGlowing();
	}

	@Override
	public void setGlowing(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.imagePanel.setGlowing(value);
		this.lock.unlock();
	}

	@Override
	public boolean isSelected() {
		return this.imagePanel.isSelected();
	}

	@Override
	public void setSelected(boolean value) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.imagePanel.setSelected(value);
		this.lock.unlock();
	}

	@Override
	public boolean mouseOver(float xPos, float yPos) {
		return this.imagePanel.mouseOver(xPos, yPos);
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
		this.textPanel.setVisible(value);
		this.lock.unlock();
	}

	private Node createRect(ColorRGBA color) {
		float width = imagePanel.getWidth();
		float height = imagePanel.getHeight();
		float xPos = this.getLocalTranslation().x;
		float yPos = this.getLocalTranslation().y;
		float zPos = this.getLocalTranslation().z + 0.00001f;
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

	public void setCounter(int counter) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.textPanel.setText(String.valueOf(counter));
		this.lock.unlock();
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

	public List<String> getCardIds() {
		return cardIds;
	}

	public void setCardIds(List<String> cardIds) {
		this.cardIds = cardIds;
	}

	public Vector2f getSize() {
		return new Vector2f(this.imagePanel.getWidth(), this.imagePanel.getHeight());
	}
}
