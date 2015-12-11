package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00212_DarkVileplume extends PokemonCardScript {

	public Script_00212_DarkVileplume(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Petal Whirlwind", att1Cost);

		this.addPokemonPower("Hay Fever");
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
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			powerAllowed = false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			powerAllowed = false;
		if (this.card.getCurrentPosition().getPositionID() != null && !PositionID.isArenaPosition(this.card.getCurrentPosition().getPositionID()))
			powerAllowed = false;

		if (powerAllowed) {
			if (!gameModel.getGameModelParameters().getPower_Activated_00212_DarkVileplume().contains(this.card.getGameID()))
				gameModel.getGameModelParameters().getPower_Activated_00212_DarkVileplume().add(new Integer(this.card.getGameID()));
		} else {
			gameModel.getGameModelParameters().getPower_Activated_00212_DarkVileplume().remove(new Integer(this.card.getGameID()));
		}
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 3 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 30, true);

		if (numberHeads > 1) {
			gameModel.sendTextMessageToAllPlayers(this.card.getName() + " is confused!", "");
			this.gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.CONFUSED);
			this.gameModel.sendGameModelToAllPlayers("");
		}
	}
}
