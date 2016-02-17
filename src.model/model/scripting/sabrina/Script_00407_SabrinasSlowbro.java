package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Triple;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00407_SabrinasSlowbro extends PokemonCardScript {

	public Script_00407_SabrinasSlowbro(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Naptime", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Screaming Headbutt", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Naptime"))
			this.Naptime();
		else
			this.ScreamingHeadbutt();
	}

	private void Naptime() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();

		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.getAttackAction().healPosition(attacker, 30);
			gameModel.sendTextMessageToAllPlayers(card.getName() + " is asleep!", "");
			gameModel.getAttackAction().inflictConditionToPosition(attacker, PokemonCondition.ASLEEP);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void ScreamingHeadbutt() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);

		// Block attack:
		gameModel.getGameModelParameters().getBlockedAttacks().add(new Triple<Integer, String, Integer>(this.card.getGameID(), "Screaming Headbutt", 3));
	}
}
