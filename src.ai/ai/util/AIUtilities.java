package ai.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ai.treebot.GameTreeMove;
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
import common.utilities.Triple;

public class AIUtilities {

	/**
	 * Computes all possible player actions that can be executed by the given player on the given game model.
	 * 
	 * @param gameModel
	 * @param player
	 * @return
	 */
	public List<GameTreeMove> computePlayerActions(LocalPokemonGameModel gameModel, Player player, PokemonGameManager server) {
		if (player.getColor() == Color.BLUE) {
			List<GameTreeMove> choosableCards = new ArrayList<>();
			// Check hand:
			Position handPos = gameModel.getPosition(PositionID.BLUE_HAND);
			for (int i = 0; i < handPos.getCards().size(); i++) {
				String handCardID = handPos.getCards().get(i).getCardId();
				List<String> actions = null;
				if (!handCardID.equals("00000"))
					actions = gameModel.getPlayerActions(i, PositionID.BLUE_HAND, player);
				if (actions != null && !actions.isEmpty()) {
					for (String action : actions) {
						if (action.equals(PlayerAction.PLAY_ENERGY_CARD.toString())) {
							for (PositionID arenaPosition : gameModel.getFullArenaPositions(Color.BLUE)) {
								Queue<List<PositionID>> posQueue = new LinkedList<>();
								List<PositionID> posList = new ArrayList<>();
								posList.add(arenaPosition);
								posQueue.add(posList);
								choosableCards.add(new GameTreeMove(new Triple<Position, Integer, String>(handPos, i, action), posQueue, new LinkedList<>(),
										new LinkedList<>(), new LinkedList<>()));
							}
						} else
							choosableCards.add(new GameTreeMove(new Triple<Position, Integer, String>(handPos, i, action)));
					}
				}
			}
			// Check active & bench:
			Position activePosition = gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON);
			List<String> actions = null;
			if (!activePosition.getTopCard().getCardId().equals("00000"))
				actions = gameModel.getPlayerActions(activePosition.size() - 1, PositionID.BLUE_ACTIVEPOKEMON, player);
			if (actions != null && !actions.isEmpty()) {
				for (String action : actions)
					choosableCards.add(new GameTreeMove(new Triple<Position, Integer, String>(activePosition, activePosition.size() - 1, action)));
			}
			for (int i = 1; i <= 5; i++) {
				actions = null;
				Position benchPosition = gameModel.getPosition(PositionID.valueOf("BLUE_BENCH_" + i));
				if (benchPosition.size() > 0) {
					String benchID = benchPosition.getTopCard().getCardId();
					if (!benchID.equals("00000"))
						actions = gameModel.getPlayerActions(benchPosition.size() - 1, PositionID.valueOf("BLUE_BENCH_" + i), player);
					if (actions != null && !actions.isEmpty()) {
						for (String action : actions)
							choosableCards.add(new GameTreeMove(new Triple<Position, Integer, String>(benchPosition, benchPosition.size() - 1, action)));
					}
				}
			}
			return choosableCards;
		} else if (player.getColor() == Color.RED) {
			List<GameTreeMove> choosableCards = new ArrayList<>();
			// Check hand:
			Position handPos = gameModel.getPosition(PositionID.RED_HAND);
			for (int i = 0; i < handPos.getCards().size(); i++) {
				String handCardID = handPos.getCards().get(i).getCardId();
				List<String> actions = null;
				if (!handCardID.equals("00000"))
					actions = gameModel.getPlayerActions(i, PositionID.RED_HAND, player);
				if (actions != null && !actions.isEmpty()) {
					for (String action : actions) {
						if (action.equals(PlayerAction.PLAY_ENERGY_CARD.toString())) {
							for (PositionID arenaPosition : gameModel.getFullArenaPositions(Color.RED)) {
								Queue<List<PositionID>> posQueue = new LinkedList<>();
								List<PositionID> posList = new ArrayList<>();
								posList.add(arenaPosition);
								posQueue.add(posList);
								choosableCards.add(new GameTreeMove(new Triple<Position, Integer, String>(handPos, i, action), posQueue, new LinkedList<>(),
										new LinkedList<>(), new LinkedList<>()));
							}
						} else
							choosableCards.add(new GameTreeMove(new Triple<Position, Integer, String>(handPos, i, action)));
					}
				}
			}
			// Check active & bench:
			Position activePosition = gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON);
			List<String> actions = null;
			if (!activePosition.getTopCard().getCardId().equals("00000"))
				actions = gameModel.getPlayerActions(activePosition.size() - 1, PositionID.RED_ACTIVEPOKEMON, player);
			if (actions != null && !actions.isEmpty()) {
				for (String action : actions)
					choosableCards.add(new GameTreeMove(new Triple<Position, Integer, String>(activePosition, activePosition.size() - 1, action)));
			}

