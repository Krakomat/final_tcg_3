package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00325_MistysTears extends TrainerCardScript {

	public Script_00325_MistysTears(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own hand contains at least 2 cards:
		Position ownDeck = gameModel.getPosition(ownHand());
		if (ownDeck.size() >= 2)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());

		Player player = this.getCardOwner();

		Card chosenCard = player.playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), 1, true, "Discard a card from your hand!").get(0);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " discards " + chosenCard.getName() + "!", chosenCard, "");
		gameModel.getAttackAction().discardCardToDiscardPile(ownHand(), chosenCard.getGameID());
		gameModel.sendGameModelToAllPlayers("");

		List<Card> cards = this.getWaterCardsFromDeck();
		if (cards.isEmpty())
			this.gameModel.sendTextMessageToAllPlayers("Deck does not contain water energy cards!", "");
		else {
			List<Card> cardList = this.getCardOwner().playerChoosesCards(cards, 2, false, "Choose up to 2 cards for your hand!");
			for (Card c : cardList) {
				gameModel.sendCardMessageToAllPlayers(this.getCardOwner().getName() + " puts " + c.getName() + " in his hand!", c, "");
				gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), c.getGameID(), true);

				// Execute animation:
				Animation animation = new CardMoveAnimation(ownDeck(), ownHand(), c.getCardId(), "");
				gameModel.sendAnimationToAllPlayers(animation);
				gameModel.sendGameModelToAllPlayers("");
			}
		}

		gameModel.getAttackAction().shufflePosition(ownDeck());
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<Card> getWaterCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			if (c.getCardId().equals("00102"))
				cardList.add(c);
		}
		return cardList;
	}
}
