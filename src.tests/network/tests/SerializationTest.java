package network.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.Database;
import model.database.Deck;
import model.database.DynamicPokemonCondition;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.game.GameModelUpdateImpl;
import model.game.PositionImpl;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;
import network.serialization.TCGSerializer;
import network.tcp.messages.ByteString;

import org.junit.Before;
import org.junit.Test;

public class SerializationTest {

	private TCGSerializer serializer;
	private List<Integer> intList;
	private List<String> stringList;
	private List<PositionID> posIDList;
	private List<Element> elementList;
	private Card card;
	private List<Card> cardList;
	private EnergyCard energyCard;
	private PokemonCard pokemonCard;
	private Position position;
	private List<Position> positionList;
	private GameModelUpdate update;
	private Deck deck;
	private List<DynamicPokemonCondition> condList;

	@Before
	public void init() {
		Database.init();
		serializer = new TCGSerializer();

		intList = new ArrayList<>();
		for (int i = 0; i < 5; i++)
			intList.add(i);

		stringList = new ArrayList<>();
		for (int i = 0; i < 5; i++)
			stringList.add("" + i);

		posIDList = new ArrayList<>();
		for (PositionID posID : PositionID.values())
			posIDList.add(posID);

		elementList = new ArrayList<>();
		for (Element element : Element.values())
			elementList.add(element);

		condList = new ArrayList<>();
		condList.add(new DynamicPokemonCondition(PokemonCondition.DAMAGEINCREASE10, 3));

		card = new Card();
		energyCard = (EnergyCard) Database.createCard("00102"); // Water Energy
		pokemonCard = (PokemonCard) Database.createCard("00002"); // Turtok
		pokemonCard.setConditions(condList);

		cardList = new ArrayList<>();
		for (int i = 1; i < 10; i++)
			cardList.add(Database.createCard("0000" + i));
		for (int i = 10; i < 100; i++)
			cardList.add(Database.createCard("000" + i));
		for (int i = 100; i < 103; i++)
			cardList.add(Database.createCard("00" + i));

		position = new PositionImpl(PositionID.BLUE_BENCH_1, Color.RED);
		position.setCards(cardList);

		positionList = new ArrayList<>();
		for (int i = 1; i < 10; i++) {
			Position pos = new PositionImpl(PositionID.BLUE_ACTIVEPOKEMON, Color.BLUE);
			pos.setCards(cardList);
			positionList.add(pos);
		}

		update = new GameModelUpdateImpl();
		update.setPositionList(positionList);
		update.setTurnNumber((short) 4);
		update.setEnergyPlayAllowed(true);
		update.setRetreatAllowed(true);
		
		deck = new Deck();
		deck.setName("TestDeck");
		deck.setCards(stringList);
	}

	@Test
	public void intListTest() throws IOException {
		ByteString b = serializer.packIntList(intList);
		List<Integer> newList = serializer.unpackIntList(b);
		for (int i = 0; i < intList.size(); i++)
			assertTrue(intList.get(i).equals(newList.get(i)));
	}

	@Test
	public void stringListTest() throws IOException {
		ByteString b = serializer.packStringList(stringList);
		List<String> newList = serializer.unpackStringList(b);
		for (int i = 0; i < stringList.size(); i++)
			assertTrue(stringList.get(i).equals(newList.get(i)));
	}

	@Test
	public void posIDListListTest() throws IOException {
		ByteString b = serializer.packPositionIDList(posIDList);
		List<PositionID> newList = serializer.unpackPositionIDList(b);
		for (int i = 0; i < posIDList.size(); i++)
			assertTrue(posIDList.get(i).equals(newList.get(i)));
	}

	@Test
	public void elementListListTest() throws IOException {
		ByteString b = serializer.packElementList(elementList);
		List<Element> newList = serializer.unpackElementList(b);
		for (int i = 0; i < elementList.size(); i++)
			assertTrue(elementList.get(i).equals(newList.get(i)));
	}

	@Test
	public void dummyCardTest() throws IOException {
		ByteString b = serializer.packCard(card);
		Card newCard = serializer.unpackCard(b);
		this.checkCard(newCard, card);
	}

	@Test
	public void energyCardTest() throws IOException {
		ByteString b = serializer.packCard(energyCard);
		EnergyCard newCard = (EnergyCard) serializer.unpackCard(b);
		this.checkCard(newCard, energyCard);
	}

	@Test
	public void pokemonCardTest() throws IOException {
		ByteString b = serializer.packCard(pokemonCard);
		PokemonCard newCard = (PokemonCard) serializer.unpackCard(b);
		this.checkCard(newCard, pokemonCard);
	}

	@Test
	public void cardListTest() throws IOException {
		ByteString b = serializer.packCardList(cardList);
		List<Card> newList = serializer.unpackCardList(b);
		for (int i = 0; i < newList.size(); i++)
			this.checkCard(newList.get(i), cardList.get(i));
	}

