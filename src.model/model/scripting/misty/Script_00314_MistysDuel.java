package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00314_MistysDuel extends TrainerCardScript {

	public Script_00314_MistysDuel(TrainerCard card, PokemonGame gameModel) {
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
		gameModel.sendTextMessageToAllPlayers("If 'Heads', then " + getCardOwner().getName() + " gets the effects of Misty's Duel!", "");
		Player player = null;
		PositionID hand = null;
		PositionID deck = null;
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			player = getCardOwner();
			hand = ownHand();
			deck = ownDeck();
		} else {
			player = getEnemyPlayer();
			hand = enemyHand();
			deck = enemyDeck();
		}

		gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles his hand into his deck!", "");
		List<Card> list = gameModel.getPosition(hand).getCards();
		List<Card> cardList = new ArrayList<>();
		for (Card c : list)
			cardList.add(c);
		for (Card c : cardList)
			gameModel.getAttackAction().moveCard(hand, deck, c.getGameID(), true);
		gameModel.getAttackAction().shufflePosition(deck);
		gameModel.sendGameModelToAllPlayers(Sounds.SHUFFLE);

		// Draw 5 cards:
		gameModel.sendTextMessageToAllPlayers(player.getName() + " draws 5 cards!", "");
		gameModel.getAttackAction().playerDrawsCards(5, player);
	}
}
