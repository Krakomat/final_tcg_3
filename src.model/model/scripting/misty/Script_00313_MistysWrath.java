package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00313_MistysWrath extends TrainerCardScript {

	public Script_00313_MistysWrath(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own deck contains at least 7 cards:
		Position ownDeck = gameModel.getPosition(ownDeck());
		if (ownDeck.size() >= 7)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		List<Card> topCards = new ArrayList<>();
		for (int i = 0; i < 7; i++)
			topCards.add(gameModel.getPosition(ownDeck()).removeTopCard());
		List<Card> chosenCards = getCardOwner().playerChoosesCards(topCards, 2, true, "Choose two cards to move to your hand!");
		for (Card c : topCards) {
			gameModel.getAttackAction().moveCard(null, ownDiscardPile(), c.getGameID(), true);
		}
		for (Card c : chosenCards) {
			gameModel.getAttackAction().moveCard(ownDiscardPile(), ownHand(), c.getGameID(), true);
		}
		gameModel.sendGameModelToAllPlayers("");
	}
}
