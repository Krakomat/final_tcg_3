package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Element;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00090_Supertrank extends TrainerCardScript {

	public Script_00090_Supertrank(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played, if there is a damaged pokemon in the players arena:
		if (this.getDamagedPositionsWithEnergy().size() > 0)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		PositionID chosenPosition = player.playerChoosesPositions(this.getDamagedPositionsWithEnergy(), 1, true, "Choose a pokemon to heal!").get(0);

		// Pay energy:
		List<Element> costs = new ArrayList<>();
		costs.add(Element.COLORLESS);
		gameModel.getAttackAction().playerPaysEnergy(player, costs, chosenPosition);

		// Execute heal(messages to clients send there):
		gameModel.getAttackAction().healPosition(chosenPosition, 40);
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());
	}

	private List<PositionID> getDamagedPositionsWithEnergy() {
		Player player = this.getCardOwner();
		List<PositionID> arenaPositions = gameModel.getFullArenaPositions(player.getColor());
		List<PositionID> damagedPositions = new ArrayList<>();
		for (PositionID posID : arenaPositions) {
			Position pos = gameModel.getPosition(posID);
			PokemonCard topCard = (PokemonCard) pos.getTopCard();
			if (topCard.getDamageMarks() > 0 && pos.getEnergyCards().size() > 0)
				damagedPositions.add(posID);
		}
		return damagedPositions;
	}
}
