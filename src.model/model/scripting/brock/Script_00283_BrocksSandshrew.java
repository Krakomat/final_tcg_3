package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00283_BrocksSandshrew extends PokemonCardScript {

	public Script_00283_BrocksSandshrew(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Sand Pit", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is not allowed to retreat the next turn!", "");
		gameModel.getGameModelParameters().activateEffect("00283", cardGameID());
	}

	public void executePreTurnActions(Player player) {
		if (gameModel.getGameModelParameters().activeEffect("00283", cardGameID())) {
			gameModel.setRetreatExecuted(true);
			gameModel.getGameModelParameters().deactivateEffect("00283", cardGameID());
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
