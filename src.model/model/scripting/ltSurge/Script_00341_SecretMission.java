package model.scripting.ltSurge;

import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00341_SecretMission extends TrainerCardScript {

	public Script_00341_SecretMission(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);

		getCardOwner().playerChoosesCards(gameModel.getPosition(enemyHand()).getCards(), 0, false, "Look at your opponents hand!");
		List<Card> discardedCards = getCardOwner().playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), gameModel.getPosition(ownHand()).getCards().size(), false,
				"Choose cards to discard!");
		int size = discardedCards.size();

		for (Card c : discardedCards) {
			gameModel.getAttackAction().discardCardToDiscardPile(ownHand(), c.getGameID(), true);
		}

		// Draw:
		gameModel.getAttackAction().playerDrawsCards(size, getCardOwner());
		gameModel.sendGameModelToAllPlayers("");
	}
}
