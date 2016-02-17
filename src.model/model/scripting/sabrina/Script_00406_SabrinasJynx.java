package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00406_SabrinasJynx extends PokemonCardScript {

	public Script_00406_SabrinasJynx(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Good Night", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Good Morning", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Good Night"))
			this.GoodNight();
		else
			this.GoodMorning();
	}

	private void GoodNight() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is asleep!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void GoodMorning() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		if (((PokemonCard) defendingPokemon).hasCondition(PokemonCondition.ASLEEP)) {
			gameModel.getAttackAction().cureCondition(defender, PokemonCondition.ASLEEP);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
