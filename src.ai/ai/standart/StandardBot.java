package ai.standart;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Preconditions;

import network.client.Player;
import network.server.PokemonGameManager;
import common.utilities.Triple;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import model.interfaces.Position;
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
		CHOOSE_ACTIVE, CHOOSE_BENCH, NORMAL;
	}

	private BotState botState;
	private AIUtilities aiUtilities;
	private LocalPokemonGameModel gameModel;

	public StandardBot() {
		this.aiUtilities = new AIUtilities();
		this.gameModel = null;
		this.botState = BotState.NORMAL;
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
		this.aiUtilities.sleep(4000);
		// (Position, positionIndex, Action)
		List<Triple<Position, Integer, String>> actionList = this.aiUtilities.computePlayerActions(this.gameModel, player);
		List<Triple<Position, Integer, String>> attackList = this.aiUtilities.filterActions(actionList, PlayerAction.ATTACK_1, PlayerAction.ATTACK_2,
				PlayerAction.RETREAT_POKEMON);
		this.aiUtilities.filterActions(attackList, PlayerAction.RETREAT_POKEMON);
		if (!actionList.isEmpty()) {
			Random r = new SecureRandom();
			int index = r.nextInt(actionList.size());
			int handCardIndex = actionList.get(index).getValue();
			server.playerPlaysCard(player, handCardIndex);
		} else if (!attackList.isEmpty()) {
			Random r = new SecureRandom();
			int index = r.nextInt(attackList.size());
			Triple<Position, Integer, String> attackTriple = attackList.get(index);
			if (attackTriple.getAction().equals(PlayerAction.ATTACK_1.toString()))
				server.executeAttack(player, ((PokemonCard) attackTriple.getKey().getTopCard()).getAttackNames().get(0));
			else if (attackTriple.getAction().equals(PlayerAction.ATTACK_2.toString()))
				server.executeAttack(player, ((PokemonCard) attackTriple.getKey().getTopCard()).getAttackNames().get(1));
		} else
			server.endTurn(player);
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
		ArrayList<PositionID> chosenPositions = new ArrayList<PositionID>();
		for (int i = 0; i < amount && i < positionList.size(); i++)
			chosenPositions.add(positionList.get(i));
		return chosenPositions;
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
}
