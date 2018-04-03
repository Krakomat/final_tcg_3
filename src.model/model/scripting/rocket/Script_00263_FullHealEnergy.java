package model.scripting.rocket;

import java.util.List;

import model.database.DynamicPokemonCondition;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.PokemonCondition;
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
		boolean hasCurableCondition = false;
		List<DynamicPokemonCondition> conditions = ((PokemonCard) this.card.getCurrentPosition().getTopCard()).getConditions();
		for (int i = 0; i < conditions.size(); i++) {
			PokemonCondition condition = conditions.get(i).getCondition();
			if (condition.equals(PokemonCondition.ASLEEP) || condition.equals(PokemonCondition.CONFUSED) || condition.equals(PokemonCondition.PARALYZED)
					|| condition.equals(PokemonCondition.POISONED) || condition.equals(PokemonCondition.TOXIC)) {
				hasCurableCondition = true;
				i--;
			}
		}
		if (hasCurableCondition) {
			gameModel.sendCardMessageToAllPlayers("All conditions on " + this.card.getName() + " are cured!", this.card, "");
			gameModel.getAttackAction().cureAllConditionsOnPosition(this.card.getCurrentPosition().getPositionID());
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
