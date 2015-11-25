package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00183_Geodude extends PokemonCardScript {

	public Script_00183_Geodude(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Stone Barrage", att1Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Stone Barrage"))
			this.stoneBarrage();
	}

	private void stoneBarrage() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		int numberHeads = 0;
		Coin c = null;
		gameModel.sendTextMessageToAllPlayers("Flip coins until tails occurs...", "");
		do {
			c = gameModel.getAttackAction().flipACoin();
			if (c == Coin.HEADS)
				numberHeads++;
		} while (c == Coin.HEADS);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 10, true);
	}
}
