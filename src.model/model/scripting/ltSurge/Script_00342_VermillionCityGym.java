package model.scripting.ltSurge;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00342_VermillionCityGym extends TrainerCardScript {

	public Script_00342_VermillionCityGym(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		if (!gameModel.getPosition(PositionID.STADIUM).isEmpty()) {
			Card stadium = gameModel.getPosition(PositionID.STADIUM).getTopCard();
			// Discard previous stadium card:
			gameModel.getAttackAction().discardCardToDiscardPile(PositionID.STADIUM, stadium.getGameID(), false);
		}
		gameModel.getPosition(PositionID.STADIUM).setColor(this.getCardOwner().getColor());
		gameModel.getAttackAction().moveCard(getCurrentPositionID(), PositionID.STADIUM, this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");
	}
}