package model.scripting.erika;

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

public class Script_00365_ErikasBellsprout extends PokemonCardScript {

	public Script_00365_ErikasBellsprout(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Thunder Wave", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Selfdestruct", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Thunder Wave"))
			this.donnerwelle();
		else
			this.finale();
	}

	private void donnerwelle() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void finale() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);

		List<PositionID> enemyBench = gameModel.getFullBenchPositions(enemy.getColor());
		for (PositionID benchPos : enemyBench)
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);

		List<PositionID> ownBench = gameModel.getFullBenchPositions(player.getColor());
		for (PositionID benchPos : ownBench)
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 40, true);
	}
}
