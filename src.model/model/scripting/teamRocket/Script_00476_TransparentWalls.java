package model.scripting.teamRocket;

import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00476_TransparentWalls extends TrainerCardScript {

	public Script_00476_TransparentWalls(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		gameModel.getGameModelParameters().addEffectParameter(this.card.getCardId(), this.cardGameID(), getCardOwner().getColor() == Color.BLUE ? 0 : 1);
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");
	}

	public void executePreTurnActions(Player playerOnTurn) {
		if ((playerOnTurn.getColor() == this.getCardOwner().getColor()
				&& gameModel.getGameModelParameters().getValueForEffectParameterKeyPair(this.card.getCardId(), this.cardGameID()) == 0 && getCardOwner().getColor() == Color.BLUE)
				|| (playerOnTurn.getColor() == this.getCardOwner().getColor()
						&& gameModel.getGameModelParameters().getValueForEffectParameterKeyPair(this.card.getCardId(), this.cardGameID()) == 1
						&& getCardOwner().getColor() == Color.RED)) {
			gameModel.getGameModelParameters().removeEffectParameter(this.card.getCardId(), this.cardGameID());
		}
	}
}
