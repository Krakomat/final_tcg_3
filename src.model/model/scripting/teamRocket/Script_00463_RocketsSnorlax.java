package model.scripting.teamRocket;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00463_RocketsSnorlax extends PokemonCardScript {

	public Script_00463_RocketsSnorlax(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Collapse", att1Cost);

		this.addPokemonPower("Restless Sleep");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		gameModel.sendTextMessageToAllPlayers(card.getName() + " is asleep!", "");
		gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.ASLEEP);
		gameModel.sendGameModelToAllPlayers("");
	}

	public int modifyIncomingDamage(int damage, Card attacker, PositionID defender) {
		if (RestlessSleepCanBeExecuted() && defender == this.getCurrentPositionID()) {
			if (attacker != null && attacker.getCurrentPosition() != null)
				this.RestlessSleep(attacker.getCurrentPosition().getPositionID());
		}
		return damage;
	}

	private boolean RestlessSleepCanBeExecuted() {
		PokemonCard pCard = (PokemonCard) this.card;
		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() == this.getCardOwner().getColor())
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		return pCard.hasCondition(PokemonCondition.ASLEEP);
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	public void RestlessSleep(PositionID attacker) {
		gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Restless Sleep!", card, "");
		// Inflict damage:
		PositionID ownPosition = this.card.getCurrentPosition().getPositionID();
		gameModel.getAttackAction().inflictDamageToPosition(Element.COLORLESS, ownPosition, attacker, 20, true);
	}
}
