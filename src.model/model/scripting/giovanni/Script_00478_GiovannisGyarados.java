package model.scripting.giovanni;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00478_GiovannisGyarados extends PokemonCardScript {

	public Script_00478_GiovannisGyarados(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Summon Storm", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Dragon Tornado", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Summon Storm"))
			this.SummonStorm();
		else
			this.DragonTornado();
	}

	private void SummonStorm() {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS && gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			Player player = this.getCardOwner();
			Player enemy = this.getEnemyPlayer();

			PositionID attacker = this.card.getCurrentPosition().getPositionID();
			PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
			Element attackerElement = ((PokemonCard) this.card).getElement();

			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, false);

			List<PositionID> enemyBench = gameModel.getFullBenchPositions(enemy.getColor());
			for (PositionID benchPos : enemyBench)
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 20, false);

			List<PositionID> ownBench = gameModel.getFullBenchPositions(player.getColor());
			for (PositionID benchPos : ownBench)
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 20, false);
		} else
			gameModel.sendTextMessageToAllPlayers("Dragon Tornado missed!", "");
	}

	private void DragonTornado() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		PokemonCard pokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		if (!pokemon.hasCondition(PokemonCondition.KNOCKOUT)) {
			PositionID activePos = enemyActive();
			PositionID chosenBenchPos = getCardOwner().playerChoosesPositions(gameModel.getFullBenchPositions(getEnemyPlayer().getColor()), 1, true,
					"Choose a new active pokemon for " + getEnemyPlayer().getName() + "!").get(0);

			// Message clients:
			Card active = gameModel.getPosition(activePos).getTopCard();
			Card bench = gameModel.getPosition(chosenBenchPos).getTopCard();
			List<Card> cardList = new ArrayList<>();
			cardList.add(active);
			cardList.add(bench);
			gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " swaps " + active.getName() + " with " + bench.getName() + "!", cardList, "");

			// Execute swap:
			gameModel.getAttackAction().swapPokemon(chosenBenchPos, activePos);
		}
	}
}
