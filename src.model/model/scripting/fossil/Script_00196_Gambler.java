package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00196_Gambler extends TrainerCardScript {

	public Script_00196_Gambler(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();

		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);

		gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles his hand into his deck!", Sounds.SHUFFLE);

		// Shuffle hand into deck:
		List<Card> handCards = gameModel.getPosition(ownHand()).getCards();
		List<Card> copyHand = new ArrayList<>();
		for (Card c : handCards)
			copyHand.add(c);

		// Move cards:
		for (Card c : copyHand)
			gameModel.getAttackAction().moveCard(ownHand(), ownDeck(), c.getGameID(), true);

		gameModel.sendGameModelToAllPlayers("");

		gameModel.sendTextMessageToAllPlayers(player.getName() + " flips a coin...", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			// Draw 8 cards:
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws 8 cards!", "");
			gameModel.getAttackAction().playerDrawsCards(8, getCardOwner());
		} else {
			// Draw 1 card:
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws 1 card!", "");
			gameModel.getAttackAction().playerDrawsCards(1, getCardOwner());
		}
		gameModel.sendGameModelToAllPlayers("");
	}
}
