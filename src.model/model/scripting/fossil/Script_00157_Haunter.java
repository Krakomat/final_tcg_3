package model.scripting.fossil;

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

public class Script_00157_Haunter extends PokemonCardScript {

	public Script_00157_Haunter(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Nightmare", att1Cost);

		this.addPokemonPower("Transparency");
	}

	public int modifyIncomingDamage(int damage, Card attacker, PositionID defender) {
		if (powerCanBeUsed() && gameModel.getGameModelParameters().getValueForEffectParameterKeyPair("00157", cardGameID()) == null) {
			gameModel.sendTextMessageToAllPlayers("Check for Haunters Pokemon Power Transparency!", "");
			Coin c = gameModel.getAttackAction().flipACoin();
			if (c == Coin.HEADS) {
				gameModel.getGameModelParameters().addEffectParameter("00157", cardGameID(), 1);
				return 0;
			} else
				gameModel.getGameModelParameters().addEffectParameter("00157", cardGameID(), 0);
		} else if (powerCanBeUsed() && gameModel.getGameModelParameters().getValueForEffectParameterKeyPair("00157", cardGameID()) == 1)
			return 0;
		return damage;
	}

	public boolean allowIncomingCondition(PokemonCondition condition) {
		if (powerCanBeUsed() && gameModel.getGameModelParameters().getValueForEffectParameterKeyPair("00157", cardGameID()) == null) {
			gameModel.sendTextMessageToAllPlayers("Check for Haunters Pokemon Power Transparency!", "");
			Coin c = gameModel.getAttackAction().flipACoin();
			if (c == Coin.HEADS) {
				gameModel.getGameModelParameters().addEffectParameter("00157", cardGameID(), 1);
				return false;
			} else
				gameModel.getGameModelParameters().addEffectParameter("00157", cardGameID(), 0);
		} else if (powerCanBeUsed() && gameModel.getGameModelParameters().getValueForEffectParameterKeyPair("00157", cardGameID()) == 1)
			return false;
		return true;
	}

	public void executeEndTurnActions() {
		gameModel.getGameModelParameters().removeEffectParameter("00157", cardGameID());
	}

	private boolean powerCanBeUsed() {
		PokemonCard pCard = (PokemonCard) this.card;

		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;

		return true;
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Nightmare"))
			this.nightmare();
	}

	private void nightmare() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
		this.gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.ASLEEP);
	}
}
