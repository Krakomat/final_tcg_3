package network.client;

import java.util.List;

import network.server.PokemonGameManager;
import model.database.Card;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;

/**
 * Interface for a player in the game.
 * 
 * @author Michael
 *
 */
public interface Player extends Account {

	/**
	 * Notifies the player, that the game has started. The player has to start his view.
	 */
	public void startGame();

	/**
	 * Shuts down the player.
	 */
	public void exit();

	/**
	 * The player chooses cards from the given array list.
	 * 
	 * @param cards
	 * @param amount
	 * @param exact
	 *            true, if the player has to choose the exact amount of cards given.
	 * @param message
	 * @return
	 */
	public List<Card> playerChoosesCards(List<Card> cards, int amount, boolean exact, String message);

	/**
	 * The player chooses positionIDs from the given array list.
	 * 
	 * @param list
	 * @param amount
	 * @param exact
	 *            true, if the player has to choose the exact amount of positions given.
	 * @param message
	 * @return
	 */
	public List<PositionID> playerChoosesPositions(List<PositionID> list, int amount, boolean exact, String message);

	/**
	 * The player chooses elements from the given array list.
	 * 
	 * @param elements
	 * @param amount
	 * @param exact
	 *            true, if the player has to choose the exact amount of elements given.
	 * @param message
	 * @return
	 */
	public List<Element> playerChoosesElements(List<Element> elements, int amount, boolean exact, String message);

	/**
	 * The player chooses attacks from the given array list.
	 * 
	 * @param attackOwner
	 *            cards that own the attacks in the attack list
	 * @param attacks
	 * @param amount
	 * @param exact
	 *            true, if the player has to choose the exact amount of attacks given.
	 * @param message
	 * @return
	 */
	public List<String> playerChoosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact, String message);

	/**
	 * This player has to choose a set out of the given energy cards, such that the given costs can be payed with this set.
	 * 
	 * @param costs
	 *            costs
	 * @param energyCards
	 *            cards to choose from
	 * @return
	 */
	public List<Card> playerPaysEnergyCosts(List<Element> costs, List<Card> energyCards);

	/**
	 * The player receives a game message from the server. He displays this message in his view.
	 * 
	 * @param message
	 */
	public void playerReceivesGameTextMessage(String message);

	/**
	 * The player receives a game message from the server. He displays this message in his view using the given card. For example the card can be displayed along
	 * with the message in the center of the view.
	 * 
	 * @param message
	 * @param card
	 */
	public void playerReceivesCardMessage(String message, Card card);

	/**
	 * The player receives a game message from the server. He displays this message in his view using the given card list. For example the cards can be displayed
	 * along with the message in the center of the view using arrows between the card images.
	 * 
	 * @param message
	 * @param cardList
	 */
	public void playerReceivesCardMessage(String message, List<Card> cardList);

	/**
	 * The player updates his representation of the game field with the given game field.
	 * 
	 * @param gameModelUpdate
	 */
	public void playerUpdatesGameModel(GameModelUpdate gameModelUpdate);

	/**
	 * Prompts the player to make a move.
	 */
	public void playerMakesMove();

	/**
	 * Gets the color of the player.
	 * 
	 * @return color
	 */
	public Color getColor();

	/**
	 * Sets the color of the player.
	 * 
	 * @param color
	 *            color to set
	 */
	public void setColor(Color color);

	/**
	 * The player receives a message from the {@link PokemonGameManager} that the game the player is currently in has been deleted. Therefore this player updated his
	 * view to the main lobby.
	 */
	public void receiveGameDeleted();

	/**
	 * Sets the PokemonGameManager for this player.
	 * 
	 * @param server
	 */
	public void setServer(PokemonGameManager server);

	/**
	 * The player reorders damage points for his own pokemon.
	 * 
	 * @param positionList
	 *            used for the view
	 * @param damageList
	 *            initial damage
	 * @param maxDistList
	 *            max damage for a pokemon
	 * @param mode
	 * @return
	 */
	public List<Integer> playerDistributesDamage(List<Position> positionList, List<Integer> damageList, List<Integer> maxDistList, DistributionMode mode);
}
