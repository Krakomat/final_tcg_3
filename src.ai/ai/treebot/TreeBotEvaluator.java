package ai.treebot;

import java.util.ArrayList;
import java.util.List;

import ai.util.AIUtilities;
import common.utilities.Pair;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class TreeBotEvaluator implements GameModelEvaluator {
	private AIUtilities aiUtilities;

	public TreeBotEvaluator() {
		this.aiUtilities = new AIUtilities();
	}

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

		// Loose 20 points for each of your remaining prize cards:
		value = value - 20 * gameModel.getGameField().getNonEmptyPriceList(color).size();

		// Gain one point for each card in your deck:
		value = value + gameModel.getPosition(PositionID.getDeckPosition(color)).size();

		// Lose one point for each card in your discard pile:
		value = value - gameModel.getPosition(PositionID.getDiscardPilePosition(color)).size();

		// Gain 2 points for each card in your hand:
		value = value + 2 * gameModel.getPosition(PositionID.getHandPosition(color)).size();

		// Gain 10 points for each pokemon card on your side:
		for (PositionID pos : gameModel.getFullBenchPositions(color))
			value = value + 10 * gameModel.getPosition(pos).getPokemonCards().size();
		value = value + 10 * gameModel.getPosition(PositionID.getActivePokemon(color)).getPokemonCards().size();

		// Loose 1 point for each damage mark on your own field:
		for (PositionID pos : gameModel.getFullBenchPositions(color)) {
			PokemonCard pokemon = (PokemonCard) gameModel.getPosition(pos).getTopCard();
			value = value - pokemon.getDamageMarks() / 10;
		}

		// Loose 2 points for each damage mark on your own active pokemon:
		PokemonCard ownActive = (PokemonCard) gameModel.getPosition(PositionID.getActivePokemon(color)).getTopCard();
		value = value - 2 * (ownActive.getDamageMarks() / 10);

		// Loose 2 points for each unnecessary energy in play, gain 4 for each needed energy in play. Gain 1 point for each attack that is ready:
		for (PositionID pos : gameModel.getFullBenchPositions(color)) {
			Pair<Integer, Integer> analysisResult = this.analyseEnergyOnPosition(gameModel.getPosition(pos));
			value = value - 2 * analysisResult.getValue();
			value = value + 4 * analysisResult.getKey();
			value = value + this.countAttacksReady(gameModel.getPosition(pos));
		}
		Pair<Integer, Integer> analysisResult = this.analyseEnergyOnPosition(gameModel.getPosition(PositionID.getActivePokemon(color)));
		value = value - 2 * analysisResult.getValue();
		value = value + 4 * analysisResult.getKey();
		value = value + this.countAttacksReady(gameModel.getPosition(PositionID.getActivePokemon(color)));

		return value;
	}

	/**
	 * Returns a tuple (necessary energy, unnecessary energy) for the given position.
	 * 
	 * @param position
	 * @return
	 */
	private Pair<Integer, Integer> analyseEnergyOnPosition(Position position) {
		int neededEnergy = 0;
		int trashEnergy = 0;

		if (!position.isEmpty()) {
			PokemonCard pokemon = (PokemonCard) position.getTopCard();
			PokemonCardScript cardScript = (PokemonCardScript) pokemon.getCardScript();
			List<Card> energyCards = position.getEnergyCards();
			List<List<Integer>> unnecessaryCardIndices = new ArrayList<>();

			for (String attack : pokemon.getAttackNames()) {
				List<Element> costs = cardScript.getAttackCosts(attack);
				unnecessaryCardIndices.add(aiUtilities.getUnneccesaryEnergyIndices(costs, energyCards));
			}

			for (int i = 0; i < energyCards.size(); i++) {
				boolean necessary = false;
				for (List<Integer> indicesList : unnecessaryCardIndices)
					if (!indicesList.contains(i))
						necessary = true;
				if (necessary)
					neededEnergy++;
				else
					trashEnergy++;
			}
		}
		return new Pair<Integer, Integer>(neededEnergy, trashEnergy);
	}

	/**
	 * Counts the number of attacks the pokemon on the given position could execute, if it were the active pokemon.
	 * 
	 * @param position
	 * @return
	 */
	private int countAttacksReady(Position position) {
		if (position.isEmpty())
			return 0;
		PokemonCard pokemon = (PokemonCard) position.getTopCard();
		PokemonCardScript cardScript = (PokemonCardScript) pokemon.getCardScript();
		int counter = 0;
		for (String attack : pokemon.getAttackNames()) {
			counter = counter + (aiUtilities.energyAvailableForAttack(cardScript.getAttackCosts(attack), (ArrayList<Element>) position.getEnergy()) == true ? 1 : 0);
		}
		return counter;
	}
}
