package gui2d.abstracts;

import model.interfaces.Position;

/**
 * Classes that implement this interface have to provide a method, which triggers when this object is selected by the user.
 * 
 * @author Michael
 *
 */
public interface SelectableNode {

	/**
	 * True, if the mouse is currently over this panel, false otherwise.
	 * 
	 * @param xPos
	 * @param yPos
	 * @return
	 */
	public boolean mouseOver(float xPos, float yPos);

	/**
	 * Updates the visualization of this selectable node by visualizing the position that was set in {@link #setPosition(Position)} and updating the visualization of
	 * the status(selected/glowing).
	 */
	public void update();

	/**
	 * Is called when the mouse enters this element.
	 */
	public void mouseEnter();

	/**
	 * Is called when the mouse exits this element.
	 */
	public void mouseExit();

	/**
	 * This method is called when the mouse selects this element. It should check, if this element has to call a function on selection(e.g. only when its glowing) or
	 * has to update its selection status.
	 */
	public void mouseSelect();

	/**
	 * Is called when the mouse is pressed on this button.
	 */
	public void mousePressed();

	/**
	 * Is called when the mouse is released on this button.
	 */
	public void mouseReleased();

	/**
	 * Is executed when the node is selected. You should be playing audio effects here.
	 */
	public void audioSelect();

	/**
	 * Returns true if the element is glowing.
	 * 
	 * @return
	 */
	public boolean isGlowing();

	/**
	 * If the object can be selected by the user, then it should visualize this by glowing or some other signs.
	 * 
	 * @param value
	 */
	public void setGlowing(boolean value);

	/**
	 * Returns true, if this element is selected.
	 * 
	 * @return
	 */
	public boolean isSelected();

	/**
	 * Has to be called, if the user selects multiple elements. When this element is selected, it should visualize it in a different fashion than the glow effect.
	 * Furthermore it overwrites the glowing effect.
	 * 
	 * @param value
	 */
	public void setSelected(boolean value);

	/**
	 * Returns true if the element is visible.
	 * 
	 * @return
	 */
	public boolean isVisible();

	/**
	 * Sets visibility for this element.
	 * 
	 * @param value
	 */
	public void setVisible(boolean value);

	/**
	 * Returns the z positon value for this selectable node ("level").
	 * 
	 * @return
	 */
	public int getLevel();
}
