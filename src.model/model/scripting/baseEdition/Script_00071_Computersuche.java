package model.scripting.baseEdition;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00071_Computersuche extends TrainerCardScript {

	public Script_00071_Computersuche(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own hand contains at least 3 cards(including this card) and the deck contains at least one card:
		Position ownHand = this.card.getCurrentPosition();
		if (ownHand.size() >= 3 && gameModel.getPosition(ownDeck()).size() > 0)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();

		// Discard trainer card before choosing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());

		// Choose two own cards:
		List<Card> chosenCards = player.playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), 2, true, "Choose 2 cards to discard!");
		for (Card chosenCard : chosenCards) {
			gameModel.sendCardMessageToAllPlayers(player.getName() + " discards " + chosenCard.getName() + "!", chosenCard, "");
			gameModel.getAttackAction().discardCardToDiscardPile(ownHand(), chosenCard.getGameID());
		}

		// Choose a card from the deck:
		List<Card> cards = gameModel.getPosition(ownDeck()).getCards();
		Card chosenCard = player.playerChoosesCards(cards, 1, true, "Choose a card from your deck!").get(0);
		// Message clients:
		gameModel.sendTextMessageToAllPlayers(player.getName() + " got a card of his choice from his deck!", "");
		// Move card:
		gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), chosenCard.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		// Shuffle deck:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getAttackAction().shufflePosition(ownDeck());
	}
}
