package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00182_Ekans extends PokemonCardScript {

	public Script_00182_Ekans(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Spit Poison", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Wrap", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Spit Poison"))
			this.spitPoison();
		else
			this.wrap();
	}

	private void spitPoison() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void wrap() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		this.gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.CONFUSED);
		this.gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
	}
}
