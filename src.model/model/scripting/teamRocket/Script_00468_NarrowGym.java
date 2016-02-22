package model.scripting.teamRocket;

import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00468_NarrowGym extends TrainerCardScript {

	public Script_00468_NarrowGym(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		if (!gameModel.getPosition(PositionID.STADIUM).isEmpty()) {
			Card stadium = gameModel.getPosition(PositionID.STADIUM).getTopCard();
			// Discard previous stadium card:
			gameModel.getAttackAction().discardCardToDiscardPile(PositionID.STADIUM, stadium.getGameID(), false);
		}
		gameModel.getPosition(PositionID.STADIUM).setColor(this.getCardOwner().getColor());
		gameModel.getAttackAction().moveCard(getCurrentPositionID(), PositionID.STADIUM, this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		this.checkPlayerBenches(getEnemyPlayer());
		this.checkPlayerBenches(getCardOwner());
	}

	private void checkPlayerBenches(Player player) {
		if (gameModel.getFullBenchPositions(player.getColor()).size() == 5) {
			PositionID posID = player.playerChoosesPositions(gameModel.getFullBenchPositions(player.getColor()), 1, true, "Choose a pokemon to return to your hand!").get(0);

			// Scoop up position:
			Position position = gameModel.getPosition(posID);
			List<Card> cards = position.getCards();

			gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " returns " + position.getTopCard().getName() + " to his hand!", position.getTopCard(), "");
			PositionID hand = ownHand();

			int size = cards.size();
			for (int i = 0; i < size; i++) {
				gameModel.getAttackAction().moveCard(posID, hand, cards.get(0).getGameID(), true);
			}
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
