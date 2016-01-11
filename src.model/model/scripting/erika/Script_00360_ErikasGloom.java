package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00360_ErikasGloom extends PokemonCardScript {

	public Script_00360_ErikasGloom(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Healing Pollen", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Magic Pollen", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Healing Pollen"))
			this.HealingPollen();
		else
			this.MagicPollen();
	}

	private void HealingPollen() {
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.getAttackAction().healPosition(getCurrentPositionID(), 40);
			gameModel.sendGameModelToAllPlayers("");
		} else
			gameModel.sendTextMessageToAllPlayers("Healing Pollen missed!", "");
	}

	private void MagicPollen() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		// Flip coin to check if defending pokemon is poisoned:
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			boolean asleep = getCardOwner().playerDecidesYesOrNo("Should the defending Pokemon be asleep?");
			if (asleep) {
				gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is asleep!", "");
				gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
				gameModel.sendGameModelToAllPlayers("");
			} else {
				boolean confused = getCardOwner().playerDecidesYesOrNo("Should the defending Pokemon be confused?");
				if (confused) {
					gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is confused!", "");
					gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
					gameModel.sendGameModelToAllPlayers("");
				} else {
					gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
					gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
					gameModel.sendGameModelToAllPlayers("");
				}
			}
		}
	}
}
