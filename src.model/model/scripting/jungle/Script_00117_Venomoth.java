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

	private Element originalType;

	public Script_00117_Venomoth(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("String Shot", att1Cost);
		this.addPokemonPower("Shift");
		this.originalType = ((PokemonCard) this.card).getElement();
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		if (gameModel.getGameModelParameters().isPower_Activated_00117_Venomoth().contains(this.card.getGameID()))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() != this.getCardOwner().getColor())
			return false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;

		for (PositionID posID : gameModel.getFullBenchPositions(player.getColor())) {
			Position pos = gameModel.getPosition(posID);
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			if (pokemon.getElement() != Element.COLORLESS)
				return true;
		}
		for (PositionID posID : gameModel.getFullArenaPositions(enemy.getColor())) {
			Position pos = gameModel.getPosition(posID);
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			if (pokemon.getElement() != Element.COLORLESS)
				return true;
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
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c, "");
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

		gameModel.getGameModelParameters().isPower_Activated_00117_Venomoth().add(this.card.getGameID());
		gameModel.sendGameModelToAllPlayers("");
	}

	public void moveToPosition(PositionID targetPosition) {
		PokemonCard pokemon = (PokemonCard) this.card;

		if (!gameModel.getAttackCondition().pokemonIsInPlay(pokemon) && pokemon.getElement() != originalType)
			pokemon.setElement(originalType);
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().isPower_Activated_00117_Venomoth().contains(this.card.getGameID())) {
			gameModel.getGameModelParameters().isPower_Activated_00117_Venomoth().remove(new Integer(this.card.getGameID()));
		}
	}
}
