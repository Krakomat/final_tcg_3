package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00265_DarkRaichu extends PokemonCardScript {

	public Script_00265_DarkRaichu(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Suprise thunder", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		gameModel.sendTextMessageToAllPlayers("Flip a coin for bench damage!", "");
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers("Flip a coin to see whose bench gets damages!", "");
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				List<PositionID> enemyBench = gameModel.getFullBenchPositions(enemy.getColor());
				for (PositionID benchPos : enemyBench)
					gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 20, false);
			} else {
				List<PositionID> ownBench = gameModel.getFullBenchPositions(player.getColor());
				for (PositionID benchPos : ownBench)
					gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);
			}
		}
	}
}
