package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00285_BrocksTrainingMethod extends TrainerCardScript {

	public Script_00285_BrocksTrainingMethod(TrainerCard card, PokemonGame gameModel) {
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

		List<Card> cards = this.getBrockCardsFromDeck();
		if (cards.isEmpty())
			this.gameModel.sendTextMessageToAllPlayers("Deck does not contain correct pokemon cards!", "");
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

	private List<Card> getBrockCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if ((pCard.getCardType() == CardType.BASICPOKEMON || pCard.getCardType() == CardType.STAGE1POKEMON || pCard.getCardType() == CardType.STAGE2POKEMON)
					&& (pCard.getName().contains("Brock")))
				cardList.add(c);
		}
		return cardList;
	}
}
