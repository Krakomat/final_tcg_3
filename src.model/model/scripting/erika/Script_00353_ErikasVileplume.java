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

public class Script_00353_ErikasVileplume extends PokemonCardScript {

	public Script_00353_ErikasVileplume(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Mega Drain", att1Cost);

		this.addPokemonPower("Pollen Defense");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		int damageDealt = this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		int healAmount = (damageDealt / 2) % 2 == 0 ? (damageDealt / 2) : (damageDealt + 5) / 2;
		if (healAmount > 0)
			this.gameModel.getAttackAction().healPosition(attacker, healAmount);
	}

	public void pokemonIsDamaged(int turnNumber, int damage, PositionID source) {
		if (PollenDefenseCanBeExecuted()) {
			if (source != null)
				this.PollenDefense(source);
		}
	}

	private boolean PollenDefenseCanBeExecuted() {
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() == this.getCardOwner().getColor())
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		return true;
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	public void PollenDefense(PositionID attacker) {
		gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Pollen Defense!", card, "");

		Card defendingPokemon = gameModel.getPosition(attacker).getTopCard();
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.CONFUSED);
			gameModel.sendGameModelToAllPlayers("");
		} else
			gameModel.sendTextMessageToAllPlayers("Pollen Defense missed!", "");
	}
}
