package model.database;

import model.enums.CardType;
import model.enums.Edition;
import model.enums.Rarity;
import model.interfaces.Position;
import model.scripting.abstracts.CardScript;

public class Card implements Comparable<Card> {

	protected String name;
	protected String imagePath;
	protected Rarity rarity;
	protected String cardId;
	protected CardType cardType;
	protected Edition edition;
	protected transient CardScript cardScript; // will not be serialized

	protected transient Position currentPosition; // will not be serialized. only used at the server
	protected int gameID;
	protected boolean visibleForPlayerBlue, visibleForPlayerRed;
	protected int playedInTurn;

	public Card() {
		name = "----";
		cardId = "00000"; // Card back
		gameID = -1;
		currentPosition = null; // Currently not in game
		setVisibleForPlayerBlue(true);
		setVisibleForPlayerRed(true);
		imagePath = "/cards/cardBack.jpg";

		setPlayedInTurn(-1);
	}

	/**
	 * Copies the given card. The CardScript and Position will not be cloned with this.
	 * 
	 * @return
	 */
	public Card copy() {
		Card c = null;
		if (this instanceof PokemonCard)
			c = new PokemonCard();
		else if (this instanceof TrainerCard)
			c = new TrainerCard();
		else if (this instanceof EnergyCard)
			c = new EnergyCard();
		else
			c = new Card();
		c.setName(name);
		c.setImagePath(imagePath);
		c.setRarity(rarity);
		c.setCardId(cardId);
		c.setCardType(cardType);
		c.setEdition(edition);
		c.setGameID(gameID);
		c.setVisibleForPlayerBlue(visibleForPlayerBlue);
		c.setVisibleForPlayerRed(visibleForPlayerRed);
		c.setPlayedInTurn(playedInTurn);
		return c;
	}

	public Position getCurrentPosition() {
		return currentPosition;
	}

	/**
	 * Sets the current position for this card. Also call moveToPosition() method of the cards card script, if the position is != null.
	 * 
	 * @param value
	 */
	public void setCurrentPosition(Position value) {
		this.currentPosition = value;
		if (value != null && this.cardScript != null)
			this.cardScript.moveToPosition(value.getPositionID());
	}

	/**
	 * Sets the current position for this card. WARNING: Do not use this method at the server. This method is only being used for the construction of the local game
	 * model.
	 * 
	 * @param value
	 */
	public void setCurrentPositionLocal(Position value) {
		this.currentPosition = value;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the rarity.
	 */
	public Rarity getRarity() {
		return this.rarity;
	}

	/**
	 * @param rarity
	 *            The rarity to set.
	 */
	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}

	/**
	 * @return Returns the cardId.
	 */
	public String getCardId() {
		return this.cardId;
	}

	/**
	 * @param cardId
	 *            The cardId to set.
	 */
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	/**
	 * @return Returns the gameID.
	 */
	public int getGameID() {
		return this.gameID;
	}

	/**
	 * @param gameID
	 *            The gameID to set.
	 */
	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	/**
	 * @return Returns the cardType.
	 */
	public CardType getCardType() {
		return this.cardType;
	}

	/**
	 * @param cardType
	 *            The cardType to set.
	 */
	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	/**
	 * @return Returns the edition.
	 */
	public Edition getEdition() {
		return this.edition;
	}

	/**
	 * @param edition
	 *            The edition to set.
	 */
	public void setEdition(Edition edition) {
		this.edition = edition;
	}

	/**
	 * @return Returns the imagePath.
	 */
	public String getImagePath() {
		return this.imagePath;
	}

	/**
	 * @param imagePath
	 *            The imagePath to set.
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String toString() {
		return this.getCardId() + " - " + this.getName();
	}

	@Override
	public int compareTo(Card c) {
		if (Integer.valueOf(this.cardId) < Integer.valueOf((c.getCardId())))
			return -1;
		else if (Integer.valueOf(this.cardId) > Integer.valueOf(c.getCardId()))
			return 1;
		return 0;
	}

	public int getPlayedInTurn() {
		return this.playedInTurn;
	}

	public void setPlayedInTurn(int playedInTurn) {
		this.playedInTurn = playedInTurn;
	}

	public boolean isVisibleForPlayerBlue() {
		return visibleForPlayerBlue;
	}

	public void setVisibleForPlayerBlue(boolean visibleForPlayerBlue) {
		this.visibleForPlayerBlue = visibleForPlayerBlue;
	}

	public boolean isVisibleForPlayerRed() {
		return visibleForPlayerRed;
	}

	public void setVisibleForPlayerRed(boolean visibleForPlayerRed) {
		this.visibleForPlayerRed = visibleForPlayerRed;
	}

	public CardScript getCardScript() {
		return cardScript;
	}

	public void setCardScript(CardScript cardScript) {
		this.cardScript = cardScript;
	}
}