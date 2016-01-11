package model.scripting.erika;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.CardType;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00357_ErikasDratini extends PokemonCardScript {

	public Script_00357_ErikasDratini(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Tail Strike", att1Cost);

		this.addPokemonPower("Strange Barrier");
	}

	@Override
	public int modifyIncomingDamage(int damage, Card attacker, PositionID defender) {
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return damage; // no modifications allowed
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return damage;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return damage;
		if (this.card.getCurrentPosition().getPositionID() != defender || gameModel.getPosition(defender).getTopCard() != this.card)
			return damage;
		if (attacker.getCardType() != CardType.BASICPOKEMON)
			return damage;

		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return damage;
		else {
			if (damage >= 20) {
				this.gameModel.sendCardMessageToAllPlayers("Erika's Dratini's Strange Barrier reduces the damage!", pCard, "");
				return 10;
			}
			return damage;
		}
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers("If heads then this attack does 20 more damage!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}
}
