package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00110_MrMime extends PokemonCardScript {

	public Script_00110_MrMime(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Meditade", att1Cost);
		this.addPokemonPower("Invisible Wall");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		int damageMarks = defendingPokemon.getDamageMarks();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10 + damageMarks, true);
	}

	@Override
	public int modifyIncomingDamage(int damage, Card attacker, PositionID defender) {
		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			return damage; // no modifications allowed
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return damage;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return damage;
		if (this.card.getCurrentPosition().getPositionID() != defender || gameModel.getPosition(defender).getTopCard() != this.card)
			return damage;

		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return damage;
		else {
			if (damage >= 30) {
				this.gameModel.sendCardMessageToAllPlayers("Mr. Mime's Invisible Wall negates the damage!", pCard, "");
				return 0;
			}
			return damage;
		}
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}
}
