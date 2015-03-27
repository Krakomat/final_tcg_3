package model.scripting.baseEdition;

import network.client.Player;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00080_Defender extends TrainerCardScript {

	private int endTurnActionCounter;
	private PositionID chosenPosition;

	public Script_00080_Defender(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
		this.endTurnActionCounter = -1;
		this.chosenPosition = null;
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can always be played
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		chosenPosition = player.playerChoosesPositions(gameModel.getFullArenaPositions(player.getColor()), 1, true, "Choose a pokemon to attach defender!").get(0);

		// Move defender to active position:
		this.gameModel.getAttackAction().moveCard(card.getCurrentPosition().getPositionID(), chosenPosition, card.getGameID(), false);
		// Add powerup condition for pokemon:
		gameModel.getAttackAction().inflictConditionToPosition(chosenPosition, PokemonCondition.HARDEN20);
		// Set endTurnActionCounter:
		this.endTurnActionCounter = 2;
	}

	public void executeEndTurnActions() {
		// Discard defender if neccessary:
		if (endTurnActionCounter > -1) {
			endTurnActionCounter--;
			if (endTurnActionCounter == 0) {
				// Discard defender:
				gameModel.getAttackAction().discardCardToDiscardPile(chosenPosition, card.getGameID());

				// Set endTurnActionCounter:
				this.endTurnActionCounter = -1;
				this.chosenPosition = null;

				gameModel.sendGameModelToAllPlayers();
			}
		}
	}
}
