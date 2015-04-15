package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00148_Pikachu extends PokemonCardScript {

	public Script_00148_Pikachu(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Spark", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0) {
			PositionID chosenPosition = player.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true,
					"Choose a bench position to damage:").get(0);
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, chosenPosition, 10, false);
		}
	}
}
