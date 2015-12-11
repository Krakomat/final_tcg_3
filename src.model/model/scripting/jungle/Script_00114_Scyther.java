package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Pair;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00114_Scyther extends PokemonCardScript {

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
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Swords Dance"))
			this.swordsDance();
		else
			this.slash();
	}

	private void swordsDance() {
		gameModel.getGameModelParameters().getAttackUsed().add(new Pair<Integer, Integer>(this.card.getGameID(), 3));
	}

	private boolean attackUsed() {
		Pair<Integer, Integer> currentPair = null;
		for (Pair<Integer, Integer> pair : gameModel.getGameModelParameters().getAttackUsed()) {
			if (pair.getKey() == this.card.getGameID())
				currentPair = pair;
		}
		if (currentPair != null)
			return true;
		return false;
	}

	private void slash() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		if (attackUsed())
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 60, true);
		else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}

	public void executeEndTurnActions() {
		Pair<Integer, Integer> currentPair = null;
		for (Pair<Integer, Integer> pair : gameModel.getGameModelParameters().getAttackUsed()) {
			if (pair.getKey() == this.card.getGameID())
				currentPair = pair;
		}

		if (currentPair != null) {
			gameModel.getGameModelParameters().getAttackUsed().remove(currentPair);
			if (currentPair.getValue() > 1)
				gameModel.getGameModelParameters().getAttackUsed().add(new Pair<Integer, Integer>(currentPair.getKey(), currentPair.getValue() - 1));
		}
	}
}
