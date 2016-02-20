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

public class Script_00454_BlainesPonyta extends PokemonCardScript {

	public Script_00454_BlainesPonyta(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Hind Kick", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		if (gameModel.getFullBenchPositions(getCardOwner().getColor()).size() > 0) {
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " chooses a new active pokemon", "");
				PositionID chosenPosition = getCardOwner()
						.playerChoosesPositions(gameModel.getFullBenchPositions(getCardOwner().getColor()), 1, true, "Choose a pokemon to swap wtih your active!").get(0);
				Card newPkm = gameModel.getPosition(chosenPosition).getTopCard();
				gameModel.sendTextMessageToAllPlayers(newPkm.getName() + " is the new active pokemon!", "");
				gameModel.getAttackAction().swapPokemon(attacker, chosenPosition);
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}
}
