package model.scripting.blaine;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00456_BlainesVulpix extends PokemonCardScript {

	public Script_00456_BlainesVulpix(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Bite", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.FIRE);
		this.addAttack("Call Will-o'-the-wisp", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Bite"))
			this.Bite();
		else
			this.CallWillothewisp();
	}

	private void Bite() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}

	private void CallWillothewisp() {
		int heads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		if (heads > 0) {
			Player player = this.getCardOwner();
			// Choose up to 2 energy cards:
			List<Card> energyCards = getFireEnergyOnPosition();
			if (heads > energyCards.size())
				heads = energyCards.size();
			List<Card> chosenEnergy = player.playerChoosesCards(energyCards, heads, true, "Choose " + heads + " energy cards to recover!");

			gameModel.sendTextMessageToAllPlayers(player.getName() + " recovers " + chosenEnergy.size() + " fire energy from his discard pile!", "");
			for (Card c : chosenEnergy) {
				// Move energy card:
				gameModel.getAttackAction().moveCard(ownDiscardPile(), ownHand(), c.getGameID(), true);
			}
		}
	}

	private List<Card> getFireEnergyOnPosition() {
		Position pos = gameModel.getPosition(ownDiscardPile());
		List<Card> cardList = new ArrayList<>();
		for (Card c : pos.getEnergyCards())
			if (c.getCardId().equals("00098"))
				cardList.add(c);
		return cardList;
	}

}
