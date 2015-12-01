package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00206_DarkGolbat extends PokemonCardScript {

	public Script_00206_DarkGolbat(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Flitter", att1Cost);

		this.addPokemonPower("Sneak Attack");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public void playFromHand() {
		super.playFromHand();

		if (this.gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() == 0
				&& this.gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty()) {
			Player player = this.getCardOwner();
			boolean usePP = player.playerDecidesYesOrNo("Do you want to use Sneak Attack?");
			if (usePP) {
				gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Sneak Attack!", card, "");
				Player enemy = this.getEnemyPlayer();
				PositionID attacker = this.card.getCurrentPosition().getPositionID();
				Element attackerElement = ((PokemonCard) this.card).getElement();
				if (gameModel.getFullArenaPositions(enemy.getColor()).size() > 0) {
					PositionID defender = player.playerChoosesPositions(gameModel.getFullArenaPositions(enemy.getColor()), 1, true,
							"Choose a pokemon that receives the damage!").get(0);
					this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
				}
			}
		}
	}

	@Override
	public void executeAttack(String attackName) {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		if (gameModel.getFullArenaPositions(enemy.getColor()).size() > 0) {
			PositionID defender = player.playerChoosesPositions(gameModel.getFullArenaPositions(enemy.getColor()), 1, true,
					"Choose a pokemon that receives the damage!").get(0);
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, false);
		}
	}
}
