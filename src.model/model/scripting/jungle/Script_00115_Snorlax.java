package model.scripting.jungle;

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

public class Script_00115_Snorlax extends PokemonCardScript {

	public Script_00115_Snorlax(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Bodyslam", att1Cost);
		this.addPokemonPower("Thick Skin");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c, "");
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	public boolean allowIncomingCondition(PokemonCondition condition) {
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return true; // just allow the condition

		PokemonCard pCard = (PokemonCard) this.card;
		if (!pCard.hasCondition(PokemonCondition.ASLEEP) && !pCard.hasCondition(PokemonCondition.CONFUSED) && !pCard.hasCondition(PokemonCondition.PARALYZED)) {
			if (condition == PokemonCondition.ASLEEP || condition == PokemonCondition.CONFUSED || condition == PokemonCondition.PARALYZED
					|| condition == PokemonCondition.POISONED || condition == PokemonCondition.TOXIC)
				gameModel.sendTextMessageToAllPlayers("Snorlax thick skin negates the condition effect!", "");
			return false;
		}
		return true;
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}
}
