package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00303_MistysSeaking extends PokemonCardScript {

	public Script_00303_MistysSeaking(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Horn Attack", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.WATER);
		this.addAttack("Mud Splash", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Horn Attack"))
			this.HornAttack();
		else
			this.MudSplash();
	}

	private void HornAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void MudSplash() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0) {
			PositionID benchPokemon = player.playerChoosesPositions(gameModel.getFullArenaPositions(enemy.getColor()), 1, true, "Choose a pokemon that receives the damage!")
					.get(0);
			gameModel.sendTextMessageToAllPlayers("If heads, then " + gameModel.getPosition(benchPokemon).getTopCard().getName() + " receives 10 damage!", "");
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS)
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPokemon, 10, false);
		}
	}
}
