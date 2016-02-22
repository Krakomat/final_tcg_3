package model.scripting.teamRocket;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00469_TrashExchange extends TrainerCardScript {

	public Script_00469_TrashExchange(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		int discardPileNr = gameModel.getPosition(ownDiscardPile()).size();
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " puts his discard pile onto his deck!", "");
		for (int i = 0; i < discardPileNr; i++) {
			int cardID = gameModel.getPosition(ownDiscardPile()).getTopCard().getGameID();
			String id = gameModel.getPosition(ownDiscardPile()).getTopCard().getCardId();
			gameModel.getAttackAction().moveCard(ownDiscardPile(), ownDeck(), cardID, true);

			Animation animation = new CardMoveAnimation(ownDiscardPile(), ownDeck(), id, "");
			gameModel.sendAnimationToAllPlayers(animation);

			gameModel.sendGameModelToAllPlayers("");
		}
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", "");
		gameModel.getPosition(ownDeck()).shuffle();
		gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);

		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " discards " + discardPileNr + " from his deck!", "");
		for (int i = 0; i < discardPileNr; i++) {
			int cardID = gameModel.getPosition(ownDeck()).getTopCard().getGameID();
			String id = gameModel.getPosition(ownDeck()).getTopCard().getCardId();
			gameModel.getAttackAction().moveCard(ownDeck(), ownDiscardPile(), cardID, true);

			Animation animation = new CardMoveAnimation(ownDeck(), ownDiscardPile(), id, "");
			gameModel.sendAnimationToAllPlayers(animation);

			gameModel.sendGameModelToAllPlayers("");
		}

		// Discard trainer card after drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");
	}
}
