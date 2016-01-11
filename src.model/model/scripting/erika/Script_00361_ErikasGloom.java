package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00361_ErikasGloom extends PokemonCardScript {

	public Script_00361_ErikasGloom(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Dream Dance", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Vile Smell", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Dream Dance"))
			this.DreamDance();
		else
			this.VileSmell();
	}

	private void DreamDance() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		gameModel.sendTextMessageToAllPlayers("If heads then " + this.card.getName() + " is asleep!", "");
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is asleep!", "");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.ASLEEP);
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void VileSmell() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		gameModel.sendTextMessageToAllPlayers("If heads then " + this.card.getName() + " is confused!", "");
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is confused!", "");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.CONFUSED);
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
		gameModel.sendGameModelToAllPlayers("");
	}
}
