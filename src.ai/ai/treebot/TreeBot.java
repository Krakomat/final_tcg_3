package ai.treebot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.common.base.Preconditions;

import network.client.Player;
import network.server.PokemonGameManager;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import ai.interfaces.Bot;
import ai.util.AIUtilities;

/**
 * A simple (but not trivial) implementation of an ai.
 * 
 * @author Michael
 *
 */
public class TreeBot implements Bot {
	private enum BotState {
		CHOOSE_ACTIVE, NORMAL;
	}

	private BotState botState;
	private AIUtilities aiUtilities;
	private LocalPokemonGameModel gameModel;
	private Queue<List<PositionID>> chosenPositionQueue;
	private Queue<List<Integer>> chosenCardsQueue; // -->GameID
	private Queue<List<Element>> chosenElementQueue;
	private Queue<List<String>> chosenAttackQueue;
	private Color botColor;

	public TreeBot() {
		this.aiUtilities = new AIUtilities();
		this.gameModel = null;
		this.chosenPositionQueue = new LinkedList<>();
		this.chosenCardsQueue = new LinkedList<>();
		this.chosenElementQueue = new LinkedList<>();
		this.chosenAttackQueue = new LinkedList<>();
	}

	@Override
	public void updateGameModel(LocalPokemonGameModel gameModel) {
		this.gameModel = gameModel;
	}

	@Override
	public void startGame() {
		this.botState = BotState.CHOOSE_ACTIVE;
	}

	@Override
	public void makeMove(PokemonGameManager server, Player player) {
		LocalPokemonGameModel copy = gameModel.copy();
		copy.setPlayerOnTurn(new PlayerSimulator(player.getColor()));
		GameTree gameTree = new GameTree(copy, new NeoTreeBotEvaluator(), server);
		GameTreeMove move = gameTree.computeMove();
		if (move == null)
			server.endTurn(player);
		else {
			// Prepare the queues:
			this.chosenCardsQueue = move.getChosenCardsQueue();
			this.chosenAttackQueue = move.getChosenAttackQueue();
			this.chosenElementQueue = move.getChosenElementQueue();
			this.chosenPositionQueue = move.getChosenPositionQueue();

			System.err.println("Executing move: " + move.toString());
			this.aiUtilities.executeMove(move, server, player);
			System.err.println("---------------------------------------------------------");
			System.err.println("---------------------------------------------------------");
		}
	}

	@Override
	public List<Card> choosesCards(List<Card> cards, int amount, boolean exact) {
		if (this.botState == BotState.CHOOSE_ACTIVE) {
			List<Card> chosenCards = new ArrayList<Card>();
			/*
			 * Choose active pokemon: Choose the pokemon with the most HP.
			 */
			PokemonCard chosenCard = (PokemonCard) cards.get(0);
			for (Card card : cards) {
				if (chosenCard.getHitpoints() < ((PokemonCard) card).getHitpoints())
					chosenCard = (PokemonCard) card;
			}
			chosenCards.add(chosenCard);
			this.botState = BotState.NORMAL;
			return chosenCards;
		} else {
			if (!this.chosenCardsQueue.isEmpty()) {
				List<Card> cardList = new ArrayList<>();
				List<Integer> gameIDList = this.chosenCardsQueue.poll();
				for (Integer id : gameIDList)
					cardList.add(gameModel.getCard(id));
				return cardList;
			} else {
				List<Card> chosenCards = new ArrayList<Card>();
				for (int i = 0; i < amount && i < cards.size(); i++)
					chosenCards.add(cards.get(i));
				return chosenCards;
			}
		}
	}

	@Override
	public List<PositionID> choosesPositions(List<PositionID> positionList, int amount, boolean exact) {
		if (!this.chosenPositionQueue.isEmpty()) {
			return this.chosenPositionQueue.poll();
		} else {
			PositionID activePosition = this.botColor == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
			if (this.gameModel.getPosition(activePosition).isEmpty() && amount == 1 && exact) {
				NeoTreeBotEvaluator evaluator = new NeoTreeBotEvaluator();
				// Choose new active pokemon:
				float value = Float.NEGATIVE_INFINITY;
				PositionID chosenPosition = null;
				for (PositionID benchPos : positionList) {
					LocalPokemonGameModel copy = gameModel.copy();
					copy.getAttackAction().movePokemonToPosition(benchPos, activePosition);
					float v = evaluator.evaluateGameModel(copy);
					if (v > value) {
						value = v;
						chosenPosition = benchPos;
					}
				}
				List<PositionID> chosenPositions = new ArrayList<PositionID>();
				chosenPositions.add(chosenPosition);
				return chosenPositions;
			} else {
				List<PositionID> chosenPositions = new ArrayList<PositionID>();
				for (int i = 0; i < amount && i < positionList.size(); i++)
					chosenPositions.add(positionList.get(i));
				return chosenPositions;
			}
		}
	}

	@Override
	public List<Element> choosesElements(List<Element> elements, int amount, boolean exact) {
		if (!this.chosenElementQueue.isEmpty()) {
			return this.chosenElementQueue.poll();
		} else {
			List<Element> chosenElements = new ArrayList<Element>();
			for (int i = 0; i < amount && i < elements.size(); i++)
				chosenElements.add(elements.get(i));
			return chosenElements;
		}
	}

	@Override
	public List<String> choosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact) {
		if (!this.chosenAttackQueue.isEmpty()) {
			return this.chosenAttackQueue.poll();
		} else {
			List<String> chosenAttacks = new ArrayList<String>();
			for (int i = 0; i < amount && i < attacks.size(); i++)
				chosenAttacks.add(attacks.get(i));
			return chosenAttacks;
		}
	}

	@Override
	public List<Card> paysEnergyCosts(List<Element> costs, List<Card> energyCards) {
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
		for (int i = 0; i < availableCards.size(); i++) {
			EnergyCard c = (EnergyCard) availableCards.get(i);
			if (!c.isBasisEnergy()) {
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

		Preconditions.checkArgument(aiUtilities.checkPaymentOk(chosenCards, costs), "Error: Payment of TreeBot was not ok! Cost: " + costs + " Payment: " + chosenCards);
		return chosenCards;
	}

	public void setColor(Color color) {
		this.botColor = color;
	}
}
