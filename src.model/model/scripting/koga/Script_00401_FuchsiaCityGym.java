package model.scripting.koga;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00401_FuchsiaCityGym extends TrainerCardScript {

	public Script_00401_FuchsiaCityGym(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	public boolean stadiumCanBeActivatedOnField(Player player) {
		if (gameModel.getGameModelParameters().activeEffect("00401", cardGameID()))
			return false;
		if (getKogaCardsFromOwnField(player).size() > 0)
			return true;
		return false;
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().activeEffect("00401", cardGameID())) {
			gameModel.getGameModelParameters().deactivateEffect("00401", cardGameID());
		}
	}

	public void executeStadiumActiveEffect(Player player) {
		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			// Choose Pokemon:
			PositionID posID = player.playerChoosesPositions(getKogaCardsFromOwnField(player), 1, true, "Choose a pokemon to shuffle into your deck!").get(0);

			// Scoop up position:
			Position position = gameModel.getPosition(posID);
			List<Card> cards = position.getCards();

			gameModel.sendCardMessageToAllPlayers(player.getName() + " chooses " + position.getTopCard().getName() + "!", position.getTopCard(), "");
			PositionID playerDeck = deck(player);

			int size = cards.size();
			for (int i = 0; i < size; i++)
				gameModel.getAttackAction().moveCard(posID, playerDeck, cards.get(0).getGameID(), true);
			gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles his deck!", Sounds.SHUFFLE);
			gameModel.getPosition(playerDeck).shuffle();
			gameModel.sendGameModelToAllPlayers("");

			if (gameModel.getFullArenaPositions(player.getColor()).isEmpty()) {
				gameModel.sendTextMessageToAllPlayers(player.getName() + " has no active pokemon anymore!", "");
				gameModel.playerLoses(player);
			} else if (gameModel.getPosition(active(player)).isEmpty()) {
				// Choose a new active pokemon in this case:
				PositionID newActive = player.playerChoosesPositions(gameModel.getFullArenaPositions(player.getColor()), 1, true, "Choose a new active pokemon!").get(0);
				Card newActivePokmn = gameModel.getPosition(newActive).getTopCard();
				gameModel.sendCardMessageToAllPlayers(player.getName() + " chooses " + newActivePokmn.getName() + " as his new active pokemon!", newActivePokmn, "");
				gameModel.getAttackAction().movePokemonToPosition(newActive, active(player));
				gameModel.sendGameModelToAllPlayers("");
			}
		}
		gameModel.getGameModelParameters().activateEffect("00401", cardGameID());
	}

	protected PositionID active(Player player) {
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_ACTIVEPOKEMON;
		else
			return PositionID.RED_ACTIVEPOKEMON;
	}

	protected PositionID deck(Player player) {
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DECK;
		else
			return PositionID.RED_DECK;
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

	private List<PositionID> getKogaCardsFromOwnField(Player player) {
		List<PositionID> posList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(player.getColor())) {
			PokemonCard pCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (pCard.getName().contains("Koga"))
				posList.add(posID);
		}
		return posList;
	}
}
