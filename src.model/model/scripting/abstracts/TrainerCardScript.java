package model.scripting.abstracts;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import network.client.Player;

/**
 * Script for a TrainerCard.
 * 
 * @author Michael
 *
 */
public abstract class TrainerCardScript extends CardScript {

	public TrainerCardScript(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction canBePlayedFromHand() {
		// Check if card is in players hand:
		if (!this.cardInHand())
			return null;

		// Check if flag in gameModelParameters is set (DarkVileplume and Muk):
		if (gameModel.getGameModelParameters().isAllowedToPlayTrainerCards() > 0
				|| (this.gameModel.getGameModelParameters().activeEffect("00212") && !this.gameModel.getGameModelParameters().activeEffect("00164")))
			return null;

		return trainerCanBePlayedFromHand();
	}

	public abstract PlayerAction trainerCanBePlayedFromHand();

	public boolean stadiumCanBeActivatedOnField(Player player) {
		// Override when needed!
		return false;
	}

	public void executeStadiumActiveEffect(Player player) {
		// Override when needed!
	}
}