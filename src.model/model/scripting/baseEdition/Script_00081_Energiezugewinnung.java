package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00081_Energiezugewinnung extends TrainerCardScript {

	public Script_00081_Energiezugewinnung(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own hand contains at least 2 cards(including this card) and the discardpile contains at least one energy card:
		Position ownHand = this.card.getCurrentPosition();
		if (ownHand.size() >= 2 && gameModel.getPosition(ownDiscardPile()).getBasicEnergyCards().size() > 0)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();

		// Discard trainer card before choosing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());

		// Choose one own card:
		Card chosenCard = player.playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), 1, true, "Choose 1 cards to discard!").get(0);
		gameModel.sendCardMessageToAllPlayers(player.getName() + " discards " + chosenCard.getName() + "!", chosenCard, "");
		gameModel.getAttackAction().discardCardToDiscardPile(ownHand(), chosenCard.getGameID());

		// Choose up to 2 energy cards:
		List<Card> energyCards = gameModel.getPosition(ownDiscardPile()).getBasicEnergyCards();
		List<Card> chooseCardList = new ArrayList<>();
		for (Card c : energyCards)
			chooseCardList.add(c);
		List<Card> chosenEnergy = player.playerChoosesCards(chooseCardList, 2, false, "Choose up to 2 energy cards to recover!");
		for (Card c : chosenEnergy) {
			// Message clients:
			gameModel.sendCardMessageToAllPlayers(player.getName() + " recovers " + c.getName() + " from his discard pile!", c, "");
			// Move energy card:
			gameModel.getAttackAction().moveCard(ownDiscardPile(), ownHand(), c.getGameID(), true);
		}
	}
}
