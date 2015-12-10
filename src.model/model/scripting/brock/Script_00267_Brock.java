package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00267_Brock extends TrainerCardScript {

	public Script_00267_Brock(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played, if there is a damaged pokemon in the players arena:
		if (this.getDamagedPositions().size() > 0)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Execute heal(messages to clients send there):
		for (PositionID posID : this.getDamagedPositions())
			gameModel.getAttackAction().healPosition(posID, 10);

		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
	}

	private List<PositionID> getDamagedPositions() {
		Player player = this.getCardOwner();
		List<PositionID> arenaPositions = gameModel.getFullArenaPositions(player.getColor());
		List<PositionID> damagedPositions = new ArrayList<>();
		for (PositionID posID : arenaPositions) {
			PokemonCard topCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (topCard.getDamageMarks() > 0)
				damagedPositions.add(posID);
		}
		return damagedPositions;
	}
}
