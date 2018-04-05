package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Triple;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00130_Persian extends PokemonCardScript {

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
		gameModel.getGameModelParameters().getBlockedAttacks().add(new Triple<Integer, String, Integer>(gameModel.getPosition(defender).getTopCard().getGameID(), "00130", 2));
	}

	public int modifyIncomingDamage(int damage, Card attacker, PositionID defender) {
		if (attacker != null) {
			if (gameModel.getGameModelParameters().attackIsBlocked("00130", attacker.getGameID()) && PositionID.isActivePosition(attacker.getCurrentPosition().getPositionID())
					&& PositionID.isActivePosition(getCurrentPositionID()))
				return damage < 10 ? 0 : damage - 10;
		}
		return damage;
	}
}
