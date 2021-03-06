package model.scripting.jungle;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00152_Pokeball extends TrainerCardScript {

	public Script_00152_Pokeball(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before choosing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		Player player = this.getCardOwner();
		gameModel.sendTextMessageToAllPlayers("If heads then " + this.card.getName() + "'s effects will be executed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			// Choose a card from the deck:
			List<Card> cards = gameModel.getPosition(ownDeck()).getPokemonCards();
			if (!cards.isEmpty()) {
				Card chosenDeckCard = player.playerChoosesCards(cards, 1, true, "Choose a pokemon card from your deck!").get(0);
				// Message clients:
				gameModel.sendCardMessageToAllPlayers(player.getName() + " gets " + chosenDeckCard.getName() + " from his deck!", chosenDeckCard, "");
				// Move card:
				gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), chosenDeckCard.getGameID(), true);
			} else
				gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + "'s deck does not contain pokemon cards!", "");

			// Shuffle deck:
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
			gameModel.getAttackAction().shufflePosition(ownDeck());

			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
