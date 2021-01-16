package model.scripting.koga;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00395_KogasGrimer extends PokemonCardScript {

	public Script_00395_KogasGrimer(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Sludge Grip", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Sludge Toss", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Sludge Grip"))
			this.SludgeGrip();
		else
			this.SludgeToss();
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Sludge Grip")) {
			// Cannot be used if the enemy player has no pokemon on his bench
			Player enemy = this.getEnemyPlayer();
			if (gameModel.getFullBenchPositions(enemy.getColor()).size() == 0)
				return false;
		}
		return super.attackCanBeExecuted(attackName);
	}

	private void SludgeGrip() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		Color color = enemy.getColor();
		PositionID activePos = color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
		PositionID chosenBenchPos = player.playerChoosesPositions(gameModel.getFullBenchPositions(color), 1, true, "Choose a new active pokemon for " + enemy.getName() + "!")
				.get(0);

		// Message clients:
		Card active = gameModel.getPosition(activePos).getTopCard();
		Card bench = gameModel.getPosition(chosenBenchPos).getTopCard();
		List<Card> cardList = new ArrayList<>();
		cardList.add(active);
		cardList.add(bench);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " swaps " + active.getName() + " with " + bench.getName() + "!", cardList, "");

		// Execute swap:
		gameModel.getAttackAction().swapPokemon(chosenBenchPos, activePos);

		// Damage:
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		this.gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
	}

	private void SludgeToss() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
