package model.scripting.blaine;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00435_BlainesMagmar extends PokemonCardScript {

	public Script_00435_BlainesMagmar(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		this.addAttack("Firebreathing", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		this.addAttack("Lava Burst", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Firebreathing"))
			this.Firebreathing();
		else
			this.LavaBurst();
	}

	private void Firebreathing() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers("If heads then this attack does 10 more damage!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void LavaBurst() {
		int deckCards = gameModel.getPosition(ownDeck()).size();
		if (deckCards > 5)
			deckCards = 5;
		int fireEnergy = 0;
		for (int i = 0; i < deckCards; i++) {
			Card c = gameModel.getPosition(ownDeck()).getTopCard();
			if (c.getCardId().equals("00098"))
				fireEnergy++;
			gameModel.sendCardMessageToAllPlayers(getCardOwner().getName() + " discards " + c.getName() + " from his deck!", c, "");
			gameModel.getAttackAction().discardCardToDiscardPile(ownDeck(), c.getGameID(), true);
			gameModel.sendGameModelToAllPlayers("");
		}

		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, fireEnergy * 20, true);
	}
}
