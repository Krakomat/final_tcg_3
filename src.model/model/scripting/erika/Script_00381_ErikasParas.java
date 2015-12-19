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

public class Script_00381_ErikasParas extends PokemonCardScript {

	public Script_00381_ErikasParas(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Irongrip", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		this.addAttack("Poison Spore", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Irongrip"))
			this.Irongrip();
		else
			this.PoisonSpore();
	}

	private void Irongrip() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void PoisonSpore() {
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is poisoned!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);

			List<PositionID> enemyBench = gameModel.getFullBenchPositions(enemy.getColor());
			for (PositionID benchPos : enemyBench)
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10, false);

			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
