package model.scripting.blaine;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00458_Fervor extends TrainerCardScript {

	public Script_00458_Fervor(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own deck contains at least 3 cards:
		Position ownDeck = gameModel.getPosition(ownDeck());
		if (ownDeck.size() >= 3)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		for (int i = 0; i < 3; i++) {
			Card topCard = gameModel.getPosition(ownDeck()).getTopCard();

			// Move card:
			if (topCard.getCardId().equals("00098")) {
				gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " moves " + topCard.getName() + " to his hand!", topCard, "");
				gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), topCard.getGameID(), true);

				// Execute animation:
				Animation animation = new CardMoveAnimation(ownDeck(), ownHand(), topCard.getCardId(), "");
				gameModel.sendAnimationToAllPlayers(animation);
			} else {
				gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " discards " + topCard.getName() + "!", topCard, "");
				gameModel.getAttackAction().moveCard(ownDeck(), ownDiscardPile(), topCard.getGameID(), true);

				// Execute animation:
				Animation animation = new CardMoveAnimation(ownDeck(), ownDiscardPile(), topCard.getCardId(), "");
				gameModel.sendAnimationToAllPlayers(animation);
			}

			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
