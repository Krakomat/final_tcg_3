package model.interfaces;

import java.util.List;

import model.enums.PositionID;

/**
 * This describes an update for the current game model, that can be send to the clients. It contains the WHOLE game model(meaning a list of {@link Position}s).
 * 
 * @author Michael
 *
 */
public interface GameModelUpdate {

	/**
	 * A pair consisting of a positionID as a key and a list of cards as a value is inserted into this GameModelUpdate.
	 * 
	 * @param position
	 */
	public void add(Position position);

	/**
	 * Returns the list of all positions of this update.
	 * 
	 * @return
	 */
	public List<Position> getPositionList();

	/**
	 * Returns the position with the given id. Returns null, if no such position exists in the position list of this update object.
	 * 
	 * @param id
	 * @return
	 */
	public Position getPosition(PositionID id);

	/**
	 * Setter for the position list. For serialization only.
	 * 
	 * @param posList
	 */
	public void setPositionList(List<Position> posList);

	/**
	 * Returns the turn number.
	 * 
	 * @return
	 */
	public short getTurnNumber();

	/**
	 * Sets the turn number.
	 * 
	 * @param turnNumber
	 */
	public void setTurnNumber(short turnNumber);

	/**
	 * Returns the energyPlayedAllowed parameter.
	 * 
	 * @return
	 */
	public boolean isEnergyPlayAllowed();

	/**
	 * Sets energyPlayedAllowed parameter.
	 * 
	 * @param energyPlayAllowed
	 */
	public void setEnergyPlayAllowed(boolean energyPlayAllowed);

	/**
	 * Returns the retreatAllowed parameter.
	 * 
	 * @return
	 */
	public boolean isRetreatAllowed();

	/**
	 * Sets the retreat allowed parameter
	 * 
	 * @param value
	 */
	public void setRetreatAllowed(boolean value);
}
