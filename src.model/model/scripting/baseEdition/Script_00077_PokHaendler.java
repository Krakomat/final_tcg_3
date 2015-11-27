package model.scripting.baseEdition;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00077_PokHaendler extends TrainerCardScript {

	public Script_00077_PokHaendler(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own hand contains a pokemon card:
		Position ownHand = this.card.getCurrentPosition();
		if (ownHand.getPokemonCards().size() > 0)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();

		// Discard trainer card before choosing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());

		if (gameModel.getPosition(ownHand()).getPokemonCards().size() > 0) {
			// Choose one own pokemon card:
			Card chosenCard = player.playerChoosesCards(gameModel.getPosition(ownHand()).getPokemonCards(), 1, true, "Choose 1 pokemon to discard!").get(0);
			gameModel.sendCardMessageToAllPlayers(player.getName() + " shuffles " + chosenCard.getName() + " into his deck!", chosenCard, "");
			// Put onto deck:
			gameModel.getAttackAction().moveCard(ownHand(), ownDeck(), chosenCard.getGameID(), true);

			// Choose a card from the deck:
			List<Card> cards = gameModel.getPosition(ownDeck()).getPokemonCards();
			Card chosenDeckCard = player.playerChoosesCards(cards, 1, true, "Choose a pokemon card from your deck!").get(0);
			// Message clients:
			gameModel.sendCardMessageToAllPlayers(player.getName() + " gets " + chosenDeckCard.getName() + " from his deck!", chosenDeckCard, "");
			// Move card:
			gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), chosenDeckCard.getGameID(), true);
			gameModel.sendGameModelToAllPlayers("");
		} else
			gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + "'s deck contains no pokemon cards!", "");

		// Shuffle deck:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getAttackAction().shufflePosition(ownDeck());
		this.gameModel.sendGameModelToAllPlayers("");
	}
}
