package network.client;

import java.util.List;

import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;

/**
 * This interface provides methods, which are called by the GUI on the player.
 * 
 * @author Michael
 *
 */
public interface GuiToPlayerCommunication {

	/**
	 * Returns the color of the player.
	 * 
	 * @return
	 */
	public Color getColor();

	/**
	 * The user decided to play the given card from his hand. This is being send
	 * to the server, which executes this move.
	 * 
	 * @param index
	 *            index of the hand card
	 */
	public void playHandCard(int index);

	/**
	 * Returns a list of {@PlayerAction}s that can be used for the given hand
	 * card.
	 * 
	 * @param index
	 * @return
	 */
	public List<PlayerAction> getPlayerActionsForHandCard(int index);

	/**
	 * The client sends a retreat command to the server.
	 * 
	 * @param benchPosition
	 */
	public void retreatPokemon();

	/**
	 * Gets a list of attack names from the server.
	 * 
	 * @param posID
	 * @return
	 */
	public List<String> getAttackNames(PositionID posID);

	/**
	 * The user decided to attack with his active pokemon and the attack
	 * represented by the given index.
	 * 
	 * @param i
	 */
	public void attack(int i);

	/**
	 * Gets a list of pokemon power names from the server.
	 * 
	 * @param posID
	 * @return
	 */
	public List<String> getPokePowerNames(PositionID posID);

	/**
	 * Executes the pokemon power of the pokemon on top of the given position.
	 * 
	 * @param positionIDForArenaGeometry
	 */
	public void pokemonPower(PositionID positionIDForArenaGeometry);

	/**
	 * Returns a list of {@PlayerAction}s that can be used for the given arena
	 * position.
	 * 
	 * @param positionID
	 * @return
	 */
	public List<PlayerAction> getPlayerActionsForArenaPosition(PositionID positionID);

	/**
	 * The player is told by the gui to send an "endTurnMessage" to the server.
	 */
	public void sendEndTurnToServer();

	/**
	 * The player surrenders.
	 * 
	 */
	public void sendSurrenderToServer();

	/**
	 * A new server is created using the localhost adress. Also this player
	 * connects to the created server.
	 */
	public void createGame();

	/**
	 * A new server is created using the localhost adress. Also this player
	 * connects to the created server. Only for local games.
	 */
	public void createLocalGame();

	/**
	 * The player connects to the game with localhost adresses.
	 * 
	 * @param ipAdress
	 */
	public void connectToGame(String ipAdress);

	/**
	 * Returns the name of the player for the gui to display.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Returns this instance as an account object. Used for the deck editor
	 * controller.
	 * 
	 * @return
	 */
	public Account asAccount();
}
