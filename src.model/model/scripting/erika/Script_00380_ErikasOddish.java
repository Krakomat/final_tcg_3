package model.scripting.erika;

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

public class Script_00380_ErikasOddish extends PokemonCardScript {

	public Script_00380_ErikasOddish(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Strange Powder", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		gameModel.sendTextMessageToAllPlayers("Check condition on defending pokemon!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
			gameModel.sendGameModelToAllPlayers("");
		} else {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is asleep!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
