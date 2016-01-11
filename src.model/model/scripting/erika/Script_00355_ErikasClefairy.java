package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00355_ErikasClefairy extends PokemonCardScript {

	public Script_00355_ErikasClefairy(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Moonwatching", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Comet Slap", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Moonwatching"))
			this.Moonwatching();
		else
			this.CometSlap();
	}

	private void Moonwatching() {
		Player player = this.getCardOwner();
		Position deck = gameModel.getPosition(ownDeck());
		List<Card> energyCards = deck.getEnergyCards();
		List<Card> basicEnergyCards = new ArrayList<>();
		for (Card c : energyCards)
			if (((EnergyCard) c).isBasisEnergy())
				basicEnergyCards.add(c);

		if (basicEnergyCards.isEmpty())
			gameModel.sendTextMessageToAllPlayers(player.getName() + "'s deck does not contain any basic energy card!", "");
		else {
			Card chosenEnergy = player.playerChoosesCards(basicEnergyCards, 1, true, "Choose a basic energy card!").get(0);
			gameModel.sendCardMessageToAllPlayers(player.getName() + " chose " + chosenEnergy.getName(), chosenEnergy, "");

			// Move:
			gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), chosenEnergy.getGameID(), false);
		}

		// Shuffle:
		gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles his deck!", Sounds.SHUFFLE);
		deck.shuffle();
	}

	private void CometSlap() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 3 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, numberHeads * 10, true);
	}
}
