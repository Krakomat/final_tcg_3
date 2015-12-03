package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00272_BrocksZubat extends PokemonCardScript {

	public Script_00272_BrocksZubat(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Alert", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Wing Attack", att2Cost);
	}

	@Override
	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Alert") && gameModel.getFullBenchPositions(this.getCardOwner().getColor()).size() == 0)
			return false;
		else
			return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Alert"))
			this.Alert();
		else
			this.WingAttack();
	}

	private void Alert() {
		Player player = this.getCardOwner();
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " draws a card!", "");
		this.gameModel.getAttackAction().playerDrawsCards(1, getCardOwner());

		Color color = player.getColor();
		PositionID activePos = color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
		PositionID chosenBenchPos = player.playerChoosesPositions(gameModel.getFullBenchPositions(color), 1, true, "Choose a new active pokemon!").get(0);

		// Message clients:
		Card active = gameModel.getPosition(activePos).getTopCard();
		Card bench = gameModel.getPosition(chosenBenchPos).getTopCard();
		List<Card> cardList = new ArrayList<>();
		cardList.add(active);
		cardList.add(bench);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " swaps " + active.getName() + " with " + bench.getName() + "!", cardList, "");

		// Execute swap:
		gameModel.getAttackAction().swapPokemon(chosenBenchPos, activePos);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void WingAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
