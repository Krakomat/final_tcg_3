package ai.util;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.enums.Color;
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
	public List<Triple<Position, Integer, String>> computePlayerActions(LocalPokemonGameModel gameModel, Player player) {
		if (player.getColor() == Color.BLUE) {
			List<Triple<Position, Integer, String>> choosableCards = new ArrayList<>();
			// Check hand:
			Position handPos = gameModel.getPosition(PositionID.BLUE_HAND);
			for (int i = 0; i < handPos.getCards().size(); i++) {
				List<String> actions = gameModel.getPlayerActions(i, PositionID.BLUE_HAND, player);
				if (!actions.isEmpty()) {
					for (String action : actions)
						choosableCards.add(new Triple<Position, Integer, String>(handPos, i, action));
				}
			}
			// Check active & bench:
			Position activePosition = gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON);
			List<String> actions = gameModel.getPlayerActions(activePosition.size() - 1, PositionID.BLUE_ACTIVEPOKEMON, player);
			if (!actions.isEmpty()) {
				for (String action : actions)
					choosableCards.add(new Triple<Position, Integer, String>(activePosition, activePosition.size() - 1, action));
			}
			for (int i = 1; i <= 5; i++) {
				Position benchPosition = gameModel.getPosition(PositionID.valueOf("BLUE_BENCH_" + i));
				if (benchPosition.size() > 0) {
					actions = gameModel.getPlayerActions(benchPosition.size() - 1, PositionID.valueOf("BLUE_BENCH_" + i), player);
					if (!actions.isEmpty()) {
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
				List<String> actions = gameModel.getPlayerActions(i, PositionID.RED_HAND, player);
				if (!actions.isEmpty()) {
					for (String action : actions)
						choosableCards.add(new Triple<Position, Integer, String>(handPos, i, action));
				}
			}
			// Check active & bench:
			Position activePosition = gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON);
			List<String> actions = gameModel.getPlayerActions(activePosition.size() - 1, PositionID.RED_ACTIVEPOKEMON, player);
			if (!actions.isEmpty()) {
				for (String action : actions)
					choosableCards.add(new Triple<Position, Integer, String>(activePosition, activePosition.size() - 1, action));
			}
			for (int i = 1; i <= 5; i++) {
				Position benchPosition = gameModel.getPosition(PositionID.valueOf("RED_BENCH_" + i));
				if (benchPosition.size() > 0) {
					actions = gameModel.getPlayerActions(benchPosition.size() - 1, PositionID.valueOf("RED_BENCH_" + i), player);
					if (!actions.isEmpty()) {
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
}
