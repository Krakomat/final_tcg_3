package gui2d.geometries;

import com.jme3.asset.TextureKey;

import gui2d.abstracts.SelectableNode;

/**
 * Represents a single hand card.
 * 
 * @author Michael
 *
 */
public abstract class HandCard2D extends Image2D implements SelectableNode {
	private int index;

	public HandCard2D(String name, TextureKey texture, float width, float height, int index) {
		super(name, texture, width, height);
		this.setIndex(index);
	}

	public abstract void mouseSelect();

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
