package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00123_Exeggutor extends PokemonCardScript {

	public Script_00123_Exeggutor(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Teleport", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Big Eggsplosion", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Teleport"))
			this.teleport();
		else
			this.bigEggsplosion();
	}

	private void teleport() {
		Player player = this.getCardOwner();
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
	}

	private void bigEggsplosion() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int coinNumber = this.card.getCurrentPosition().getEnergy().size();
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips " + coinNumber + " coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(coinNumber);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 20, true);
	}
}
