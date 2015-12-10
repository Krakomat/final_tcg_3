package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00322_MistysSeel extends PokemonCardScript {

	private boolean changeRetreatInGameModel;

	public Script_00322_MistysSeel(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		this.addAttack("Frostbite", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Mirage", att2Cost);

		this.changeRetreatInGameModel = false;
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Frostbite"))
			this.Frostbite();
		else
			this.Mirage();
	}

	private void Frostbite() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is not allowed to retreat the next turn!", "");
		changeRetreatInGameModel = true;
	}

	public void executePreTurnActions() {
		if (changeRetreatInGameModel) {
			gameModel.setRetreatExecuted(true);
			changeRetreatInGameModel = false;
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void Mirage() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is blinded!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.BLIND);
		gameModel.sendGameModelToAllPlayers("");
	}
}
