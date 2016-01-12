package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00205_DarkDugtrio extends PokemonCardScript {

	public Script_00205_DarkDugtrio(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.ROCK);
		att1Cost.add(Element.ROCK);
		this.addAttack("Knock Down", att1Cost);

		this.addPokemonPower("Sinkhole");
	}

	private boolean sinkholeCanBeExecuted() {
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() == this.getCardOwner().getColor())
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		return true;
	}

	public void pokemonRetreated(PositionID newPositionOfRetreatedPkmn) {
		if (gameModel.getPosition(newPositionOfRetreatedPkmn).getColor() != this.getCardOwner().getColor() && sinkholeCanBeExecuted()) {
			gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Sinkhole!", this.card, "");
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				PositionID attacker = this.card.getCurrentPosition().getPositionID();
				PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
				Element attackerElement = ((PokemonCard) this.card).getElement();
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
			}
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
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
	}
}
