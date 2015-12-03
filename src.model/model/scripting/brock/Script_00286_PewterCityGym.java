package model.scripting.brock;

import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00286_PewterCityGym extends TrainerCardScript {

	public Script_00286_PewterCityGym(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return null;
	}

	@Override
	public void playFromHand() {

	}
}
