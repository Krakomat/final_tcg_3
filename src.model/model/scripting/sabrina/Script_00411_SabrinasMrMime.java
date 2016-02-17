package model.scripting.sabrina;

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

public class Script_00411_SabrinasMrMime extends PokemonCardScript {

	public Script_00411_SabrinasMrMime(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Sleight of Hand", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Slap", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Sleight of Hand"))
			this.SleightofHand();
		else
			this.Slap();
	}

	private void SleightofHand() {
		List<Card> chosenCards = getCardOwner().playerChoosesCards(gameModel.getPosition(ownHand()).getCards(), 3, false, "Choose up to 3 cards!");
		int number = chosenCards.size();

		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " puts " + number + " cards on his deck!", "");
		for (Card c : chosenCards) {
			gameModel.getAttackAction().moveCard(ownHand(), ownDeck(), c.getGameID(), true);
		}

		Player player = this.getCardOwner();
		Position deck = gameModel.getPosition(ownDeck());
		if (number > 0) {
			List<Card> energyCards = deck.getEnergyCards();
			List<Card> basicEnergyCards = new ArrayList<>();
			for (Card c : energyCards)
				if (((EnergyCard) c).isBasisEnergy())
					basicEnergyCards.add(c);

			if (basicEnergyCards.isEmpty())
				gameModel.sendTextMessageToAllPlayers(player.getName() + "'s deck does not contain any basic energy card!", "");
			else {
				if (basicEnergyCards.size() < number)
					number = basicEnergyCards.size();
				List<Card> chosenEnergy = player.playerChoosesCards(basicEnergyCards, number, true, "Choose " + number + " basic energy cards!");
				for (Card c : chosenEnergy) {
					gameModel.sendCardMessageToAllPlayers(player.getName() + " chose " + c.getName(), chosenEnergy, "");
					// Move:
					gameModel.getAttackAction().moveCard(ownDeck(), ownHand(), c.getGameID(), false);
				}
			}
		}

		// Shuffle:
		gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles his deck!", Sounds.SHUFFLE);
		deck.shuffle();
	}

	private void Slap() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
