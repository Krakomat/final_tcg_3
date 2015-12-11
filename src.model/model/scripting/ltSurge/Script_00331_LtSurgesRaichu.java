package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00331_LtSurgesRaichu extends PokemonCardScript {

	public Script_00331_LtSurgesRaichu(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Mega Punch", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Thunderbolt", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Mega Punch"))
			this.MegaPunch();
		else
			this.Thunderbolt();
	}

	private void MegaPunch() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}

	private void Thunderbolt() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 100, true);

		// Discard all energy cards:
		gameModel.getAttackAction().removeAllEnergyFromPosition(attacker);
		gameModel.sendGameModelToAllPlayers("");
	}
}
