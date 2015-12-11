package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import gui2d.animations.Animation;
import gui2d.animations.CardMoveAnimation;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00330_LtSurgesElectabuzz extends PokemonCardScript {

	public Script_00330_LtSurgesElectabuzz(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Charge", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Electric Current", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Charge"))
			this.Charge();
		else
			this.ElectricCurrent();
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

	private void ElectricCurrent() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		Position ownActive = gameModel.getPosition(ownActive());
		Card card = null;
		for (Card c : ownActive.getEnergyCards())
			if (c.getCardId().equals("00100"))
				card = c;

		if (card != null) {
			if (gameModel.getFullBenchPositions(getCardOwner().getColor()).isEmpty()) {
				// Discard energy card:
				gameModel.getAttackAction().moveCard(ownActive(), ownDiscardPile(), card.getGameID(), true);
				gameModel.sendGameModelToAllPlayers("");
			} else {
				PositionID benchPos = getCardOwner().playerChoosesPositions(gameModel.getFullBenchPositions(getCardOwner().getColor()), 1, true,
						"Choose a Pokemon that should get " + card.getName() + " from " + this.card.getName()).get(0);
				gameModel.getAttackAction().moveCard(this.card.getCurrentPosition().getPositionID(), benchPos, card.getGameID(), false);

				// Execute animation:
				Animation animation = new CardMoveAnimation(this.card.getCurrentPosition().getPositionID(), benchPos, card.getCardId(), Sounds.EQUIP);
				gameModel.sendAnimationToAllPlayers(animation);
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}
}
