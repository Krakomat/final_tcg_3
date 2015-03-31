package ai.standard;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import com.google.common.base.Preconditions;

import network.client.Player;
import network.server.PokemonGameManager;
import common.utilities.Triple;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;
import ai.interfaces.Bot;
import ai.util.AIUtilities;

/**
 * A simple (but not trivial) implementation of an ai.
 * 
 * @author Michael
 *
 */
public class StandardBot implements Bot {
	private enum BotState {
		CHOOSE_ACTIVE, NORMAL;
	}

	private BotState botState;
	private AIUtilities aiUtilities;
	private LocalPokemonGameModel gameModel;
	private Color color;
	private Queue<List<PositionID>> choosePositionQueue;

	public StandardBot() {
		this.aiUtilities = new AIUtilities();
		this.gameModel = null;
		this.botState = BotState.NORMAL;
		this.choosePositionQueue = new LinkedList<>();
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
		aiUtilities.sleep(4000);
		// (Position, positionIndex, Action)
		List<Triple<Position, Integer, String>> actionList = aiUtilities.computePlayerActions(gameModel, player, server);
		List<Triple<Position, Integer, String>> energyList = aiUtilities.filterActions(actionList, PlayerAction.PLAY_ENERGY_CARD);
		List<Triple<Position, Integer, String>> attackList = aiUtilities.filterActions(actionList, PlayerAction.ATTACK_1, PlayerAction.ATTACK_2,
				PlayerAction.RETREAT_POKEMON);
		@SuppressWarnings("unused")
		List<Triple<Position, Integer, String>> retreatList = aiUtilities.filterActions(attackList, PlayerAction.RETREAT_POKEMON);

		if (!actionList.isEmpty()) {
			Random r = new SecureRandom();
			int index = r.nextInt(actionList.size());
			int handCardIndex = actionList.get(index).getValue();
			server.playerPlaysCard(player, handCardIndex);
			return;// End makeMove()
		}
		if (!energyList.isEmpty()) {
			boolean turnDone = checkAndExecuteEnergy(player, server);
			if (turnDone)
				return;
		}
		if (!attackList.isEmpty()) {
			Random r = new SecureRandom();
			int index = r.nextInt(attackList.size());
			Triple<Position, Integer, String> attackTriple = attackList.get(index);
			if (attackTriple.getAction().equals(PlayerAction.ATTACK_1.toString()))
				server.executeAttack(player, ((PokemonCard) attackTriple.getKey().getTopCard()).getAttackNames().get(0));
			else if (attackTriple.getAction().equals(PlayerAction.ATTACK_2.toString()))
				server.executeAttack(player, ((PokemonCard) attackTriple.getKey().getTopCard()).getAttackNames().get(1));
			else {
				System.err.println("Bot couldn't decide on an attack!");
				server.endTurn(player);
			}
			return;// End makeMove()
		} else
			server.endTurn(player);
		return;// End makeMove()
	}

	/**
	 * Checks if the bot wants to play an energy card.
	 * 
	 * @param player
	 * @param server
	 * @return
	 */
	private boolean checkAndExecuteEnergy(Player player, PokemonGameManager server) {
		// Compute possible targets:
		Map<PositionID, List<Element>> missingEnergyMap = this.computeMissingEnergy();
		PositionID chosenPosition = null;
		int energyCardIndex = -1;

		// Choose target & card:
		if (missingEnergyMap.containsKey(PositionID.getActivePokemon(color))) {
			for (Element element : missingEnergyMap.get(PositionID.getActivePokemon(color))) {
				// Check if we have that energy in our hand:
				int index = this.hasEnergyCardForElement(element);
				if (index > -1) {
					chosenPosition = PositionID.getActivePokemon(color);
					energyCardIndex = index;
					break; // End for
				}
			}
		}
		if (!missingEnergyMap.isEmpty() && chosenPosition == null && energyCardIndex == -1) {
			for (PositionID positionID : missingEnergyMap.keySet()) {
				for (Element element : missingEnergyMap.get(positionID)) {
					// Check if we have that energy in our hand:
					int index = this.hasEnergyCardForElement(element);
					if (index > -1) {
						chosenPosition = positionID;
						energyCardIndex = index;
						break; // End inner for
					}
				}
				if (chosenPosition != null && energyCardIndex > -1)
					break; // End outer for
			}
		}

		// Play the energy card:
		if (chosenPosition != null && energyCardIndex > -1) {
			List<PositionID> posList = new ArrayList<>();
			posList.add(chosenPosition);
			this.choosePositionQueue.add(posList);
			server.playerPlaysCard(player, energyCardIndex);
			return true; // End makeMove()
		}
		return false;
	}

