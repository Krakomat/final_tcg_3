package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00426_SabrinasDrowzee extends PokemonCardScript {

	public Script_00426_SabrinasDrowzee(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Energy Support", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Mind Shock", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Energy Support"))
			this.EnergySupport();
		else
			this.MindShock();
	}

	private void EnergySupport() {
		Player player = this.getCardOwner();
		Position deck = gameModel.getPosition(ownDeck());
		List<Card> energyCards = deck.getEnergyCards();
		List<Card> basicEnergyCards = new ArrayList<>();
		for (Card c : energyCards)
			if (c.getCardId().equals("00101"))
				basicEnergyCards.add(c);

		if (basicEnergyCards.isEmpty())
			gameModel.sendTextMessageToAllPlayers(player.getName() + "'s deck does not contain any psychic energy card!", "");
		else {
			Card chosenEnergy = basicEnergyCards.get(0);
			gameModel.sendCardMessageToAllPlayers(player.getName() + " chose " + chosenEnergy.getName(), chosenEnergy, "");

			// Move:
			gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), chosenEnergy.getGameID(), false);
		}

		// Shuffle:
		gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles his deck!", Sounds.SHUFFLE);
		deck.shuffle();
		gameModel.sendGameModelToAllPlayers("");
	}

	private void MindShock() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, false);
	}
}
