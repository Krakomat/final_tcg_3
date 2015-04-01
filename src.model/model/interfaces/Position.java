package model.interfaces;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;

/**
 * Interface for a Position of the game field.
 * 
 * @author Michael
 *
 */
public interface Position {

	/**
	 * Creates a copy of this position.
	 * 
	 * @return
	 */
	public Position copy();

	/**
	 * Returns true, if this position doesn't contain any cards.
	 * 
	 * @return
	 */
	public boolean isEmpty();

	/**
	 * Returns the top card of this position. If the position is an active or a bench position. then the top card (if it exists) is always a {@link PokemonCard}.
	 * 
	 * @return top card or null, if position is empty.
	 */
	public Card getTopCard();

	/**
	 * Returns the number of cards, that are on this position. Returns 0 if the position is empty.
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Returns a list of all card, which are on this position. Return an empty list if this position is empty.
	 * 
	 * @return
	 */
	public List<Card> getCards();

	/**
	 * Returns true, if the top pokemon on this position is damaged. Returns false, if this position is empty.
	 * 
	 * @return
	 */
	public boolean isDamaged();

	/**
	 * Returns true, if the top pokemon is a descendant of one of the other pokemon cards on this position, false otherwise.
	 * 
	 * @return
	 */
	public boolean isEvolved();

	/**
	 * Returns a list of the energy that is on this position. This does not need to be equal to the amount of energy cards(consider 2xcolorless energy, or
	 * electrode).
	 * 
	 * @return
	 */
	public List<Element> getEnergy();

	/**
	 * Sets the list of provided energy on this position. Doesn't change the energy cards attached to this position.
	 * 
	 * @param providedEnergy
	 */
	public void setEnergy(List<Element> providedEnergy);

	/**
	 * Returns the card that is at the given index of the card list of this position.
	 * 
	 * @param i
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the index is too big or < 0.
	 * @return
	 */
	public Card getCardAtIndex(int i) throws ArrayIndexOutOfBoundsException;

	/**
	 * Sets the card list.
	 * 
	 * @param cards
	 */
	public void setCards(List<Card> cards);

	/**
	 * Returns the id of this position.
	 * 
	 * @return
	 */
	public PositionID getPositionID();

	/**
	 * Setter for the positionID.
	 * 
	 * @param id
	 */
	public void setPositionID(PositionID id);

	/**
	 * Removes the given card from this position
	 * 
	 * @param c
	 */
	public boolean removeFromPosition(Card c);

	/**
	 * Adds the given card to this position
	 * 
	 * @param c
	 */
	public void addToPosition(Card c);

	/**
	 * Removes the top card of this position(card at index size-1) and returns it. Returns null, if this position is empty.
	 * 
	 * @return
	 */
	public Card removeTopCard();

	/**
	 * Returns all {@link EnergyCard}s on this position.
	 * 
	 * @return
	 */
	public ArrayList<Card> getEnergyCards();

	/**
	 * Returns all {@link TrainerCard}s on this position.
	 * 
	 * @return
	 */
	public List<Card> getTrainerCards();

	/**
	 * Returns all {@link PokemonCard}s on this position.
	 * 
	 * @return
	 */
	public List<Card> getPokemonCards();

	/**
	 * Returns the color of this position.
	 * 
	 * @return
	 */
	public Color getColor();

	/**
	 * Sets the color of this position.
	 * 
	 * @param color
	 */
	public void setColor(Color color);

	/**
	 * Shuffles the cards positions on this position.
	 */
	public void shuffle();

	/**
	 * Returns true, if the position is visible for the given player.
	 * 
	 * @param playerColor
	 * @return
	 */
	public boolean isVisibleForPlayer(Color playerColor);

	/**
	 * Sets visibility for all cards on this position
	 * 
	 * @param value
	 * @param playerColor
	 *            Color of the player, for which visibility should be set
	 */
	public void setVisible(boolean value, Color playerColor);

	/**
	 * Returns the number of energy cards that provide the given element.
	 * 
	 * @param ele
	 * @return
	 */
	public Integer getAmountOfEnergy(Element ele);
}
