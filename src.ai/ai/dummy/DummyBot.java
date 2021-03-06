package ai.dummy;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import ai.interfaces.Bot;
import ai.interfaces.BotBorder;
import ai.util.AIUtilities;
import network.client.Player;
import network.server.PokemonGameManager;
import model.database.Card;
import model.database.EnergyCard;
import model.enums.Color;
import model.enums.Element;
import model.enums.PositionID;
import model.game.GameModelUpdate;

/**
 * Represents a stupid bot, which only ends his turn.
 * 
 * @author Michael
 *
 */
public class DummyBot implements Bot {

	private AIUtilities aiUtilities;

	@Override
	public void startGame() {
		this.aiUtilities = new AIUtilities();
	}

	@Override
	public void updateGameModel(GameModelUpdate update, BotBorder botBorder, PokemonGameManager server) {

	}

	@Override
	public void makeMove(PokemonGameManager server, Player player) {
		server.endTurn(player);
	}

	@Override
	public List<Card> choosesCards(List<Card> cards, int amount, boolean exact) {
		List<Card> chosenCards = new ArrayList<Card>();
		for (int i = 0; i < amount && i < cards.size(); i++)
			chosenCards.add(cards.get(i));
		return chosenCards;
	}

	@Override
	public List<PositionID> choosesPositions(List<PositionID> positionList, int amount, boolean exact) {
		List<PositionID> chosenPositions = new ArrayList<PositionID>();
		for (int i = 0; i < amount && i < positionList.size(); i++)
			chosenPositions.add(positionList.get(i));
		return chosenPositions;
	}

	@Override
	public List<Element> choosesElements(List<Element> elements, int amount, boolean exact) {
		List<Element> chosenElements = new ArrayList<Element>();
		for (int i = 0; i < amount && i < elements.size(); i++)
			chosenElements.add(elements.get(i));
		return chosenElements;
	}

	@Override
	public List<String> choosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact) {
		List<String> chosenAttacks = new ArrayList<String>();
		for (int i = 0; i < amount && i < attacks.size(); i++)
			chosenAttacks.add(attacks.get(i));
		return chosenAttacks;
	}

	@Override
	public List<Card> paysEnergyCosts(List<Element> costs, List<Card> energyCards) {
		List<Card> chosenCards = new ArrayList<>();
		List<Card> availableCards = new ArrayList<>();
		for (Card c : energyCards)
			availableCards.add(c);

		// Get a copy of the color- and colorless costs:
		List<Element> colorCosts = new ArrayList<>();
		int colorless = 0;
		for (Element element : costs)
			if (element != Element.COLORLESS)
				colorCosts.add(element);
			else
				colorless++;

		// Pay color costs:
		for (Element element : colorCosts) {
			boolean payed = false;
			for (int i = 0; i < availableCards.size() && !payed; i++) {
				EnergyCard c = (EnergyCard) availableCards.get(i);
				if (c.getProvidedEnergy().contains(element)) {
					payed = true;
					availableCards.remove(i);
					i--;
					chosenCards.add(c);
				}
			}
		}

		// Pay colorless costs with non-basic energy:
		for (int i = 0; i < colorless; i++) {
			EnergyCard c = (EnergyCard) availableCards.get(i);
			if (c.getProvidedEnergy().size() <= colorless && !c.isBasisEnergy()) {
				chosenCards.add(c);
				colorless = colorless - c.getProvidedEnergy().size();
				availableCards.remove(i);
				i--;
			}
		}

		// Pay colorless costs with basic energy:
		for (int i = 0; i < colorless; i++) {
			EnergyCard c = (EnergyCard) availableCards.get(i);
			if (c.getProvidedEnergy().size() <= colorless) {
				chosenCards.add(c);
				colorless = colorless - c.getProvidedEnergy().size();
				availableCards.remove(i);
				i--;
			}
		}

		Preconditions.checkArgument(aiUtilities.checkPaymentOk(chosenCards, costs), "Error: Payment of DummyBot was not ok! Cost: " + costs + " Payment: "
				+ chosenCards);
		return chosenCards;
	}

	@Override
	public void setColor(Color color) {

	}
}
