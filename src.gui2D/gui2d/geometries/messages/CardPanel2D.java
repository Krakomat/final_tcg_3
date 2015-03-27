package gui2d.geometries.messages;

import java.util.List;

import model.database.Card;
import model.database.Database;
import gui2d.abstracts.Panel2D;
import gui2d.abstracts.SelectableNode;

import com.jme3.scene.Node;

import common.utilities.Lock;

/**
 * A texted Button.
 * 
 * @author Michael
 *
 */
public abstract class CardPanel2D extends Node implements SelectableNode {

	/** Images */
	private Panel2D cardImage1, cardImage2;
	/** Actual panel element */
	private Panel2D panel;
	/** Level for the starting zPosition */
	private float level;
	private float width, height;
	private boolean visible;
	private Lock lock;

	private boolean oneCard;

	public CardPanel2D(String name, float width, float height) {
		this.name = name;
		this.visible = true;
		this.lock = new Lock();
		this.oneCard = false;
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

		cardImage1 = new Panel2D(name, Database.getAssetKey("panel_bg"), width * 0.4f, width * 0.4f * 1.141f) {
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
		cardImage1.setLocalTranslation(width * 0.05f, (height - width * 0.4f * 1.141f) / 2, 0.00001f);
		this.attachChild(cardImage1);

		cardImage2 = new Panel2D(name, Database.getAssetKey("panel_bg"), width * 0.4f, width * 0.4f * 1.141f) {
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
		cardImage2.setLocalTranslation(width * 0.55f, (height - width * 0.4f * 1.141f) / 2, 0.00001f);
		this.attachChild(cardImage2);
	}

	/**
	 * Sets the text for this button.
	 * 
	 * @param text
	 */
	public void setCard(List<Card> cards) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.cardImage1.setTexture(Database.getTextureKey(cards.get(0).getCardId()));
		if (cards.size() > 1) {
			this.cardImage2.setTexture(Database.getTextureKey(cards.get(1).getCardId()));
			this.oneCard = false;
		} else
			this.oneCard = true;

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

			if (this.oneCard)
				cardImage1.setLocalTranslation(width * 0.5f - cardImage1.getWidth() / 2, (height - width * 0.4f * 1.141f) / 2, 0.00001f);
			else
				cardImage1.setLocalTranslation(width * 0.05f, (height - width * 0.4f * 1.141f) / 2, 0.00001f);

			if (!this.hasChild(cardImage1))
				this.attachChild(cardImage1);

			if (!this.oneCard && !this.hasChild(cardImage2))
				this.attachChild(cardImage2);
			else if (this.oneCard)
				this.detachChild(cardImage2);
		} else {
			if (this.hasChild(panel))
				this.detachChild(panel);

			if (this.hasChild(cardImage1))
				this.detachChild(cardImage1);

			if (this.hasChild(cardImage2))
				this.detachChild(cardImage2);
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
}
