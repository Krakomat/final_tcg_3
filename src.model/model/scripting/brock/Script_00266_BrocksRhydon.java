package model.scripting.brock;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00266_BrocksRhydon extends PokemonCardScript {

	public Script_00266_BrocksRhydon(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Lariat", att1Cost);

		this.addPokemonPower("Bench Guard");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If Tails then Lariat does nothing!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 70, true);
		else
			gameModel.sendTextMessageToAllPlayers("Lariat does nothing!", "");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public int modifyIncomingDamage(int damage, Card attacker, PositionID defender) {
		if (benchGuardCanBeExecuted() && gameModel.getFullBenchPositions(getCardOwner().getColor()).contains(defender)
				&& defender != this.card.getCurrentPosition().getPositionID()) {
			Player player = getCardOwner();
			Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
			boolean answer = player.playerDecidesYesOrNo("Do you want deviate 10 of " + defendingPokemon.getName() + "'s damage to " + this.card.getName() + "?");
			if (answer) {
				gameModel.sendCardMessageToAllPlayers(this.card.getName() + " executes Bench Guard!", this.card, "");
				gameModel.getAttackAction().inflictDamageToPosition(Element.COLORLESS, attacker != null ? attacker.getCurrentPosition().getPositionID() : null,
						this.getCurrentPositionID(), 10, false);
				return damage - 10;
			}
		}
		return damage;
	}

	private boolean benchGuardCanBeExecuted() {
		if (!PositionID.isBenchPosition(this.getCurrentPositionID()))
			return false;
		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		if (!PositionID.isBenchPosition(this.getCurrentPositionID()))
			return false;
		return true;
	}
}
