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

public class Script_00058_Pikachu extends PokemonCardScript {

	public Script_00058_Pikachu(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Nagen", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Donnerschock", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Nagen"))
			this.nagen();
		else
			this.donnerschock();
	}

	private void nagen() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void donnerschock() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		// Flip coin to check if defending pokemon damages itself:
		gameModel.sendTextMessageToAllPlayers("If tails then " + attackingPokemon.getName() + " damages itself!");
		Coin c = gameModel.getAttackAction().flipACoin();
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c);
		if (c == Coin.TAILS) {
			gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " damages itself!");
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 10, true);
			gameModel.sendGameModelToAllPlayers();
		}
	}
}
