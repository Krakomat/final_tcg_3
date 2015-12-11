package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00328_LtSurgesMagneton extends PokemonCardScript {

	public Script_00328_LtSurgesMagneton(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.LIGHTNING);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Mega Shock", att1Cost);
		this.addPokemonPower("Energy Charge");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;

		if (!PositionID.isActivePosition(this.getCurrentPositionID()))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (getEnergizedBenchPositions().size() == 0)
			return false;
		return super.pokemonPowerCanBeExecuted(powerName);
	}

	@Override
	public void executePokemonPower(String powerName) {
		Player player = this.getCardOwner();
		List<Card> cardList = new ArrayList<>();
		cardList.add(card);

		List<PositionID> energizedPositions = getEnergizedBenchPositions();

		PositionID chosenPosition = player.playerChoosesPositions(energizedPositions, 1, true, "Choose a Pokemon to move an energy to " + this.card.getName() + "!").get(0);

		// Distribute energy:
		Position position = gameModel.getPosition(chosenPosition);
		PokemonCard pokemon = (PokemonCard) position.getTopCard();
		cardList.add(pokemon);
		this.gameModel.sendCardMessageToAllPlayers(player.getName() + " moves an energy from " + pokemon.getName() + " to " + this.card.getName() + "!", cardList, "");

		Card card = null;
		for (Card c : position.getEnergyCards())
			if (c.getCardId().equals("00100"))
				card = c;
		if (card != null) {
			gameModel.getAttackAction().moveCard(ownDiscardPile(), getCurrentPositionID(), card.getGameID(), false);
		}

		gameModel.sendGameModelToAllPlayers("");
	}

	private List<PositionID> getEnergizedBenchPositions() {
		Player player = this.getCardOwner();
		List<PositionID> benchPositions = gameModel.getFullBenchPositions(player.getColor());
		List<PositionID> energizedPositions = new ArrayList<>();

		for (PositionID posID : benchPositions) {
			Position position = gameModel.getPosition(posID);
			for (Card c : position.getEnergyCards()) {
				if (c.getCardId().equals("00100")) {
					energizedPositions.add(posID);
					break;
				}
			}
		}
		return energizedPositions;
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + this.card.getName() + " hurts itself!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.TAILS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 20, true);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
