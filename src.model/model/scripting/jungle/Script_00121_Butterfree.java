package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00121_Butterfree extends PokemonCardScript {

	public Script_00121_Butterfree(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Whirlwind", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		this.addAttack("Mega Drain", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Whirlwind"))
			this.whirlwind();
		else
			this.megaDrain();
	}

	private void whirlwind() {
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

	private void megaDrain() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		int damageDealt = this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		int healAmount = (damageDealt / 2) % 2 == 0 ? (damageDealt / 2) : (damageDealt + 5) / 2;
		if (healAmount > 0)
			this.gameModel.getAttackAction().healPosition(attacker, healAmount);
	}
}
