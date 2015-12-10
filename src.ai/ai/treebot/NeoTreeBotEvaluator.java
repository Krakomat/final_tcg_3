package ai.treebot;

import java.util.ArrayList;
import java.util.List;

import ai.util.AIUtilities;
import common.utilities.Pair;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.Element;
import model.enums.GameState;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class NeoTreeBotEvaluator implements GameModelEvaluator {
	private AIUtilities aiUtilities;

	public NeoTreeBotEvaluator() {
		this.aiUtilities = new AIUtilities();
	}

	@Override
	public float evaluateGameModel(LocalPokemonGameModel gameModel) {
		float value = 0;
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
	private float evaluatePlayerModel(LocalPokemonGameModel gameModel, Color color) {
		// Return Infinity, if you won:
		if (gameModel.getGameState() == GameState.BLUE_WON && color == Color.BLUE)
			return Float.POSITIVE_INFINITY;

		float value = 0;

		// Gain 20 points for each of your remaining prize cards:
		value = value + 20 * gameModel.getGameField().getEmptyPriceList(color).size();

		// Gain one point for each card in your deck:
		int deckSize = gameModel.getPosition(PositionID.getDeckPosition(color)).size();
		if (deckSize < 21)
			value = value + deckSize * 4;
		else
			value = value + 60 + deckSize;

		// Gain 1 point for each trainer card in your discard pile:
		value = value + gameModel.getPosition(PositionID.getDiscardPilePosition(color)).getTrainerCards().size();

		// Gain 2 points for each card in your hand:
		value = value + 2 * gameModel.getPosition(PositionID.getHandPosition(color)).size();

		// Gain 4 points for each pokemon card on your side:
		for (PositionID pos : gameModel.getFullBenchPositions(color))
			value = value + 4 * gameModel.getPosition(pos).getPokemonCards().size();
		value = value + 4 * gameModel.getPosition(PositionID.getActivePokemon(color)).getPokemonCards().size();

		// Gain up to 20 points for each position in your arena, depending on how much damage the position contains:
		for (PositionID pos : gameModel.getFullArenaPositions(color)) {
			PokemonCard pokemon = (PokemonCard) gameModel.getPosition(pos).getTopCard();
			float damage = pokemon.getDamageMarks();
			float maxHP = pokemon.getHitpoints();
			value = value + 20 - 20 * (damage / maxHP);
		}

		PokemonCard ownActive = (PokemonCard) gameModel.getPosition(PositionID.getActivePokemon(color)).getTopCard();

		if (ownActive != null) {
			// Gain 1 point for each negative condition on your active pokemon:
			if (ownActive.hasCondition(PokemonCondition.DAMAGEINCREASE10) || ownActive.hasCondition(PokemonCondition.DESTINY) || ownActive.hasCondition(PokemonCondition.HARDEN20)
					|| ownActive.hasCondition(PokemonCondition.HARDEN30) || ownActive.hasCondition(PokemonCondition.INVULNERABLE)
					|| ownActive.hasCondition(PokemonCondition.NO_DAMAGE) || ownActive.hasCondition(PokemonCondition.RETALIATION))
				value = value + 1;
		}

		// Gain 3 for each needed energy in play. Adds multiplier depending on the energy's position:
		for (PositionID pos : gameModel.getFullArenaPositions(color)) {
			PokemonCard pokemon = (PokemonCard) gameModel.getPosition(pos).getTopCard();
			Pair<Integer, Integer> analysisResult = this.analyseEnergyOnPosition(gameModel.getPosition(pos));
			float energyValue = 3 * analysisResult.getKey();

			// *1.5 for stage 1 pokemon:
			if (pokemon.getCardType() == CardType.STAGE1POKEMON)
				energyValue = energyValue * 1.5f;
			// *2 for stage 2 pokemon:
			if (pokemon.getCardType() == CardType.STAGE2POKEMON)
				energyValue = energyValue * 2f;
			// *2 for active pokemon:
			if (PositionID.isActivePosition(pos))
				energyValue = energyValue * 2f;

			value = value + energyValue;
		}

		// Gain 4 points, if you own the current stadium card:
		if (!gameModel.getPosition(PositionID.STADIUM).isEmpty() && gameModel.getPosition(PositionID.STADIUM).getColor() == color)
			value = value + 4;

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
}
