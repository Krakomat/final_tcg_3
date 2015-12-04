package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00283_BrocksSandshrew extends PokemonCardScript {

	private boolean changeRetreatInGameModel;

	public Script_00283_BrocksSandshrew(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Sand pit", att1Cost);

		this.changeRetreatInGameModel = false;
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
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
}
