package model.database;

import model.enums.CardType;

public class TrainerCard extends Card {
	private boolean stadiumCard;

	public TrainerCard() {
		super();
		this.setCardType(CardType.TRAINER);
	}

	public TrainerCard(Card c) {
		super();
		setCardType(CardType.TRAINER);// Wert wird später geändert
		this.cardId = c.getCardId();
		this.name = c.getName();
		this.imagePath = c.getImagePath();
		this.cardType = c.getCardType();
		this.rarity = c.getRarity();
		this.edition = c.getEdition();
		this.cardScript = c.getCardScript();
	}

	@Override
	public Card copy() {
		TrainerCard c = (TrainerCard) super.copy();
		c.setStadiumCard(this.stadiumCard);
		return c;
	}

	public boolean isStadiumCard() {
		return stadiumCard;
	}

	public void setStadiumCard(boolean stadiumCard) {
		this.stadiumCard = stadiumCard;
	}
}