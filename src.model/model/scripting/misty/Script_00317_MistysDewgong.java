package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00317_MistysDewgong extends PokemonCardScript {

	public Script_00317_MistysDewgong(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		this.addAttack("Ice Throw", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Take Down", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Ice Throw"))
			this.IceThrow();
		else
			this.TakeDown();
	}

	private void IceThrow() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		Element defenderElement = ((PokemonCard) gameModel.getPosition(defender).getTopCard()).getElement();

		if (defenderElement != Element.ROCK)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
	}

	private void TakeDown() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 60, true);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 20, true);
	}
}
