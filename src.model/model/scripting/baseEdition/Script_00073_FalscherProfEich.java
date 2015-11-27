package model.scripting.baseEdition;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00073_FalscherProfEich extends TrainerCardScript {

	public Script_00073_FalscherProfEich(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the enemy deck contains at least 7 cards:
		Position enemy = gameModel.getPosition(enemyDeck());
		if (enemy.size() >= 7)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " discards all cards!", "");
		// Discard whole hand:
		gameModel.getAttackAction().playerDiscardsAllCards(getEnemyPlayer());
		gameModel.sendGameModelToAllPlayers("");

		// Draw 7 cards:
		gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " draws 7 cards!", "");
		gameModel.getAttackAction().playerDrawsCards(7, getEnemyPlayer());
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());
	}
}
