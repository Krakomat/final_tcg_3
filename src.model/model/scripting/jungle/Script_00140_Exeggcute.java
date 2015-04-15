package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00140_Exeggcute extends PokemonCardScript {

	public Script_00140_Exeggcute(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Hypnosis", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		this.addAttack("Leech Seed", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Hypnosis"))
			this.hypnosis();
		else
			this.leechSeed();
	}

	private void hypnosis() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();

		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is asleep!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void leechSeed() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		int amount = this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Heal:
		if (amount > 0)
			gameModel.getAttackAction().healPosition(attacker, 10);
	}
}
