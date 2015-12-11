package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00344_LtSurgesJolteon extends PokemonCardScript {

	public Script_00344_LtSurgesJolteon(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("High Voltage", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Thunder Flare", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("High Voltage"))
			this.HighVoltage();
		else
			this.ThunderFlare();
	}

	private void HighVoltage() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " can't play Trainer cards next turn!", "");
			gameModel.getGameModelParameters().setAllowedToPlayTrainerCards((short) 2);
		}
	}

	private void ThunderFlare() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		int damage = ((PokemonCard) this.card).getDamageMarks();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30 + damage, true);

		gameModel.sendTextMessageToAllPlayers("If heads then " + this.card.getName() + " hurts itself!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.TAILS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 30, true);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
