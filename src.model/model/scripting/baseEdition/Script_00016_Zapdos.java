package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00016_Zapdos extends PokemonCardScript {

	public Script_00016_Zapdos(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Thunder", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Thunderbolt", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Thunder"))
			this.donner();
		else
			this.donnerblitz();
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

	private void donnerblitz() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 100, true);

		// Discard all energy cards:
		gameModel.getAttackAction().removeAllEnergyFromPosition(attacker);
		gameModel.sendGameModelToAllPlayers("");
	}
}
