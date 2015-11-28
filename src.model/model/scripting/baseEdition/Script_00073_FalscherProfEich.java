package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00073_FalscherProfEich extends TrainerCardScript {

	public Script_00073_FalscherProfEich(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the enemy deck contains at least 7 cards:
		Position enemy = gameModel.getPosition(enemyDeck());
		if (enemy.size() >= 7)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " shuffles his hand into his deck!", "");
		List<Card> list = gameModel.getPosition(enemyHand()).getCards();
		List<Card> cardList = new ArrayList<>();
		for (Card c : list)
			cardList.add(c);
		for (Card c : cardList)
			gameModel.getAttackAction().moveCard(enemyHand(), enemyDeck(), c.getGameID(), true);
		gameModel.getAttackAction().shufflePosition(enemyDeck());
		gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);

		// Draw 7 cards:
		gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " draws 7 cards!", "");
		gameModel.getAttackAction().playerDrawsCards(7, getEnemyPlayer());
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());
	}
}
