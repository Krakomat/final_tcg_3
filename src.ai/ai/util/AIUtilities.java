package ai.util;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import network.server.PokemonGameManager;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import model.interfaces.Position;
import model.scripting.abstracts.ServerCards;
import common.utilities.Triple;

public class AIUtilities {

	/**
	 * Computes all possible player actions that can be executed by the given player on the given game model.
	 * 
	 * @param gameModel
	 * @param player
	 * @return
	 */
	public List<Triple<Position, Integer, String>> computePlayerActions(LocalPokemonGameModel gameModel, Player player, PokemonGameManager server) {
		if (player.getColor() == Color.BLUE) {
			List<Triple<Position, Integer, String>> choosableCards = new ArrayList<>();
			// Check hand:
			Position handPos = gameModel.getPosition(PositionID.BLUE_HAND);
			for (int i = 0; i < handPos.getCards().size(); i++) {
				String handCardID = handPos.getCards().get(i).getCardId();
				List<String> actions = null;
				if (!ServerCards.createInstance().contains(handCardID) && !handCardID.equals("00000"))
					actions = gameModel.getPlayerActions(i, PositionID.BLUE_HAND, player);
				if (actions != null && !actions.isEmpty()) {
					for (String action : actions)
						choosableCards.add(new Triple<Position, Integer, String>(handPos, i, action));
				}
			}
			// Check active & bench:
			Position activePosition = gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON);
			List<String> actions = null;
			if (!ServerCards.createInstance().contains(activePosition.getTopCard().getCardId()) && !activePosition.getTopCard().getCardId().equals("00000"))
				actions = gameModel.getPlayerActions(activePosition.size() - 1, PositionID.BLUE_ACTIVEPOKEMON, player);
			if (actions != null && !actions.isEmpty()) {
				for (String action : actions)
					choosableCards.add(new Triple<Position, Integer, String>(activePosition, activePosition.size() - 1, action));
			}
			for (int i = 1; i <= 5; i++) {
				Position benchPosition = gameModel.getPosition(PositionID.valueOf("BLUE_BENCH_" + i));
				if (benchPosition.size() > 0) {
					String benchID = benchPosition.getTopCard().getCardId();
					if (!ServerCards.createInstance().contains(benchID) && !benchPosition.equals("00000"))
						actions = gameModel.getPlayerActions(benchPosition.size() - 1, PositionID.valueOf("BLUE_BENCH_" + i), player);
					if (actions != null && !actions.isEmpty()) {
						for (String action : actions)
							choosableCards.add(new Triple<Position, Integer, String>(benchPosition, benchPosition.size() - 1, action));
					}
				}
			}
			return choosableCards;
		} else if (player.getColor() == Color.RED) {
			List<Triple<Position, Integer, String>> choosableCards = new ArrayList<>();
			// Check hand:
			Position handPos = gameModel.getPosition(PositionID.RED_HAND);
			for (int i = 0; i < handPos.getCards().size(); i++) {
				String handCardID = handPos.getCards().get(i).getCardId();
				List<String> actions = null;
				if (!ServerCards.createInstance().contains(handCardID) && !handCardID.equals("00000"))
					actions = gameModel.getPlayerActions(i, PositionID.RED_HAND, player);
				if (actions != null && !actions.isEmpty()) {
					for (String action : actions)
						choosableCards.add(new Triple<Position, Integer, String>(handPos, i, action));
				}
			}
			// Check active & bench:
			Position activePosition = gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON);
			List<String> actions = null;
			if (!ServerCards.createInstance().contains(activePosition.getTopCard().getCardId()) && !activePosition.getTopCard().getCardId().equals("00000"))
				actions = gameModel.getPlayerActions(activePosition.size() - 1, PositionID.RED_ACTIVEPOKEMON, player);
			if (actions != null && !actions.isEmpty()) {
				for (String action : actions)
					choosableCards.add(new Triple<Position, Integer, String>(activePosition, activePosition.size() - 1, action));
			}
			for (int i = 1; i <= 5; i++) {
				Position benchPosition = gameModel.getPosition(PositionID.valueOf("RED_BENCH_" + i));
				if (benchPosition.size() > 0) {
					String benchID = benchPosition.getTopCard().getCardId();
					if (!ServerCards.createInstance().contains(benchID) && !benchID.equals("00000"))
						actions = gameModel.getPlayerActions(benchPosition.size() - 1, PositionID.valueOf("RED_BENCH_" + i), player);
					if (actions != null && !actions.isEmpty()) {
						for (String action : actions)
							choosableCards.add(new Triple<Position, Integer, String>(benchPosition, benchPosition.size() - 1, action));
					}
				}
			}
			return choosableCards;
		} else {
			System.err.println("Received playerMakesMove() from server, but no color assigned to this player");
			return null;
		}
	}

	/**
	 * Waits for the given amount.
	 * 
	 * @param milis
	 */
	public void sleep(long milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modifies the given actionList by filtering OUT all the given PlayerActions.
	 * 
	 * @param actionList
	 * @param filterActions
	 * @return all triples that have been filtered out
	 */
	public List<Triple<Position, Integer, String>> filterActions(List<Triple<Position, Integer, String>> actionTriples, PlayerAction... filterActions) {
		List<Triple<Position, Integer, String>> outList = new ArrayList<>();

		for (int i = 0; i < actionTriples.size(); i++) {
			Triple<Position, Integer, String> triple = actionTriples.get(i);
			boolean removed = false;
			String action = triple.getAction();
			for (PlayerAction playerAction : filterActions) {
				if (action.equals(playerAction.toString()) && !removed) {
					removed = true;
					actionTriples.remove(i);
					i--;
					outList.add(triple);
				}
			}
		}
		return outList;
	}

	/**
	 * Checks if the given list of cards is able to pay the given costs of energy.
	 * 
	 * @param chosenEnergyCards
	 * @param costs
	 * @return
	 */
	public boolean checkPaymentOk(List<Card> chosenEnergyCards, List<Element> costs) {
		List<Element> chosenEnergy = new ArrayList<Element>();
		for (Card c : chosenEnergyCards) {
			List<Element> energy = ((EnergyCard) c).getProvidedEnergy();
			for (Element ele : energy)
				chosenEnergy.add(ele);
		}

		// Get a copy of the color- and colorless costs:
		List<Element> colorCosts = new ArrayList<>();
		int colorless = 0;
		for (Element element : costs)
			if (element != Element.COLORLESS)
				colorCosts.add(element);
			else
				colorless++;

		// Try to pay color costs:
		for (Element element : chosenEnergy) {
			boolean payed = false;
			for (int i = 0; i < colorCosts.size() && !payed; i++) {
				Element costElement = colorCosts.get(i);
				if (costElement == element) {
					colorCosts.remove(costElement);
					payed = true;
				}
			}
		}
		boolean colorPayed = colorCosts.size() == 0;

		// Try to pay the rest costs(colorless):
		boolean colorlessPayed = chosenEnergy.size() >= colorless;

		return colorPayed && colorlessPayed;
	}

	/**
	 * Executes the given move on the given simulator.
	 * 
	 * @param move
	 * @param gameSimulator
	 * @param executingPlayer
	 */
	public void executeMove(Triple<Position, Integer, String> move, PokemonGameManager gameSimulator, Player executingPlayer) {
		PlayerAction action = PlayerAction.valueOf(move.getAction());
		switch (action) {
		case ATTACK_1:
			gameSimulator.executeAttack(executingPlayer, ((PokemonCard) move.getKey().getTopCard()).getAttackNames().get(0));
			break;
		case ATTACK_2:
			gameSimulator.executeAttack(executingPlayer, ((PokemonCard) move.getKey().getTopCard()).getAttackNames().get(1));
			break;
		case EVOLVE_POKEMON:
			gameSimulator.playerPlaysCard(executingPlayer, move.getValue());
			break;
		case PLAY_ENERGY_CARD:
			gameSimulator.playerPlaysCard(executingPlayer, move.getValue());
			break;
		case PLAY_TRAINER_CARD:
			gameSimulator.playerPlaysCard(executingPlayer, move.getValue());
			break;
		case POKEMON_POWER:
			throw new UnsupportedOperationException("PokemonPowers cannot be simulated right now!");
		case PUT_ON_BENCH:
			gameSimulator.playerPlaysCard(executingPlayer, move.getValue());
			break;
		case RETREAT_POKEMON:
			gameSimulator.retreatPokemon(executingPlayer);
			break;
		case SHOW_CARDS_ON_POSITION:
			throw new UnsupportedOperationException("SHOW_CARDS_ON_POSITION does not need to be simulated!");
		default:
			throw new UnsupportedOperationException("Schei�fehler :D");
		}
	}
}
