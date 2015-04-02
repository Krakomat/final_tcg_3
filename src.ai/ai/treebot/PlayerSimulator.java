package ai.treebot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ai.util.AIUtilities;

import com.google.common.base.Preconditions;

import model.database.Card;
import model.database.Deck;
import model.database.EnergyCard;
import model.enums.AccountType;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;
import network.client.Player;
import network.server.PokemonGameManager;

/**
 * Simulates a player locally. This player only has to make decisions.
 * 
 * @author Michael
 *
 */
public class PlayerSimulator implements Player {
	private Color color;
	private AIUtilities aiUtilities;
	private Queue<List<PositionID>> chosenPositionQueue;
	private Queue<List<Integer>> chosenCardsQueue; // -->GameID
	private Queue<List<Element>> chosenElementQueue;
	private Queue<List<String>> chosenAttackQueue;

	public PlayerSimulator(Color color) {
		this.color = color;
		this.aiUtilities = new AIUtilities();
		this.chosenPositionQueue = new LinkedList<>();
		this.chosenCardsQueue = new LinkedList<>();
		this.chosenElementQueue = new LinkedList<>();
		this.chosenAttackQueue = new LinkedList<>();
	}

	@Override
	public void setDeck(Deck deck) {

	}

	@Override
	public long getID() {
		return 0;
	}

	@Override
	public String getName() {
		return "Local Player";
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public Deck getDeck() {
		return null;
	}

	@Override
	public AccountType getAccountType() {
		return null;
	}

	@Override
	public void startGame() {

	}

	@Override
	public void exit() {

	}

	@Override
	public List<Card> playerChoosesCards(List<Card> cards, int amount, boolean exact, String message) {
		List<Card> chosenCards = new ArrayList<Card>();
		List<Integer> storedCards = new ArrayList<>();
		for (int i = 0; i < amount && i < cards.size(); i++) {
			chosenCards.add(cards.get(i));
			storedCards.add(cards.get(i).getGameID());
		}
		this.chosenCardsQueue.add(storedCards);
		return chosenCards;
	}

	@Override
	public List<PositionID> playerChoosesPositions(List<PositionID> positionList, int amount, boolean exact, String message) {
		List<PositionID> chosenPositions = new ArrayList<PositionID>();
		List<PositionID> storedPositions = new ArrayList<PositionID>();
		for (int i = 0; i < amount && i < positionList.size(); i++) {
			chosenPositions.add(positionList.get(i));
			storedPositions.add(positionList.get(i));
		}
		this.chosenPositionQueue.add(storedPositions);
		return chosenPositions;
	}

	@Override
	public List<Element> playerChoosesElements(List<Element> elements, int amount, boolean exact, String message) {
		List<Element> chosenElements = new ArrayList<Element>();
		List<Element> storedElements = new ArrayList<Element>();
		for (int i = 0; i < amount && i < elements.size(); i++) {
			chosenElements.add(elements.get(i));
			storedElements.add(elements.get(i));
		}
		this.chosenElementQueue.add(storedElements);
		return chosenElements;
	}

	@Override
	public List<String> playerChoosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact, String message) {
		List<String> chosenAttacks = new ArrayList<String>();
		List<String> storedAttacks = new ArrayList<String>();
		for (int i = 0; i < amount && i < attacks.size(); i++) {
			chosenAttacks.add(attacks.get(i));
			storedAttacks.add(attacks.get(i));
		}
		this.chosenAttackQueue.add(storedAttacks);
		return chosenAttacks;
	}

	@Override
	public List<Card> playerPaysEnergyCosts(List<Element> costs, List<Card> energyCards) {
		List<Card> chosenCards = new ArrayList<>();
		List<Card> availableCards = new ArrayList<>();
		for (Card c : energyCards)
			availableCards.add(c);

		// Get a copy of the color- and colorless costs:
		List<Element> colorCosts = new ArrayList<>();
		int colorless = 0;
		for (Element element : costs)
			if (element != Element.COLORLESS)
				colorCosts.add(element);
			else
				colorless++;

		// Pay color costs:
		for (Element element : colorCosts) {
			boolean payed = false;
			for (int i = 0; i < availableCards.size() && !payed; i++) {
				EnergyCard c = (EnergyCard) availableCards.get(i);
				if (c.getProvidedEnergy().contains(element)) {
					payed = true;
					availableCards.remove(i);
					i--;
					chosenCards.add(c);
				}
			}
		}

		// Pay colorless costs with non-basic energy:
		for (int i = 0; i < colorless; i++) {
			EnergyCard c = (EnergyCard) availableCards.get(i);
			if (c.getProvidedEnergy().size() >= colorless && !c.isBasisEnergy()) {
				chosenCards.add(c);
				colorless = colorless - c.getProvidedEnergy().size();
				availableCards.remove(i);
				i--;
			}
		}

		// Pay colorless costs with basic energy:
		for (int i = 0; i < colorless; i++) {
			EnergyCard c = (EnergyCard) availableCards.get(i);
			if (c.getProvidedEnergy().size() >= colorless) {
				chosenCards.add(c);
				colorless = colorless - c.getProvidedEnergy().size();
				availableCards.remove(i);
				i--;
			}
		}

		Preconditions.checkArgument(aiUtilities.checkPaymentOk(chosenCards, costs), "Error: Payment of StandardBot was not ok! Cost: " + costs + " Payment: "
				+ chosenCards);
		return chosenCards;
	}

	@Override
	public List<Integer> playerDistributesDamage(List<Position> positionList, List<Integer> damageList, List<Integer> maxDistList, DistributionMode mode) {
		return damageList;
	}

	@Override
	public void playerReceivesGameTextMessage(String message, String sound) {

	}

	@Override
	public void playerReceivesCardMessage(String message, Card card, String sound) {

	}

	@Override
	public void playerReceivesCardMessage(String message, List<Card> cardList, String sound) {

	}

	@Override
	public void playerUpdatesGameModel(GameModelUpdate gameModelUpdate, String sound) {

	}

	@Override
	public void playerMakesMove() {

	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void receiveGameDeleted() {

	}

	@Override
	public void setServer(PokemonGameManager server) {

	}

	@Override
	public void playerReceivesSound(String sound) {

	}

	public Queue<List<PositionID>> getChosenPositionQueue() {
		return chosenPositionQueue;
	}

	public Queue<List<Integer>> getChosenCardsQueue() {
		return chosenCardsQueue;
	}

	public Queue<List<Element>> getChosenElementQueue() {
		return chosenElementQueue;
	}

	public Queue<List<String>> getChosenAttackQueue() {
		return chosenAttackQueue;
	}
}
