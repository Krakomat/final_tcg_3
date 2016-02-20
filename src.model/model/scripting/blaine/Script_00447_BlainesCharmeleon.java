package model.scripting.blaine;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00447_BlainesCharmeleon extends PokemonCardScript {

	public Script_00447_BlainesCharmeleon(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		this.addAttack("Fire Claws", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		this.addAttack("Bonfire", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Fire Claws"))
			this.FireClaws();
		else
			this.Bonfire();
	}

	private void FireClaws() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}

	private void Bonfire() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int heads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		List<Element> costs = new ArrayList<>();
		for (int i = 0; i < heads; i++)
			costs.add(Element.FIRE);

		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " has to pay 3 fire energy!", "");
		gameModel.getAttackAction().playerPaysEnergy(getCardOwner(), costs, attacker);

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10 * heads, false);
		List<PositionID> enemyBench = gameModel.getFullBenchPositions(getEnemyPlayer().getColor());
		for (PositionID benchPos : enemyBench)
			gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 10 * heads, false);
	}
}
