package model.scripting.erika;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00354_Erika extends TrainerCardScript {

	public Script_00354_Erika(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		boolean done = false;
		for (int i = 0; i < 3 && !done; i++) {
			boolean answer = getCardOwner().playerDecidesYesOrNo("Do you want to draw a card?");
			if (answer)
				gameModel.getAttackAction().playerDrawsCards(1, getCardOwner());
			else
				done = true;
		}

		done = false;
		for (int i = 0; i < 3 && !done; i++) {
			boolean answer = getEnemyPlayer().playerDecidesYesOrNo("Do you want to draw a card?");
			if (answer)
				gameModel.getAttackAction().playerDrawsCards(1, getEnemyPlayer());
			else
				done = true;
		}
	}
}
