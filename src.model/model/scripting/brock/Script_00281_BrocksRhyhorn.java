package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00281_BrocksRhyhorn extends PokemonCardScript {

	public Script_00281_BrocksRhyhorn(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Drill Tackle", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If one coin shows Tails then Drill Tackle does nothing!", "");
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 70, true);
			} else
				gameModel.sendTextMessageToAllPlayers("Drill Tackle does nothing!", "");
		} else
			gameModel.sendTextMessageToAllPlayers("Drill Tackle does nothing!", "");
	}
}
