package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00092_EnergieAbsauger extends TrainerCardScript {

	public Script_00092_EnergieAbsauger(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played, if there is a pokemon with energy in the enemies
		// arena:
		if (this.getPositionsWithEnergy().size() > 0) {
			if (gameModel.stadiumActive("00464") && gameModel.getPosition(ownHand()).size() < 3)
				return null;
			return PlayerAction.PLAY_TRAINER_CARD;
		}
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);

		Player player = this.getCardOwner();
		if (gameModel.stadiumActive("00464")) {
			// Choose two own cards:
			List<Card> chosenCards = player.playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), 2, true, "Choose 2 cards to discard!");
			for (Card chosenCard : chosenCards) {
				gameModel.sendCardMessageToAllPlayers(player.getName() + " discards " + chosenCard.getName() + "!", chosenCard, "");
				gameModel.getAttackAction().discardCardToDiscardPile(ownHand(), chosenCard.getGameID(), true);
			}
		}

		PositionID chosenPosition = player.playerChoosesPositions(this.getPositionsWithEnergy(), 1, true, "Choose a pokemon to rip enery from!").get(0);
		Card pokemon = gameModel.getPosition(chosenPosition).getTopCard();
		List<Card> energyList = gameModel.getPosition(chosenPosition).getEnergyCards();
		List<Card> chooseCardList = new ArrayList<>();
		for (Card c : energyList)
			chooseCardList.add(c);

		// Choose energy card:
		Card chosenEnergy = player.playerChoosesCards(chooseCardList, 1, true, "Choose an energy card to discard!").get(0);

		// Message clients:
		gameModel.sendCardMessageToAllPlayers(player.getName() + " removes " + chosenEnergy.getName() + " from " + pokemon.getName() + "!", chosenEnergy, "");
		// Discard energy card:
		gameModel.getAttackAction().discardCardToDiscardPile(chosenPosition, chosenEnergy.getGameID(), true);
	}

	private List<PositionID> getPositionsWithEnergy() {
		Player enemy = this.getEnemyPlayer();
		List<PositionID> arenaPositions = gameModel.getFullArenaPositions(enemy.getColor());
		List<PositionID> energyPositions = new ArrayList<>();
		for (PositionID posID : arenaPositions) {
			Position pos = gameModel.getPosition(posID);
			if (pos.getEnergyCards().size() > 0 && !((PokemonCard) pos.getTopCard()).hasCondition(PokemonCondition.BROCKS_PROTECTION))
				energyPositions.add(posID);
		}
		return energyPositions;
	}
}
