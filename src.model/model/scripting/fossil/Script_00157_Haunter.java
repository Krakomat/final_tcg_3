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

	private boolean coinFlipped;
	private boolean powerActive;

	public Script_00157_Haunter(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Nightmare", att1Cost);

		this.addPokemonPower("Transparency");
		this.powerActive = false;
		this.coinFlipped = false;
	}

	public int modifyIncomingDamage(int damage, Card attacker) {
		if (powerCanBeUsed() && !coinFlipped) {
			gameModel.sendTextMessageToAllPlayers("Check for Haunters Pokemon Power Transparency!", "");
			Coin c = gameModel.getAttackAction().flipACoin();
			if (c == Coin.HEADS)
				this.powerActive = true;
			this.coinFlipped = true;
			if (this.powerActive)
				return 0;
		} else if (powerCanBeUsed() && coinFlipped && powerActive)
			return 0;
		return damage;
	}

	public boolean allowIncomingCondition(PokemonCondition condition) {
		if (powerCanBeUsed() && !coinFlipped) {
			gameModel.sendTextMessageToAllPlayers("Check for Haunters Pokemon Power Transparency!", "");
			Coin c = gameModel.getAttackAction().flipACoin();
			if (c == Coin.HEADS)
				this.powerActive = true;
			this.coinFlipped = true;
			if (this.powerActive)
				return false;
		} else if (powerCanBeUsed() && coinFlipped && powerActive)
			return false;
		return true;
	}

	public void executeEndTurnActions() {
		this.powerActive = false;
		this.coinFlipped = false;
	}

	private boolean powerCanBeUsed() {
		PokemonCard pCard = (PokemonCard) this.card;

		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
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
