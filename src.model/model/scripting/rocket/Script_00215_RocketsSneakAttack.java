package model.scripting.rocket;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00215_RocketsSneakAttack extends TrainerCardScript {

	public Script_00215_RocketsSneakAttack(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		
		this.playerRevealsHand(getEnemyPlayer());

		List<Card> cards = gameModel.getPosition(enemyHand()).getTrainerCards();
		if (cards.isEmpty())
			this.gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + "'s hand does not contain trainer cards!", "");
		else {
			Card c = this.getCardOwner().playerChoosesCards(cards, 1, true, "Choose a trainer card for your opponent to discard!").get(0);
			gameModel.sendCardMessageToAllPlayers(getEnemyPlayer().getName() + " discards " + c.getName() + "!", c, "");
			gameModel.getAttackAction().discardCardToDiscardPile(enemyHand(), c.getGameID(), true);
		}
		gameModel.sendGameModelToAllPlayers("");
	}

	private void playerRevealsHand(Player player) {
		Player opponent = null;
		Position handPosition = null;
		if (player.getColor() == Color.BLUE) {
			opponent = gameModel.getPlayerRed();
			handPosition = gameModel.getPosition(PositionID.BLUE_HAND);
		} else {
			opponent = gameModel.getPlayerBlue();
			handPosition = gameModel.getPosition(PositionID.RED_HAND);
		}

		opponent.playerChoosesCards(handPosition.getCards(), 12, false, "Look at your opponents hand...");
	}
}
