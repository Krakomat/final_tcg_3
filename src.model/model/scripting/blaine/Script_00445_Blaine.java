package model.scripting.blaine;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00445_Blaine extends TrainerCardScript {

	public Script_00445_Blaine(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before choosing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);

		if (gameModel.getGameModelParameters().getValueForEffectParameterKeyPair("00445", this.cardGameID()) == null) {
			gameModel.getGameModelParameters().addEffectParameter("00445", this.cardGameID(), 0);
		}
		gameModel.sendGameModelToAllPlayers("");
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().getValueForEffectParameterKeyPair("00445", this.cardGameID()) != null) {
			gameModel.getGameModelParameters().removeEffectParameter("00445", this.cardGameID());
		}
	}
}
