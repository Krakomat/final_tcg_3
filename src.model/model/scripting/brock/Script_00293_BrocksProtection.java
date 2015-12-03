package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;
import network.client.Player;

public class Script_00293_BrocksProtection extends TrainerCardScript {

	public Script_00293_BrocksProtection(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		if (this.getBrockCardsFromOwnField().size() == 0)
			return null;
		return PlayerAction.PLAY_TRAINER_CARD;
	}

	@Override
	public void playFromHand() {
		Player player = getCardOwner();
		PositionID chosenPosition = player.playerChoosesPositions(getBrockCardsFromOwnField(), 1, true, "Choose a pokemon to attach Brocks protection to!").get(0);

		// Attach card:
		gameModel.getAttackAction().moveCard(this.card.getCurrentPosition().getPositionID(), chosenPosition, this.card.getGameID(), false);

		// Execute animation:
		Animation animation = new CardMoveAnimation(this.card.getCurrentPosition().getPositionID(), chosenPosition, card.getCardId(), Sounds.EQUIP);
		gameModel.sendAnimationToAllPlayers(animation);

		gameModel.getAttackAction().inflictConditionToPosition(chosenPosition, PokemonCondition.BROCKS_PROTECTION);
	}

	public void leavePosition(PositionID oldPosition) {
		if (PositionID.isArenaPosition(oldPosition))
			gameModel.getAttackAction().cureCondition(oldPosition, PokemonCondition.BROCKS_PROTECTION);
	}

	private List<PositionID> getBrockCardsFromOwnField() {
		List<PositionID> posList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(getCardOwner().getColor())) {
			PokemonCard pCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (pCard.getName().contains("Brock"))
				posList.add(posID);
		}
		return posList;
	}
}
