package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00291_BrocksDiglett extends PokemonCardScript {

	public Script_00291_BrocksDiglett(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		this.addAttack("Suprise Attack", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Tremor", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Suprise Attack"))
			this.SupriseAttack();
		else
			this.Tremor();
	}

	private void SupriseAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If Tails then Suprise Attack does nothing!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		else
			gameModel.sendTextMessageToAllPlayers("Suprise Attack does nothing!", "");
	}

	private void Tremor() {
		Player player = this.getCardOwner();

		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);

		List<PositionID> ownBench = gameModel.getFullBenchPositions(player.getColor());
		for (PositionID benchPos : ownBench)
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);
	}
}
