package model.scripting.misty;

import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00296_Misty extends TrainerCardScript {

	public Script_00296_Misty(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own hand contains at least 3 cards(including this card):
		Position ownHand = this.card.getCurrentPosition();
		if (ownHand.size() >= 3)
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

		gameModel.getGameModelParameters().setActivated_00296_Misty(true);
		gameModel.sendGameModelToAllPlayers("");
	}

	public void executeEndTurnActions() {
		gameModel.getGameModelParameters().setActivated_00296_Misty(false);
	}
}
