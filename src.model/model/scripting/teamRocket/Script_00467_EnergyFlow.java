package model.scripting.teamRocket;

import java.util.ArrayList;
import java.util.List;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import model.database.Card;
import model.database.TrainerCard;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.TrainerCardScript;

public class Script_00467_EnergyFlow extends TrainerCardScript {

	public Script_00467_EnergyFlow(TrainerCard card, PokemonGame gameModel) {
		super(card, gameModel);
	}

	@Override
	public PlayerAction trainerCanBePlayedFromHand() {
		if (getEnergizedPositons().size() > 0)
			return PlayerAction.PLAY_TRAINER_CARD;
		return null;
	}

	@Override
	public void playFromHand() {
		// Discard trainer card before drawing!
		gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), this.card.getGameID(), true);
		gameModel.sendGameModelToAllPlayers("");

		for (PositionID posID : this.getEnergizedPositons()) {
			Card pokemon = gameModel.getPosition(posID).getTopCard();
			if (getCardOwner().playerDecidesYesOrNo("Do you want to return Energy Cards to your hand from " + pokemon.getName() + "?")) {
				List<Card> energyCards = getCardOwner().playerChoosesCards(gameModel.getPosition(posID).getEnergyCards(), gameModel.getPosition(posID).getEnergyCards().size(),
						false, "Return any number of Energy Cards to your hand!");
				for (Card energyCard : energyCards) {
					gameModel.getAttackAction().moveCard(posID, ownHand(), energyCard.getGameID(), true);
					gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " returns " + energyCard.getName() + " from " + pokemon.getName() + " to his hand!",
							energyCard, "");
					Animation animation = new CardMoveAnimation(posID, ownHand(), card.getCardId(), "");
					gameModel.sendAnimationToAllPlayers(animation);
					gameModel.sendGameModelToAllPlayers("");
				}
			}
		}

		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " finished Energy Flow!", "");
	}

	private List<PositionID> getEnergizedPositons() {
		List<PositionID> erg = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(getCardOwner().getColor())) {
			if (gameModel.getPosition(posID).getEnergyCards().size() > 0)
				erg.add(posID);
		}
		return erg;
	}
}
