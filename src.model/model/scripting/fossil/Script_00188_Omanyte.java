package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00188_Omanyte extends PokemonCardScript {

	public Script_00188_Omanyte(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Water Gun", att1Cost);

		this.addPokemonPower("Clairvoyance");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	public void moveToPosition(PositionID targetPosition) {
		super.moveToPosition(targetPosition);
		checkOmanitePower();
	}

	public void playFromHand() {
		super.playFromHand();
		checkOmanitePower();
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		super.pokemonGotCondition(turnNumber, condition);
		checkOmanitePower();
	}

	public void pokemonGotConditionsRemoved(int turnNumber) {
		super.pokemonGotConditionsRemoved(turnNumber);
		checkOmanitePower();
	}

	public void executeEndTurnActions() {
		super.executeEndTurnActions();
		checkOmanitePower();
	}

	public void executePreTurnActions() {
		super.executePreTurnActions();
		checkOmanitePower();
	}

	private void checkOmanitePower() {
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
			if (!gameModel.getGameModelParameters().activeEffect("00188", cardGameID()))
				gameModel.getGameModelParameters().activateEffect("00188", cardGameID());
		} else if (gameModel.getGameModelParameters().activeEffect("00188", cardGameID())) {
			gameModel.getGameModelParameters().deactivateEffect("00188", cardGameID());
		}

		boolean playerHasPower = false;
		Player player = this.getCardOwner();

		for (Integer gameID : gameModel.getGameModelParameters().getActiveEffectGameIDs("00188")) {
			Card amonite = gameModel.getCard(gameID);
			if (amonite.getCurrentPosition().getColor() == player.getColor())
				playerHasPower = true;
		}

		if (playerHasPower)
			gameModel.getPosition(enemyHand()).setVisible(true, player.getColor());
		else
			gameModel.getPosition(enemyHand()).setVisible(false, player.getColor());
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Water Gun"))
			this.waterGun();
	}

	private void waterGun() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		List<Element> energy = gameModel.getPosition(attacker).getEnergy();
		int waterCounter = -1;
		for (Element ele : energy)
			if (ele == Element.WATER)
				waterCounter++;

		if (waterCounter < 0)
			waterCounter = 0;
		waterCounter = waterCounter % 3;

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10 + (10 * waterCounter), true);
	}
}
