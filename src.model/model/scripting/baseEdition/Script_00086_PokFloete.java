package model.scripting.baseEdition;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00086_PokFloete extends TrainerCardScript {

	public Script_00086_PokFloete(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the enemy players bench is not full and there is at least one basic pokemon in the enemy players discard pile.
		if (gameModel.getFullBenchPositions(getEnemyPlayer().getColor()).size() < 5)
			if (gameModel.getAttackCondition().positionHasBasicPokemon(enemyDiscardPile()))
				return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		List<Card> basicPokemon = gameModel.getBasicPokemonOnPosition(enemyDiscardPile());

		PokemonCard chosenCard = (PokemonCard) player.playerChoosesCards(basicPokemon, 1, true, "Choose a pokemon to revive for " + enemy.getName() + "!").get(0);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " revives " + chosenCard.getName(), chosenCard);

		// Put on bench:
		gameModel.getAttackAction().putBasicPokemonOnBench(enemy, chosenCard);

		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());
	}

	private PositionID enemyDiscardPile() {
		Player enemy = this.getEnemyPlayer();
		if (enemy.getColor() == Color.BLUE)
			return PositionID.BLUE_DISCARDPILE;
		else
			return PositionID.RED_DISCARDPILE;
	}
}
