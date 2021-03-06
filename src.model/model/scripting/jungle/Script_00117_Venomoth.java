package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00117_Venomoth extends PokemonCardScript {

	public Script_00117_Venomoth(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Venom Powder", att1Cost);
		this.addPokemonPower("Shift");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		if (gameModel.getGameModelParameters().activeEffect("00117", cardGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;

		for (PositionID posID : gameModel.getFullBenchPositions(player.getColor())) {
			Position pos = gameModel.getPosition(posID);
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			if (pokemon.getElement() != Element.COLORLESS)
				return super.pokemonPowerCanBeExecuted(powerName);
		}
		for (PositionID posID : gameModel.getFullArenaPositions(enemy.getColor())) {
			Position pos = gameModel.getPosition(posID);
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			if (pokemon.getElement() != Element.COLORLESS)
				return super.pokemonPowerCanBeExecuted(powerName);
		}

		return false;
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is poisoned and confused!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned and confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		List<Element> elementList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullBenchPositions(player.getColor())) {
			Position pos = gameModel.getPosition(posID);
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			Element element = pokemon.getElement();
			if (element != Element.COLORLESS && !elementList.contains(element))
				elementList.add(element);
		}
		for (PositionID posID : gameModel.getFullArenaPositions(enemy.getColor())) {
			Position pos = gameModel.getPosition(posID);
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			Element element = pokemon.getElement();
			if (element != Element.COLORLESS && !elementList.contains(element))
				elementList.add(element);
		}

		Preconditions.checkArgument(elementList.size() > 0, "Error: ElementList size is 0!");
		Element chosenElement = player.playerChoosesElements(elementList, 1, true, "Choose a new type for " + this.card.getName() + "!").get(0);

		PokemonCard pokemon = (PokemonCard) this.card;
		pokemon.setElement(chosenElement);
		gameModel.sendTextMessageToAllPlayers(this.card.getName() + "'s new type is " + chosenElement + "!", "");

		gameModel.getGameModelParameters().activateEffect("00117", cardGameID());
		gameModel.sendGameModelToAllPlayers("");
	}

	public void moveToPosition(PositionID targetPosition) {
		PokemonCard pokemon = (PokemonCard) this.card;

		if (!gameModel.getAttackCondition().pokemonIsInPlay(pokemon))
			pokemon.setElement(Element.GRASS);
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().activeEffect("00117", cardGameID())) {
			gameModel.getGameModelParameters().deactivateEffect("00117", cardGameID());
		}
	}
}
