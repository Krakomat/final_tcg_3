package model.scripting.rocket;

import model.database.EnergyCard;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.EnergyCardScript;

public class Script_00263_FullHealEnergy extends EnergyCardScript {

	public Script_00263_FullHealEnergy(EnergyCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public void playFromHand() {
		super.playFromHand();

		// Clean conditions on pokemon:
		gameModel.getAttackAction().cureAllConditionsOnPosition(this.card.getCurrentPosition().getPositionID());
	}
}
