package model.scripting.baseEdition;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00078_Aufwisch extends TrainerCardScript {

	public Script_00078_Aufwisch(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the player has pokemon on his bench
		Player player = this.getCardOwner();
		if (gameModel.getFullBenchPositions(player.getColor()).size() > 0)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		PositionID targetPosition = player.playerChoosesPositions(gameModel.getFullArenaPositions(player.getColor()), 1, true, "Choose a position to scoop up!")
				.get(0);

		// Scoop up position:
		Position position = gameModel.getPosition(targetPosition);
		List<Card> cards = position.getCards();
		PokemonCard basicPokemon = null;
		for (int i = 0; i < cards.size(); i++)
			if (cards.get(i).getCardType().equals(CardType.BASICPOKEMON))
				basicPokemon = (PokemonCard) cards.get(i);

		if (basicPokemon != null) {
			gameModel.sendCardMessageToAllPlayers(player.getName() + " returns " + basicPokemon.getName() + " to his hand!", basicPokemon, "");
			PositionID playerHand = null;
			PositionID playerDiscard = null;
			if (position.getColor() == Color.BLUE) {
				playerHand = PositionID.BLUE_HAND;
				playerDiscard = PositionID.BLUE_DISCARDPILE;
			} else {
				playerHand = PositionID.RED_HAND;
				playerDiscard = PositionID.RED_DISCARDPILE;
			}
			gameModel.getAttackAction().moveCard(targetPosition, playerHand, basicPokemon.getGameID(), false);
			int size = cards.size();
			for (int i = 0; i < size; i++)
				gameModel.getAttackAction().moveCard(targetPosition, playerDiscard, cards.get(0).getGameID(), true);
		} else
			System.err.println("No Basic pokemon on position in method scoopUpPosition");
		gameModel.sendGameModelToAllPlayers("");

		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());

		// Check if active position was scooped up - choose a new active pokemon in this case:
		if (targetPosition == ownActive()) {
			PositionID newActive = player.playerChoosesPositions(gameModel.getFullArenaPositions(position.getColor()), 1, true, "Choose a new active pokemon!").get(
					0);
			Card newActivePokmn = gameModel.getPosition(newActive).getTopCard();
			gameModel.sendCardMessageToAllPlayers(player.getName() + " chooses " + newActivePokmn.getName() + " as his new active pokemon!", newActivePokmn, "");
			gameModel.getAttackAction().movePokemonToPosition(newActive, ownActive());
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
