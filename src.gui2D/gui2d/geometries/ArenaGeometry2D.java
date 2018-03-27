package gui2d.geometries;

import java.util.ArrayList;
import java.util.List;

import model.database.Database;
import model.database.DynamicPokemonCondition;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.Sounds;
import src.gui2D.particleSystem.GlowingBorder;

import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioData;
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

public abstract class ArenaGeometry2D extends Node implements SelectableNode {

	static final String DAMAGE_BOX = "hp_empty";
	static final String NO_DAMAGE_BOX = "hp_filled";

	/** Actual image element */
	private Panel2D imagePanel;
	/** Level for the starting zPosition */
	private int level;
	private boolean mirrorInverted;

	private boolean visible;
	private Node selectedNode;
	private Node glowingNode;

	private List<Image2D> hpBoxes;
	private List<Image2D> energyBoxes;
	private List<Image2D> conditionBoxes;

	private List<DynamicPokemonCondition> conditionList;
	private List<Element> energyList;
	private int damageMarks, hitPoints;
	private Lock lock;
	private String topCardID;

	private AudioNode clickSoundNode;

	public ArenaGeometry2D(String name, TextureKey texture, float width, float height, boolean mirrorInverted) {
		this.level = 0;
		this.name = name;
		this.visible = true;
		this.mirrorInverted = mirrorInverted;
		this.hpBoxes = new ArrayList<>();
		this.energyBoxes = new ArrayList<>();
		this.conditionBoxes = new ArrayList<>();
		this.damageMarks = 0;
		this.hitPoints = 0;
		this.conditionList = new ArrayList<>();
		this.energyList = new ArrayList<>();
		this.lock = new Lock();
		this.topCardID = "";

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
		glowingNode = this.createRect(ColorRGBA.Green);
		initObjects();
	}

	private void initObjects() {
		float height = imagePanel.getHeight();
		float width = imagePanel.getWidth();
		float zPos = imagePanel.getzPos();
		float damageBoxWidth = width / 6;
		float damageBoxHeight = height / 6;

		// Set the damage Marks:
		for (int i = 0; i < 12; i++) {
			float boxXPos = mirrorInverted ? width + ((i % 6) * damageBoxWidth) : -damageBoxWidth - ((i % 6) * damageBoxWidth);
			float boxYPos = damageBoxHeight - (damageBoxHeight * (i / 6));

			Image2D damageBox = new Image2D("DamageBox", Database.getAssetKey(DAMAGE_BOX), damageBoxWidth, damageBoxHeight, BlendMode.Alpha) {
				@Override
				public void mouseSelect() {
					// Do nothing here
				}

				@Override
				public void mouseSelectRightClick() {
					// nothing to do here
				}
			};
			damageBox.setLocalTranslation(boxXPos, boxYPos, zPos);
			this.hpBoxes.add(damageBox);
			this.attachChild(damageBox);
		}

		// Set the energy on this position:
		for (int i = 0; i < 12; i++) {
			float boxXPos = mirrorInverted ? width + ((i % 6) * damageBoxWidth) : -damageBoxWidth - ((i % 6) * damageBoxWidth);
			float boxYPos = (height - damageBoxHeight) - (damageBoxHeight * (i / 6));

			Element energy = Element.COLORLESS; // Default energy
			Image2D damageBox = new Image2D("DamageBox", Database.getAssetKey(energy.toString()), damageBoxWidth, damageBoxHeight, BlendMode.Alpha) {
				@Override
				public void mouseSelect() {
					// Do nothing here
				}

				@Override
				public void mouseSelectRightClick() {
					// nothing to do here
				}
			};
			damageBox.setLocalTranslation(boxXPos, boxYPos, zPos);
			this.energyBoxes.add(damageBox);
			this.attachChild(damageBox);
		}

		// Set the energy on this position:
		for (int i = 0; i < 12; i++) {
			float boxXPos = mirrorInverted ? width + ((i % 6) * damageBoxWidth) : -damageBoxWidth - ((i % 6) * damageBoxWidth);
			float boxYPos = (height - damageBoxHeight * 4) + (damageBoxHeight * (i / 6));

			PokemonCondition condition = PokemonCondition.ASLEEP; // default condition
			Image2D damageBox = new Image2D("DamageBox", Database.getAssetKey(condition.toString()), damageBoxWidth, damageBoxHeight, BlendMode.Alpha) {
				@Override
				public void mouseSelect() {
					// Do nothing here
				}

				@Override
				public void mouseSelectRightClick() {
					// nothing to do here
				}
			};
			damageBox.setLocalTranslation(boxXPos, boxYPos, zPos);
			this.conditionBoxes.add(damageBox);
			this.attachChild(damageBox);
		}

		// Init audio:
		this.clickSoundNode = new AudioNode(GUI2D.getInstance().getAssetManager(), Sounds.BUTTON_CLICKED, AudioData.DataType.Buffer);
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

			if (this.isSelected() && !this.hasChild(selectedNode)) {
				this.attachChild(selectedNode);
			} else if (!this.isSelected())
				this.detachChild(selectedNode);
		} else {
			this.detachChild(imagePanel);
			this.detachChild(glowingNode);
			this.detachChild(selectedNode);
		}
		for (int i = 0; i < 12; i++) {
			this.hpBoxes.get(i).update();
			this.conditionBoxes.get(i).update();
			this.energyBoxes.get(i).update();
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
		if (this.visible) {
			for (int i = 0; i < 12; i++) {
				if ((this.hitPoints / 10) > i)
					this.hpBoxes.get(i).setVisible(true);
				else
					this.hpBoxes.get(i).setVisible(false);

				if (this.conditionList.size() > i)
					this.conditionBoxes.get(i).setVisible(true);
				else
					this.conditionBoxes.get(i).setVisible(false);

				if (this.energyList.size() > i)
					this.energyBoxes.get(i).setVisible(true);
				else
					this.energyBoxes.get(i).setVisible(false);
			}
		} else {
			for (int i = 0; i < 12; i++) {
				this.hpBoxes.get(i).setVisible(false);
				this.conditionBoxes.get(i).setVisible(false);
				this.energyBoxes.get(i).setVisible(false);
			}
		}
		this.lock.unlock();
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	private Node createRect(ColorRGBA color) {
		float width = imagePanel.getWidth();
		float height = imagePanel.getHeight();
		float xPos = imagePanel.getxPos();
		float yPos = imagePanel.getyPos();
		float zPos = level + 0.00001f;
		Node selectedNode = new Node();
		GlowingBorder lineUpRight = new GlowingBorder((xPos + width) / 2, yPos + height, zPos, (xPos + width) / 2, 2, color, BlendMode.Alpha);
		selectedNode.attachChild(lineUpRight);

		GlowingBorder lineRightLeft = new GlowingBorder((xPos + width) / 2, yPos, zPos, (xPos + width) / 2, 2, color, BlendMode.Alpha);
		lineRightLeft.rotate(0, 0, Degree.degreeToRadiant(180));
		selectedNode.attachChild(lineRightLeft);

		GlowingBorder lineLeftUp = new GlowingBorder(xPos, (yPos + height) / 2, zPos, (yPos + height) / 2, 3, color, BlendMode.Alpha);
		lineLeftUp.rotate(0, 0, Degree.degreeToRadiant(90));
		selectedNode.attachChild(lineLeftUp);

		GlowingBorder lineRightDown = new GlowingBorder(xPos + width, (yPos + height) / 2, zPos, (yPos + height) / 2, 3, color, BlendMode.Alpha);
		lineRightDown.rotate(0, 0, Degree.degreeToRadiant(-90));
		selectedNode.attachChild(lineRightDown);

		return selectedNode;
	}

	public void setTexture(TextureKey tex) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		imagePanel.setTexture(tex);
		this.lock.unlock();
	}

