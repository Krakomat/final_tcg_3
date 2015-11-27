package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00158_Hitmonlee extends PokemonCardScript {

	public Script_00158_Hitmonlee(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Stretch Kick", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		this.addAttack("High Jump Kick", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Stretch Kick"))
			this.stretchKick();
		else
			this.highJumpKick();
	}

	private void stretchKick() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0) {
			PositionID defender = player.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true,
					"Choose a pokemon that receives the damage!").get(0);
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, false);
		}
	}

	private void highJumpKick() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);
	}
}
