package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00150_Spearow extends PokemonCardScript {
	/** Turnnumber at which Spearow got damaged/a condition the last time */
	private int lastAttackTurn;
	/** Stores all damaging actions against Spearow */
	private Map<Integer, Integer> damageHistory;
	/** Stores all conditions inflicted against Spearow */
	private Map<Integer, PokemonCondition> conditionHistory;

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
		// Check if Spearow was attacked last turn:
		int currentTurnNumber = gameModel.getTurnNumber();
		if (currentTurnNumber - 1 == lastAttackTurn) {
			PositionID attacker = this.card.getCurrentPosition().getPositionID();
			PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
			Element attackerElement = ((PokemonCard) this.card).getElement();

			if (this.damageHistory.containsKey(lastAttackTurn))
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, damageHistory.get(lastAttackTurn), true);

			if (this.conditionHistory.containsKey(lastAttackTurn))
				gameModel.getAttackAction().inflictConditionToPosition(defender, conditionHistory.get(lastAttackTurn));
		} else
			gameModel.sendTextMessageToAllPlayers("Mirror Move does nothing!", "");
	}

	public void pokemonIsDamaged(int turnNumber, int damage) {
		this.lastAttackTurn = turnNumber;
		this.damageHistory.put(turnNumber, damage);
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		this.lastAttackTurn = turnNumber;
		this.conditionHistory.put(turnNumber, condition);
	}
}
