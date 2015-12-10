package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00085_PokCenter extends TrainerCardScript {

	public Script_00085_PokCenter(TrainerCard card, PokemonGame gameModel) {
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
		List<PositionID> positionList = this.getDamagedPositions();
		for (PositionID posID : positionList) {

			PokemonCard pokemonCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (pokemonCard.getDamageMarks() > 0) {
				gameModel.getAttackAction().fullHealPosition(posID);
				gameModel.getAttackAction().removeAllEnergyFromPosition(posID);
			}

			gameModel.sendGameModelToAllPlayers("");
		}

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
