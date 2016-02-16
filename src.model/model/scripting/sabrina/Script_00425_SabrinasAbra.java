package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00425_SabrinasAbra extends PokemonCardScript {

	public Script_00425_SabrinasAbra(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Psyscan", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Quick Attack", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Psyscan"))
			this.Psyscan();
		else
			this.QuickAttack();
	}

	private void Psyscan() {
		if (gameModel.getPosition(enemyHand()).isEmpty())
			gameModel.sendTextMessageToAllPlayers("No cards in " + getEnemyPlayer().getName() + "'s hand!", "");
		else {
			getCardOwner().playerChoosesCards(gameModel.getPosition(enemyHand()).getCards(), 99, false, "Look at your opponents hand!");
		}
	}

	private void QuickAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers("If heads then this attack does 20 more damage!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}
}
