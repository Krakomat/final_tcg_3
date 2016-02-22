package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00418_SabrinasGolduck extends PokemonCardScript {

	public Script_00418_SabrinasGolduck(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Damage Shift", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.WATER);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Water Spray", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Damage Shift"))
			this.DamageShift();
		else
			this.WaterSpray();
	}

	private void DamageShift() {
		List<PokemonCard> damagedPokemon = damagedPokemon();
		for (PokemonCard pokemon : damagedPokemon) {
			pokemon.setDamageMarks(pokemon.getDamageMarks() - 10);
		}

		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
		defendingPokemon.setDamageMarks(damagedPokemon.size() * 10 + defendingPokemon.getDamageMarks());

		if (defendingPokemon.getDamageMarks() >= defendingPokemon.getHitpoints() && !defendingPokemon.hasCondition(PokemonCondition.KNOCKOUT)) {
			defendingPokemon.setDamageMarks(defendingPokemon.getHitpoints());
			gameModel.getAttackAction().inflictConditionToPosition(defendingPokemon.getCurrentPosition().getPositionID(), PokemonCondition.KNOCKOUT);
			gameModel.sendGameModelToAllPlayers("");
			gameModel.cleanDefeatedPositions();
		} else
			gameModel.sendGameModelToAllPlayers("");
		gameModel.sendTextMessageToAllPlayers("Damage marks have been moved!", "");
	}

	private List<PokemonCard> damagedPokemon() {
		List<PokemonCard> pList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(getCardOwner().getColor())) {
			PokemonCard c = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (c.getDamageMarks() > 0)
				pList.add(c);
		}
		return pList;
	}

	private void WaterSpray() {
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
