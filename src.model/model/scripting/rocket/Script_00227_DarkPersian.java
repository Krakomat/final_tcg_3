package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00227_DarkPersian extends PokemonCardScript {

	public Script_00227_DarkPersian(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Fascinate", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Poison Claws", att2Cost);
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Fascinate")) {
			// Cannot be used if the enemy player has no pokemon on his bench
			Player enemy = this.getEnemyPlayer();
			if (gameModel.getFullBenchPositions(enemy.getColor()).size() == 0)
				return false;
		}
		return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Fascinate"))
			this.Fascinate();
		else
			this.PoisonClaws();
	}

	private void Fascinate() {
		gameModel.sendTextMessageToAllPlayers("Check if Fascinate is effective...", "");
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			Player player = this.getCardOwner();
			Player enemy = this.getEnemyPlayer();
			Color color = enemy.getColor();
			PositionID activePos = color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
			PositionID chosenBenchPos = player.playerChoosesPositions(gameModel.getFullBenchPositions(color), 1, true,
					"Choose a new active pokemon for " + enemy.getName() + "!").get(0);

			// Message clients:
			Card active = gameModel.getPosition(activePos).getTopCard();
			Card bench = gameModel.getPosition(chosenBenchPos).getTopCard();
			List<Card> cardList = new ArrayList<>();
			cardList.add(active);
			cardList.add(bench);
			gameModel.sendCardMessageToAllPlayers(player.getName() + " swaps " + active.getName() + " with " + bench.getName() + "!", cardList, "");

			// Execute swap:
			gameModel.getAttackAction().swapPokemon(chosenBenchPos, activePos);
		} else
			gameModel.sendTextMessageToAllPlayers("Fascinate does nothing!", "");
	}

	private void PoisonClaws() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		// Flip coin to check if defending pokemon is poisoned:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is poisoned!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
