package model.database;

import java.util.ArrayList;

import model.enums.Edition;

public class Booster {

	private Edition edition;
	private ArrayList<Card> cards = new ArrayList<Card>();


	public Booster(Edition e) {
		setEdition(e);
		setCards(new ArrayList<Card>());
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
	 * @return Returns the cards.
	 */
	public ArrayList<Card> getCards() {
		return this.cards;
	}


	/**
	 * @param cards
	 *            The cards to set.
	 */
	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}
}