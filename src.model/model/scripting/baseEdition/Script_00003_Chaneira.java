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

public class Script_00003_Chaneira extends PokemonCardScript {

	public Script_00003_Chaneira(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Zähneknirschen", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Risikotackle", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Zähneknirschen"))
			this.zaehneknirschen();
		else
			this.risikotackle();
	}

	private void zaehneknirschen() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();

		// Flip coin to check if active pokemon is protected:
		gameModel.sendTextMessageToAllPlayers("If heads then " + attackingPokemon.getName() + " protects itself!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c, "");
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(attackingPokemon.getName() + " protects itself!", "");
			gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.NO_DAMAGE);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void risikotackle() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 80, true);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 80, true);
	}
}
