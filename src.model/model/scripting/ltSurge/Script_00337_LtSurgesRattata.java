package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Pair;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00337_LtSurgesRattata extends PokemonCardScript {

	public Script_00337_LtSurgesRattata(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Focus Energy", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Gnaw", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Focus Energy"))
			this.FocusEnergy();
		else
			this.Gnaw();
	}

	private void FocusEnergy() {
		gameModel.getGameModelParameters().getAttackUsed().add(new Pair<Integer, Integer>(this.card.getGameID(), 3));
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

	private void Gnaw() {
		int mult = 1;
		if (attackUsed())
			mult = 2;
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20 * mult, true);
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
}
