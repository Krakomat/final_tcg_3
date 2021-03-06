package model.scripting.abstracts;

import network.client.Player;
import model.database.Card;
import model.database.EnergyCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;

/**
 * Script, which is given to each card in the game.
 * 
 * @author Michael
 *
 */
public abstract class CardScript {
	protected PokemonGame gameModel;
	protected Card card;

	public CardScript(Card card, PokemonGame gameModel) {
		this.card = card;
		this.gameModel = gameModel;
	}

	/**
	 * Returns a {@link PlayerAction}, if the card can be played from the respective players hand, null otherwise. Returns also null, if the card is not in the players hand!
	 * 
	 * @return
	 */
	public abstract PlayerAction canBePlayedFromHand();

	/**
	 * Returns true, if this card is in either the hand of player blue or player red.
	 * 
	 * @return
	 */
	protected boolean cardInHand() {
		PositionID posID = this.card.getCurrentPosition().getPositionID();
		if (posID != PositionID.BLUE_HAND && posID != PositionID.RED_HAND)
			return false;
		return true;
	}

	/**
	 * Plays the card from the respective players hand.
	 */
	public abstract void playFromHand();

	/**
	 * Is called, if the card has any actions that are executed only at the end of a players turn(e.g. Pluspower has to be discarded from the pokemon at the end of the players
	 * turn).
	 */
	public void executeEndTurnActions() {
		// Only override when needed
	}

	/**
	 * Will be executed immediately before the player that is on turn next, will start his turn.
	 */
	public void executePreTurnActions(Player playerOnTurn) {
		// Only override when needed
	}

	/**
	 * Is called whenever the card is leaving its current position.
	 * 
	 * @param targetPosition
	 */
	public void leavePosition(PositionID oldPosition) {
		// Only override when needed
	}

	/**
	 * Is called whenever the card is being moved to a new position.
	 * 
	 * @param targetPosition
	 */
	public void moveToPosition(PositionID targetPosition) {
		// Only override when needed
	}

	/**
	 * Is called whenever the player plays an energy card.
	 * 
	 * @param energyCard
	 */
	public void energyCardPlayed(EnergyCard energyCard) {
		// Only override when needed
	}

	/**
	 * Is called whenever the underlying position of this card has changed!
	 */
	public void positionChanged() {
		// Only override when needed
	}

	/**
	 * Returns the positionID on which the card is at right now.
	 * 
	 * @return
	 */
	protected PositionID getCurrentPositionID() {
		return this.card.getCurrentPosition().getPositionID();
	}

	/**
	 * Gets the enemy player of this card.
	 * 
	 * @return
	 */
	protected Player getEnemyPlayer() {
		if (this.card.getCurrentPosition().getColor() == Color.BLUE)
			return gameModel.getPlayerRed();
		else if (this.card.getCurrentPosition().getColor() == Color.RED)
			return gameModel.getPlayerBlue();
		else
			throw new IllegalArgumentException("Error: Wrong Color for the position of the card!");
	}

	/**
	 * Gets the owner player of the card.
	 * 
	 * @return
	 */
	protected Player getCardOwner() {
		if (this.card.getCurrentPosition().getColor() == Color.BLUE)
			return gameModel.getPlayerBlue();
		else if (this.card.getCurrentPosition().getColor() == Color.RED)
			return gameModel.getPlayerRed();
		else
			throw new IllegalArgumentException("Error: Wrong Color for the position of the card!");
	}

	public void setCard(Card c) {
		this.card = c;
	}

	protected PositionID ownDeck() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DECK;
		else
			return PositionID.RED_DECK;
	}

	protected PositionID enemyDeck() {
		Player enemy = this.getEnemyPlayer();
		if (enemy.getColor() == Color.BLUE)
			return PositionID.BLUE_DECK;
		else
			return PositionID.RED_DECK;
	}

	protected PositionID ownActive() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_ACTIVEPOKEMON;
		else
			return PositionID.RED_ACTIVEPOKEMON;
	}

	protected PositionID enemyActive() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.RED_ACTIVEPOKEMON;
		else
			return PositionID.BLUE_ACTIVEPOKEMON;
	}

	protected PositionID ownDiscardPile() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DISCARDPILE;
		else
			return PositionID.RED_DISCARDPILE;
	}

	protected PositionID enemyDiscardPile() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.RED_DISCARDPILE;
		else
			return PositionID.BLUE_DISCARDPILE;
	}

	protected PositionID ownHand() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_HAND;
		else
			return PositionID.RED_HAND;
	}

	protected PositionID enemyHand() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.RED_HAND;
		else
			return PositionID.BLUE_HAND;
	}

	protected Integer cardGameID() {
		return this.card.getGameID();
	}
}