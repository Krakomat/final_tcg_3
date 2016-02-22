package model.scripting.teamRocket;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00471_RocketsZapdos extends PokemonCardScript {

	public Script_00471_RocketsZapdos(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Plasma", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Electroburn", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Plasma"))
			this.Plasma();
		else
			this.Electroburn();
	}

	private void Plasma() {
		Position discardPile = gameModel.getPosition(ownDiscardPile());
		Card card = null;
		for (Card c : discardPile.getEnergyCards())
			if (c.getCardId().equals("00100"))
				card = c;
		if (card != null) {
			gameModel.getAttackAction().moveCard(ownDiscardPile(), getCurrentPositionID(), card.getGameID(), false);
			gameModel.sendGameModelToAllPlayers("");
		} else {
			gameModel.sendTextMessageToAllPlayers("No Lightning energy found!", "");
		}
	}

	private void Electroburn() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 70, true);

		Position pos = gameModel.getPosition(getCurrentPositionID());
		int lightning = 0;
		for (Card c : pos.getEnergyCards())
			if (c.getCardId().equals("00100"))
				lightning++;

		if (lightning > 0)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, lightning * 10, true);
	}
}
