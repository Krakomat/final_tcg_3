package model.scripting.blaine;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00441_BlainesLastResort extends TrainerCardScript {

	public Script_00441_BlainesLastResort(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		Position ownHand = gameModel.getPosition(ownHand());
		if (ownHand.size() == 1)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws 5 cards!", "");
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");
		gameModel.getAttackAction().playerDrawsCards(5, getCardOwner());
	}
}
