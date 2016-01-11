package model.scripting.erika;

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

public class Script_00356_Erikas_Victreebel extends PokemonCardScript {

	public Script_00356_Erikas_Victreebel(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Razor Leaf", att1Cost);

		this.addPokemonPower("Fragrance Trap");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;

		if (gameModel.getGameModelParameters().getPower_Activated_00356_ErikasVictreebel().contains(this.card.getGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (gameModel.getFullBenchPositions(getEnemyPlayer().getColor()).isEmpty())
			return false;
		return super.pokemonPowerCanBeExecuted(powerName);
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().getPower_Activated_00356_ErikasVictreebel().contains(this.card.getGameID())) {
			gameModel.getGameModelParameters().getPower_Activated_00356_ErikasVictreebel().remove(new Integer(this.card.getGameID()));
		}
	}

	@Override
	public void executePokemonPower(String powerName) {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
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
		} else
			gameModel.sendTextMessageToAllPlayers("Fragrance Trap missed!", "");

		gameModel.getGameModelParameters().getPower_Activated_00356_ErikasVictreebel().add(this.card.getGameID());
		gameModel.sendGameModelToAllPlayers("");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);
	}
}
