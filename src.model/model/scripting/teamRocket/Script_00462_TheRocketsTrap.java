package model.scripting.teamRocket;

import java.security.SecureRandom;
import java.util.Random;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00462_TheRocketsTrap extends TrainerCardScript {

	public Script_00462_TheRocketsTrap(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			Random r = new SecureRandom();
			Position hand = gameModel.getPosition(enemyHand());
			int maxDiscard = hand.size() < 3 ? hand.size() : 3;
			for (int i = 0; i < maxDiscard; i++) {
				int index = r.nextInt(hand.size());
				Card c = hand.getCardAtIndex(index);
				gameModel.getAttackAction().moveCard(enemyHand(), enemyDeck(), c.getGameID(), true);
			}
			gameModel.getAttackAction().shufflePosition(enemyDeck());
			gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " shuffles " + maxDiscard + " cards into his deck!", "");
			gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);
		}
	}
}
