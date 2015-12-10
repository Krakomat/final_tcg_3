package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00299_MistysPoliwrath extends PokemonCardScript {

	public Script_00299_MistysPoliwrath(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Water Ring", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		List<PositionID> enemyBench = gameModel.getFullBenchPositions(enemy.getColor());
		for (PositionID benchPos : enemyBench)
			if (((PokemonCard) gameModel.getPosition(benchPos).getTopCard()).getElement() != Element.WATER)
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);

		List<PositionID> ownBench = gameModel.getFullBenchPositions(player.getColor());
		for (PositionID benchPos : ownBench)
			if (((PokemonCard) gameModel.getPosition(benchPos).getTopCard()).getElement() != Element.WATER)
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);
	}
}
