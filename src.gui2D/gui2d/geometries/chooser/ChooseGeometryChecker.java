package gui2d.geometries.chooser;

/**
 * Has to be set to an instance of a ChooseWindow.
 * 
 * @author Michael
 *
 */
public interface ChooseGeometryChecker {
	/**
	 * Returns true if the current selection of the user is valid or not.
	 * 
	 * @return
	 */
	public abstract boolean checkSelectionIsOk();
}
