package ai.interfaces;

import java.util.List;

import model.database.Card;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import network.client.Player;
import network.server.PokemonGameManager;

/**
 * Interface for a bot model.
 * 
 * @author Michael
 *
 */
public interface Bot {
	/**
	 * The bot updates his game model.
	 * 
	 * @param gameModel
	 */
	public void updateGameModel(LocalPokemonGameModel gameModel);

	/**
	 * The bot gets notified that the game has been started.
	 */
	public void startGame();

	/**
	 * The bot has to make a move. Implement this method by starting a separate thread. Otherwise the bot won't be able to send requests to the server!
	 * 
	 * @param server
	 * @param player
	 */
	public void makeMove(PokemonGameManager server, Player player);

	/**
	 * The bot needs to choose cards.
	 * 
	 * @param cards
	 * @param amount
	 * @param exact
	 * @return
	 */
	public List<Card> choosesCards(List<Card> cards, int amount, boolean exact);

	/**
	 * The bot needs to choose positions.
	 * 
	 * @param positionList
	 * @param amount
	 * @param exact
	 * @return
	 */
	public List<PositionID> choosesPositions(List<PositionID> positionList, int amount, boolean exact);

	/**
	 * The bot needs to choose elements.
	 * 
	 * @param elements
	 * @param amount
	 * @param exact
	 * @return
	 */
	public List<Element> choosesElements(List<Element> elements, int amount, boolean exact);

	/**
	 * The bot needs to choose attacks.
	 * 
	 * @param attackOwner
	 * @param attacks
	 * @param amount
	 * @param exact
	 * @return
	 */
	public List<String> choosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact);

	/**
	 * The bot needs to pay energy costs.
	 * 
	 * @param costs
	 * @param energyCards
	 * @return
	 */
	public List<Card> paysEnergyCosts(List<Element> costs, List<Card> energyCards);

	/**
	 * Sets the color for this bot.
	 * 
	 * @param color
	 */
	public void setColor(Color color);
}
