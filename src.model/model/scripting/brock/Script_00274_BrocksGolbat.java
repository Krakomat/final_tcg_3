package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00274_BrocksGolbat extends PokemonCardScript {

	public Script_00274_BrocksGolbat(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Dive", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Spiral Dive", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Dive"))
			this.Dive();
		else
			this.SpiralDive();
	}

	private void Dive() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}

	private void SpiralDive() {
		Player player = this.getEnemyPlayer();

		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		List<PositionID> ownBench = gameModel.getFullBenchPositions(player.getColor());
		for (PositionID benchPos : ownBench)
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);
	}
}
