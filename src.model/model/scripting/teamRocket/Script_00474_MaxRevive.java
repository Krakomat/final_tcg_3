package model.scripting.teamRocket;

import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00474_MaxRevive extends TrainerCardScript {

	public Script_00474_MaxRevive(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own deck contains at least 2 cards:
		Position ownDeck = gameModel.getPosition(ownDeck());
		if (ownDeck.size() >= 2 && ownDeck.getBasicEnergyCards().size() >= 2) {
			// Can be played if the players bench is not full and there is at least one basic pokemon in the players discard pile.
			if (gameModel.getFullBenchPositions(getCardOwner().getColor()).size() < 5 && !(this.gameModel.getCurrentStadium() != null
					&& this.gameModel.getCurrentStadium().getCardId().equals("00468") && this.gameModel.getFullBenchPositions(getCardOwner().getColor()).size() == 4))
				if (gameModel.getAttackCondition().positionHasBasicPokemon(ownDiscardPile()))
					return PlayerAction.PLAY_TRAINER_CARD;
		}
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();

		// Discard trainer card before choosing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);

		// Choose two own cards:
		List<Card> chosenCards = player.playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), 2, true, "Choose 2 cards to discard!");
		for (Card chosenCard : chosenCards) {
			gameModel.sendCardMessageToAllPlayers(player.getName() + " discards " + chosenCard.getName() + "!", chosenCard, "");
			gameModel.getAttackAction().discardCardToDiscardPile(ownHand(), chosenCard.getGameID(), true);
			gameModel.sendGameModelToAllPlayers("");
		}

		List<Card> basicPokemon = gameModel.getBasicPokemonOnPosition(ownDiscardPile());

		PokemonCard chosenCard = (PokemonCard) player.playerChoosesCards(basicPokemon, 1, true, "Choose a pokemon to revive!").get(0);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " revives " + chosenCard.getName(), chosenCard, "");

		PokemonCard realCard = (PokemonCard) gameModel.getCard(chosenCard.getGameID());
		// Put on bench:
		gameModel.getAttackAction().putBasicPokemonOnBench(player, realCard);
	}
}
