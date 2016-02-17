package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00410_SabrinasGastly extends PokemonCardScript {

	public Script_00410_SabrinasGastly(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Spook", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is allowed to retreat the next turn:
		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is not allowed to retreat the next turn!", "");
		gameModel.getGameModelParameters().activateEffect("00410", cardGameID());
	}

	public void executePreTurnActions() {
		if (gameModel.getGameModelParameters().activeEffect("00410", cardGameID())) {
			gameModel.setRetreatExecuted(true);
			gameModel.getGameModelParameters().deactivateEffect("00410", cardGameID());
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
