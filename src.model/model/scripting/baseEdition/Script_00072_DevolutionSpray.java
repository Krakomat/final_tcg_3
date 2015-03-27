package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00072_DevolutionSpray extends TrainerCardScript {

	public Script_00072_DevolutionSpray(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the card owner has an evolution pokemon on his side of the field:
		if (gameModel.getAttackCondition().hasEvolutionPokemonOnField(getCardOwner()))
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		PositionID chosenPosition = player.playerChoosesPositions(getEvolutionPokemonPositions(), 1, true, "Choose a pokemon do devolve!").get(0);

		// Devolve:
		PositionID discardPile = ownDiscardPile();

		PokemonCard topPokemon = (PokemonCard) gameModel.getPosition(chosenPosition).getTopCard();
		int damage = topPokemon.getDamageMarks();
		boolean devolveFinished = false;
		Card c = topPokemon;
		while (!devolveFinished) {
			if (c.getCardType().equals(CardType.STAGE1POKEMON) || c.getCardType().equals(CardType.STAGE2POKEMON)) {
				gameModel.getAttackAction().moveCard(chosenPosition, discardPile, c.getGameID(), true);
				c = gameModel.getPosition(chosenPosition).getTopCard();
			} else
				devolveFinished = true;
		}
		PokemonCard basicPokemon = (PokemonCard) gameModel.getPosition(chosenPosition).getTopCard();
		basicPokemon.setDamageMarks(damage);
		if (basicPokemon.getHitpoints() == basicPokemon.getDamageMarks())
			gameModel.getAttackAction().inflictConditionToPosition(chosenPosition, PokemonCondition.KNOCKOUT);
	}

	private PositionID ownDiscardPile() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DISCARDPILE;
		else
			return PositionID.RED_DISCARDPILE;
	}

	/**
	 * Collects all stage1/stage2 pokemon for the player.
	 * 
	 * @return
	 */
	private List<PositionID> getEvolutionPokemonPositions() {
		Player player = this.getCardOwner();
		List<PositionID> pokemonList = gameModel.getFullArenaPositions(player.getColor());
		List<PositionID> evolutionList = new ArrayList<>();

		for (PositionID posID : pokemonList) {
			PokemonCard pCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (pCard.getCardType() == CardType.STAGE1POKEMON || pCard.getCardType() == CardType.STAGE2POKEMON)
				evolutionList.add(posID);
		}

		return evolutionList;
	}
}
