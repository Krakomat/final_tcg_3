package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00358_ErikasExeggcute extends PokemonCardScript {

	public Script_00358_ErikasExeggcute(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Deflector", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Egg Bomb", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Deflector"))
			this.Deflector();
		else
			this.EggBomb();
	}

	private void Deflector() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		gameModel.sendTextMessageToAllPlayers(this.card.getName() + " defends itself!", "");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.HALF_DAMAGE);
	}

	private void EggBomb() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 20, true);
	}
}
