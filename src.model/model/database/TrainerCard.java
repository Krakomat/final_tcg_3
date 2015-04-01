package model.database;

import model.enums.CardType;

public class TrainerCard extends Card {

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
		return c;
	}
}