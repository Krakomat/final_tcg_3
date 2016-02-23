package model.scripting.giovanni;

import java.util.ArrayList;
import java.util.List;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00494_GiovannisLastResort extends TrainerCardScript {

	public Script_00494_GiovannisLastResort(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		if (getGiovanniPokemonInArena().size() > 0)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		PositionID chosenPos = getCardOwner().playerChoosesPositions(getGiovanniPokemonInArena(), 1, true, "Choose a pokemon to fully heal!").get(0);
		gameModel.getAttackAction().fullHealPosition(chosenPos);

		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " discards his hand!", "");
		gameModel.getAttackAction().playerDiscardsAllCards(getCardOwner());
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<PositionID> getGiovanniPokemonInArena() {
		List<PositionID> erg = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(getCardOwner().getColor()))
			if (gameModel.getPosition(posID).getTopCard().getName().contains("Giovanni"))
				erg.add(posID);
		return erg;
	}
}
