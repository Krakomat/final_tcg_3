package model.scripting.rocket;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00260_NightlyGarbageRun extends TrainerCardScript {

	public Script_00260_NightlyGarbageRun(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		if (gameModel.getPosition(ownDiscardPile()).getPokemonCards().size() + gameModel.getPosition(ownDiscardPile()).getBasicEnergyCards().size() == 0)
			return null;
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());

		if (gameModel.getPosition(ownDiscardPile()).getPokemonCards().size() > 0) {
			Player player = this.getCardOwner();
			List<Card> chooseCards = new ArrayList<>();
			for (Card c : gameModel.getPosition(ownDiscardPile()).getBasicEnergyCards())
				chooseCards.add(c);
			for (Card c : gameModel.getPosition(ownDiscardPile()).getPokemonCards())
				chooseCards.add(c);

			List<Card> cards = player.playerChoosesCards(chooseCards, 3, false, "Choose up to 3 pokemon cards or basic energy cards from your discard pile!");

			if (!cards.isEmpty()) {
				for (Card card : cards) {
					gameModel.sendCardMessageToAllPlayers(player.getName() + " shuffles " + card.getName() + " from his discard pile into his deck!", card, "");
					gameModel.getAttackAction().moveCard(ownDiscardPile(), ownDeck(), card.getGameID(), true);

					// Execute animation:
					Animation animation = new CardMoveAnimation(ownDiscardPile(), ownDeck(), card.getCardId(), "");
					gameModel.sendAnimationToAllPlayers(animation);
					gameModel.getAttackAction().shufflePosition(ownDeck());
					gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);
				}
			}
		}
	}
}
