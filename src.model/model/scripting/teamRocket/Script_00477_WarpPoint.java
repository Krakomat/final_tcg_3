package model.scripting.teamRocket;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00477_WarpPoint extends TrainerCardScript {

	public Script_00477_WarpPoint(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		Player player = this.getEnemyPlayer();
		Color color = player.getColor();

		if (gameModel.getFullBenchPositions(color).size() > 0) {
			PositionID activePos = color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
			PositionID chosenBenchPos = player.playerChoosesPositions(gameModel.getFullBenchPositions(color), 1, true, "Choose a new active pokemon!").get(0);

			// Message clients:
			Card active = gameModel.getPosition(activePos).getTopCard();
			Card bench = gameModel.getPosition(chosenBenchPos).getTopCard();
			List<Card> cardList = new ArrayList<>();
			cardList.add(active);
			cardList.add(bench);
			gameModel.sendCardMessageToAllPlayers(player.getName() + " swaps " + active.getName() + " with " + bench.getName() + "!", cardList, "");

			// Execute swap:
			gameModel.getAttackAction().swapPokemon(chosenBenchPos, activePos);
		}

		player = this.getCardOwner();
		color = player.getColor();
		if (gameModel.getFullBenchPositions(color).size() > 0) {
			PositionID activePos = color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
			PositionID chosenBenchPos = player.playerChoosesPositions(gameModel.getFullBenchPositions(color), 1, true, "Choose a new active pokemon!").get(0);

			// Message clients:
			Card active = gameModel.getPosition(activePos).getTopCard();
			Card bench = gameModel.getPosition(chosenBenchPos).getTopCard();
			List<Card> cardList = new ArrayList<>();
			cardList.add(active);
			cardList.add(bench);
			gameModel.sendCardMessageToAllPlayers(player.getName() + " swaps " + active.getName() + " with " + bench.getName() + "!", cardList, "");

			// Execute swap:
			gameModel.getAttackAction().swapPokemon(chosenBenchPos, activePos);
		}
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
	}
}
