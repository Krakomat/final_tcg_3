package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00228_DarkPrimeape extends PokemonCardScript {

	public Script_00228_DarkPrimeape(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Frenzied Attack", att1Cost);

		this.addPokemonPower("Frenzy");
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
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		this.gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.CONFUSED);
	}

	public int modifyOutgoingDamage(int damageAmount) {
		if (this.frenzyCanBeExecuted())
			return damageAmount + 30;
		else
			return super.modifyOutgoingDamage(damageAmount);
	}

	private boolean frenzyCanBeExecuted() {
		PokemonCard pCard = (PokemonCard) this.card;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (pCard.hasCondition(PokemonCondition.CONFUSED))
			return true;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;

		return false;
	}
}
