package model.scripting.blaine;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00455_BlainesRhyhorn extends PokemonCardScript {

	public Script_00455_BlainesRhyhorn(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		this.addAttack("Horn Charge", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Overrun", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Horn Charge"))
			this.HornCharge();
		else
			this.Overrun();
	}

	private void HornCharge() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If Tails then Horn Charge does nothing!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		else
			gameModel.sendTextMessageToAllPlayers("Horn Charge does nothing!", "");
	}

	private void Overrun() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		if (gameModel.getFullBenchPositions(getEnemyPlayer().getColor()).size() > 0) {
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " chooses a new active pokemon for " + getEnemyPlayer().getName() + "!", "");
				PositionID chosenPosition = getCardOwner().playerChoosesPositions(gameModel.getFullBenchPositions(getEnemyPlayer().getColor()), 1, true,
						"Choose a pokemon to swap with " + getEnemyPlayer().getName() + "'s active!").get(0);
				Card newPkm = gameModel.getPosition(chosenPosition).getTopCard();
				gameModel.sendTextMessageToAllPlayers(newPkm.getName() + " is the new active pokemon!", "");
				gameModel.getAttackAction().swapPokemon(defender, chosenPosition);
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}
}
