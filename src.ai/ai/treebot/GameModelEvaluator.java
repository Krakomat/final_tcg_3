package ai.treebot;

import model.game.LocalPokemonGameModel;

/**
 * Classes that implement this interface have to be able to evaluate a {@link LocalPokemonGameModel}.
 * 
 * @author Michael
 *
 */
public interface GameModelEvaluator {
	/**
	 * Evaluates the game model and returns an integer value that represents the evaluation result.
	 * 
	 * @param gameModel
	 * @return
	 */
	public float evaluateGameModel(LocalPokemonGameModel gameModel);
}
