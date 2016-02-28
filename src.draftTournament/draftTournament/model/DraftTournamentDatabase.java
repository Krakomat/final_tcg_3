package draftTournament.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Preconditions;

import arenaMode.model.ArenaFighter;
import arenaMode.model.ArenaFighterFactory;
import common.utilities.ProbabilityCoin;
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
	private List<Card> commonCards1, uncommonCards1, rareCards1;
	private List<Card> commonCards2, uncommonCards2, rareCards2;
	private List<Card> commonTrainerCards, uncommonTrainerCards, rareTrainerCards;
	private List<Card> commonColorlessCards, uncommonColorlessCards, rareColorlessCards;
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
		Rarity rarity = null;
		if (setRarity == null)
			rarity = this.generateRarity();
		else
			rarity = setRarity;

		switch (rarity) {
		case COMMON:
			return generateRandomCards(commonCards1, commonCards2, commonTrainerCards, commonColorlessCards, 3);
		case UNCOMMON:
			return generateRandomCards(uncommonCards1, uncommonCards2, uncommonTrainerCards, uncommonColorlessCards, 3);
		case RARE:
			return generateRandomCards(rareCards1, rareCards2, rareTrainerCards, rareColorlessCards, 3);
		default:
			return new ArrayList<>();
		}
	}

	private List<Card> generateRandomCards(List<Card> pokemonCards1, List<Card> pokemonCards2, List<Card> trainerCards, List<Card> colorlessCards, int number) {
		List<Card> erg = new ArrayList<>();
		ProbabilityCoin coin = new ProbabilityCoin();
		final float trainerCardPercentage = 0.15f;
		final float colorlessCardPercentage = 0.15f;

		while (erg.size() < number) {
			List<Card> chosenCardSet = null;
			if (coin.tossCoin(trainerCardPercentage)) {
				chosenCardSet = trainerCards;
			} else if (coin.tossCoin(colorlessCardPercentage)) {
				chosenCardSet = colorlessCards;
			} else {
				if (coin.tossCoin(0.5f))
					chosenCardSet = pokemonCards1;
				else
					chosenCardSet = pokemonCards2;
			}

			boolean cardAdded = false;
			while (!cardAdded) {
				Card c = chosenCardSet.get(random.nextInt(chosenCardSet.size()));
				boolean alreadyIn = false;
				for (Card card : erg)
					if (card.getCardId().equals(c.getCardId()))
						alreadyIn = true;
				if (!alreadyIn) {
					erg.add(c);
					cardAdded = true;
				}
			}
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
		commonCards1 = new ArrayList<>();
		uncommonCards1 = new ArrayList<>();
		rareCards1 = new ArrayList<>();
		commonCards2 = new ArrayList<>();
		uncommonCards2 = new ArrayList<>();
		rareCards2 = new ArrayList<>();
		commonColorlessCards = new ArrayList<>();
		uncommonColorlessCards = new ArrayList<>();
		rareColorlessCards = new ArrayList<>();
		commonTrainerCards = new ArrayList<>();
		uncommonTrainerCards = new ArrayList<>();
		rareTrainerCards = new ArrayList<>();

		for (String id : Database.getFullCardLibrary().getCards()) {
			Card c = Database.createCard(id);
			if (validEditions.contains(c.getEdition())) {
				if (c instanceof PokemonCard) {
					if (chosenElements.contains(((PokemonCard) c).getElement()))
						this.addCardToSet((PokemonCard) c, chosenElements);
					else if (((PokemonCard) c).getElement() == Element.COLORLESS)
						this.addCardToColorlessCardSet(c);
				} else if (c instanceof EnergyCard) {
					if (!((EnergyCard) c).isBasisEnergy())
						this.addCardToColorlessCardSet(c);
				} else {
					// Trainer card
					this.addCardToTrainerCardSet(c);
				}
			}
		}
	}

	private void addCardToTrainerCardSet(Card c) {
		switch (c.getRarity()) {
		case COMMON:
			commonTrainerCards.add(c);
			break;
		case HOLO:
			rareTrainerCards.add(c);
			break;
		case LEGENDARY:
			rareTrainerCards.add(c);
			break;
		case RARE:
			rareTrainerCards.add(c);
			break;
		case UNCOMMON:
			uncommonTrainerCards.add(c);
			break;
		default:
			break;
		}
	}

	private void addCardToColorlessCardSet(Card c) {
		switch (c.getRarity()) {
		case COMMON:
			commonColorlessCards.add(c);
			break;
		case HOLO:
			rareColorlessCards.add(c);
			break;
		case LEGENDARY:
			rareColorlessCards.add(c);
			break;
		case RARE:
			rareColorlessCards.add(c);
			break;
		case UNCOMMON:
			uncommonColorlessCards.add(c);
			break;
		default:
			break;
		}
	}

	private void addCardToSet(PokemonCard c, List<Element> chosenElements) {
		Preconditions.checkArgument(chosenElements.size() == 2);
		if (c.getElement() == chosenElements.get(0)) {
			switch (c.getRarity()) {
			case COMMON:
				commonCards1.add(c);
				break;
			case HOLO:
				rareCards1.add(c);
				break;
			case LEGENDARY:
				rareCards1.add(c);
				break;
			case RARE:
				rareCards1.add(c);
				break;
			case UNCOMMON:
				uncommonCards1.add(c);
				break;
			default:
				break;
			}
		} else {
			switch (c.getRarity()) {
			case COMMON:
				commonCards2.add(c);
				break;
			case HOLO:
				rareCards2.add(c);
				break;
			case LEGENDARY:
				rareCards2.add(c);
				break;
			case RARE:
				rareCards2.add(c);
				break;
			case UNCOMMON:
				uncommonCards2.add(c);
				break;
			default:
				break;
			}
		}
	}
}
