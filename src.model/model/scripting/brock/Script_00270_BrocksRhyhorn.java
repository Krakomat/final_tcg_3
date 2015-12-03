package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00270_BrocksRhyhorn extends PokemonCardScript {

	public Script_00270_BrocksRhyhorn(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Horn Toss", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Take Down", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Horn Toss"))
			this.HornToss();
		else
			this.TakeDown();
	}

	private void HornToss() {
		// Get the player:
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		if (gameModel.getFullBenchPositions(player.getColor()).size() > 0 && !defendingPokemon.hasCondition(PokemonCondition.INVULNERABLE)) {
			// Let enemy choose bench pokemon and swap it with his active:
			gameModel.sendTextMessageToAllPlayers(enemy.getName() + " chooses a new active pokemon", "");
			PositionID chosenPosition = enemy.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true, "Choose a pokemon to swap wtih your active!")
					.get(0);
			Card newPkm = gameModel.getPosition(chosenPosition).getTopCard();
			gameModel.sendTextMessageToAllPlayers(newPkm.getName() + " is the new active pokemon!", "");
			gameModel.getAttackAction().swapPokemon(defender, chosenPosition);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void TakeDown() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 10, true);
	}
}
