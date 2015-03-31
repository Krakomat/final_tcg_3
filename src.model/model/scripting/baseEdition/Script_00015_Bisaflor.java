package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00015_Bisaflor extends PokemonCardScript {

	public Script_00015_Bisaflor(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Solarstrahl", att1Cost);
		this.addPokemonPower("Energietransfer");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 60, true);
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Can be executed when Bisaflow is in play and isn't asleep, confused or paralyzed, there is some pokemon with grass energy on the owners side of the field
		// and its owner is on turn and finally there have to be at least 2 pokemon on the owners side of the field
		PokemonCard pCard = (PokemonCard) this.card;
		Player player = this.getCardOwner();

		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() != this.getCardOwner().getColor())
			return false;
		if (gameModel.getFullArenaPositions(player.getColor()).size() < 2)
			return false;

		for (PositionID posID : gameModel.getFullArenaPositions(player.getColor())) {
			Position pos = gameModel.getPosition(posID);
			if (pos.getAmountOfEnergy(Element.GRASS) > 0)
				return true;
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

		// Create an integer list with the starting energy specification:
		List<Integer> energyList = new ArrayList<Integer>();
		// Create the integer list with the pokemons max distributions:
		List<Integer> maxDistList = new ArrayList<Integer>();
		int sum = 0;
		for (Position pos : positionList) {
			energyList.add(pos.getAmountOfEnergy(Element.GRASS));
			maxDistList.add(60); // infinite many energy cards allowed
			sum = sum + pos.getAmountOfEnergy(Element.GRASS);
		}

		// Send data to player and let him distribute:
		List<Integer> distribution = player.playerDistributesDamage(positionList, energyList, maxDistList, DistributionMode.GRASS_ENERGY);

		// Check if lists have the same size:
		if (distribution.size() != energyList.size()) {
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

		// Distribute energy:
		List<EnergyCard> energyCardPool = new ArrayList<>();
		for (int i = 0; i < positionList.size(); i++) {
			Position position = positionList.get(i);

			// Remove all grass energy:
			List<EnergyCard> energyCards = new ArrayList<>();
			for (Card e : position.getEnergyCards())
				energyCards.add((EnergyCard) e);
			for (EnergyCard e : energyCards) {
				if (e.getProvidedEnergy().contains(Element.GRASS)) {
					position.removeFromPosition(e);
					e.setCurrentPosition(null);
					energyCardPool.add(e);
				}
			}
		}

		// Add grass energy:
		for (int i = 0; i < positionList.size(); i++) {
			Position position = positionList.get(i);
			for (int j = 0; j < distribution.get(i); j++) {
				EnergyCard grass = energyCardPool.remove(0);
				position.getCards().add(0, grass);
				grass.setCurrentPosition(position);
			}
		}

		gameModel.sendTextMessageToAllPlayers("Energy cards have been reordered!", "");
		gameModel.sendGameModelToAllPlayers("");
	}
}
