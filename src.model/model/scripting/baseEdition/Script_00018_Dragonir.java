package model.scripting.baseEdition;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00018_Dragonir extends PokemonCardScript {

	public Script_00018_Dragonir(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Slam", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Hyperstrahl", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Slam"))
			this.slam();
		else
			this.hyperstrahl();
	}

	private void slam() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 2 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(2);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 30, true);
	}

	private void hyperstrahl() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Position defendingPosition = gameModel.getPosition(defender);
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Check for energy at enemy position:
		if (defendingPosition.getEnergyCards().size() > 0) {
			// Choose one of them and remove it:
			List<Card> cardList = new ArrayList<>();
			for (Card c : defendingPosition.getEnergyCards())
				cardList.add(c);

			Card chosenEnergyCard = player.playerChoosesCards(cardList, 1, true, "Choose one energy card to remove!").get(0);
			PositionID discardPile = null;
			if (chosenEnergyCard.getCurrentPosition().getColor() == Color.BLUE)
				discardPile = PositionID.BLUE_DISCARDPILE;
			else
				discardPile = PositionID.RED_DISCARDPILE;

			gameModel.getAttackAction().moveCard(chosenEnergyCard.getCurrentPosition().getPositionID(), discardPile, chosenEnergyCard.getGameID(), true);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
