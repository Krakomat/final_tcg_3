package ai.treebot;

import model.enums.Color;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;

public class TreeBotEvaluator implements GameModelEvaluator {

	@Override
	public int evaluateGameModel(LocalPokemonGameModel gameModel) {
		int value = 0;
		Color color = gameModel.getPlayerOnTurn().getColor();

		value = value + this.evaluatePlayerModel(gameModel, color); // own evaluation
		value = value - this.evaluatePlayerModel(gameModel, color == Color.BLUE ? Color.RED : Color.BLUE); // enemy evaluation

		return value;
	}

	/**
	 * Evaluates the gameModel for the given player.
	 * 
	 * @param gameModel
	 * @param color
	 * @return
	 */
	private int evaluatePlayerModel(LocalPokemonGameModel gameModel, Color color) {
		int value = 0;

		// Gain one point for each card in your deck:
		value = value + gameModel.getPosition(PositionID.getDeckPosition(color)).size();

		// Lose one point for each card in your discard pile:
		value = value - gameModel.getPosition(PositionID.getDiscardPilePosition(color)).size();

		// Gain 5 points for each card in your hand:
		value = value + 5 * gameModel.getPosition(PositionID.getHandPosition(color)).size();

		// Gain 10 points for each pokemon on your bench:
		value = value + 10 * gameModel.getFullBenchPositions(color).size();

		// Gain 10 points for each card on your active position:
		value = value + 10 * gameModel.getPosition(PositionID.getActivePokemon(color)).size();

		// Loose 20 points for each of your remaining prize cards:
		value = value - 20 * gameModel.getGameField().getNonEmptyPriceList(color).size();

		return value;
	}
}
