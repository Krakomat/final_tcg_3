package model.scripting.koga;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00398_KogasTangela extends PokemonCardScript {

	public Script_00398_KogasTangela(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Sleep Powder", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Grasping Vine", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Sleep Powder"))
			this.SleepPowder();
		else
			this.GraspingVine();
	}

	private void SleepPowder() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
		this.gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
	}

	private void GraspingVine() {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " draws 2 cards!", "");
			this.gameModel.getAttackAction().playerDrawsCards(2, getCardOwner());
		} else
			gameModel.sendTextMessageToAllPlayers("Grasping Vine d failed!", "");

	}
}
