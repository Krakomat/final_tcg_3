package model.scripting.blaine;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00452_BlainesGrowlithe extends PokemonCardScript {

	public Script_00452_BlainesGrowlithe(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		this.addAttack("Stoke", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Bodyslam", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Stoke"))
			this.Stoke();
		else
			this.Bodyslam();
	}

	private void Stoke() {
		Player player = this.getCardOwner();
		Position deck = gameModel.getPosition(ownDeck());
		List<Card> energyCards = deck.getEnergyCards();
		List<Card> basicEnergyCards = new ArrayList<>();
		for (Card c : energyCards)
			if (c.getCardId().equals("00098"))
				basicEnergyCards.add(c);

		if (basicEnergyCards.isEmpty())
			gameModel.sendTextMessageToAllPlayers(player.getName() + "'s deck does not contain any fire energy card!", "");
		else {
			Card chosenEnergy = basicEnergyCards.get(0);
			gameModel.sendCardMessageToAllPlayers(player.getName() + " chose " + chosenEnergy.getName(), chosenEnergy, "");

			// Move:
			gameModel.getAttackAction().moveCard(ownDeck(), ownActive(), chosenEnergy.getGameID(), false);
			this.pokemonGotEnergy((EnergyCard) chosenEnergy);
		}

		// Shuffle:
		gameModel.sendTextMessageToAllPlayers(player.getName() + " shuffles his deck!", Sounds.SHUFFLE);
		deck.shuffle();
		gameModel.sendGameModelToAllPlayers("");
	}

	private void Bodyslam() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
