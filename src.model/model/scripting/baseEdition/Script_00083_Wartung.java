package model.scripting.baseEdition;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00083_Wartung extends TrainerCardScript {

	public Script_00083_Wartung(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own hand contains at least 3 cards(including this card):
		Position ownHand = this.card.getCurrentPosition();
		if (ownHand.size() >= 3)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();

		// Discard trainer card before choosing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());

		// Choose two cards:
		List<Card> chosenCards = player.playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), 2, true, "Choose 2 cards to shuffle into your deck!");

		// Put onto deck:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " puts 2 cards onto his deck!", "");
		for (Card c : chosenCards)
			gameModel.getAttackAction().moveCard(ownHand(), ownDeck(), c.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		// Shuffle deck:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getAttackAction().shufflePosition(ownDeck());

		// Draw one card:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " draws 1 card!", "");
		gameModel.getAttackAction().playerDrawsCards(1, player);
	}

	private PositionID ownHand() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_HAND;
		else
			return PositionID.BLUE_HAND;
	}

	private PositionID ownDeck() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DECK;
		else
			return PositionID.RED_DECK;
	}
}
