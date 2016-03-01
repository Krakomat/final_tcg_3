package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00118_Victreebel extends PokemonCardScript {

	public Script_00118_Victreebel(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Lure", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		this.addAttack("Acid", att2Cost);
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Lure") && gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()).size() == 0)
			return false;
		return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Lure"))
			this.lure();
		else
			this.acid();
	}

	private void lure() {
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
		this.gameModel.sendGameModelToAllPlayers("");
	}

	private void acid() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is allowed to retreat the next turn:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is not allowed to retreat the next turn!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is not allowed to retreat the next turn!", "");
			gameModel.getGameModelParameters().activateEffect("00118", cardGameID());
		}
	}

	public void executePreTurnActions(Player player) {
		if (gameModel.getGameModelParameters().activeEffect("00118", cardGameID())) {
			gameModel.setRetreatExecuted(true);
			gameModel.getGameModelParameters().deactivateEffect("00118", cardGameID());
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
