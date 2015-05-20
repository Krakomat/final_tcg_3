package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00174_Kingler extends PokemonCardScript {

	public Script_00174_Kingler(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Flail", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Crabhammer", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Flail"))
			this.flail();
		else
			this.crabhammer();
	}

	private void flail() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PokemonCard attackingPokemon = (PokemonCard) gameModel.getPosition(attacker).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int damageOnAttacker = attackingPokemon.getDamageMarks();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, damageOnAttacker, true);
	}

	private void crabhammer() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
	}
}
