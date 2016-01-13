package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00153_Aerodactyl extends PokemonCardScript {

	public Script_00153_Aerodactyl(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Wing Attack", att1Cost);

		this.addPokemonPower("Prehistoric Power");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	public void moveToPosition(PositionID targetPosition) {
		super.moveToPosition(targetPosition);
		checkPower();
	}

	public void playFromHand() {
		super.playFromHand();
		checkPower();
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		super.pokemonGotCondition(turnNumber, condition);
		checkPower();
	}

	public void pokemonGotConditionsRemoved(int turnNumber) {
		super.pokemonGotConditionsRemoved(turnNumber);
		checkPower();
	}

	public void executeEndTurnActions() {
		super.executeEndTurnActions();
		checkPower();
	}

	public void executePreTurnActions() {
		super.executePreTurnActions();
		checkPower();
	}

	private void checkPower() {
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

		if (powerAllowed) {
			if (!gameModel.getGameModelParameters().activeEffect("00153", cardGameID()))
				gameModel.getGameModelParameters().activateEffect("00153", cardGameID());
		} else if (gameModel.getGameModelParameters().activeEffect("00153", cardGameID())) {
			gameModel.getGameModelParameters().deactivateEffect("00153", cardGameID());
		}
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Wing Attack"))
			this.wingAttack();
	}

	private void wingAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}
}
