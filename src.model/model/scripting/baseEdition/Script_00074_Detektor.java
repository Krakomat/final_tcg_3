package model.scripting.baseEdition;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00074_Detektor extends TrainerCardScript {

	public Script_00074_Detektor(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own hand contains at least 3 cards(including this card) and the discardpile contains at least one trainer card:
		Position ownHand = this.card.getCurrentPosition();
		if (ownHand.size() >= 3 && gameModel.getPosition(ownDiscardPile()).getTrainerCards().size() > 0)
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

		// Choose a trainer card:
		List<Card> trainerCards = gameModel.getPosition(ownDiscardPile()).getTrainerCards();
		Card chosenCard = player.playerChoosesCards(trainerCards, 1, true, "Choose a trainer card to recover!").get(0);
		// Message clients:
		gameModel.sendCardMessageToAllPlayers(player.getName() + " recovers " + chosenCard.getName() + " from his discard pile!", chosenCard, "");
		// Move trainer card:
		gameModel.getAttackAction().moveCard(ownDiscardPile(), ownHand(), chosenCard.getGameID(), true);
	}
}
