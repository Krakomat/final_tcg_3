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

public class Script_00173_Graveler extends PokemonCardScript {

	public Script_00173_Graveler(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Harden", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Rock Throw", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Harden"))
			this.harden();
		else
			this.rockThrow();
	}

	private void harden() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();
		gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " is protects itself!", "");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.HARDEN30);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void rockThrow() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
	}
}
