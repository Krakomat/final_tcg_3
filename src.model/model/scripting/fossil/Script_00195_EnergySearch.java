package model.scripting.fossil;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
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
		gameModel.sendTextMessageToAllPlayers("If heads then " + this.card.getName() + "'s effects will be executed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c, "");
		if (c == Coin.HEADS) {
			// Choose a card from the deck:
			List<Card> cards = gameModel.getPosition(ownDeck()).getPokemonCards();
			Card chosenDeckCard = player.playerChoosesCards(cards, 1, true, "Choose a pokemon card from your deck!").get(0);
			// Message clients:
			gameModel.sendCardMessageToAllPlayers(player.getName() + " gets " + chosenDeckCard.getName() + " from his deck!", chosenDeckCard, "");
			// Move card:
			gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), chosenDeckCard.getGameID(), true);

			// Shuffle deck:
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
			gameModel.getAttackAction().shufflePosition(ownDeck());

			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private PositionID ownHand() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_HAND;
		else
			return PositionID.RED_HAND;
	}

	private PositionID ownDeck() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DECK;
		else
			return PositionID.RED_DECK;
	}
}
