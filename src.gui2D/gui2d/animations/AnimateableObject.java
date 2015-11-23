package gui2d.animations;

public interface AnimateableObject {

	/**
	 * Sets up the animateable object to get animated.
	 */
	public void startAnimation();

	/**
	 * Resets the animateable object.
	 */
	public void resetAnimation();

	/**
	 * True, if this object has finished its animation.
	 * 
	 * @return
	 */
	public boolean animationDone();

	/**
	 * Executes one animation step for this object. Only use this in the main simpleUpdate method from the gui.
	 * 
	 * @param tpf
	 *            time per frame in miliseconds
	 */
	public void simpleUpdate(float tpf);
}
