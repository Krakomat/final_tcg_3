package model.scripting.baseEdition;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00091_Bill extends TrainerCardScript {

	public Script_00091_Bill(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own deck contains at least 2 cards:
		Position ownDeck = gameModel.getPosition(ownDeck());
		if (ownDeck.size() >= 2)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws 2 cards!", "");
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");
		gameModel.getAttackAction().playerDrawsCards(2, getCardOwner());
	}
}
