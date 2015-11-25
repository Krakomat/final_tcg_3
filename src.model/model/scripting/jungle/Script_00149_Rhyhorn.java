package model.scripting.jungle;

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

public class Script_00149_Rhyhorn extends PokemonCardScript {

	public Script_00149_Rhyhorn(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Leer", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.ROCK);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Horn Attack", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Leer"))
			this.leer();
		else
			this.hornAttack();
	}

	private void leer() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();

		// Flip coin to check if active pokemon is protected from attacks:
		gameModel.sendTextMessageToAllPlayers("If heads then " + attackingPokemon.getName() + " can't be attacked next turn!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " protects itself!", "");
			gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.INVULNERABLE);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void hornAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
	}
}
