package model.scripting.baseEdition;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00088_ProfEich extends TrainerCardScript {

	public Script_00088_ProfEich(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own deck contains at least 7 cards:
		Position ownDeck = gameModel.getPosition(ownDeck());
		if (ownDeck.size() >= 7)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " discards all cards!", "");
		// Discard whole hand:
		gameModel.getAttackAction().playerDiscardsAllCards(getCardOwner());
		gameModel.sendGameModelToAllPlayers("");

		// Draw 7 cards:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName()  + " draws 7 cards!", "");
		gameModel.getAttackAction().playerDrawsCards(7, getCardOwner());
	}
}
