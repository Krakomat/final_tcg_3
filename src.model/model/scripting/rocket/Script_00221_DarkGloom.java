package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00221_DarkGloom extends PokemonCardScript {

	public Script_00221_DarkGloom(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Poisonpowder", att1Cost);
		
		this.addPokemonPower("Pollen Stench");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;

		if (gameModel.getGameModelParameters().getPower_Activated_00221_DarkGloom().contains(this.card.getGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		return super.pokemonPowerCanBeExecuted(powerName);
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().getPower_Activated_00221_DarkGloom().contains(this.card.getGameID())) {
			gameModel.getGameModelParameters().getPower_Activated_00221_DarkGloom().remove(new Integer(this.card.getGameID()));
		}
	}

	@Override
	public void executePokemonPower(String powerName) {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(gameModel.getPosition(enemyActive()).getTopCard().getName() + " is confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(enemyActive(), PokemonCondition.CONFUSED);
		} else {
			gameModel.sendTextMessageToAllPlayers(gameModel.getPosition(ownActive()).getTopCard().getName() + " is confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(ownActive(), PokemonCondition.CONFUSED);
		}

		gameModel.getGameModelParameters().getPower_Activated_00221_DarkGloom().add(this.card.getGameID());
		gameModel.sendGameModelToAllPlayers("");
	}


	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		this.gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
	}
}
