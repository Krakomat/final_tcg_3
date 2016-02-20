package model.scripting.blaine;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00450_BlainesCharmander extends PokemonCardScript {

	public Script_00450_BlainesCharmander(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		this.addAttack("Fire Tail Slap", att1Cost);
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Fire Tail Slap")) {
			List<Card> energyCards = gameModel.getPosition(getCurrentPositionID()).getEnergyCards();
			for (Card c : energyCards)
				if (c.getCardId().equals("00098"))
					return super.attackCanBeExecuted(attackName);
			return false;
		}
		return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.TAILS) {
			// Pay energy:
			List<Element> costs = new ArrayList<>();
			costs.add(Element.FIRE);
			gameModel.getAttackAction().playerPaysEnergy(getCardOwner(), costs, card.getCurrentPosition().getPositionID());
		}
	}
}