	/**
	 * Checks if the bot has an energy card for the given element in his hand and returns its index, if this is true. Returns -1 if this is not true.
	 * 
	 * @param element
	 * @return
	 */
	private int hasEnergyCardForElement(Element element) {
		Position handPosition = this.gameModel.getPosition(PositionID.getHandPosition(color));
		for (int i = 0; i < handPosition.getCards().size(); i++) {
			if (handPosition.getCards().get(i) instanceof EnergyCard) {
				EnergyCard energyCard = (EnergyCard) handPosition.getCards().get(i);
				if (energyCard.getProvidedEnergy().contains(element) || element == Element.COLORLESS)
					return i;
			}
		}
		return -1;
	}

	/**
	 * Computes a map of pairs that contains all positions of this bot(key) and their missing amount of energy(value).
	 * 
	 * @return
	 */
	private Map<PositionID, List<Element>> computeMissingEnergy() {
		Map<PositionID, List<Element>> resultMap = new HashMap<>();
		for (PositionID positionID : gameModel.getFullArenaPositions(color)) {
			Position position = gameModel.getPosition(positionID);
			PokemonCard pokemon = (PokemonCard) position.getTopCard();

			for (String attackName : pokemon.getAttackNames()) {
				List<Element> energyContained = new ArrayList<>();
				for (Element element : position.getEnergy())
					energyContained.add(element);

				// "Pay" costs:
				List<Element> costs = ((PokemonCardScript) pokemon.getCardScript()).getAttackCosts(attackName);
				for (int i = 0; i < costs.size(); i++) {
					Element element = costs.get(i);
					if (energyContained.contains(element)) {
						energyContained.remove(element);
						costs.remove(i);
						i--;
					}
				}
				// Compute remaining colorless energy:
				int colorless = 0;
				for (Element element : costs)
					if (element == Element.COLORLESS)
						colorless++;
				if (costs.size() > 0) {
					// We potentially found some missing energy --> Try to pay the rest via colorless:
					if (energyContained.size() < colorless || energyContained.size() == 0) {
						// Found you!
						resultMap.put(position.getPositionID(), costs);
						break; // end for
					}
				}
			}
		}
		return resultMap;
	}

	@Override
	public List<Card> choosesCards(List<Card> cards, int amount, boolean exact) {
		List<Card> chosenCards = new ArrayList<Card>();
		if (this.botState == BotState.CHOOSE_ACTIVE) {
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
		} else {
			for (int i = 0; i < amount && i < cards.size(); i++)
				chosenCards.add(cards.get(i));
		}
		return chosenCards;
	}

	@Override
	public List<PositionID> choosesPositions(List<PositionID> positionList, int amount, boolean exact) {
		if (!this.choosePositionQueue.isEmpty()) {
			return this.choosePositionQueue.poll();
		} else {
			List<PositionID> chosenPositions = new ArrayList<PositionID>();
			for (int i = 0; i < amount && i < positionList.size(); i++)
				chosenPositions.add(positionList.get(i));
			return chosenPositions;
		}
	}

	@Override
	public List<Element> choosesElements(List<Element> elements, int amount, boolean exact) {
		List<Element> chosenElements = new ArrayList<Element>();
		for (int i = 0; i < amount && i < elements.size(); i++)
			chosenElements.add(elements.get(i));
		return chosenElements;
	}

	@Override
	public List<String> choosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact) {
		List<String> chosenAttacks = new ArrayList<String>();
		for (int i = 0; i < amount && i < attacks.size(); i++)
			chosenAttacks.add(attacks.get(i));
		return chosenAttacks;
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

	public void setColor(Color color) {
		this.color = color;
	}
}
