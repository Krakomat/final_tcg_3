package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00382_ErikasKindness extends TrainerCardScript {

	public Script_00382_ErikasKindness(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Execute heal(messages to clients send there):
		List<PositionID> damagedPositions = this.getDamagedPositions();
		for (PositionID posID : damagedPositions)
			gameModel.getAttackAction().healPosition(posID, 20);

		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
	}

	private List<PositionID> getDamagedPositions() {
		Player player = this.getCardOwner();
		List<PositionID> arenaPositions = gameModel.getFullArenaPositions(player.getColor());
		List<PositionID> enemyArenaPositions = gameModel.getFullArenaPositions(getEnemyPlayer().getColor());
		List<PositionID> damagedPositions = new ArrayList<>();
		for (PositionID posID : arenaPositions) {
			PokemonCard topCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (topCard.getDamageMarks() > 0)
				damagedPositions.add(posID);
		}
		for (PositionID posID : enemyArenaPositions) {
			PokemonCard topCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (topCard.getDamageMarks() > 0)
				damagedPositions.add(posID);
		}
		return damagedPositions;
	}
}
