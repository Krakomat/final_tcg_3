package model.scripting.ltSurge;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00340_LtSurgesTreaty extends TrainerCardScript {

	public Script_00340_LtSurgesTreaty(TrainerCard card, PokemonGame gameModel) {
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

		boolean answer = getEnemyPlayer().playerDecidesYesOrNo("Yes: Everyone gets a prize card. No: " + getCardOwner().getName() + " draws a card.");

		if (answer) {
			gameModel.playerTakesPrize(getCardOwner().getColor(), 1);
			gameModel.playerTakesPrize(getEnemyPlayer().getColor(), 1);
			gameModel.sendGameModelToAllPlayers("");
			gameModel.cleanDefeatedPositions(); // Checks for loser
		} else {
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws 2 cards!", "");
			gameModel.getAttackAction().playerDrawsCards(2, getCardOwner());
		}
	}
}
