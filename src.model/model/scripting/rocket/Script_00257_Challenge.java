package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00257_Challenge extends TrainerCardScript {

	public Script_00257_Challenge(TrainerCard card, PokemonGame gameModel) {
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
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		int playerBenchSize = gameModel.getFullBenchPositions(player.getColor()).size();
		int enemyBenchSize = gameModel.getFullBenchPositions(enemy.getColor()).size();
		int maxBenchSize = this.gameModel.getCurrentStadium() != null && this.gameModel.getCurrentStadium().getCardId().equals("00468") ? 4 : 5;
		if (playerBenchSize == maxBenchSize && enemyBenchSize == maxBenchSize) {
			gameModel.sendTextMessageToAllPlayers("Both benches are full!", "");
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws 2 cards!", "");
			gameModel.getAttackAction().playerDrawsCards(2, getCardOwner());
		} else {
			boolean accept = enemy.playerDecidesYesOrNo("Do you accept the challenge?");
			if (accept) {
				gameModel.sendTextMessageToAllPlayers(enemy.getName() + " accepted the challenge!", "");
				{
					if (playerBenchSize < maxBenchSize) {
						List<Card> playerCards = player.playerChoosesCards(getBasicPokemonFromDeck(ownDeck()), maxBenchSize - playerBenchSize, false,
								"Choose up to " + (maxBenchSize - playerBenchSize) + " pokemon for your bench!");
						for (Card c : playerCards)
							gameModel.getAttackAction().moveCard(ownDeck(), getLowestBenchPosition(player), c.getGameID(), true);
					}
				}
				{
					if (enemyBenchSize < maxBenchSize) {
						List<Card> playerCards = enemy.playerChoosesCards(getBasicPokemonFromDeck(enemyDeck()), maxBenchSize - enemyBenchSize, false,
								"Choose up to " + (maxBenchSize - enemyBenchSize) + " pokemon for your bench!");
						for (Card c : playerCards)
							gameModel.getAttackAction().moveCard(enemyDeck(), getLowestBenchPosition(enemy), c.getGameID(), true);
					}
				}
				gameModel.sendGameModelToAllPlayers("");
			} else {
				gameModel.sendTextMessageToAllPlayers(enemy.getName() + " rejected the challenge!", "");
				gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws 2 cards!", "");
				gameModel.getAttackAction().playerDrawsCards(2, getCardOwner());
			}
			gameModel.getAttackAction().shufflePosition(ownDeck());
			gameModel.getAttackAction().shufflePosition(enemyDeck());
			gameModel.sendTextMessageToAllPlayers("Both player shuffle their decks!", Sounds.SHUFFLE);
		}
	}

	private List<Card> getBasicPokemonFromDeck(PositionID deck) {
		List<Card> cardList = new ArrayList<>();
		for (Card c : gameModel.getPosition(deck).getPokemonCards()) {
			PokemonCard pCard = (PokemonCard) c;
			if (pCard.getCardType() == CardType.BASICPOKEMON)
				cardList.add(c);
		}
		return cardList;
	}

	private PositionID getLowestBenchPosition(Player player) {
		// Get the lowest bench position that is empty:
		PositionID benchPosition = null;
		for (int i = 5; i >= 1; i--) {
			Position pos = gameModel.getPosition(PositionID.getBenchPosition(player.getColor(), i));
			if (pos.isEmpty())
				benchPosition = pos.getPositionID();
		}
		return benchPosition;
	}
}
