package gui2d.geometries;

import com.jme3.asset.TextureKey;
import com.jme3.material.RenderState.BlendMode;

import gui2d.abstracts.SelectableNode;

/**
 * Represents a single hand card.
 * 
 * @author Michael
 *
 */
public abstract class HandCard2D extends Image2D implements SelectableNode {
	private int index;
	private int currentScrollIndex;

	public HandCard2D(String name, TextureKey texture, float width, float height, int index) {
		super(name, texture, width, height, BlendMode.Alpha);
		this.setIndex(index);
		this.currentScrollIndex = 0;
	}

	public abstract void mouseSelect();

	public abstract void mouseSelectRightClick();

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getCurrentScrollIndex() {
		return currentScrollIndex;
	}

	public void setCurrentScrollIndex(int currentScrollIndex) {
		this.currentScrollIndex = currentScrollIndex;
	}
}
