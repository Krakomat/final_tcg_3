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

public class Script_00138_Cubone extends PokemonCardScript {

	public Script_00138_Cubone(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Snivel", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		this.addAttack("Rage", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Snivel"))
			this.snivel();
		else
			this.rage();
	}

	private void snivel() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		// Remember defender:
		gameModel.getGameModelParameters().getBlockedAttacks().add(new Triple<Integer, String, Integer>(gameModel.getPosition(defender).getTopCard().getGameID(), "00138", 2));
	}

	private void rage() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard attackingPokemon = (PokemonCard) gameModel.getPosition(attacker).getTopCard();

		int damageMarks = attackingPokemon.getDamageMarks();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10 + damageMarks, true);
	}

	public int modifyIncomingDamage(int damage, Card attacker, PositionID defender) {
		if (attacker != null) {
			if (gameModel.getGameModelParameters().attackIsBlocked("00138", attacker.getGameID()) && PositionID.isActivePosition(attacker.getCurrentPosition().getPositionID())
					&& PositionID.isActivePosition(getCurrentPositionID()))
				return damage < 20 ? 0 : damage - 20;
		}
		return damage;
	}
}
