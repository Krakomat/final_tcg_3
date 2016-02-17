package model.scripting.sabrina;

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

public class Script_00404_SabrinasVenomoth extends PokemonCardScript {

	public Script_00404_SabrinasVenomoth(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Healing Pollen", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Sonic Distortion", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Healing Pollen"))
			this.HealingPollen();
		else
			this.SonicDistortion();
	}

	private void HealingPollen() {
		int amount = gameModel.getAttackAction().flipCoinsCountHeads(3) * 10;
		for (PositionID posID : getDamagedCardsFromOwnField())
			gameModel.getAttackAction().healPosition(posID, amount);
	}

	private List<PositionID> getDamagedCardsFromOwnField() {
		List<PositionID> posList = new ArrayList<>();
		for (PositionID posID : gameModel.getFullArenaPositions(getCardOwner().getColor())) {
			PokemonCard pCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (pCard.getDamageMarks() > 0)
				posList.add(posID);
		}
		return posList;
	}

	private void SonicDistortion() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS && gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
