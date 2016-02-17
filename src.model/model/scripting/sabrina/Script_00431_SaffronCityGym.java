package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00431_SaffronCityGym extends TrainerCardScript {

	public Script_00431_SaffronCityGym(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	public boolean stadiumCanBeActivatedOnField(Player player) {
		List<PositionID> sabrinaList = this.getSabrinaCardsFromOwnField(player);
		for (PositionID posID : sabrinaList) {
			if (gameModel.getPosition(posID).getBasicEnergyCards().size() > 0)
				return true;
		}
		return false;
	}

	public void executeStadiumActiveEffect(Player player) {
		List<PositionID> temp = this.getSabrinaCardsFromOwnField(player);
		List<PositionID> sabrinaList = new ArrayList<>();
		for (PositionID posID : temp) {
			if (gameModel.getPosition(posID).getBasicEnergyCards().size() > 0)
				sabrinaList.add(posID);
		}

		PositionID sabrinaPos = player.playerChoosesPositions(sabrinaList, 1, true, "Choose a position to take energy from!").get(0);
		Card energy = player.playerChoosesCards(gameModel.getPosition(sabrinaPos).getBasicEnergyCards(), 1, true, "Choose an energy card to take from this position!").get(0);
		List<Card> cards = new ArrayList<>();
		cards.add(energy);
		cards.add(gameModel.getPosition(sabrinaPos).getTopCard());
		gameModel.sendCardMessageToAllPlayers(player.getName() + " takes one " + energy.getName() + " from " + gameModel.getPosition(sabrinaPos).getTopCard().getName() + "!",
				cards, "");
		gameModel.getAttackAction().moveCard(sabrinaPos, hand(player), energy.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");
	}

	protected PositionID hand(Player player) {
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_HAND;
		else
			return PositionID.RED_HAND;
	}

	@Override
	public void playFromHand() {
		if (!gameModel.getPosition(PositionID.STADIUM).isEmpty()) {
			Card stadium = gameModel.getPosition(PositionID.STADIUM).getTopCard();
			// Discard previous stadium card:
			gameModel.getAttackAction().discardCardToDiscardPile(PositionID.STADIUM, stadium.getGameID(), false);
		}
		gameModel.getPosition(PositionID.STADIUM).setColor(this.getCardOwner().getColor());
		gameModel.getAttackAction().moveCard(getCurrentPositionID(), PositionID.STADIUM, this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<PositionID> getSabrinaCardsFromOwnField(Player player) {
		List<PositionID> posList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(player.getColor())) {
			PokemonCard pCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (pCard.getName().contains("Sabrina"))
				posList.add(posID);
		}
		return posList;
	}
}
