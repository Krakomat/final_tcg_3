package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00120_Wigglytuff extends PokemonCardScript {

	public Script_00120_Wigglytuff(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Lullaby", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Do the Wave", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Lullaby"))
			this.lullaby();
		else
			this.doTheWave();
	}

	private void lullaby() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is asleep!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void doTheWave() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		int damage = 10 + gameModel.getFullBenchPositions(player.getColor()).size() * 10;
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, damage, true);
	}
}
