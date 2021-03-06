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

public class Script_00186_Kabuto extends PokemonCardScript {

	public Script_00186_Kabuto(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Scratch", att1Cost);

		this.addPokemonPower("Kabuto Armor");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public int modifyIncomingDamage(int damage, Card attacker, PositionID defender) {
		if (!kabutoArmorCanBeExecuted() && this.card.getCurrentPosition().getPositionID() == defender && gameModel.getPosition(defender).getTopCard() == this.card)
			return damage;
		else {
			int newDamage = damage / 2;
			if (newDamage % 10 != 0)
				newDamage = newDamage - (newDamage % 10); // round down
			return newDamage;
		}
	}

	private boolean kabutoArmorCanBeExecuted() {
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		return true;
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Scratch"))
			this.scratch();
	}

	private void scratch() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}
}
