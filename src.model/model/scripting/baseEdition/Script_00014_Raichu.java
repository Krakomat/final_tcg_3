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

public class Script_00014_Raichu extends PokemonCardScript {

	public Script_00014_Raichu(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Agility", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Thunder", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Agility"))
			this.agilitaet();
		else
			this.donner();
	}

	private void agilitaet() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());

		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if active pokemon is protected:
		gameModel.sendTextMessageToAllPlayers("If heads then " + attackingPokemon.getName() + " protects itself from all effects!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " protects itself!", "");
			gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.INVULNERABLE);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void donner() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 60, true);

		// Flip coin to check if defending pokemon damages itself:
		gameModel.sendTextMessageToAllPlayers("If tails then " + attackingPokemon.getName() + " damages itself!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.TAILS) {
			gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " damages itself!", "");
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 30, true);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
