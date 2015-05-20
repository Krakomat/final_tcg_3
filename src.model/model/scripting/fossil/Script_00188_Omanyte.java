package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
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

	public void moveToPosition(PositionID targetPosition) {
		super.moveToPosition(targetPosition);

		// Remove gameID to the power list of Omanite:
		if (!PositionID.isArenaPosition(targetPosition)) {
			this.gameModel.getGameModelParameters().getPower_Activated_00188_Omanite().remove(new Integer(this.card.getGameID()));
		} else if (!gameModel.getGameModelParameters().getPower_Activated_00188_Omanite().contains(this.card.getGameID()))
			gameModel.getGameModelParameters().getPower_Activated_00188_Omanite().add(new Integer(this.card.getGameID()));
		checkOmanitePower();
	}

	public void playFromHand() {
		super.playFromHand();

		if (!gameModel.getGameModelParameters().getPower_Activated_00188_Omanite().contains(this.card.getGameID()))
			gameModel.getGameModelParameters().getPower_Activated_00188_Omanite().add(new Integer(this.card.getGameID()));
		checkOmanitePower();
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		super.pokemonGotCondition(turnNumber, condition);

		// Remove gameID to the power list of Aerodactyl:
		if (condition == PokemonCondition.ASLEEP || condition == PokemonCondition.CONFUSED || condition == PokemonCondition.PARALYZED) {
			gameModel.getGameModelParameters().getPower_Activated_00188_Omanite().remove(new Integer(this.card.getGameID()));
		}
		checkOmanitePower();
	}

	public void pokemonGotConditionsRemoved(int turnNumber) {
		if (!gameModel.getGameModelParameters().getPower_Activated_00188_Omanite().contains(this.card.getGameID()))
			gameModel.getGameModelParameters().getPower_Activated_00188_Omanite().add(new Integer(this.card.getGameID()));
		checkOmanitePower();
	}

	private void checkOmanitePower() {
		boolean playerHasPower = false;
		Player player = this.getCardOwner();

		for (Integer gameID : gameModel.getGameModelParameters().getPower_Activated_00188_Omanite()) {
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

	private PositionID enemyHand() {
		Player enemy = getEnemyPlayer();
		if (enemy.getColor() == Color.BLUE)
			return PositionID.BLUE_HAND;
		else
			return PositionID.RED_HAND;
	}
}
