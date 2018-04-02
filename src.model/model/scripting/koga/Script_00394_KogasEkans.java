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

public class Script_00394_KogasEkans extends PokemonCardScript {

	public Script_00394_KogasEkans(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Fast-Acting Poison", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If both coins shows Heads then the defending Pokemon is confused and poisoned!", "");
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				gameModel.sendTextMessageToAllPlayers(this.gameModel.getPosition(defender).getTopCard().getName() + " is confused and poisoned!", "");
				this.gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
				this.gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
			}
		}
	}
}
