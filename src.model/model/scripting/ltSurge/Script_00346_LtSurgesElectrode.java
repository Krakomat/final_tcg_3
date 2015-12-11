package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00346_LtSurgesElectrode extends PokemonCardScript {

	public Script_00346_LtSurgesElectrode(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Power Ball", att1Cost);
		this.addPokemonPower("Shock Blast");
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

		gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + " flips 3 coins...", "");
		int numberHeads = gameModel.getAttackAction().flipCoinsCountHeads(3);
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30 + numberHeads * 10, true);
	}

	public void pokemonIsDamaged(int turnNumber, int damage, PositionID source) {
		if (shockBlastCanBeExecuted()) {
			gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Shock Blast!", card, Sounds.ACTIVATE_TRAINER);
			if (gameModel.getAttackAction().flipACoin() == Coin.TAILS) {
				Element attackerElement = ((PokemonCard) this.card).getElement();
				PositionID attacker = this.card.getCurrentPosition().getPositionID();
				PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);
				gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, attacker, 20, true);
			}
		}
	}

	private boolean shockBlastCanBeExecuted() {
		if (!PositionID.isActivePosition(this.getCurrentPositionID()))
			return false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		return true;
	}
}
