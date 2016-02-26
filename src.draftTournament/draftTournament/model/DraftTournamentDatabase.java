package draftTournament.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Preconditions;

import arenaMode.model.ArenaFighter;
import arenaMode.model.ArenaFighterFactory;
import model.database.Card;
import model.database.Database;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Edition;
import model.enums.Element;
import model.enums.Rarity;

public class DraftTournamentDatabase {
	private final Edition[] VALID_EDITIONS = { Edition.BASE, Edition.JUNGLE, Edition.FOSSIL, Edition.ROCKET };
	private final Integer[] PERCENTAGE_FOR_RARITY = { 65, 30, 5 };
	private List<Edition> validEditions;
	private List<Element> elements;
	private List<Card> commonCards, uncommonCards, rareCards;
	private Random random;
	private List<ArenaFighter> opponents;

	public DraftTournamentDatabase() {
		random = new SecureRandom();
		elements = Element.getAllElements();
		elements.remove(Element.COLORLESS);
		validEditions = new ArrayList<>();
		for (Edition e : VALID_EDITIONS)
			validEditions.add(e);
		this.initOpponents();
	}

	public void initOpponents() {
		opponents = ArenaFighterFactory.getAllArenaFighters();
		while (opponents.size() != 3)
			opponents.remove(random.nextInt(opponents.size()));
	}

	public ArenaFighter getOpponent(int index) {
		Preconditions.checkArgument(index < 3);
		return opponents.get(index);
	}

	public List<Element> getRandomElementList(int number) {
		Preconditions.checkArgument(Element.getAllElements().size() - 1 > 0);
		Preconditions.checkArgument(number <= Element.getAllElements().size() - 1);

		List<Integer> indices = this.createIndices(number, Element.getAllElements().size() - 1);
		List<Element> erg = new ArrayList<>();
		for (Integer index : indices)
			erg.add(elements.get(index));
		return erg;
	}

	private List<Integer> createIndices(int number, int maxIndex) {
		Preconditions.checkArgument(number <= maxIndex);

		List<Integer> indices = new ArrayList<>();
		int full = 0;
		while (full < number) {
			int index = random.nextInt(maxIndex);
			if (!indices.contains(index)) {
				indices.add(index);
				full++;
			}
		}
		return indices;
	}

	public List<Card> getRandomCardList(int number, Rarity setRarity) {
		List<Card> erg = new ArrayList<>();
		Rarity rarity = null;
		if (setRarity == null)
			rarity = this.generateRarity();
		else
			rarity = setRarity;

		switch (rarity) {
		case COMMON:
			List<Integer> indices = this.createIndices(number, commonCards.size());
			for (Integer index : indices)
				erg.add(commonCards.get(index));
			break;
		case UNCOMMON:
			indices = this.createIndices(number, uncommonCards.size());
			for (Integer index : indices)
				erg.add(uncommonCards.get(index));
			break;
		case RARE:
			indices = this.createIndices(number, rareCards.size());
			for (Integer index : indices)
				erg.add(rareCards.get(index));
			break;
		default:
			break;
		}

		return erg;
	}

	private Rarity generateRarity() {
		int rngNumber = random.nextInt(100);
		if (rngNumber < PERCENTAGE_FOR_RARITY[2])
			return Rarity.RARE;
		if (rngNumber < PERCENTAGE_FOR_RARITY[1] + PERCENTAGE_FOR_RARITY[2])
			return Rarity.UNCOMMON;
		else
			return Rarity.COMMON;
	}

	public void initializeCardSet(List<Element> chosenElements, boolean includeTrainerCards) {
		commonCards = new ArrayList<>();
		uncommonCards = new ArrayList<>();
		rareCards = new ArrayList<>();

		for (String id : Database.getFullCardLibrary().getCards()) {
			Card c = Database.createCard(id);
			if (validEditions.contains(c.getEdition())) {
				if (c instanceof PokemonCard) {
					if (chosenElements.contains(((PokemonCard) c).getElement()))
						this.addCardToSet(c);
				} else if (c instanceof EnergyCard) {
					if (!((EnergyCard) c).isBasisEnergy())
						this.addCardToSet(c);
				} else {
					// Trainer card
					this.addCardToSet(c);
				}
			}
		}
	}

	private void addCardToSet(Card c) {
		switch (c.getRarity()) {
		case COMMON:
			commonCards.add(c);
			break;
		case HOLO:
			rareCards.add(c);
			break;
		case LEGENDARY:
			rareCards.add(c);
			break;
		case RARE:
			rareCards.add(c);
			break;
		case UNCOMMON:
			uncommonCards.add(c);
			break;
		default:
			break;
		}
	}
}
