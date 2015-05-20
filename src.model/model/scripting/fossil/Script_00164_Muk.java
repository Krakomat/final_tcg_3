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
	public void executeAttack(String attackName) {
		if (attackName.equals("Sludge"))
			this.sludge();
	}

	public void moveToPosition(PositionID targetPosition) {
		super.moveToPosition(targetPosition);

		// Remove gameID to the power list of Muk:
		if (!PositionID.isArenaPosition(targetPosition))
			this.gameModel.getGameModelParameters().getPower_Active_00164_Muk().remove(new Integer(this.card.getGameID()));
	}

	public void playFromHand() {
		super.playFromHand();

		// Add gameID to the power list of Muk:
		this.gameModel.getGameModelParameters().getPower_Active_00164_Muk().add(this.card.getGameID());
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		super.pokemonGotCondition(turnNumber, condition);

		// Remove gameID to the power list of Muk:
		if (condition == PokemonCondition.ASLEEP || condition == PokemonCondition.CONFUSED || condition == PokemonCondition.PARALYZED)
			this.gameModel.getGameModelParameters().getPower_Active_00164_Muk().remove(new Integer(this.card.getGameID()));
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
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c, "");
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
