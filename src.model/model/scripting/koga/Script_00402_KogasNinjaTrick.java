package model.scripting.koga;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00402_KogasNinjaTrick extends TrainerCardScript {

	public Script_00402_KogasNinjaTrick(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		if (getKogaCardsFromOwnField().isEmpty())
			return null;
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Move KogasNinjaTrick to active koga pokemon:
		this.gameModel.getAttackAction().moveCard(card.getCurrentPosition().getPositionID(), ownActive(), card.getGameID(), false);
		// Add condition for pokemon:
		gameModel.getAttackAction().inflictConditionToPosition(ownActive(), PokemonCondition.KOGAS_NINJA_TRICK);
	}

	@Override
	public void moveToPosition(PositionID targetPosition) {
		// Discard KogasNinjaTrick if neccessary:
		if (PositionID.isBenchPosition(targetPosition)) {
			// Discard KogasNinjaTrick:
			gameModel.getAttackAction().discardCardToDiscardPile(targetPosition, card.getGameID(), true);
			gameModel.getAttackAction().cureCondition(targetPosition, PokemonCondition.KOGAS_NINJA_TRICK);

			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private List<PositionID> getKogaCardsFromOwnField() {
		List<PositionID> posList = new ArrayList<>();
		PokemonCard pCard = (PokemonCard) gameModel.getPosition(ownActive()).getTopCard();
		if (pCard.getName().contains("Koga"))
			posList.add(ownActive());
		return posList;
	}
}
