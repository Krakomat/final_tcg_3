package model.scripting.baseEdition;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PokemonCondition;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00084_Pluspower extends TrainerCardScript {

	private int endTurnActionCounter;

	public Script_00084_Pluspower(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
		this.endTurnActionCounter = -1;
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can always be played
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Move pluspower to active position:
		this.gameModel.getAttackAction().moveCard(card.getCurrentPosition().getPositionID(), ownActive(), card.getGameID(), false);
		// Add powerup condition for pokemon:
		gameModel.getAttackAction().inflictConditionToPosition(ownActive(), PokemonCondition.DAMAGEINCREASE10);
		// Set endTurnActionCounter:
		this.endTurnActionCounter = 1;
	}

	public void executeEndTurnActions() {
		// Discard pluspower if neccessary:
		if (endTurnActionCounter > -1) {
			endTurnActionCounter--;
			if (endTurnActionCounter == 0) {
				// Discard pluspower:
				gameModel.getAttackAction().discardCardToDiscardPile(ownActive(), card.getGameID());

				// Set endTurnActionCounter:
				this.endTurnActionCounter = -1;

				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}
}
