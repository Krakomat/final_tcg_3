package model.scripting.koga;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00384_KogasDitto extends PokemonCardScript {

	public Script_00384_KogasDitto(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Giant Growth", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Pound", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Giant Growth"))
			this.GiantGrowth();
		else
			this.Pound();
	}

	private void GiantGrowth() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PokemonCard attackingPokemon = (PokemonCard) gameModel.getPosition(attacker).getTopCard();

		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			attackingPokemon.setHitpoints(80);
			gameModel.sendGameModelToAllPlayers("");
		} else
			gameModel.sendTextMessageToAllPlayers("Giant Growth failed!", "");
	}

	public void moveToPosition(PositionID targetPosition) {
		PokemonCard attackingPokemon = (PokemonCard) this.card;
		attackingPokemon.setHitpoints(40);
		if (attackingPokemon.getHitpoints() >= 40 && PositionID.isArenaPosition(targetPosition)) {
			attackingPokemon.setHitpoints(40);
			gameModel.getAttackAction().inflictConditionToPosition(getCurrentPositionID(), PokemonCondition.KNOCKOUT);
			gameModel.cleanDefeatedPositions();
		}
	}

	private void Pound() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		PokemonCard attackingPokemon = (PokemonCard) this.card;

		if (attackingPokemon.getHitpoints() == 80)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);
	}
}
