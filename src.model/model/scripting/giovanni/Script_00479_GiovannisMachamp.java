package model.scripting.giovanni;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00479_GiovannisMachamp extends PokemonCardScript {

	public Script_00479_GiovannisMachamp(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Hurricane Punch", att1Cost);

		this.addPokemonPower("Fortitude");
	}

	public boolean allowIncomingCondition(PokemonCondition condition) {
		if (FortitudeCanBeExecuted() && condition == PokemonCondition.KNOCKOUT) {
			return this.Fortitude();
		}
		return true;
	}

	public boolean Fortitude() {
		gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Fortitude!", card, "");
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			PokemonCard pokemon = (PokemonCard) this.card;
			pokemon.setDamageMarks(pokemon.getHitpoints() - 10);
			return false; // Do not accept knockout condition
		}
		return true;
	}

	private boolean FortitudeCanBeExecuted() {
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

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 4 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(4);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 30, true);
	}
}
