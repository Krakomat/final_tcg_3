package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import model.database.Card;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00416_Sabrina extends TrainerCardScript {

	public Script_00416_Sabrina(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		if (getSabrinaCardsFromOwnField().size() >= 2)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		List<PositionID> sabrina = getSabrinaCardsFromOwnField();
		PositionID from = getCardOwner().playerChoosesPositions(sabrina, 1, true, "Choose a pokemon to take energy from!").get(0);
		sabrina.remove(from);
		PositionID to = getCardOwner().playerChoosesPositions(sabrina, 1, true, "Choose a pokemon to give energy to!").get(0);

		List<Card> energy = gameModel.getPosition(from).getEnergyCards();
		for (Card c : energy) {
			gameModel.getAttackAction().moveCard(from, to, c.getGameID(), false);

			// Execute animation:
			Animation animation = new CardMoveAnimation(from, to, c.getCardId(), Sounds.EQUIP);
			gameModel.sendAnimationToAllPlayers(animation);
		}

		Card fromC = gameModel.getPosition(from).getTopCard();
		Card toC = gameModel.getPosition(to).getTopCard();
		List<Card> list = new ArrayList<>();
		list.add(fromC);
		list.add(toC);
		gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " transfers all energy cards from " + fromC.getName() + " to " + toC.getName() + "!", list, "");
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<PositionID> getSabrinaCardsFromOwnField() {
		List<PositionID> posList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(getCardOwner().getColor())) {
			PokemonCard pCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (pCard.getName().contains("Sabrina"))
				posList.add(posID);
		}
		return posList;
	}
}
