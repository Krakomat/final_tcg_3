package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00001_Simsala extends PokemonCardScript {

	public Script_00001_Simsala(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Confuse Ray", att1Cost);
		this.addPokemonPower("Damage Swap");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		// Flip coin to check if defending pokemon is poisoned:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is confused!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Can be executed when Simsala is in play and isn't asleep, confused or paralyzed, there is some damaged pokemon on the owners side of the field and its
		// owner is on turn and finally there have to be at least 2 pokemon on the owners side of the field
		PokemonCard pCard = (PokemonCard) this.card;
		Player player = this.getCardOwner();

		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (gameModel.getFullArenaPositions(player.getColor()).size() < 2)
			return false;

		for (PositionID posID : gameModel.getFullArenaPositions(player.getColor())) {
			Position pos = gameModel.getPosition(posID);
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			if (pokemon.getDamageMarks() > 0)
				return super.pokemonPowerCanBeExecuted(powerName);
		}
		return false;
	}

	@Override
	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();

		// Create the position list with all pokemon on the owners side of the field:
		List<Position> positionList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(player.getColor())) {
			Position pos = gameModel.getPosition(posID);
			positionList.add(pos);
		}

		// Create an integer list with the starting damage specification:
		List<Integer> damageList = new ArrayList<Integer>();
		// Create the integer list with the pokemons hitpoints for the max distribution:
		List<Integer> maxDistList = new ArrayList<Integer>();
		int sum = 0;
		for (Position pos : positionList) {
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			damageList.add(pokemon.getDamageMarks() / 10);
			maxDistList.add((pokemon.getHitpoints() / 10) - 1); // not allowed to knockout pokemon!
			sum = sum + pokemon.getDamageMarks() / 10;
		}

		// Send data to player and let him distribute:
		List<Integer> distribution = player.playerDistributesDamage(positionList, damageList, maxDistList, DistributionMode.DAMAGE);

		// Check if lists have the same size:
		if (distribution.size() != damageList.size()) {
			System.err.println("Error: Distribution was not correct(ListSize)!");
			gameModel.playerLoses(player);
		}

		// Check if the distribution sum is valid:
		int chosenSum = 0;
		for (Integer i : distribution) {
			chosenSum = chosenSum + i;
		}
		if (chosenSum != sum) {
			System.err.println("Error: Distribution was not correct(sum)!");
			gameModel.playerLoses(player);
		}

		// Check if no pokemon would get knocked out by the given distribution:
		for (int i = 0; i < positionList.size(); i++) {
			Position position = positionList.get(i);
			PokemonCard pokemon = (PokemonCard) position.getTopCard();
			int hitPoints = pokemon.getHitpoints() / 10;
			int distPoints = distribution.get(i);
			if (hitPoints <= distPoints) {
				System.err.println("Error: Distribution was not correct(Pokemon would get knocked out)!");
				gameModel.playerLoses(player);
			}
		}

		// Distribute damage:
		for (int i = 0; i < positionList.size(); i++) {
			Position position = positionList.get(i);
			PokemonCard pokemon = (PokemonCard) position.getTopCard();
			int damage = distribution.get(i) * 10;
			pokemon.setDamageMarks(damage);
		}

		gameModel.sendTextMessageToAllPlayers("Damage marks have been reordered!", "");
		gameModel.sendGameModelToAllPlayers("");
	}
}
