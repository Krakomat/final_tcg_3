package ai.treebot;

import gui2d.animations.Animation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ai.util.AIUtilities;
import arenaMode.model.ArenaFighterCode;

import com.google.common.base.Preconditions;

import model.database.Card;
import model.database.Deck;
import model.database.EnergyCard;
import model.enums.AccountType;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PositionID;
import model.game.GameModelUpdate;
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
	private Queue<List<PositionID>> chosenPositionQueue, simulatePositionQueue;
	private Queue<List<Integer>> chosenCardsQueue, simulateCardQueue; // -->GameID
	private Queue<List<String>> chosenAttackQueue, simulateAttackQueue;
	private Queue<List<Element>> chosenElementQueue, simulateElementQueue;

	public PlayerSimulator(Color color) {
		this.color = color;
		this.aiUtilities = new AIUtilities();
		this.chosenPositionQueue = new LinkedList<>();
		this.chosenCardsQueue = new LinkedList<>();
		this.chosenElementQueue = new LinkedList<>();
		this.chosenAttackQueue = new LinkedList<>();
		this.simulatePositionQueue = new LinkedList<>();
		this.simulateCardQueue = new LinkedList<>();
		this.simulateAttackQueue = new LinkedList<>();
		this.simulateElementQueue = new LinkedList<>();
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
	public String getAvatarPath() {
		throw new UnsupportedOperationException("");
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
		if (!this.simulateCardQueue.isEmpty()) {
			List<Integer> intList = this.simulateCardQueue.poll();
			List<Card> chosenCards = new ArrayList<Card>();
			for (Card c : cards)
				if (intList.contains(c.getGameID()))
					chosenCards.add(c);
			return chosenCards;
		} else {
			List<Card> chosenCards = new ArrayList<Card>();
			for (int i = 0; i < amount && i < cards.size(); i++)
				chosenCards.add(cards.get(i));
			return chosenCards;
		}
	}

	@Override
	public List<PositionID> playerChoosesPositions(List<PositionID> positionList, int amount, boolean exact, String message) {
		if (!this.simulatePositionQueue.isEmpty()) {
			return this.simulatePositionQueue.poll();
		} else {
			List<PositionID> chosenPositions = new ArrayList<PositionID>();
			for (int i = 0; i < amount && i < positionList.size(); i++)
				chosenPositions.add(positionList.get(i));
			return chosenPositions;
		}
	}

	@Override
	public List<Element> playerChoosesElements(List<Element> elements, int amount, boolean exact, String message) {
		if (!this.simulateElementQueue.isEmpty()) {
			return this.simulateElementQueue.poll();
		} else {
			List<Element> chosenElements = new ArrayList<Element>();
			for (int i = 0; i < amount && i < elements.size(); i++)
				chosenElements.add(elements.get(i));
			return chosenElements;
		}
	}

	@Override
	public List<String> playerChoosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact, String message) {
		if (!this.simulateAttackQueue.isEmpty()) {
			return this.simulateAttackQueue.poll();
		} else {
			List<String> chosenAttacks = new ArrayList<String>();
			for (int i = 0; i < amount && i < attacks.size(); i++)
				chosenAttacks.add(attacks.get(i));
			return chosenAttacks;
		}
	}

	@Override
	public boolean playerDecidesYesOrNo(String question) {
		return true; // always answer 'yes'
	}

	@Override
	public List<Card> playerPaysEnergyCosts(List<Element> costs, List<Card> energyCards) {
		if (energyCards.isEmpty()) {
			System.err.println("Player pays costs: ");
			System.err.println("Costs: " + costs);
			System.err.println("EnergyCards: " + energyCards);
		}
		List<Card> chosenCards = new ArrayList<>();
		List<Card> availableCards = new ArrayList<>();
		List<Card> copyOfAvailableCards = new ArrayList<>();
		for (Card c : energyCards) {
			availableCards.add(c);
			copyOfAvailableCards.add(c);
		}

		// Get a copy of the color- and colorless costs:
		List<Element> colorCosts = new ArrayList<>();
		int colorless = 0;
		for (Element element : costs)
			if (element != Element.COLORLESS)
				colorCosts.add(element);
			else
				colorless++;
		int colorlessAtStart = colorless;

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
		for (int i = 0; i < availableCards.size(); i++) {
			EnergyCard c = (EnergyCard) availableCards.get(i);
			if (c.getProvidedEnergy().size() <= colorless && !c.isBasisEnergy()) {
				chosenCards.add(c);
				colorless = colorless - c.getProvidedEnergy().size();
				availableCards.remove(i);
				i--;
			}
		}

		// Pay colorless costs with basic energy:
		for (int i = 0; i < availableCards.size(); i++) {
			EnergyCard c = (EnergyCard) availableCards.get(i);
			if (c.getProvidedEnergy().size() <= colorless) {
				chosenCards.add(c);
				colorless = colorless - c.getProvidedEnergy().size();
				availableCards.remove(i);
				i--;
			}
		}
		// In case there are still colorless costs left: Pay with non-basic energy and 'overpay':
		if (colorless > 0) {
			for (int i = 0; i < availableCards.size(); i++) {
				EnergyCard c = (EnergyCard) availableCards.get(i);
				if (c.getProvidedEnergy().size() >= colorless && !c.isBasisEnergy()) {
					chosenCards.add(c);
					colorless = colorless - c.getProvidedEnergy().size();
					availableCards.remove(i);
					i--;
				}
			}
		}

		Preconditions.checkArgument(aiUtilities.checkPaymentOk(chosenCards, costs), "Error: Payment of PlayerSimulator was not ok! Cost: " + costs + " Payment: " + chosenCards
				+ " AvailableCards: " + copyOfAvailableCards + " colorlessAtStart: " + colorlessAtStart + ", colorless: " + colorless + " colorcosts: " + colorCosts);
		return chosenCards;
	}

	// public static void main(String[] args) {
	// Database.init();
	// List<Element> costs = new ArrayList<>();
	// costs.add(Element.COLORLESS);
	// List<Card> energyCards = new ArrayList<>();
	// energyCards.add(Database.createCard("00096"));
	// energyCards.add(Database.createCard("00264"));
	// // energyCards.add(Database.createCard("00101"));
	//
	// PlayerSimulator simulator = new PlayerSimulator(Color.BLUE);
	// List<Card> payment = simulator.playerPaysEnergyCosts(costs, energyCards);
	// System.out.println(payment);
	// }

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
		Queue<List<PositionID>> copy = new LinkedList<List<PositionID>>();
		for (List<PositionID> list : this.chosenPositionQueue) {
			List<PositionID> copyList = new ArrayList<>();
			for (PositionID posID : list)
				copyList.add(posID);
			copy.add(copyList);
		}
		return copy;
	}

	public Queue<List<Integer>> getChosenCardsQueue() {
		Queue<List<Integer>> copy = new LinkedList<List<Integer>>();
		for (List<Integer> list : this.chosenCardsQueue) {
			List<Integer> copyList = new ArrayList<>();
			for (Integer posID : list)
				copyList.add(posID);
			copy.add(copyList);
		}
		return copy;
	}

	public Queue<List<Element>> getChosenElementQueue() {
		Queue<List<Element>> copy = new LinkedList<List<Element>>();
		for (List<Element> list : this.chosenElementQueue) {
			List<Element> copyList = new ArrayList<>();
			for (Element posID : list)
				copyList.add(posID);
			copy.add(copyList);
		}
		return copy;
	}

	public Queue<List<String>> getChosenAttackQueue() {
		Queue<List<String>> copy = new LinkedList<List<String>>();
		for (List<String> list : this.chosenAttackQueue) {
			List<String> copyList = new ArrayList<>();
			for (String posID : list)
				copyList.add(posID);
			copy.add(copyList);
		}
		return copy;
	}

	public void setChosenPositionQueue(Queue<List<PositionID>> queue) {
		this.chosenPositionQueue = queue;
		this.simulatePositionQueue = new LinkedList<>();
		for (List<PositionID> posList : this.chosenPositionQueue)
			this.simulatePositionQueue.add(posList);
	}

	public void setChosenCardsQueue(Queue<List<Integer>> chosenCardsQueue) {
		this.chosenCardsQueue = chosenCardsQueue;
		this.simulateCardQueue = new LinkedList<>();
		for (List<Integer> posList : this.chosenCardsQueue)
			this.simulateCardQueue.add(posList);
	}

	public void setChosenElementQueue(Queue<List<Element>> chosenElementQueue) {
		this.chosenElementQueue = chosenElementQueue;
		this.simulateElementQueue = new LinkedList<>();
		for (List<Element> posList : this.chosenElementQueue)
			this.simulateElementQueue.add(posList);
	}

	public void setChosenAttackQueue(Queue<List<String>> chosenAttackQueue) {
		this.chosenAttackQueue = chosenAttackQueue;
		this.simulateAttackQueue = new LinkedList<>();
		for (List<String> posList : this.chosenAttackQueue)
			this.simulateAttackQueue.add(posList);
	}

	/**
	 * Makes all the input queues empty.
	 */
	public void flushQueues() {
		this.chosenAttackQueue.clear();
		this.chosenCardsQueue.clear();
		this.chosenElementQueue.clear();
		this.chosenPositionQueue.clear();
		this.simulateAttackQueue.clear();
		this.simulateCardQueue.clear();
		this.simulateElementQueue.clear();
		this.simulatePositionQueue.clear();
	}

	@Override
	public void playerReceivesAnimation(Animation animation) {

	}

	@Override
	public List<ArenaFighterCode> getDefeatedArenaFighters() {
		return null;
	}

	@Override
	public void setDefeatedArenaFighters(List<ArenaFighterCode> defeatedArenaFighters) {

	}

	@Override
	public List<String> getUnlockedCards() {
		return null;
	}

	@Override
	public void setUnlockedCards(List<String> unlockedCards) {

	}

	@Override
	public void setBotDifficulty(int diff) {

	}

	@Override
	public int getPrizeCards() {
		return 0;
	}

	@Override
	public void setPriceCards(int number) {
	}
}