			for (int i = 1; i <= 5; i++) {
				actions = null;
				Position benchPosition = gameModel.getPosition(PositionID.valueOf("RED_BENCH_" + i));
				if (benchPosition.size() > 0) {
					String benchID = benchPosition.getTopCard().getCardId();
					if (!benchID.equals("00000"))
						actions = gameModel.getPlayerActions(benchPosition.size() - 1, PositionID.valueOf("RED_BENCH_" + i), player);
					if (actions != null && !actions.isEmpty()) {
						for (String action : actions)
							choosableCards.add(new GameTreeMove(new Triple<Position, Integer, String>(benchPosition, benchPosition.size() - 1, action)));
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
	public List<GameTreeMove> filterActions(List<GameTreeMove> actionTriples, PlayerAction... filterActions) {
		List<GameTreeMove> outList = new ArrayList<>();

		for (int i = 0; i < actionTriples.size(); i++) {
			GameTreeMove move = actionTriples.get(i);
			boolean removed = false;
			String action = move.getTriple().getAction();
			for (PlayerAction playerAction : filterActions) {
				if (action.equals(playerAction.toString()) && !removed) {
					removed = true;
					actionTriples.remove(i);
					i--;
					outList.add(move);
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
	public void executeMove(GameTreeMove move, PokemonGameManager gameSimulator, Player executingPlayer) {
		PlayerAction action = PlayerAction.valueOf(move.getTriple().getAction());
		switch (action) {
		case ATTACK_1:
			gameSimulator.executeAttack(executingPlayer, ((PokemonCard) move.getTriple().getKey().getTopCard()).getAttackNames().get(0));
			break;
		case ATTACK_2:
			gameSimulator.executeAttack(executingPlayer, ((PokemonCard) move.getTriple().getKey().getTopCard()).getAttackNames().get(1));
			break;
		case EVOLVE_POKEMON:
			gameSimulator.playerPlaysCard(executingPlayer, move.getTriple().getValue());
			break;
		case PLAY_ENERGY_CARD:
			gameSimulator.playerPlaysCard(executingPlayer, move.getTriple().getValue());
			break;
		case PLAY_TRAINER_CARD:
			gameSimulator.playerPlaysCard(executingPlayer, move.getTriple().getValue());
			break;
		case POKEMON_POWER:
			throw new UnsupportedOperationException("PokemonPowers cannot be simulated right now!");
		case PUT_ON_BENCH:
			gameSimulator.playerPlaysCard(executingPlayer, move.getTriple().getValue());
			break;
		case RETREAT_POKEMON:
			gameSimulator.retreatPokemon(executingPlayer);
			break;
		case SHOW_CARDS_ON_POSITION:
			throw new UnsupportedOperationException("SHOW_CARDS_ON_POSITION does not need to be simulated!");
		default:
			throw new UnsupportedOperationException("Scheiﬂfehler :D");
		}
	}

	/**
	 * Returns the indices of the given energy cards in the given list, if they are not needed to pay the given costs.
	 * 
	 * @param attackCosts
	 * @param energyCards
	 * @return
	 */
	public List<Integer> getUnneccesaryEnergyIndices(List<Element> attackCosts, List<Card> energyCards) {
		List<Card> chosenCards = new ArrayList<>();
		List<Card> availableCards = new ArrayList<>();
		for (Card c : energyCards)
			availableCards.add(0, c); // Add in reverse, s.t. the oldest energy cards will be checked first!

		// Get a copy of the color- and colorless costs:
		List<Element> colorCosts = new ArrayList<>();
		int colorless = 0;
		for (Element element : attackCosts)
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
			if (i < availableCards.size()) {
				EnergyCard c = (EnergyCard) availableCards.get(i);
				if (c.getProvidedEnergy().size() <= colorless && !c.isBasisEnergy()) {
					chosenCards.add(c);
					colorless = colorless - c.getProvidedEnergy().size();
					availableCards.remove(i);
					i--;
				}
			}
		}

		// Pay colorless costs with basic energy:
		for (int i = 0; i < colorless; i++) {
			if (i < availableCards.size()) {
				EnergyCard c = (EnergyCard) availableCards.get(i);
				if (c.getProvidedEnergy().size() <= colorless) {
					chosenCards.add(c);
					colorless = colorless - c.getProvidedEnergy().size();
					availableCards.remove(i);
					i--;
				}
			}
		}

		List<Integer> remainingIndices = new ArrayList<>();
		for (int i = 0; i < energyCards.size(); i++)
			if (!chosenCards.contains(energyCards.get(i)))
				remainingIndices.add(i);
		return remainingIndices;
	}

	/**
	 * Checks if the given costs can be paid with the given elements.
	 * 
	 * @param costs
	 * @param onField
	 * @return
	 */
	public boolean energyAvailableForAttack(List<Element> costs, ArrayList<Element> onField) {
		// Check if costs are available at the position:
		@SuppressWarnings("unchecked")
		ArrayList<Element> copy = (ArrayList<Element>) onField.clone();
		ArrayList<Element> colors = new ArrayList<Element>();
		ArrayList<Element> colorless = new ArrayList<Element>();
		for (int i = 0; i < costs.size(); i++) {
			if (costs.get(i).equals(Element.COLORLESS))
				colorless.add(costs.get(i));
			else
				colors.add(costs.get(i));
		}

		for (int i = 0; i < colors.size(); i++) {
			Element e = colors.get(i);
			boolean found = false;
			for (int j = 0; j < copy.size(); j++) {
				if (e.equals(copy.get(j)) && !found) {
					copy.remove(j);
					found = true;
				}
			}
			if (!found)
				return false;
		}

		return colorless.size() <= copy.size();
	}
}
