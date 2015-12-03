package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Pair;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00269_BrocksOnix extends PokemonCardScript {

	public Script_00269_BrocksOnix(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Bind", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		this.addAttack("Tunneling", att2Cost);
	}

	@Override
	public boolean attackCanBeExecuted(String attackName) {
		Pair<Integer, Integer> currentPair = null;
		for (Pair<Integer, Integer> pair : gameModel.getGameModelParameters().getTunnelingUsed_00269_BrocksOnix()) {
			if (pair.getKey() == this.card.getGameID())
				currentPair = pair;
		}
		if (currentPair != null)
			return false;
		return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Bind"))
			this.Bind();
		else
			this.Tunneling();
	}

	private void Bind() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	public void executeEndTurnActions() {
		Pair<Integer, Integer> currentPair = null;
		for (Pair<Integer, Integer> pair : gameModel.getGameModelParameters().getTunnelingUsed_00269_BrocksOnix()) {
			if (pair.getKey() == this.card.getGameID())
				currentPair = pair;
		}

		if (currentPair != null) {
			gameModel.getGameModelParameters().getTunnelingUsed_00269_BrocksOnix().remove(currentPair);
			if (currentPair.getValue() > 1)
				gameModel.getGameModelParameters().getTunnelingUsed_00269_BrocksOnix().add(new Pair<Integer, Integer>(currentPair.getKey(), currentPair.getValue() - 1));
		}
	}

	private void Tunneling() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		if (gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()).size() > 2) {
			List<PositionID> fullBenchPos = gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor());
			PositionID pos1 = getCardOwner().playerChoosesPositions(fullBenchPos, 1, true, "Choose 2 bench pokemon to receive damage!").get(0);
			fullBenchPos.remove(pos1);
			PositionID pos2 = getCardOwner().playerChoosesPositions(fullBenchPos, 1, true, "Choose a bench pokemon to receive damage!").get(0);
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, pos1, 20, false);
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, pos2, 20, false);

		} else {
			for (PositionID benchPos : gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()))
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 20, false);
		}
	}
}
