package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00326_LtSurgesElectabuzz extends PokemonCardScript {

	public Script_00326_LtSurgesElectabuzz(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Charge", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Discharge", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Charge"))
			this.Charge();
		else
			this.Discharge();
	}

	private void Charge() {
		Position discardPile = gameModel.getPosition(ownDiscardPile());
		List<Card> cardList = new ArrayList<>();
		for (Card c : discardPile.getEnergyCards())
			if (c.getCardId().equals("00100"))
				cardList.add(c);
		if (!cardList.isEmpty()) {
			List<Card> moveList = getCardOwner().playerChoosesCards(cardList, 2, false, "Choose up to two cards to attach to " + this.card.getName());
			for (Card c : moveList)
				gameModel.getAttackAction().moveCard(ownDiscardPile(), getCurrentPositionID(), c.getGameID(), false);
			gameModel.sendGameModelToAllPlayers("");
		} else {
			gameModel.sendTextMessageToAllPlayers("No Lightning energy found!", "");
		}
	}

	private void Discharge() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		List<Card> lightningEnergy = getLightningEnergyOnPosition();
		int size = lightningEnergy.size();
		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips " + size + " coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(size);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 30, true);

		// Discard all lightning energy cards:
		for (Card c : lightningEnergy) {
			gameModel.getAttackAction().discardCardToDiscardPile(getCurrentPositionID(), c.getGameID(), true);
		}
		gameModel.sendGameModelToAllPlayers("");
	}

	private List<Card> getLightningEnergyOnPosition() {
		Position pos = gameModel.getPosition(getCurrentPositionID());
		List<Card> cardList = new ArrayList<>();
		for (Card c : pos.getEnergyCards())
			if (c.getCardId().equals("00100"))
				cardList.add(c);
		return cardList;
	}
}
