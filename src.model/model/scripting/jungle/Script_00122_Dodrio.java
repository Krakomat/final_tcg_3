package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00122_Dodrio extends PokemonCardScript {

	public Script_00122_Dodrio(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Rage", att1Cost);

		this.addPokemonPower("Retreat Aid");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard attackingPokemon = (PokemonCard) gameModel.getPosition(attacker).getTopCard();

		int damageMarks = attackingPokemon.getDamageMarks();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10 + damageMarks, true);
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	public int modifyRetreatCosts(int retreatCosts, Color color) {
		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			return super.modifyRetreatCosts(retreatCosts, color);
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return super.modifyRetreatCosts(retreatCosts, color);
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return super.modifyRetreatCosts(retreatCosts, color);
		if (color != this.getCardOwner().getColor())
			return super.modifyRetreatCosts(retreatCosts, color);
		if (!PositionID.isBenchPosition(this.card.getCurrentPosition().getPositionID()))
			return super.modifyRetreatCosts(retreatCosts, color);
		if (retreatCosts == 0)
			return 0;
		return super.modifyRetreatCosts(retreatCosts, color) - 1;
	}
}
