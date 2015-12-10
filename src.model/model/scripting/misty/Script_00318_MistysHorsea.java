package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00318_MistysHorsea extends PokemonCardScript {

	public Script_00318_MistysHorsea(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		this.addAttack("Ink Spurt", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is blinded!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.BLIND);
		gameModel.sendGameModelToAllPlayers("");
	}
}
