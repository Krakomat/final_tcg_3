package model.scripting.teamRocket;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00473_MasterBall extends TrainerCardScript {

	public Script_00473_MasterBall(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		// Can be played if the own deck contains at least 2 cards:
		Position ownDeck = gameModel.getPosition(ownDeck());
		if (ownDeck.size() >= 7)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		Position pos = gameModel.getPosition(ownDeck());
		List<Card> cardList = new ArrayList<>();

		for (int i = 0; i < 7; i++) {
			Card card = pos.getCardAtIndex(pos.size() - 1 - i);
			if (card instanceof PokemonCard)
				cardList.add(pos.getCardAtIndex(pos.size() - 1 - i));
		}

		Card c = getCardOwner().playerChoosesCards(cardList, 1, true, "Choose a pokemon to move into your hand!").get(0);

		gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " puts " + c.getName() + " into his hand!", c, "");
		gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), c.getGameID(), true);
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getAttackAction().shufflePosition(ownDeck());
		gameModel.sendGameModelToAllPlayers("");
	}
}
