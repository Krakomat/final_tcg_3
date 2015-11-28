package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00259_ImposterOaksRevenge extends TrainerCardScript {

	public Script_00259_ImposterOaksRevenge(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		if (gameModel.getPosition(ownHand()).size() < 2)
			return null;
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());

		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		Card chosenCard = player.playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), 1, true, "Discard a card from your hand!").get(0);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " discards " + chosenCard.getName() + "!", chosenCard, "");
		gameModel.getAttackAction().discardCardToDiscardPile(ownHand(), chosenCard.getGameID());
		gameModel.sendGameModelToAllPlayers("");

		gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " shuffles his hand into his deck!", "");
		List<Card> list = gameModel.getPosition(enemyHand()).getCards();
		List<Card> cardList = new ArrayList<>();
		for (Card c : list)
			cardList.add(c);
		for (Card c : cardList)
			gameModel.getAttackAction().moveCard(enemyHand(), enemyDeck(), c.getGameID(), true);
		gameModel.getAttackAction().shufflePosition(enemyDeck());
		gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);

		// Draw 4 cards:
		gameModel.sendTextMessageToAllPlayers(enemy.getName() + " draws 4 cards!", "");
		gameModel.getAttackAction().playerDrawsCards(4, enemy);
	}
}
