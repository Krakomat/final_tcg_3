package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00135_Tauros extends PokemonCardScript {

	public Script_00135_Tauros(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Stomp", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Rampage", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Stomp"))
			this.stomp();
		else
			this.rampage();
	}

	private void stomp() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers("If heads then this attack does 10 more damage!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}

	private void rampage() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard attackingPokemon = (PokemonCard) gameModel.getPosition(attacker).getTopCard();

		int damageMarks = attackingPokemon.getDamageMarks();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20 + damageMarks, true);
		
		// Flip coin to check if attacking pokemon is confused:
		gameModel.sendTextMessageToAllPlayers("If tails then " + attackingPokemon.getName() + " is confused!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.TAILS) {
			gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " is confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.CONFUSED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
