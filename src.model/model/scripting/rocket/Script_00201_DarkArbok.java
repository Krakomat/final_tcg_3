package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00201_DarkArbok extends PokemonCardScript {

	public Script_00201_DarkArbok(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Stare", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		this.addAttack("Poison Vapor", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Stare"))
			this.Stare();
		else
			this.PoisonVapor();
	}

	private void Stare() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		if (gameModel.getFullArenaPositions(enemy.getColor()).size() > 0) {
			PositionID defender = player.playerChoosesPositions(gameModel.getFullArenaPositions(enemy.getColor()), 1, true,
					"Choose a pokemon that receives the damage!").get(0);
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, false);
			this.gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POKEMON_POWER_BLOCK);
		}
	}

	private void PoisonVapor() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		Player enemy = this.getEnemyPlayer();
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
		this.gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);

		List<PositionID> enemyBench = gameModel.getFullBenchPositions(enemy.getColor());
		for (PositionID benchPos : enemyBench)
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);
	}
}
