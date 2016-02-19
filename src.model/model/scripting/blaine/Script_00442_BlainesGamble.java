package model.scripting.blaine;

import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00442_BlainesGamble extends TrainerCardScript {

	public Script_00442_BlainesGamble(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		Position ownHand = gameModel.getPosition(ownHand());
		if (ownHand.size() >= 2)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		Position ownHand = gameModel.getPosition(ownHand());
		List<Card> chosenCards = getCardOwner().playerChoosesCards(ownHand.getCards(), ownHand.size(), false, "Discard any number of cards from your hand!");
		int number = chosenCards.size();

		for (Card c : chosenCards) {
			gameModel.getAttackAction().discardCardToDiscardPile(ownHand(), c.getGameID(), true);
		}
		gameModel.sendGameModelToAllPlayers("");

		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws " + number + " cards!", "");
			gameModel.getAttackAction().playerDrawsCards(2, getCardOwner());
		}
	}
}
