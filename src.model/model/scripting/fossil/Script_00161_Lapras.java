package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00161_Lapras extends PokemonCardScript {

	public Script_00161_Lapras(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Water Gun", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		this.addAttack("Confuse Ray", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Water Gun"))
			this.waterGun();
		else
			this.confuseRay();
	}

	private void waterGun() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void confuseRay() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		this.gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.CONFUSED);
		this.gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
	}
}
