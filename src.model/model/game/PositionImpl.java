package model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.Position;

public class PositionImpl implements Position {

	private PositionID positionID;
	private List<Card> cards;
	private Color color;
	private boolean visibleForPlayerBlue, visibleForPlayerRed, changed;

	/**
	 * Only used for serialization!
	 */
	public PositionImpl() {
		cards = new ArrayList<Card>();
		changed = true;
	}

	public PositionImpl(PositionID id, Color color) {
		this.setPositionID(id);
		cards = new ArrayList<Card>();
		this.color = color;
		visibleForPlayerBlue = false;
		visibleForPlayerRed = false;
		changed = true;
	}

	@Override
	public Position copy() {
		Position position = new PositionImpl();
		position.setPositionID(positionID);
		List<Card> cardList = new ArrayList<>();
		for (Card c : this.cards) {
			Card copy = c.copy();
			copy.setCurrentPosition(position);
			cardList.add(copy);
		}
		position.setCards(cardList);
		position.setColor(color);
		position.setVisible(visibleForPlayerBlue, Color.BLUE);
		position.setVisible(visibleForPlayerRed, Color.RED);

		return position;
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}

	public void addToPosition(Card card) {
		this.cards.add(card);
		changed = true;
	}

	public Card getTopCard() {
		if (this.cards.isEmpty())
			return null;
		return this.cards.get(this.cards.size() - 1);
	}

	public boolean removeFromPosition(Card value) {
		if (this.cards.contains(value)) {
			changed = true;
			return this.cards.remove(value);
		}
		return false;
	}

	public List<Element> getEnergy() {
		List<Element> energy = new ArrayList<Element>();
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i) instanceof EnergyCard) {
				for (int j = 0; j < ((EnergyCard) cards.get(i)).getProvidedEnergy().size(); j++) {
					energy.add(((EnergyCard) cards.get(i)).getProvidedEnergy().get(j));
				}
			}
		}
		return energy;
	}

	public void shuffle() {
		ArrayList<Card> hList = new ArrayList<Card>();
		Random r = new Random();
		while (!cards.isEmpty()) {
			int random = r.nextInt(cards.size());
			Card c = cards.remove(random);
			hList.add(c);
		}
		cards = hList;
		changed = true;
	}

	public int size() {
		return cards.size();
	}

	public ArrayList<Card> getEnergyCards() {
		ArrayList<Card> temp = new ArrayList<Card>();
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i) instanceof EnergyCard) {
				temp.add((EnergyCard) cards.get(i));
			}
		}
		return temp;
	}

	@Override
	public ArrayList<Card> getBasicEnergyCards() {
		ArrayList<Card> temp = new ArrayList<Card>();
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i) instanceof EnergyCard && ((EnergyCard) cards.get(i)).isBasisEnergy()) {
				temp.add((EnergyCard) cards.get(i));
			}
		}
		return temp;
	}

	@Override
	public List<Card> getTrainerCards() {
		List<Card> temp = new ArrayList<Card>();
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i) instanceof TrainerCard) {
				temp.add(cards.get(i));
			}
		}
		return temp;
	}

	@Override
	public List<Card> getPokemonCards() {
		List<Card> temp = new ArrayList<Card>();
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i) instanceof PokemonCard) {
				temp.add(cards.get(i));
			}
		}
		return temp;
	}

	public boolean isDamaged() {
		if (!this.isEmpty() && this.getTopCard() instanceof PokemonCard) {
			return ((PokemonCard) getTopCard()).getDamageMarks() > 0;
		}
		return false;
	}

	public boolean isEvolved() {
		if (!this.isEmpty() && this.getTopCard() instanceof PokemonCard) {
			return !((PokemonCard) getTopCard()).getCardType().equals(CardType.BASICPOKEMON);
		}
		return false;
	}

	public Card getCardAtIndex(int i) throws ArrayIndexOutOfBoundsException {
		if (i < 0 || i >= cards.size())
			throw new ArrayIndexOutOfBoundsException("Array out of bounds in method getCardAtIndex of class PositionImpl. Stackposition = " + i);
		return cards.get(i);
	}

	public PositionID getPositionID() {
		return positionID;
	}

	@Override
	public void setPositionID(PositionID id) {
		this.positionID = id;
		changed = true;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
		changed = true;
	}

	@Override
	public boolean isVisibleForPlayer(Color playerColor) {
		if (playerColor == Color.BLUE)
			return this.visibleForPlayerBlue;
		else
			return this.visibleForPlayerRed;
	}

	public void setVisible(boolean value, Color playerColor) {
		if (playerColor == Color.BLUE)
			this.visibleForPlayerBlue = value;
		else
			this.visibleForPlayerRed = value;

		for (int i = 0; i < cards.size(); i++) {
			if (playerColor == Color.BLUE)
				cards.get(i).setVisibleForPlayerBlue(value);
			else
				cards.get(i).setVisibleForPlayerRed(value);
		}
		changed = true;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		changed = true;
	}

	@Override
	public Card removeTopCard() {
		if (this.isEmpty())
			return null;
		changed = true;
		return this.cards.remove(this.size() - 1);
	}

	@Override
	public Integer getAmountOfEnergy(Element ele) {
		int counter = 0;
		for (Card e : this.getEnergyCards())
			if (((EnergyCard) e).getProvidedEnergy().contains(ele))
				counter++;
		return counter;
	}

	@Override
	public boolean isChanged() {
		return changed;
	}

	@Override
	public void setChanged(boolean flag) {
		this.changed = flag;
	}
}
