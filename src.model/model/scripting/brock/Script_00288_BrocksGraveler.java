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

public class Script_00288_BrocksGraveler extends PokemonCardScript {

	public Script_00288_BrocksGraveler(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Tackle", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		this.addAttack("Detonate", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Tackle"))
			this.Tackle();
		else
			this.Detonate();
	}

	private void Tackle() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}

	private void Detonate() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);

		List<PositionID> enemyBench = gameModel.getFullBenchPositions(enemy.getColor());
		for (PositionID benchPos : enemyBench)
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);

		List<PositionID> ownBench = gameModel.getFullBenchPositions(player.getColor());
		for (PositionID benchPos : ownBench)
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 50, true);

		if (gameModel.getPosition(PositionID.STADIUM).size() > 0) {
			Card stadium = gameModel.getPosition(PositionID.STADIUM).getTopCard();
			gameModel.getAttackAction().discardCardToDiscardPile(PositionID.STADIUM, stadium.getGameID(), false);
			gameModel.getPosition(PositionID.STADIUM).setColor(Color.WHITE);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
