package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00226_DarkMuk extends PokemonCardScript {

	public Script_00226_DarkMuk(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Sludge Punch", att1Cost);

		this.addPokemonPower("Sticky Goo");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		this.gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
	}

	public int modifyRetreatCosts(int retreatCosts, Color color) {
		if (this.StickyGooCanBeExecuted()) {
			return retreatCosts + 2;
		}
		return super.modifyRetreatCosts(retreatCosts, color);
	}

	private boolean StickyGooCanBeExecuted() {
		PokemonCard pCard = (PokemonCard) this.card;
		PositionID posID = pCard.getCurrentPosition().getPositionID();
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() == this.getCardOwner().getColor()) // Don't modify own retreat costs
			return false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;
		if (posID != ownActive())
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		return true;
	}

}
