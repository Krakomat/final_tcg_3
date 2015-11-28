package model.scripting.rocket;

import network.client.Player;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00258_Digger extends TrainerCardScript {

	public Script_00258_Digger(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		boolean tails = false;
		Player flippingPlayer = player;

		while (!tails) {
			gameModel.sendTextMessageToAllPlayers(flippingPlayer.getName() + " flips a coin!", "");

			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				// Change flipping player:
				if (flippingPlayer == player)
					flippingPlayer = enemy;
				else
					flippingPlayer = player;
			} else {
				tails = true;
			}
		}

		// Do damage:
		if (flippingPlayer == player)
			gameModel.getAttackAction().inflictDamageToPosition(Element.COLORLESS, null, ownActive(), 10, false);
		else
			gameModel.getAttackAction().inflictDamageToPosition(Element.COLORLESS, null, enemyActive(), 10, false);
	}
}
