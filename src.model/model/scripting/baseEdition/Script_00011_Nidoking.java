package model.scripting.baseEdition;

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

public class Script_00011_Nidoking extends PokemonCardScript {

	public Script_00011_Nidoking(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Trash", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		this.addAttack("Toxic", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Trash"))
			this.pruegeln();
		else
			this.toxin();
	}

	private void pruegeln() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if defending pokemon damages itself:
		gameModel.sendTextMessageToAllPlayers("If heads then the attack gets stronger!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		} else {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
			gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " damages itself!", "");
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 10, true);
		}
		gameModel.sendGameModelToAllPlayers("");
	}

	private void toxin() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is infected with toxin!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.TOXIC);
		gameModel.sendGameModelToAllPlayers("");
	}
}
