package model.scripting.baseEdition;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00075_Goere extends TrainerCardScript {

	public Script_00075_Goere(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can always be played
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	private boolean playerReady, enemyReady; // Allowed!

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		playerReady = false;
		enemyReady = false;

		// Discard trainer card before revealing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);

		// Show hands to both players:
		new Thread(new Runnable() {
			@Override
			public void run() {
				playerRevealsHand(enemy);
				enemyReady = true;
			}
		}).start();
		playerRevealsHand(player);
		playerReady = true;

		// Wait for both players:
		while (!(playerReady && enemyReady))
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		// Discard trainer cards:
		gameModel.sendTextMessageToAllPlayers("Both players shuffle trainer cards into their decks!", Sounds.SHUFFLE);
		playerPutsTrainerCardsOnDeck(player);
		playerPutsTrainerCardsOnDeck(enemy);

		// Shuffle decks:
		gameModel.getAttackAction().shufflePosition(PositionID.BLUE_DECK);
		gameModel.getAttackAction().shufflePosition(PositionID.RED_DECK);
	}

	private void playerRevealsHand(Player player) {
		Player opponent = null;
		Position handPosition = null;
		if (player.getColor() == Color.BLUE) {
			opponent = gameModel.getPlayerRed();
			handPosition = gameModel.getPosition(PositionID.BLUE_HAND);
		} else {
			opponent = gameModel.getPlayerBlue();
			handPosition = gameModel.getPosition(PositionID.RED_HAND);
		}

		opponent.playerChoosesCards(handPosition.getCards(), 12, false, "Look at your opponents hand...");
	}

	private void playerPutsTrainerCardsOnDeck(Player player) {
		PositionID handPos = null;
		PositionID deckPos = null;
		if (player.getColor() == Color.BLUE) {
			handPos = PositionID.BLUE_HAND;
			deckPos = PositionID.BLUE_DECK;
		} else {
			handPos = PositionID.RED_HAND;
			deckPos = PositionID.RED_DECK;
		}
		List<Card> handCards = gameModel.getPosition(handPos).getCards();
		for (int i = 0; i < handCards.size(); i++) {
			if (handCards.get(i).getCardType().equals(CardType.TRAINER)) {
				gameModel.getAttackAction().moveCard(handPos, deckPos, handCards.get(i).getGameID(), true);
				i--;
			}
		}
	}
}
