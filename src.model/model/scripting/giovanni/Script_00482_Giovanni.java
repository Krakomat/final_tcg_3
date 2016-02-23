package model.scripting.giovanni;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00482_Giovanni extends TrainerCardScript {

	public Script_00482_Giovanni(TrainerCard card, PokemonGame gameModel) {
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

		PositionID posID = getCardOwner().playerChoosesPositions(getGiovanniPokemonInArena(), 1, true, "Choose a pokemon to apply Giovanni to!").get(0);
		Card c = gameModel.getPosition(posID).getTopCard();
		gameModel.getGameModelParameters().activateEffect("00482", c.getGameID());
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().activeEffect("00482"))
			gameModel.getGameModelParameters().deactivateEffect("00482");
	}

	private List<PositionID> getGiovanniPokemonInArena() {
		List<PositionID> erg = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(getCardOwner().getColor()))
			if (gameModel.getPosition(posID).getTopCard().getName().contains("Giovanni"))
				erg.add(posID);
		return erg;
	}
}
