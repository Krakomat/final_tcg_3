package model.scripting.brock;

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

public class Script_00282_BrocksSandshrew extends PokemonCardScript {

	public Script_00282_BrocksSandshrew(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Defense Curl", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Rolling Attack", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Defense Curl"))
			this.DefenseCurl();
		else
			this.RollingAttack();
	}

	private void DefenseCurl() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();

		// Flip coin to check if active pokemon is protected:
		gameModel.sendTextMessageToAllPlayers("If heads then " + attackingPokemon.getName() + " protects itself!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " protects itself!", "");
			gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.NO_DAMAGE);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void RollingAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
