package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00428_SabrinasGastly extends PokemonCardScript {

	public Script_00428_SabrinasGastly(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Suffocating Gas", att1Cost);

		this.addPokemonPower("Gaseous Form");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	public void moveToPosition(PositionID targetPosition) {
		super.moveToPosition(targetPosition);
		checkGaseousFormPower();
	}

	public void playFromHand() {
		super.playFromHand();
		checkGaseousFormPower();
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		super.pokemonGotCondition(turnNumber, condition);
		checkGaseousFormPower();
	}

	public void pokemonGotConditionsRemoved(int turnNumber) {
		super.pokemonGotConditionsRemoved(turnNumber);
		checkGaseousFormPower();
	}

	public void executeEndTurnActions() {
		super.executeEndTurnActions();
		checkGaseousFormPower();
	}

	public void executePreTurnActions() {
		super.executePreTurnActions();
		checkGaseousFormPower();
	}

	private void checkGaseousFormPower() {
		boolean powerAllowed = true;
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			powerAllowed = false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			powerAllowed = false;
		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			powerAllowed = false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			powerAllowed = false;
		if (this.card.getCurrentPosition().getPositionID() != null && !PositionID.isArenaPosition(this.card.getCurrentPosition().getPositionID()))
			powerAllowed = false;

		PokemonCard pokemonCard = (PokemonCard) this.card;
		int psychic = 0;
		if (powerAllowed) {
			// Set HP:
			for (Card c : gameModel.getPosition(getCurrentPositionID()).getBasicEnergyCards()) {
				if (c.getCardId().equals("00101"))
					psychic += 10;
			}
		}
		pokemonCard.setHitpoints(40 + psychic);

		if (pokemonCard.getDamageMarks() >= pokemonCard.getHitpoints() && PositionID.isArenaPosition(this.getCurrentPositionID())
				&& !pokemonCard.hasCondition(PokemonCondition.KNOCKOUT)) {
			pokemonCard.setHitpoints(40);
			gameModel.getAttackAction().inflictConditionToPosition(getCurrentPositionID(), PokemonCondition.KNOCKOUT);
			gameModel.cleanDefeatedPositions();
		}
	}
}
