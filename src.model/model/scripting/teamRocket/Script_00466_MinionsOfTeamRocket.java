package model.scripting.teamRocket;

import java.util.List;

import model.database.Card;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.game.PokemonGameModelImpl;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00466_MinionsOfTeamRocket extends TrainerCardScript {

	public Script_00466_MinionsOfTeamRocket(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		if (gameModel.getFullBenchPositions(getEnemyPlayer().getColor()).size() > 0)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS && gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			PositionID posID = getCardOwner()
					.playerChoosesPositions(gameModel.getFullBenchPositions(getEnemyPlayer().getColor()), 1, true, "Choose a pokemon to return to your opponents hand!").get(0);
			// Scoop up position:
			Position position = gameModel.getPosition(posID);
			List<Card> cards = position.getCards();
			gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " chooses " + position.getTopCard().getName() + "!", position.getTopCard(), "");
			PositionID hand = enemyHand();

			int size = cards.size();
			for (int i = 0; i < size; i++) {
				gameModel.getAttackAction().moveCard(posID, hand, cards.get(0).getGameID(), true);
			}
			gameModel.sendGameModelToAllPlayers("");
		} else {
			// End turn:
			if (gameModel instanceof PokemonGameModelImpl)
				((PokemonGameModelImpl) gameModel).getPokemonGameManager().endTurn(getCardOwner());
		}
	}
}
