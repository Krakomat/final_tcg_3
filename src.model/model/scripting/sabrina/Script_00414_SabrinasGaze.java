package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00414_SabrinasGaze extends TrainerCardScript {

	public Script_00414_SabrinasGaze(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);

		int enemyHand = gameModel.getPosition(enemyHand()).size();
		int ownHand = gameModel.getPosition(ownHand()).size();

		gameModel.sendTextMessageToAllPlayers("Both players shuffles their hands into their decks!", "");
		List<Card> list = gameModel.getPosition(enemyHand()).getCards();
		List<Card> cardList = new ArrayList<>();
		for (Card c : list)
			cardList.add(c);
		for (Card c : cardList)
			gameModel.getAttackAction().moveCard(enemyHand(), enemyDeck(), c.getGameID(), true);
		gameModel.getAttackAction().shufflePosition(enemyDeck());

		list = gameModel.getPosition(ownHand()).getCards();
		cardList = new ArrayList<>();
		for (Card c : list)
			cardList.add(c);
		for (Card c : cardList)
			gameModel.getAttackAction().moveCard(ownHand(), ownDeck(), c.getGameID(), true);
		gameModel.getAttackAction().shufflePosition(ownDeck());

		gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);

		// Draw cards:
		if (ownHand > 0) {
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws " + ownHand + " cards!", "");
			gameModel.getAttackAction().playerDrawsCards(ownHand, getCardOwner());
		}
		if (enemyHand > 0) {
			gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " draws " + enemyHand + " cards!", "");
			gameModel.getAttackAction().playerDrawsCards(enemyHand, getEnemyPlayer());
		}
	}
}
