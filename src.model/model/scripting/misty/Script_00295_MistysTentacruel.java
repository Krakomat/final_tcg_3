package model.scripting.misty;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00295_MistysTentacruel extends PokemonCardScript {

	public Script_00295_MistysTentacruel(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Jellyfish Poison", att1Cost);
		this.addPokemonPower("Flee");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("Check codition on the defending pokemon!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
			gameModel.sendGameModelToAllPlayers("");
		} else {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public void pokemonIsDamaged(int turnNumber, int damage, PositionID source) {
		if (fleeCanBeExecuted()) {
			Player player = getCardOwner();
			boolean answer = player.playerDecidesYesOrNo("Do you want to swap out " + this.card.getName() + "?");
			if (answer) {
				Color color = player.getColor();
				PositionID activePos = color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
				PositionID chosenBenchPos = player.playerChoosesPositions(gameModel.getFullBenchPositions(color), 1, true, "Choose a new active pokemon!").get(0);

				// Message clients:
				Card active = gameModel.getPosition(activePos).getTopCard();
				Card bench = gameModel.getPosition(chosenBenchPos).getTopCard();
				List<Card> cardList = new ArrayList<>();
				cardList.add(active);
				cardList.add(bench);
				gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Flee!", this.card, "");
				gameModel.sendCardMessageToAllPlayers(player.getName() + " swaps " + active.getName() + " with " + bench.getName() + "!", cardList, "");

				// Execute swap:
				gameModel.getAttackAction().swapPokemon(chosenBenchPos, activePos);
			}
		}
	}

	private boolean fleeCanBeExecuted() {
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		if (!PositionID.isActivePosition(this.getCurrentPositionID()))
			return false;
		if (gameModel.getFullBenchPositions(getCardOwner().getColor()).isEmpty())
			return false;
		return true;
	}
}
