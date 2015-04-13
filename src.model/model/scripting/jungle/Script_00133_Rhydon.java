package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00133_Rhydon extends PokemonCardScript {

	public Script_00133_Rhydon(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Horn Attack", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		this.addAttack("Ram", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Horn Attack"))
			this.hornAttack();
		else
			this.ram();
	}

	private void hornAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}

	private void ram() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 20, true);

		Player enemy = this.getEnemyPlayer();
		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0) {

			PositionID chosenBenchPos = enemy.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true, "Choose a new active pokemon!")
					.get(0);

			// Message clients:
			Card active = gameModel.getPosition(defender).getTopCard();
			Card bench = gameModel.getPosition(chosenBenchPos).getTopCard();
			List<Card> cardList = new ArrayList<>();
			cardList.add(active);
			cardList.add(bench);
			gameModel.sendCardMessageToAllPlayers(enemy.getName() + " swaps " + active.getName() + " with " + bench.getName() + "!", cardList, "");

			// Execute swap:
			gameModel.getAttackAction().swapPokemon(chosenBenchPos, defender);
		}
	}
}
