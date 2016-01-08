package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00371_ErikasMaids extends TrainerCardScript {

	public Script_00371_ErikasMaids(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own hand contains at least 3 cards:
		Position ownHand = gameModel.getPosition(ownHand());
		if (ownHand.size() >= 3)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		Player player = this.getCardOwner();
		// Choose two own cards:
		List<Card> chosenCards = player.playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), 2, true, "Choose 2 cards to discard!");
		for (Card chosenCard : chosenCards) {
			gameModel.sendCardMessageToAllPlayers(player.getName() + " discards " + chosenCard.getName() + "!", chosenCard, "");
			gameModel.getAttackAction().discardCardToDiscardPile(ownHand(), chosenCard.getGameID(), true);
		}

		// Choose up to two cards from the deck:
		List<Card> cards = getErikaCardsFromDeck();
		if (cards.isEmpty()) {
			List<Card> erikaCards = player.playerChoosesCards(cards, 2, false, "Choose up to two cards from your deck!");
			for (Card c : erikaCards) {
				// Message clients:
				gameModel.sendCardMessageToAllPlayers(player.getName() + " takes " + c.getName() + " in his hand!", c, "");
				// Move card:
				gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), c.getGameID(), true);
				gameModel.sendGameModelToAllPlayers("");
			}
		} else
			gameModel.sendTextMessageToAllPlayers("Deck does not contain any cards with 'Erika' in their names!", "");

		// Shuffle deck:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getAttackAction().shufflePosition(ownDeck());

	}

	private List<Card> getErikaCardsFromDeck() {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(ownDeck()).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if ((pCard.getCardType() == CardType.BASICPOKEMON || pCard.getCardType() == CardType.STAGE1POKEMON || pCard.getCardType() == CardType.STAGE2POKEMON)
					&& (pCard.getName().contains("Erika")))
				cardList.add(c);
		}
		return cardList;
	}
}
