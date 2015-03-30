package model.scripting.baseEdition;

import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.EnergyCardScript;
import network.client.Player;

/**
 * Script for the doll token!
 * 
 * @author Michael
 *
 */
public class Script_00104_LektrobalToken extends EnergyCardScript {

	private PokemonCard lektrobal;

	/**
	 * For local purposes only!
	 * 
	 * @param token
	 * @param gameModel
	 */
	public Script_00104_LektrobalToken(EnergyCard token, PokemonGame gameModel) {
		super(token, gameModel);
		this.lektrobal = null;
	}

	public Script_00104_LektrobalToken(EnergyCard token, PokemonGame gameModel, PokemonCard lektrobal) {
		super(token, gameModel);
		this.lektrobal = lektrobal;
	}

	public void moveToPosition(PositionID targetPosition) {
		final Player player = this.getCardOwner();
		if (targetPosition == ownDiscardPile(player)) {
			Position pos = gameModel.getPosition(targetPosition);

			// Remove token from position:
			boolean success = pos.removeFromPosition(this.card);
			if (!success)
				System.err.println("Couldn't remove doll from position");
			this.card.setCurrentPosition(null);

			// Unregister token from gameModel:
			gameModel.unregisterCard(this.card);

			// Add Lektrobal to discard pile:
			pos.addToPosition(this.lektrobal);
			this.lektrobal.setCurrentPosition(pos);
			gameModel.sendGameModelToAllPlayers();
		}
	}

	private PositionID ownDiscardPile(Player player) {
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DISCARDPILE;
		else
			return PositionID.RED_DISCARDPILE;
	}
}
