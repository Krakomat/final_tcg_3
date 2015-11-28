package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00208_DarkHypno extends PokemonCardScript {

	public Script_00208_DarkHypno(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Psypunch", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Bench Manipulation", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Psypunch"))
			this.Psypunch();
		else
			this.BenchManipulation();
	}

	private void Psypunch() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}

	private void BenchManipulation() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int numberOfCoins = gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()).size();
		gameModel.sendTextMessageToAllPlayers(this.getEnemyPlayer().getName() + " flips " + numberOfCoins + " coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(numberOfCoins);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 20, false);
	}
}
