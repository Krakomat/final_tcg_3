package model.scripting.fossil;

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
import network.client.Player;

public class Script_00164_Muk extends PokemonCardScript {

	public Script_00164_Muk(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Sludge", att1Cost);

		this.addPokemonPower("Toxic Gas");
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

	public void executePreTurnActions(Player playerOnTurn) {
		super.executePreTurnActions(playerOnTurn);
		checkPower();
	}

	private void checkPower() {
		boolean powerAllowed = true;
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			powerAllowed = false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			powerAllowed = false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			powerAllowed = false;
		if (this.card.getCurrentPosition().getPositionID() != null && !PositionID.isArenaPosition(this.card.getCurrentPosition().getPositionID()))
			powerAllowed = false;

		if (powerAllowed) {
			if (!gameModel.getGameModelParameters().activeEffect("00164", cardGameID()))
				gameModel.getGameModelParameters().activateEffect("00164", cardGameID());
		} else if (gameModel.getGameModelParameters().activeEffect("00164", cardGameID())) {
			gameModel.getGameModelParameters().deactivateEffect("00164", cardGameID());
		}
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Sludge"))
			this.sludge();
	}

	private void sludge() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		// Flip coin to check if defending pokemon is poisoned:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is poisoned!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
