package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Triple;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00150_Spearow extends PokemonCardScript {

	public Script_00150_Spearow(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Peck", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Mirror Move", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Peck"))
			this.peck();
		else
			this.mirrorMove();
	}

	private void peck() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void mirrorMove() {
		// Check if tauboga was attacked last turn:
		Integer damage = 0;
		List<PokemonCondition> cond = new ArrayList<>();
		for (Triple<Integer, String, Integer> triple : gameModel.getGameModelParameters().getBlockedAttacks()) {
			if (triple.getKey() == cardGameID() && PokemonCondition.isValue(triple.getValue())) {
				PokemonCondition c = PokemonCondition.valueOf(triple.getValue());
				cond.add(c);
			}
			if (triple.getKey() == cardGameID() && isInt(triple.getValue()))
				damage = Integer.valueOf(triple.getValue());
		}
		if (damage > 0 || cond.size() > 0) {
			PositionID attacker = this.card.getCurrentPosition().getPositionID();
			PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
			Element attackerElement = ((PokemonCard) this.card).getElement();

			if (damage > 0)
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, damage, true);

			if (cond.size() > 0) {
				for (PokemonCondition c : cond)
					gameModel.getAttackAction().inflictConditionToPosition(defender, c);
			}
		} else
			gameModel.sendTextMessageToAllPlayers("Mirror Move does nothing!", "");
	}

	private boolean isInt(String value) {
		try {
			Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public void pokemonIsDamaged(int turnNumber, int damage, PositionID source) {
		gameModel.getGameModelParameters().getBlockedAttacks().add(new Triple<Integer, String, Integer>(cardGameID(), String.valueOf(damage), 2));
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		gameModel.getGameModelParameters().getBlockedAttacks().add(new Triple<Integer, String, Integer>(cardGameID(), condition.toString(), 2));
	}
}
