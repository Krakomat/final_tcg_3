package model.game;

import java.util.List;

import network.client.Player;
import model.database.DynamicPokemonCondition;
import model.database.PokemonCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;

public class AttackCondition {

	private PokemonGame gameModel;

	/**
	 * Constructor
	 * 
	 * @param gameModel
	 *            this has to be a {@link PokemonGameModel}, which can be manipulated by this class.
	 */
	public AttackCondition(PokemonGame gameModel) {
		super();
		this.gameModel = gameModel;
	}

	public boolean hasEvolutionPokemonOnField(Player player) {
		if (player.getColor() == Color.BLUE) {
			if (!gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			if (!gameModel.getPosition(PositionID.BLUE_BENCH_1).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_1).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.BLUE_BENCH_1).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			if (!gameModel.getPosition(PositionID.BLUE_BENCH_2).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_2).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.BLUE_BENCH_2).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			if (!gameModel.getPosition(PositionID.BLUE_BENCH_3).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_3).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.BLUE_BENCH_3).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			if (!gameModel.getPosition(PositionID.BLUE_BENCH_4).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_4).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.BLUE_BENCH_4).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			if (!gameModel.getPosition(PositionID.BLUE_BENCH_5).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.BLUE_BENCH_5).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.BLUE_BENCH_5).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			return false;
		} else {
			if (!gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			if (!gameModel.getPosition(PositionID.RED_BENCH_1).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_1).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.RED_BENCH_1).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			if (!gameModel.getPosition(PositionID.RED_BENCH_2).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_2).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.RED_BENCH_2).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			if (!gameModel.getPosition(PositionID.RED_BENCH_3).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_3).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.RED_BENCH_3).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			if (!gameModel.getPosition(PositionID.RED_BENCH_4).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_4).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.RED_BENCH_4).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			if (!gameModel.getPosition(PositionID.RED_BENCH_5).isEmpty()
					&& (((PokemonCard) gameModel.getPosition(PositionID.RED_BENCH_5).getTopCard()).getCardType().equals(CardType.STAGE1POKEMON) || ((PokemonCard) gameModel
							.getPosition(PositionID.RED_BENCH_5).getTopCard()).getCardType().equals(CardType.STAGE2POKEMON)))
				return true;
			return false;
		}
	}

	public boolean pokemonHasCondition(PositionID position, PokemonCondition condition) {
		if (gameModel.getPosition(position).isEmpty())
			return false;
		List<DynamicPokemonCondition> conditionList = ((PokemonCard) gameModel.getPosition(position).getTopCard()).getConditions();
		for (int i = 0; i < conditionList.size(); i++) {
			PokemonCondition pCondition = conditionList.get(i).getCondition();
			if (pCondition.equals(condition))
				return true;
		}
		return false;
	}

	/**
	 * Returns true, if the given position contains any basic pokemon. Also true if an evolved arena position contains a basic pokemon.
	 * 
	 * @param posID
	 * @return
	 */
	public boolean positionHasBasicPokemon(PositionID posID) {
		Position pos = gameModel.getPosition(posID);
		for (int i = 0; i < pos.size(); i++)
			if (pos.getCardAtIndex(i).getCardType().equals(CardType.BASICPOKEMON))
				return true;
		return false;
	}

	/**
	 * Returns true, if the given pokemon is in the arena (active or bench position). Returns false otherwise.
	 * 
	 * @param pCard
	 * @return
	 */
	public boolean pokemonIsInPlay(PokemonCard pCard) {
		PositionID posID = pCard.getCurrentPosition().getPositionID();
		if (posID == PositionID.BLUE_ACTIVEPOKEMON || posID == PositionID.BLUE_BENCH_1 || posID == PositionID.BLUE_BENCH_2 || posID == PositionID.BLUE_BENCH_3
				|| posID == PositionID.BLUE_BENCH_4 || posID == PositionID.BLUE_BENCH_5 || posID == PositionID.RED_ACTIVEPOKEMON || posID == PositionID.RED_BENCH_1
				|| posID == PositionID.RED_BENCH_2 || posID == PositionID.RED_BENCH_3 || posID == PositionID.RED_BENCH_4 || posID == PositionID.RED_BENCH_5)
			return true;
		return false;
	}
}
