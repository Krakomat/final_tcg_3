package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00093_Windhauch extends TrainerCardScript {

	public Script_00093_Windhauch(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the enemy player has pokemon on his bench
		Player enemy = this.getEnemyPlayer();
		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		Color color = enemy.getColor();
		PositionID activePos = color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
		PositionID chosenBenchPos = player.playerChoosesPositions(gameModel.getFullBenchPositions(color), 1, true,
				"Choose a new active pokemon for " + enemy.getName() + "!").get(0);

		// Message clients:
		Card active = gameModel.getPosition(activePos).getTopCard();
		Card bench = gameModel.getPosition(chosenBenchPos).getTopCard();
		List<Card> cardList = new ArrayList<>();
		cardList.add(active);
		cardList.add(bench);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " swaps " + active.getName() + " with " + bench.getName() + "!", cardList, "");

		// Execute swap:
		gameModel.getAttackAction().swapPokemon(chosenBenchPos, activePos);
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());
	}
}
