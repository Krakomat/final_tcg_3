package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00130_Persian extends PokemonCardScript {

	private int defenderGameID, duration;

	public Script_00130_Persian(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Scratch", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Pounce", att2Cost);

		this.defenderGameID = -1;
		this.duration = 0;
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Scratch"))
			this.scratch();
		else
			this.pounce();
	}

	private void scratch() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}

	private void pounce() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		// Remember defender:
		this.defenderGameID = gameModel.getPosition(defender).getTopCard().getGameID();
		this.duration = 2;
	}

	public int modifyIncomingDamage(int damage, Card attacker) {
		if (attacker != null) {
			if (attacker.getGameID() == this.defenderGameID && PositionID.isActivePosition(attacker.getCurrentPosition().getPositionID()))
				return damage < 10 ? 0 : damage - 10;
		}
		return damage;
	}

	public void moveToPosition(PositionID targetPosition) {
		if (PositionID.isBenchPosition(targetPosition)) {
			this.defenderGameID = -1;
			this.duration = 0;
		}
	}

	public void executeEndTurnActions() {
		if (this.duration > 0)
			this.duration--;
		if (this.duration == 0)
			this.defenderGameID = -1;
	}
}
