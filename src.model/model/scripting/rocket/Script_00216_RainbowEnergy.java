package model.scripting.rocket;

import model.database.EnergyCard;
import model.enums.Element;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.EnergyCardScript;

public class Script_00216_RainbowEnergy extends EnergyCardScript {

	public Script_00216_RainbowEnergy(EnergyCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public void playFromHand() {
		super.playFromHand();

		// Inflict damage to pokemon:
		gameModel.getAttackAction().inflictDamageToPosition(Element.RAINBOW, null, this.card.getCurrentPosition().getPositionID(), 10, false);
	}
}
