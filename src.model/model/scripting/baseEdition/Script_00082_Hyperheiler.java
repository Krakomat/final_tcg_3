package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00082_Hyperheiler extends TrainerCardScript {

	public Script_00082_Hyperheiler(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played, if there is a pokemon with a negative condition in the players arena:
		if (this.getInfectedPositions().size() > 0)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();

		PositionID chosenPosition = player.playerChoosesPositions(getInfectedPositions(), 1, true, "Choose a pokemon to heal!").get(0);
		Card targetPokemon = gameModel.getPosition(chosenPosition).getTopCard();

		// Execute heal(messages to clients send there):
		gameModel.getAttackAction().cureAllConditionsOnPosition(chosenPosition);
		gameModel.sendCardMessageToAllPlayers("All conditions on " + targetPokemon.getName() + " are cured", targetPokemon, "");

		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
	}

	private List<PositionID> getInfectedPositions() {
		Player player = this.getCardOwner();
		List<PositionID> arenaPositions = gameModel.getFullArenaPositions(player.getColor());
		List<PositionID> damagedPositions = new ArrayList<>();
		for (PositionID posID : arenaPositions) {
			PokemonCard topCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (topCard.hasNegativeCondition())
				damagedPositions.add(posID);
		}
		return damagedPositions;
	}
}
