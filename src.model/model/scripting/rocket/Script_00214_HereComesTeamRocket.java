package model.scripting.rocket;

import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00214_HereComesTeamRocket extends TrainerCardScript {

	public Script_00214_HereComesTeamRocket(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());

		this.setPrizeVisible(PositionID.BLUE_PRICE_1);
		this.setPrizeVisible(PositionID.BLUE_PRICE_2);
		this.setPrizeVisible(PositionID.BLUE_PRICE_3);
		this.setPrizeVisible(PositionID.BLUE_PRICE_4);
		this.setPrizeVisible(PositionID.BLUE_PRICE_5);
		this.setPrizeVisible(PositionID.BLUE_PRICE_6);

		this.setPrizeVisible(PositionID.RED_PRICE_1);
		this.setPrizeVisible(PositionID.RED_PRICE_2);
		this.setPrizeVisible(PositionID.RED_PRICE_3);
		this.setPrizeVisible(PositionID.RED_PRICE_4);
		this.setPrizeVisible(PositionID.RED_PRICE_5);
		this.setPrizeVisible(PositionID.RED_PRICE_6);
	}

	private void setPrizeVisible(PositionID posID) {
		if (!gameModel.getPosition(posID).isEmpty()) {
			gameModel.getPosition(posID).setVisible(true, Color.BLUE);
			gameModel.getPosition(posID).setVisible(true, Color.RED);
		}
	}
}
