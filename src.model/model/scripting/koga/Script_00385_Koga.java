package model.scripting.koga;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00385_Koga extends TrainerCardScript {

	public Script_00385_Koga(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);

		gameModel.getGameModelParameters().setActivated_00385_Koga(true);
		gameModel.sendGameModelToAllPlayers("");
	}

	public void executeEndTurnActions() {
		gameModel.getGameModelParameters().setActivated_00385_Koga(false);
	}
}