	private void checkCard(Card newCard, Card oldCard) {
		assertTrue(oldCard.getCardId().equals(newCard.getCardId()));
		assertTrue((oldCard.getCardType() == null && newCard.getCardType() == null) || oldCard.getCardType().equals(newCard.getCardType()));
		assertTrue((oldCard.getEdition() == null && newCard.getEdition() == null) || oldCard.getEdition().equals(newCard.getEdition()));
		assertTrue(oldCard.getGameID() == newCard.getGameID());
		assertTrue(oldCard.getImagePath().equals(newCard.getImagePath()));
		assertTrue(oldCard.getName().equals(newCard.getName()));
		assertTrue(oldCard.getPlayedInTurn() == newCard.getPlayedInTurn());
		assertTrue((oldCard.getRarity() == null && newCard.getRarity() == null) || oldCard.getRarity().equals(newCard.getRarity()));
		assertTrue(oldCard.isVisibleForPlayerBlue() == newCard.isVisibleForPlayerBlue());
		assertTrue(oldCard.isVisibleForPlayerRed() == newCard.isVisibleForPlayerRed());

		if (newCard instanceof PokemonCard) {
			PokemonCard oldPokemonCard = (PokemonCard) oldCard;
			PokemonCard c = (PokemonCard) newCard;
			assertTrue(oldPokemonCard.getElement().equals(c.getElement()));
			assertTrue(oldPokemonCard.getHitpoints() == c.getHitpoints());
			assertTrue(oldPokemonCard.getWeakness() == c.getWeakness() || oldPokemonCard.getWeakness().equals(c.getWeakness()));
			assertTrue(oldPokemonCard.getResistance() == c.getResistance() || oldPokemonCard.getResistance().equals(c.getResistance()));
			for (int i = 0; i < oldPokemonCard.getRetreatCosts().size(); i++)
				assertTrue(oldPokemonCard.getRetreatCosts().get(i).equals(c.getRetreatCosts().get(i)));
			assertTrue(oldPokemonCard.getEvolvesFrom().equals(c.getEvolvesFrom()));
			assertTrue(oldPokemonCard.getDamageMarks() == c.getDamageMarks());
			assertTrue(oldPokemonCard.getCurrentWeakness() == c.getCurrentWeakness() || oldPokemonCard.getCurrentWeakness().equals(c.getCurrentWeakness()));
			assertTrue(oldPokemonCard.getCurrentResistance() == c.getCurrentResistance() || oldPokemonCard.getCurrentResistance().equals(c.getCurrentResistance()));
			assertTrue(oldPokemonCard.isPriceValueable() == c.isPriceValueable());
		} else if (newCard instanceof EnergyCard) {
			EnergyCard oldEnergyCard = (EnergyCard) oldCard;
			EnergyCard c = (EnergyCard) newCard;
			assertTrue(oldEnergyCard.isBasisEnergy() == c.isBasisEnergy());
			for (int i = 0; i < oldEnergyCard.getProvidedEnergy().size(); i++)
				assertTrue(oldEnergyCard.getProvidedEnergy().get(i).equals(c.getProvidedEnergy().get(i)));
		}
	}

	@Test
	public void positionTest() throws IOException {
		ByteString b = serializer.packPosition(position);
		Position newPosition = serializer.unpackPosition(b);
		this.checkPosition(position, newPosition);
	}

	@Test
	public void positionListTest() throws IOException {
		ByteString b = serializer.packPositionList(positionList);
		List<Position> newList = serializer.unpackPositionList(b);
		for (int i = 0; i < newList.size(); i++)
			this.checkPosition(newList.get(i), positionList.get(i));
	}

	@Test
	public void conditionListTest() throws IOException {
		ByteString b = serializer.packConditionList(condList);
		List<DynamicPokemonCondition> newList = serializer.unpackConditionList(b);
		for (int i = 0; i < newList.size(); i++) {
			assertTrue(condList.get(i).getRemainingTurns() == newList.get(i).getRemainingTurns());
			assertTrue(condList.get(i).getCondition().equals(newList.get(i).getCondition()));
		}
	}

	private void checkPosition(Position oldPosition, Position newPosition) {
		assertTrue(oldPosition.getPositionID().equals(newPosition.getPositionID()));
		for (int i = 0; i < newPosition.getCards().size(); i++)
			this.checkCard(newPosition.getCards().get(i), oldPosition.getCards().get(i));
		assertTrue(oldPosition.getColor().equals(newPosition.getColor()));
		assertTrue(oldPosition.isVisibleForPlayer(Color.BLUE) == newPosition.isVisibleForPlayer(Color.BLUE));
		assertTrue(oldPosition.isVisibleForPlayer(Color.RED) == newPosition.isVisibleForPlayer(Color.RED));
	}

	@Test
	public void gameModelUpdateTest() throws IOException {
		ByteString b = serializer.packGameModelUpdate(update);
		GameModelUpdate newUpdate = serializer.unpackGameModelUpdate(b);

		assertTrue(newUpdate.getTurnNumber() == 4);
		assertTrue(newUpdate.isEnergyPlayAllowed() == true);
		assertTrue(newUpdate.isRetreatAllowed() == true);
		for (int i = 0; i < newUpdate.getPositionList().size(); i++)
			this.checkPosition(newUpdate.getPositionList().get(i), update.getPositionList().get(i));
	}

	@Test
	public void deckTest() throws IOException {
		ByteString b = serializer.packDeck(deck);
		Deck newDeck = serializer.unpackDeck(b);

		assertTrue(newDeck.getName().equals(deck.getName()));
		for (int i = 0; i < newDeck.getCards().size(); i++)
			assertTrue(newDeck.getCards().get(i).equals(deck.getCards().get(i)));
	}
}
