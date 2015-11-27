package model.scripting.fossil;

import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00194_MrFuji extends TrainerCardScript {

	public Script_00194_MrFuji(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		Player player = this.getCardOwner();
		if (this.gameModel.getFullBenchPositions(player.getColor()).size() == 0)
			return null;

		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		Player player = this.getCardOwner();
		PositionID targetPosition = player.playerChoosesPositions(gameModel.getFullBenchPositions(player.getColor()), 1, true,
				"Choose a position to shuffle into your deck!").get(0);

		// Scoop up position:
		Position position = gameModel.getPosition(targetPosition);
		List<Card> cards = position.getCards();

		gameModel.sendCardMessageToAllPlayers(player.getName() + " chooses the positions of " + position.getTopCard().getName() + "!", position.getTopCard(), "");
		PositionID playerDeck = ownDeck();

		int size = cards.size();
		for (int i = 0; i < size; i++)
			gameModel.getAttackAction().moveCard(targetPosition, playerDeck, cards.get(0).getGameID(), true);
		gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getPosition(playerDeck).shuffle();
		gameModel.sendGameModelToAllPlayers("");

		// Discard trainer card:
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID());
	}
}
