package model.scripting.teamRocket;

import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00475_RocketsSecretExperiment extends TrainerCardScript {

	public Script_00475_RocketsSecretExperiment(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		Position ownDeck = gameModel.getPosition(ownDeck());
		if (ownDeck.size() >= 1)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);

		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			Player player = this.getCardOwner();

			// Choose a card from the deck:
			List<Card> cards = gameModel.getPosition(ownDeck()).getCards();
			Card chosenCard = player.playerChoosesCards(cards, 1, true, "Choose a card from your deck!").get(0);

			// Message clients:
			gameModel.sendTextMessageToAllPlayers(player.getName() + " got a card of his choice from his deck!", "");
			// Move card:
			gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), chosenCard.getGameID(), true);
			gameModel.sendGameModelToAllPlayers("");

			// Shuffle deck:
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
			gameModel.getAttackAction().shufflePosition(ownDeck());
		} else {
			gameModel.getGameModelParameters().setAllowedToPlayTrainerCards((short) 1);
			gameModel.getGameModelParameters().addEffectParameter(this.card.getCardId(), this.cardGameID(), getCardOwner().getColor() == Color.BLUE ? 0 : 1);
			gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " can't play Trainer cards until the end of his next turn!", "");
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	public void executePreTurnActions(Player playerOnTurn) {
		Integer value = gameModel.getGameModelParameters().getValueForEffectParameterKeyPair(this.card.getCardId(), this.cardGameID());
		if (value != null) {
			if ((playerOnTurn.getColor() == this.getCardOwner().getColor() && value == 0 && getCardOwner().getColor() == Color.BLUE)
					|| (playerOnTurn.getColor() == this.getCardOwner().getColor() && value == 1 && getCardOwner().getColor() == Color.RED)) {
				gameModel.getGameModelParameters().removeEffectParameter(this.card.getCardId(), this.cardGameID());
				gameModel.getGameModelParameters().setAllowedToPlayTrainerCards((short) 1);
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}
}
