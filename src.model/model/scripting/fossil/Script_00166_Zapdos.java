package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00166_Zapdos extends PokemonCardScript {

	public Script_00166_Zapdos(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Thunderstorm", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Thunderstorm"))
			this.thunderstorm();
	}

	private void thunderstorm() {
		Player enemyPlayer = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);

		int numberOfTails = 0;
		for (PositionID benchPos : gameModel.getFullBenchPositions(enemyPlayer.getColor())) {
			gameModel.sendTextMessageToAllPlayers(gameModel.getPosition(benchPos).getTopCard().getName() + ":", "");
			Coin c = gameModel.getAttackAction().flipACoin();
			if (c == Coin.HEADS)
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchPos, 20, false);
			else
				numberOfTails++;
		}

		if (numberOfTails > 0)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, numberOfTails * 10, true);
	}
}
