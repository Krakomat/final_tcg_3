package model.scripting.baseEdition;

import model.database.Card;
import model.database.EnergyCard;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.EnergyCardScript;

/**
 * Script for the doll token!
 * 
 * @author Michael
 *
 */
public class Script_00104_LektrobalToken extends EnergyCardScript {

	/**
	 * For local purposes only!
	 * 
	 * @param token
	 * @param gameModel
	 */
	public Script_00104_LektrobalToken(EnergyCard token, PokemonGame gameModel) {
		super(token, gameModel);
	}

	public void moveToPosition(PositionID targetPosition) {
		if (targetPosition == ownDiscardPile()) {
			Position pos = gameModel.getPosition(targetPosition);

			// Remove token from position:
			boolean success = pos.removeFromPosition(this.card);
			if (!success)
				System.err.println("Couldn't remove doll from position");
			this.card.setCurrentPosition(null);

			// Unregister token from gameModel:
			gameModel.unregisterCard(this.card);

			// Add Lektrobal to discard pile:
			Integer gameID = gameModel.getGameModelParameters().getActiveEffectGameIDs("00021").get(0);
			gameModel.getGameModelParameters().deactivateEffect("00070", gameID);
			Card lektrobal = gameModel.getCard(gameID);

			pos.addToPosition(lektrobal);
			lektrobal.setCurrentPosition(pos);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