	public void setConditionList(List<DynamicPokemonCondition> conditionList) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.conditionList = conditionList;
		for (int i = 0; i < 12; i++) {
			Image2D conditionBox = this.conditionBoxes.get(i);
			if (i < this.conditionList.size()) {
				PokemonCondition condition = this.conditionList.get(i).getCondition();
				conditionBox.setTexture(Database.getAssetKey(condition.toString()));
				conditionBox.setVisible(true);
			} else
				conditionBox.setVisible(false);
		}
		this.lock.unlock();
	}

	public void setEnergyList(List<Element> energyList) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.energyList = energyList;
		for (int i = 0; i < 12; i++) {
			Image2D energyBox = this.energyBoxes.get(i);
			if (i < this.energyList.size()) {
				Element element = this.energyList.get(i);
				energyBox.setTexture(Database.getAssetKey(element.toString()));
				energyBox.setVisible(true);
			} else
				energyBox.setVisible(false);
		}
		this.lock.unlock();
	}

	public List<Element> getEnergyList() {
		return energyList;
	}

	public void setDamageMarks(int damageMarks) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.damageMarks = damageMarks;
		this.lock.unlock();
	}

	public void setHitPoints(int hitPoints) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.hitPoints = hitPoints;
		for (int i = 0; i < 12; i++) {
			Image2D damageBox = this.hpBoxes.get(i);
			if (i < (this.hitPoints / 10)) {
				TextureKey tex = (damageMarks / 10) > i ? Database.getAssetKey(DAMAGE_BOX) : Database.getAssetKey(NO_DAMAGE_BOX);
				damageBox.setTexture(tex);
				damageBox.setVisible(true);
			} else
				damageBox.setVisible(false);
		}
		this.lock.unlock();
	}

	public String getTopCardID() {
		return this.topCardID;
	}

	public void setTopCardID(String id) {
		this.topCardID = id;
	}

	public Vector2f getSize() {
		return new Vector2f(this.imagePanel.getWidth(), this.imagePanel.getHeight());
	}
}
