package model.scripting.rocket;

import model.database.EnergyCard;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.EnergyCardScript;

public class Script_00264_PotionEnergy extends EnergyCardScript {

	public Script_00264_PotionEnergy(EnergyCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public void playFromHand() {
		super.playFromHand();

		if (this.card.getCurrentPosition().isDamaged())
			// Clean conditions on pokemon:
			gameModel.getAttackAction().healPosition(this.card.getCurrentPosition().getPositionID(), 10);
	}
}