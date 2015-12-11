package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00333_LtSurgesRaticate extends PokemonCardScript {

	public Script_00333_LtSurgesRaticate(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Super Fang", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int remainingHP = defendingPokemon.getHitpoints() - defendingPokemon.getDamageMarks();
		remainingHP = remainingHP / 2;

		// Round up:
		if (remainingHP % 2 != 0)
			remainingHP += 5;

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, remainingHP, true);
	}
}
