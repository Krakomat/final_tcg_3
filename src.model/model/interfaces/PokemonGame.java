package model.interfaces;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.GameState;
import model.enums.PositionID;
import model.game.AttackAction;
import model.game.AttackCondition;

/**
 * Interface for the overall game model.
 * 
 * @author Michael
 *
 */
public interface PokemonGame {

	/**
	 * Starts a new game by setting all positions to their default status. Furthermore instances of cards of both players decks are created, assigned with game ids
	 * and set on their respective deck positions. The game is started then.
	 */
	void initNewGame();

	/**
	 * Creates an instance of a {@link GameModelUpdate}, which represents everything the given player is able to see on the board/hands.
	 * 
	 * @param player
	 * @return
	 */
	GameModelUpdate getGameModelForPlayer(Player player);

	/**
	 * Searches for the position with the given position id.
	 * 
	 * @param posID
	 *            position id
	 * @return position
	 */
	Position getPosition(PositionID posID);

	/**
	 * Returns the player, who is currently on turn. Returns null if no player is on turn.
	 * 
	 * @return player on turn
	 */
	Player getPlayerOnTurn();

	/**
	 * Returns the player, whose color is red.
	 * 
	 * @return
	 */
	Player getPlayerRed();

	/**
	 * Sets the red player.
	 * 
	 * @param playerRed
	 */
	void setPlayerRed(Player playerRed);

	/**
	 * Returns the player, whose color is blue.
	 * 
	 * @return
	 */
	Player getPlayerBlue();

	/**
	 * Sets the blue player.
	 * 
	 * @param playerBlue
	 */
	void setPlayerBlue(Player playerBlue);

	/**
	 * Returns a set of all registered cards from this game model.
	 * 
	 * @return
	 */
	List<Card> getAllCards();

	/**
	 * Gets the card with the given game id. Returns null, if no such card was found.
	 * 
	 * @param cardGameID
	 * @return
	 */
	Card getCard(int cardGameID);

	/**
	 * Registers the given card at this game model. Has to be called whenever a card is dynamically created in a running game.
	 * 
	 * @param card
	 */
	void registerCard(Card card);

	/**
	 * Unregisters the given card from this game model. Has to be called whenever a card that was dynamically created in a running game is removed.
	 * 
	 * @param card
	 */
	void unregisterCard(Card card);

	/**
	 * Collects all basic pokemon on the given position in an ArrayList and returns them.
	 * 
	 * @param posID
	 * @return
	 */
	ArrayList<Card> getBasicPokemonOnPosition(PositionID posID);

	/**
	 * Returns all positions with cards on them from the players arena fields.
	 * 
	 * @param playerColor
	 * @return
	 */
	ArrayList<PositionID> getFullArenaPositions(Color playerColor);

	/**
	 * Returns all bench positions with cards on them from the players arena fields.
	 * 
	 * @param playerColor
	 * @return
	 */
	ArrayList<PositionID> getFullBenchPositions(Color playerColor);

	/**
	 * Returns an array list of the positionsIDs, if the given card can be put on a basic pokemon(in the arena of the given player), which evolves into the given
	 * card. Also checks, evolution is allowed in this turn(one cannot put a pokemon on the bench and evolve it in the same turn).
	 * 
	 * @param c
	 * @param color
	 * @return
	 */
	ArrayList<PositionID> getPositionsForEvolving(PokemonCard c, Color color);

	/**
	 * Sends a cardMessage to all players.
	 * 
	 * @param string
	 * @param cardList
	 */
	void sendCardMessageToAllPlayers(String string, List<Card> cardList);

	/**
	 * Sends a cardMessage to all players.
	 * 
	 * @param string
	 * @param cardList
	 */
	void sendCardMessageToAllPlayers(String string, Card card);

	/**
	 * A new text message is send to all players.
	 * 
	 * @param message
	 */
	void sendTextMessageToAllPlayers(String message);

	/**
	 * Sends the game model to all players.
	 * 
	 * @param string
	 * @param cardList
	 */
	void sendGameModelToAllPlayers();

	/**
	 * Returns the attackAction instance of this game model.
	 * 
	 * @return
	 */
	AttackAction getAttackAction();

	/**
	 * Returns the AttackCondition instance of this game model.
	 * 
	 * @return
	 */
	AttackCondition getAttackCondition();

	/**
	 * Setter for energy played.
	 * 
	 * @param b
	 */
	void setEnergyPlayed(boolean b);

	/**
	 * Getter for the turn number.
	 * 
	 * @return
	 */
	int getTurnNumber();

	/**
	 * Getter for the game state.
	 * 
	 * @return
	 */
	GameState getGameState();

	/**
	 * Actions that are executed, when a player has ended his turn. Should be called before {@link #betweenTurns()} and {@link #nextTurn()}.
	 */
	void executeEndTurn();

	/**
	 * Switches the turns. Should be called after a players turn has ended and also after {@link #executeEndTurn()}.
	 */
	void nextTurn();

	/**
	 * Executes actions that occur between players turns. Should be called after {@link #executeEndTurn()} and before {@link #nextTurn()}.
	 */
	void betweenTurns();

	/**
	 * Returns the id of this game.
	 * 
	 * @return
	 */
	long getGameID();

	/**
	 * Checks for defeated pokemon and cleans up the arena positions. Players select price cards here. Also the game state is being changed here, if the game has a
	 * looser.
	 */
	void cleanDefeatedPositions();

	void playerLoses(Player player);

	/**
	 * Getter for energyPlayed parameter.
	 * 
	 * @return
	 */
	boolean getEnergyPlayed();

	PositionID getDefendingPosition(Color attackerColor);
}
