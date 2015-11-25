package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.EnergyCard;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00195_EnergySearch extends TrainerCardScript {

	public Script_00195_EnergySearch(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		Position deck = gameModel.getPosition(ownDeck());
		List<Card> energyCards = deck.getEnergyCards();
		List<Card> basicEnergyCards = new ArrayList<>();
		for (Card c : energyCards)
			if (((EnergyCard) c).isBasisEnergy())
				basicEnergyCards.add(c);

		if (basicEnergyCards.isEmpty())
			gameModel.sendTextMessageToAllPlayers(player.getName() + "'s deck does not contain any basic energy card!", "");
		else {
			Card chosenEnergy = player.playerChoosesCards(basicEnergyCards, 1, true, "Choose a basic energy card!").get(0);
			gameModel.sendCardMessageToAllPlayers(player.getName() + " chose " + chosenEnergy.getName(), chosenEnergy, "");

			// Move:
			gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), chosenEnergy.getGameID(), false);
		}

		// Shuffle:
		gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles his deck!", Sounds.SHUFFLE);
		deck.shuffle();
		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());
		gameModel.sendGameModelToAllPlayers("");
	}

	private PositionID ownDeck() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DECK;
		else
			return PositionID.RED_DECK;
	}

	private PositionID ownHand() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_HAND;
		else
			return PositionID.RED_HAND;
	}
}
