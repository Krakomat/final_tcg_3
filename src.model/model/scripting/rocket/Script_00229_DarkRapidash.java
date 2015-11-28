package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00229_DarkRapidash extends PokemonCardScript {

	public Script_00229_DarkRapidash(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Rear Kick", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		this.addAttack("Flame Pillar", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Rear Kick"))
			this.RearKick();
		else
			this.FlamePillar();
	}

	private void RearKick() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}

	private void FlamePillar() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		if (!gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()).isEmpty()) {
			boolean removeEnergy = player.playerDecidesYesOrNo("Do you want to remove a fire energy card for additional bench damage?");
			if (removeEnergy) {
				// Pay energy:
				List<Element> costs = new ArrayList<>();
				costs.add(Element.FIRE);
				gameModel.getAttackAction().playerPaysEnergy(player, costs, card.getCurrentPosition().getPositionID());
				gameModel.sendGameModelToAllPlayers("");

				PositionID benchDefender = player.playerChoosesPositions(gameModel.getFullBenchPositions(this.getEnemyPlayer().getColor()), 1, true,
						"Choose a pokemon that receives the damage!").get(0);
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchDefender, 10, false);
			}
		}
	}
}
