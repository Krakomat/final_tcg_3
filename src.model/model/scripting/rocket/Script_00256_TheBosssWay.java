package model.scripting.rocket;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00256_TheBosssWay extends TrainerCardScript {

	public Script_00256_TheBosssWay(TrainerCard card, PokemonGame gameModel) {
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

		List<Card> cards = this.getDarkCardsFromDeck();
		if (cards.isEmpty())
			this.gameModel.sendTextMessageToAllPlayers("Deck does not contain correct evolution cards!", "");
		else {
			Card c = this.getCardOwner().playerChoosesCards(cards, 1, true, "Choose a card for your hand!").get(0);
			gameModel.sendCardMessageToAllPlayers(this.getCardOwner().getName() + " puts " + c.getName() + " in his hand!", c, "");
			gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), c.getGameID(), true);

			// Execute animation:
			Animation animation = new CardMoveAnimation(ownDeck(), ownHand(), c.getCardId(), "");
			gameModel.sendAnimationToAllPlayers(animation);
			gameModel.sendGameModelToAllPlayers("");
		}

		gameModel.getAttackAction().shufflePosition(ownDeck());
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<Card> getDarkCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if ((pCard.getCardType() == CardType.STAGE1POKEMON || pCard.getCardType() == CardType.STAGE2POKEMON) && (pCard.getName().contains("Dark")))
				cardList.add(c);
		}
		return cardList;
	}
}
