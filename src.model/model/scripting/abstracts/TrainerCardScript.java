package model.scripting.abstracts;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;

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

		return trainerCanBePlayedFromHand();
	}

	public abstract PlayerAction trainerCanBePlayedFromHand();
}