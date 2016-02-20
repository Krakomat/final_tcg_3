package model.scripting.abstracts;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PlayerAction;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;

/**
 * Script for a EnergyCard.
 * 
 * @author Michael
 *
 */
public abstract class EnergyCardScript extends CardScript {

	protected List<Element> providedEnergy;

	public EnergyCardScript(EnergyCard card, PokemonGame gameModel) {
		super(card, gameModel);
		this.providedEnergy = ((EnergyCard) this.card).getProvidedEnergy();
	}

	public PlayerAction canBePlayedFromHand() {
		// Check if card is in players hand:
		if (!this.cardInHand())
			return null;

		Player player = this.getCardOwner();
		if (this.getArenaPositionForEnergy(player.getColor()).isEmpty())
			return null;

		if (!this.gameModel.getEnergyPlayed())
			return PlayerAction.PLAY_ENERGY_CARD;
		else if (gameModel.getGameModelParameters().getValueForUniqueEffectParameterKey("00445") != null
				&& gameModel.getGameModelParameters().getValueForUniqueEffectParameterKey("00445") > 0 && card.getCardId().equals("00098")) {
			// Blaine effect: We may be able to add a second fire energy in this turn!
			// Check if the pokemon that received the first fire energy is still in play:
			for (PositionID posID : gameModel.getFullArenaPositions(getCardOwner().getColor())) {
				Card c = gameModel.getPosition(posID).getTopCard();
				if (c.getGameID() == gameModel.getGameModelParameters().getValueForUniqueEffectParameterKey("00445"))
					return PlayerAction.PLAY_ENERGY_CARD;
			}
		}
		return null;
	}

	@Override
	public void playFromHand() {
		PositionID startPosID = this.card.getCurrentPosition().getPositionID();
		Player player = this.card.getCurrentPosition().getColor() == Color.BLUE ? this.gameModel.getPlayerBlue() : this.gameModel.getPlayerRed();
		if (player == null)
			throw new IllegalArgumentException("Error: Couldn't find player in playFromHand of EnergyCardScript for id: " + card.getCardId());

		// Check second fire Blaine:
		boolean secondFireBlaine = false;
		Integer gameID = gameModel.getGameModelParameters().getValueForUniqueEffectParameterKey("00445");
		if (gameID != null && gameID > 0 && card.getCardId().equals("00098")) {
			secondFireBlaine = true;
		}

		PositionID chosenPosition = null;
		if (!secondFireBlaine)
			chosenPosition = player.playerChoosesPositions(this.getArenaPositionForEnergy(player.getColor()), 1, true, "Choose a position:").get(0);
		else
			chosenPosition = gameModel.getCard(gameID).getCurrentPosition().getPositionID();

		if (this.gameModel.getPosition(chosenPosition).getTopCard() != null) {
			Card basicPkmn = this.gameModel.getPosition(chosenPosition).getTopCard();
			List<Card> cardList = new ArrayList<Card>();
			cardList.add(basicPkmn);
			cardList.add(card);
			this.gameModel.sendCardMessageToAllPlayers(player.getName() + " attaches an Energy-Card to " + basicPkmn.getName(), cardList, "");
			this.gameModel.getAttackAction().moveCard(card.getCurrentPosition().getPositionID(), chosenPosition, card.getGameID(), false);

			// Check Blaine:
			Integer value = gameModel.getGameModelParameters().getValueForUniqueEffectParameterKey("00445");
			if (value != null && value == 0 && card.getCardId().equals("00098") && basicPkmn.getName().contains("Blaine")) {
				gameModel.getGameModelParameters().replaceUniqueEffectParameter("00445", basicPkmn.getGameID());
			}
			if (value != null && value > 0) {
				gameModel.getGameModelParameters().replaceUniqueEffectParameter("00445", -1);
			}

			this.gameModel.setEnergyPlayed(true);

			// Execute animation:
			Animation animation = new CardMoveAnimation(startPosID, chosenPosition, card.getCardId(), Sounds.EQUIP);
			gameModel.sendAnimationToAllPlayers(animation);

			this.gameModel.sendGameModelToAllPlayers("");

			// Notify pokemon card script:
			PokemonCardScript script = (PokemonCardScript) ((PokemonCard) basicPkmn).getCardScript();
			script.pokemonGotEnergy((EnergyCard) card);

			EnergyCard eneCard = (EnergyCard) this.card;
			// Call energy played of all card scripts:
			for (Card c : gameModel.getAllCards())
				c.getCardScript().energyCardPlayed(eneCard);
		} else
			throw new IllegalArgumentException("Error: chosen position does not contain a card");
	}

	private List<PositionID> getArenaPositionForEnergy(Color playerColor) {
		List<PositionID> list = new ArrayList<>();
		for (PositionID posID : this.gameModel.getFullArenaPositions(playerColor)) {
			PokemonCard card = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (!card.hasCondition(PokemonCondition.NO_ENERGY)) {
				list.add(posID);
			}
		}
		return list;
	}
}