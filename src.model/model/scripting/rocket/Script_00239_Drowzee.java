package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00239_Drowzee extends PokemonCardScript {

	public Script_00239_Drowzee(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		this.addPokemonPower("Long-Distance Hypnosis");
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Nightmare", att1Cost);
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;

		if (gameModel.getGameModelParameters().activeEffect("00239", cardGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		return super.pokemonPowerCanBeExecuted(powerName);
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().activeEffect("00239", cardGameID())) {
			gameModel.getGameModelParameters().deactivateEffect("00239", cardGameID());
		}
	}

	@Override
	public void executePokemonPower(String powerName) {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(gameModel.getPosition(enemyActive()).getTopCard().getName() + " is asleep!", "");
			gameModel.getAttackAction().inflictConditionToPosition(enemyActive(), PokemonCondition.ASLEEP);
		} else {
			gameModel.sendTextMessageToAllPlayers(gameModel.getPosition(ownActive()).getTopCard().getName() + " is asleep!", "");
			gameModel.getAttackAction().inflictConditionToPosition(ownActive(), PokemonCondition.ASLEEP);
		}

		gameModel.getGameModelParameters().activateEffect("00239", cardGameID());
		gameModel.sendGameModelToAllPlayers("");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is asleep!", "");
		gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
		gameModel.sendGameModelToAllPlayers("");
	}
}
