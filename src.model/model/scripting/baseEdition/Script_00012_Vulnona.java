package model.scripting.baseEdition;

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

public class Script_00012_Vulnona extends PokemonCardScript {

	public Script_00012_Vulnona(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Lockvogel", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		this.addAttack("Feuersturm", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Lockvogel"))
			this.lockvogel();
		else
			this.feuersturm();
	}

	private void lockvogel() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0 && !defendingPokemon.hasCondition(PokemonCondition.INVULNERABLE)) {
			// Let player choose bench pokemon and swap it with the enemies active pokemon:
			PositionID chosenPosition = player.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true,
					"Choose a pokemon to swap wtih your active!").get(0);
			Card newPkm = gameModel.getPosition(chosenPosition).getTopCard();
			gameModel.sendTextMessageToAllPlayers(newPkm.getName() + " is the new active pokemon!", "");
			gameModel.getAttackAction().swapPokemon(defender, chosenPosition);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void feuersturm() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());

		// Pay energy:
		List<Element> costs = new ArrayList<>();
		costs.add(Element.FIRE);
		gameModel.getAttackAction().playerPaysEnergy(player, costs, card.getCurrentPosition().getPositionID());

		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 80, true);
	}
}
