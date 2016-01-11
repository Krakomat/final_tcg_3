package model.database;

import java.util.ArrayList;
import java.util.List;

import model.enums.CardType;
import model.enums.Element;

public class EnergyCard extends Card {

	public static Element getOriginalEnergy(String cardId) {
		switch (cardId) {
		case "00096":
			return Element.COLORLESS;
		case "00097":
			return Element.ROCK;
		case "00098":
			return Element.FIRE;
		case "00099":
			return Element.GRASS;
		case "00100":
			return Element.LIGHTNING;
		case "00101":
			return Element.PSYCHIC;
		case "00102":
			return Element.WATER;
		case "00104":
			return Element.RAINBOW;
		case "00216":
			return Element.RAINBOW;
		case "00263":
			return Element.COLORLESS;
		case "00264":
			return Element.COLORLESS;
		}
		return null;
	}

	private boolean basisEnergy;
	private List<Element> providedEnergy;

	public EnergyCard() {
		super();
		providedEnergy = new ArrayList<Element>();
		this.setCardType(CardType.ENERGY);
	}

	public EnergyCard(Card c) {
		super();
		providedEnergy = new ArrayList<Element>();
		this.setCardType(CardType.ENERGY);
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
		EnergyCard c = (EnergyCard) super.copy();
		c.setBasisEnergy(basisEnergy);
		List<Element> pEnergy = new ArrayList<>();
		for (Element element : this.providedEnergy)
			pEnergy.add(element);
		c.setProvidedEnergy(pEnergy);
		return c;
	}

	/**
	 * @return Returns the basisEnergy.
	 */
	public boolean isBasisEnergy() {
		return this.basisEnergy;
	}

	/**
	 * @param basisEnergy
	 *            The basisEnergy to set.
	 */
	public void setBasisEnergy(boolean basisEnergy) {
		this.basisEnergy = basisEnergy;
	}

	/**
	 * @return Returns the providedEnergy.
	 */
	public List<Element> getProvidedEnergy() {
		return this.providedEnergy;
	}

	/**
	 * @param providedEnergy
	 *            The providedEnergy to set.
	 */
	public void setProvidedEnergy(List<Element> providedEnergy) {
		this.providedEnergy = providedEnergy;
	}
}