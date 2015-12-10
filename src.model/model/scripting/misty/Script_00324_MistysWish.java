package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00324_MistysWish extends TrainerCardScript {

	public Script_00324_MistysWish(TrainerCard card, PokemonGame gameModel) {
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
		// Discard trainer card before choosing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());

		// Choose prize to look at:
		PositionID prizePos = getCardOwner().playerChoosesPositions(getFullPrizePositions(), 1, true, "Choose a prize position!").get(0);

		// Look at card:
		List<Card> cardList = new ArrayList<>();
		Card prizeCard = gameModel.getPosition(prizePos).getTopCard();
		cardList.add(prizeCard);
		getCardOwner().playerChoosesCards(cardList, 1, false, "Look at the prize card!");

		// Ask enemy for swap:
		boolean switchCards = getEnemyPlayer().playerDecidesYesOrNo("May " + getCardOwner().getName() + " switch the card on " + prizePos + " with one of his hand cards?");

		if (switchCards) {
			gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " accepts the offer!", "");

			Card handCard = getCardOwner().playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), 1, true, "Choose a hand card to swap out!").get(0);

			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " switches cards!", "");
			gameModel.getAttackAction().moveCard(prizePos, ownHand(), prizeCard.getGameID(), true);
			gameModel.getAttackAction().moveCard(ownHand(), prizePos, handCard.getGameID(), true);
		} else {
			gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " declined the offer!", "");
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws 1 card!", "");
			gameModel.getAttackAction().playerDrawsCards(1, getCardOwner());
		}
		gameModel.sendGameModelToAllPlayers("");
	}

	public List<PositionID> getFullPrizePositions() {
		List<PositionID> prizeList = new ArrayList<>();
		for (int i = 1; i <= 6; i++) {
			PositionID posID = PositionID.getPrizePosition(getCardOwner().getColor(), i);
			prizeList.add(posID);
		}
		return prizeList;
	}
}
