package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00373_GoodManners extends TrainerCardScript {

	public Script_00373_GoodManners(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		Position ownHand = gameModel.getPosition(ownHand());
		for (Card c : ownHand.getPokemonCards())
			if (c.getCardType() == CardType.BASICPOKEMON)
				return null;
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		this.playerRevealsHand(getCardOwner());

		List<Card> cards = this.getBasicPokemonCardsFromDeck();
		if (cards.isEmpty())
			this.gameModel.sendTextMessageToAllPlayers("Deck does not contain correct basic pokemon cards!", "");
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

	private List<Card> getBasicPokemonCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if (pCard.getCardType() == CardType.BASICPOKEMON)
				cardList.add(c);
		}
		return cardList;
	}

	private void playerRevealsHand(Player player) {
		Player opponent = null;
		Position handPosition = null;
		if (player.getColor() == Color.BLUE) {
			opponent = gameModel.getPlayerRed();
			handPosition = gameModel.getPosition(PositionID.BLUE_HAND);
		} else {
			opponent = gameModel.getPlayerBlue();
			handPosition = gameModel.getPosition(PositionID.RED_HAND);
		}

		opponent.playerChoosesCards(handPosition.getCards(), 12, false, "Look at your opponents hand...");
	}
}
