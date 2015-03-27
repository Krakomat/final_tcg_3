package gui2d.abstracts;

import gui2d.GUI2D;

import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

/**
 * Defines a simple panel that can be attached to the guiNode.
 * 
 * @author Michael
 *
 */
public abstract class Panel2D extends Geometry {

	protected float width, height, xPos, yPos, zPos;
	/** True, if the panel is visible. */
	protected boolean visible;
	/** True if the panel is selected. A panel can only be either selected or glowing. */
	protected boolean selected;
	/** True if the panel is glowing. A panel can only be either selected or glowing. */
	protected boolean glowing;
	private Material material;

	/**
	 * Creates a new Panel with the given background texture.
	 * 
	 * @param name
	 * @param backgroundTexture
	 * @param width
	 * @param height
	 */
	public Panel2D(String name, TextureKey backgroundTexture, float width, float height) {
		super(name, new Quad(width, height));
		this.width = width;
		this.height = height;
		this.selected = false;
		this.glowing = false;
		this.visible = true;

		// Material:
		material = new Material(GUI2D.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		material.setTexture("ColorMap", GUI2D.getInstance().getAssetManager().loadTexture(backgroundTexture));
		material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

		this.setCullHint(CullHint.Never);
		this.setLocalTranslation(0, 0, 0); // default
		this.setMaterial(material);
	}

	public void setLocalTranslation(float xPos, float yPos, float zPos) {
		super.setLocalTranslation(xPos, yPos, zPos);
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
	}

	/**
	 * Is called whenever the mouse enters this panel. That is when the 2D-check for this panel is positive for the first time in a while.
	 */
	public abstract void mouseEnter();

	/**
	 * Is called whenever the mouse exits this panel. That is when the 2D-check for this panel is negative for the first time in a while.
	 */
	public abstract void mouseExit();

	/**
	 * Is called when the mouse clicks on this panel.
	 */
	public abstract void mouseSelect();

	/**
	 * When the mouse is pressed on this panel.
	 */
	public abstract void mousePressed();

	/**
	 * When the mouse is released on this panel.
	 */
	public abstract void mouseReleased();

	/**
	 * Getter for selected.
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * Getter for glowing.
	 * 
	 * @return
	 */
	public boolean isGlowing() {
		return this.glowing;
	}

	/**
	 * Sets the selected value.
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Sets the glowing value.
	 * 
	 * @param glowing
	 */
	public void setGlowing(boolean glowing) {
		this.glowing = glowing;
	}

	/**
	 * Sets the texture for this panel.
	 * 
	 * @param texture
	 */
	public void setTexture(TextureKey texture) {
		material.setTexture("ColorMap", GUI2D.getInstance().getAssetManager().loadTexture(texture));
	}

	/**
	 * True, if the mouse is currently over this panel, false otherwise.
	 * 
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	public boolean mouseOver(float xPos, float yPos) {
		float x = this.getWorldTranslation().x;
		float y = this.getWorldTranslation().y;

		if (xPos >= x && xPos <= (x + this.getWidth()) && yPos >= y && yPos <= (y + this.getHeight()))
			return true;
		return false;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getxPos() {
		return xPos;
	}

	public void setxPos(float xPos) {
		this.xPos = xPos;
	}

	public float getyPos() {
		return yPos;
	}

	public void setyPos(float yPos) {
		this.yPos = yPos;
	}

	public float getzPos() {
		return zPos;
	}

	public void setzPos(float zPos) {
		this.zPos = zPos;
	}
}
