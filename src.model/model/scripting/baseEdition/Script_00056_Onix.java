package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00056_Onix extends PokemonCardScript {

	public Script_00056_Onix(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		this.addAttack("Steinwurf", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		this.addAttack("Härtner", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Steinwurf"))
			this.steinwurf();
		else
			this.haertner();
	}

	private void steinwurf() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void haertner() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();
		gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " is protects itself!");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.HARDEN30);
		gameModel.sendGameModelToAllPlayers();
	}
}
