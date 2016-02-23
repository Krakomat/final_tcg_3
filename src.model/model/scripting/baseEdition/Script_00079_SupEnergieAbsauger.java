package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00079_SupEnergieAbsauger extends TrainerCardScript {

	public Script_00079_SupEnergieAbsauger(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played, if there is a pokemon with energy in the enemies arena and a pokemon with energy in the own arena:
		if (this.getPositionsWithEnergy(this.getEnemyPlayer()).size() > 0 && this.getPositionsWithEnergy(this.getCardOwner()).size() > 0) {
			if (gameModel.stadiumActive("00464") && gameModel.getPosition(ownHand()).size() < 3)
				return null;
			return PlayerAction.PLAY_TRAINER_CARD;
		}
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);

		if (gameModel.stadiumActive("00464")) {
			// Choose two own cards:
			List<Card> chosenCards = player.playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), 2, true, "Choose 2 cards to discard!");
			for (Card chosenCard : chosenCards) {
				gameModel.sendCardMessageToAllPlayers(player.getName() + " discards " + chosenCard.getName() + "!", chosenCard, "");
				gameModel.getAttackAction().discardCardToDiscardPile(ownHand(), chosenCard.getGameID(), true);
			}
		}

		// Remove 1 energy card from one of your own pokemon:
		PositionID chosenPosition = player.playerChoosesPositions(this.getPositionsWithEnergy(player), 1, true, "Choose a pokemon to pay energy!").get(0);
		Card pokemon = gameModel.getPosition(chosenPosition).getTopCard();

		// Pay one energy card:
		List<Card> energyList = gameModel.getPosition(chosenPosition).getEnergyCards();
		List<Card> chooseCardList = new ArrayList<>();
		for (Card c : energyList)
			chooseCardList.add(c);
		// Choose energy card:
		Card chosenEnergy = player.playerChoosesCards(chooseCardList, 1, true, "Choose an energy card to discard!").get(0);
		// Message clients:
		gameModel.sendCardMessageToAllPlayers(player.getName() + " removes " + chosenEnergy.getName() + " from " + pokemon.getName() + "!", chosenEnergy, "");
		gameModel.getAttackAction().discardCardToDiscardPile(chosenPosition, chosenEnergy.getGameID(), true);

		// Remove up to 2 energy cards from one of the enemies pokemon:
		PositionID chosenEnemyPosition = player.playerChoosesPositions(this.getPositionsWithEnergy(enemy), 1, true, "Choose a pokemon to rip energy from!").get(0);
		Card enemyPokemon = gameModel.getPosition(chosenEnemyPosition).getTopCard();
		List<Card> enemyEnergyList = gameModel.getPosition(chosenEnemyPosition).getEnergyCards();
		List<Card> chooseEnemyCardList = new ArrayList<>();
		for (Card c : enemyEnergyList)
			chooseEnemyCardList.add(c);
		// Choose up to 2 energy cards:
		List<Card> chosenEnemyEnergy = player.playerChoosesCards(chooseEnemyCardList, 2, false, "Choose up to 2 energy cards to discard!");
		for (Card c : chosenEnemyEnergy) {
			// Message clients:
			gameModel.sendCardMessageToAllPlayers(player.getName() + " removes " + c.getName() + " from " + enemyPokemon.getName() + "!", c, "");
			// Discard energy card:
			gameModel.getAttackAction().discardCardToDiscardPile(chosenEnemyPosition, c.getGameID(), true);
		}
	}

	private List<PositionID> getPositionsWithEnergy(Player player) {
		List<PositionID> arenaPositions = gameModel.getFullArenaPositions(player.getColor());
		List<PositionID> energyPositions = new ArrayList<>();
		for (PositionID posID : arenaPositions) {
			Position pos = gameModel.getPosition(posID);
			if (pos.getEnergyCards().size() > 0)
				energyPositions.add(posID);
		}
		return energyPositions;
	}
}
