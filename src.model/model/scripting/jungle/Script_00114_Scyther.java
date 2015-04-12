package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00114_Scyther extends PokemonCardScript {

	private boolean swordDanceActivated;
	private int endTurnActionCounter;

	public Script_00114_Scyther(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Swords Dance", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Slash", att2Cost);

		swordDanceActivated = false;
		this.endTurnActionCounter = -1;
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Swords Dance"))
			this.swordsDance();
		else
			this.slash();
	}

	private void swordsDance() {
		this.swordDanceActivated = true;
		this.endTurnActionCounter = 3;
	}

	private void slash() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		if (swordDanceActivated)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 60, true);
		else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}

	public void executeEndTurnActions() {
		if (endTurnActionCounter > -1) {
			endTurnActionCounter--;
			if (endTurnActionCounter == 0) {
				this.swordDanceActivated = false;
			}
		}
	}
}
