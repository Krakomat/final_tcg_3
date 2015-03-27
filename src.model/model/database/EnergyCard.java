package model.database;

import java.util.ArrayList;
import java.util.List;

import model.enums.CardType;
import model.enums.Element;

public class EnergyCard extends Card {

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

	public boolean providesEnergy(Element element) {
		for (int i = 0; i < providedEnergy.size(); i++)
			if (providedEnergy.get(i).equals(element))
				return true;
		return false;
	}

}