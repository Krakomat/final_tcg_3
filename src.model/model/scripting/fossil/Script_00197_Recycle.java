package model.scripting.fossil;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00197_Recycle extends TrainerCardScript {

	public Script_00197_Recycle(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		if (gameModel.getPosition(ownDiscardPile()).getCards().isEmpty())
			return null;

		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		gameModel.sendTextMessageToAllPlayers("If heads then " + this.card.getName() + "'s effects will be executed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c, "");
		if (c == Coin.HEADS) {
			// Choose a card from the discard pile:
			List<Card> cards = gameModel.getPosition(ownDiscardPile()).getCards();
			Card chosenCard = player.playerChoosesCards(cards, 1, true, "Choose a pokemon card from your discard pile!").get(0);

			// Message clients:
			gameModel.sendCardMessageToAllPlayers(player.getName() + " puts " + chosenCard.getName() + " on his deck!", chosenCard, "");

			// Move card:
			gameModel.getAttackAction().moveCard(ownDiscardPile(), ownDeck(), chosenCard.getGameID(), true);

			// Discard trainer card:
			gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());

			gameModel.sendGameModelToAllPlayers("");
		}
		gameModel.sendTextMessageToAllPlayers("Recycle was not successful!", "");
	}

	private PositionID ownDiscardPile() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DISCARDPILE;
		else
			return PositionID.RED_DISCARDPILE;
	}

	private PositionID ownDeck() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DECK;
		else
			return PositionID.RED_DECK;
	}
}