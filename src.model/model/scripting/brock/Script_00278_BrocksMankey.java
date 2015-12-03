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

public class Script_00278_BrocksMankey extends PokemonCardScript {

	public Script_00278_BrocksMankey(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Taunt", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		this.addAttack("Light Kick", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Taunt"))
			this.Taunt();
		else
			this.LightKick();
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Taunt")) {
			// Cannot be used if the enemy player has no pokemon on his bench
			Player player = this.getEnemyPlayer();
			PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
			PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
			if (gameModel.getFullBenchPositions(player.getColor()).size() == 0 || defendingPokemon.hasCondition(PokemonCondition.INVULNERABLE))
				return false;
		}
		return super.attackCanBeExecuted(attackName);
	}

	private void Taunt() {
		// Get the player:
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		if (gameModel.getFullBenchPositions(player.getColor()).size() > 0 && !defendingPokemon.hasCondition(PokemonCondition.INVULNERABLE)) {
			// Let enemy choose bench pokemon and swap it with his active:
			gameModel.sendTextMessageToAllPlayers(enemy.getName() + " chooses a new active pokemon", "");
			PositionID chosenPosition = player
					.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true, "Choose a pokemon to swap wtih your enemies active one!").get(0);
			Card newPkm = gameModel.getPosition(chosenPosition).getTopCard();
			gameModel.sendTextMessageToAllPlayers(newPkm.getName() + " is the new active pokemon!", "");
			gameModel.getAttackAction().swapPokemon(defender, chosenPosition);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void LightKick() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}
}
