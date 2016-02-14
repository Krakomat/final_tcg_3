package model.scripting.koga;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00386_KogasArbok extends PokemonCardScript {

	public Script_00386_KogasArbok(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Poison Buildup", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		this.addAttack("Poison Power", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Poison Buildup"))
			this.PoisonBuildup();
		else
			this.PoisonPower();
	}

	private void PoisonBuildup() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();

		gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " is poisoned!", "");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.POISONED);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void PoisonPower() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POISONED) || ((PokemonCard) this.card).hasCondition(PokemonCondition.TOXIC)) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
