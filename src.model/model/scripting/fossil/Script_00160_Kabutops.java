package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00160_Kabutops extends PokemonCardScript {

	public Script_00160_Kabutops(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Sharp Sickle", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		this.addAttack("Absorb", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Sharp Sickle"))
			this.sharpSickle();
		else
			this.absorb();
	}

	private void sharpSickle() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}

	private void absorb() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		int damageDealt = this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		int healAmount = (damageDealt / 2) % 2 == 0 ? (damageDealt / 2) : (damageDealt + 10) / 2;
		this.gameModel.getAttackAction().healPosition(attacker, healAmount);
	}
}
