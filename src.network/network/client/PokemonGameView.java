package network.client;

import gui2d.animations.Animation;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import model.interfaces.Position;

/**
 * Every kind of user interface has to implement this interface in order to communicate with the {@link Player}.
 * 
 * @author Michael
 *
 */
public interface PokemonGameView {

	/**
	 * Starts the view.
	 */
	public void startGame();

	/**
	 * Stops the view.
	 */
	public void stopGame();

	/**
	 * Returns true, if this view is already started, meaning that the method simpleInitApp() has already been fully executed.
	 */
	public boolean isStarted();

	/**
	 * The user chooses cards from the given array list.
	 * 
	 * @param positionList
	 * @param amount
	 * @param exact
	 *            true, if the user has to choose the exact amount of positions given.
	 * @param message
	 * @return
	 */
	public ArrayList<Card> userChoosesCards(List<Card> cards, int amount, boolean exact, String message);

	/**
	 * The user chooses positionIDs from the given array list.
	 * 
	 * @param positionList
	 * @param amount
	 * @param exact
	 *            true, if the user has to choose the exact amount of positions given.
	 * @param message
	 * @return
	 */
	public List<PositionID> userChoosesPositions(List<PositionID> positionList, int amount, boolean exact, String message);

	/**
	 * The user chooses elements from the given array list.
	 * 
	 * @param elements
	 * @param amount
	 * @param exact
	 *            true, if the user has to choose the exact amount of elements given.
	 * @param message
	 * @return
	 */
	public List<Element> userChoosesElements(List<Element> elements, int amount, boolean exact, String message);

	/**
	 * The user chooses attacks from the given array list.
	 * 
	 * @param attackOwner
	 * @param attacks
	 * @param amount
	 * @param exact
	 *            true, if the user has to choose the exact amount of attacks given.
	 * @param message
	 * @return
	 */
	public ArrayList<String> userChoosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact, String message);

	/**
	 * The user chooses energy cards from the given array list such that the given costs can be payed with those.
	 * 
	 * @param costs
	 * @param energyCards
	 * @return
	 */
	public List<Card> userPaysEnergyCosts(List<Element> costs, List<Card> energyCards);

	/**
	 * The user updates his representation of the game field with the given game field.
	 * 
	 * @param gameModel
	 * @param ownColor
	 *            the color, that the player, who updates this view, has.
	 * @param sound
	 */
	public void userUpdatesGameModel(LocalPokemonGameModel gameModel, Color ownColor, String sound);

	/**
	 * The user receives a game message from the server. He displays this message in his view.
	 * 
	 * @param message
	 * @param sound
	 */
	public void userReceivesGameTextMessage(String message, String sound);

	/**
	 * The user receives a game message from the server. He displays this message in his view using the given card. For example the card can be displayed along with
	 * the message in the center of the view.
	 * 
	 * @param message
	 * @param card
	 * @param sound
	 */
	public void userReceivesCardMessage(String message, Card card, String sound);

	/**
	 * The user receives a game message from the server. He displays this message in his view using the given card list. For example the cards can be displayed along
	 * with the message in the center of the view using arrows between the card images.
	 * 
	 * @param message
	 * @param cardList
	 * @param sound
	 */
	public void userReceivesCardMessage(String message, List<Card> cardList, String sound);

	/**
	 * Sets the given cards choosability (via PosID, index) to the given value.
	 * 
	 * @param position
	 * @param i
	 * @param value
	 */
	public void setCardChoosable(Position position, int i, boolean value);

	/**
	 * Setter for the visibility of the end turn button.
	 * 
	 * @param b
	 */
	public void setEndTurnButtonVisible(boolean b);

	/**
	 * Creates a choose window that lets the user distribute damage.
	 * 
	 * @param positionList
	 * @param damageList
	 * @param maxDistList
	 * @param mode
	 * @return
	 */
	public List<Integer> userDistributesDamage(List<Position> positionList, List<Integer> damageList, List<Integer> maxDistList, DistributionMode mode);

	/**
	 * The view plays the given sound.
	 * 
	 * @param sound
	 */
	public void playSound(String sound);

	/**
	 * Plays the given animation. Waits, until the animation has finished.
	 * 
	 * @param animation
	 */
	public void playAnimation(Animation animation);

	/**
	 * Pops a window where the user has to choose either yes or no.
	 * 
	 * @param question
	 * @return
	 */
	public boolean userAnswersQuestion(String question);
}
