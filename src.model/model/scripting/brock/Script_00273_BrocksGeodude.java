package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00273_BrocksGeodude extends PokemonCardScript {

	public Script_00273_BrocksGeodude(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Tackle", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Lucky Shot", att2Cost);
	}

	@Override
	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Lucky Shot") && gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()).size() == 0)
			return false;
		else
			return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Tackle"))
			this.Tackle();
		else
			this.LuckyShot();
	}

	private void Tackle() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void LuckyShot() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0) {
			PositionID defender = player.playerChoosesPositions(gameModel.getFullArenaPositions(enemy.getColor()), 1, true, "Choose a pokemon that receives the damage!").get(0);
			gameModel.sendTextMessageToAllPlayers("If Heads, then " + gameModel.getPosition(defender).getTopCard().getName() + " receives 30 damage!", "");
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS)
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, false);
			else
				gameModel.sendTextMessageToAllPlayers("Lucky Shot failed!", "");
		}
	}
}
