package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00369_ErikasTangela extends PokemonCardScript {

	public Script_00369_ErikasTangela(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Vine Slap", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Stretch Vine", att2Cost);
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Stretch Vine") && gameModel.getFullBenchPositions(getEnemyPlayer().getColor()).isEmpty())
			return false;
		return true;
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Vine Slap"))
			this.VineSlap();
		else
			this.StretchVine();
	}

	private void VineSlap() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void StretchVine() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		if (gameModel.getFullBenchPositions(enemy.getColor()).size() > 0) {
			PositionID benchDefender = player.playerChoosesPositions(gameModel.getFullBenchPositions(enemy.getColor()), 1, true, "Choose a pokemon that receives the damage!")
					.get(0);
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchDefender, 20, false);
		}
	}
}
