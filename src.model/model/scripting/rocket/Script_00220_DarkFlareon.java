package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00220_DarkFlareon extends PokemonCardScript {

	public Script_00220_DarkFlareon(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Rage", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		this.addAttack("Playing with Fire", att2Cost);
	}

	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Playing with Fire")) {
			// Cannot be used if there are no 'real' fire energy attached to Dark Flareon
			if (realFireEnergy().isEmpty())
				return false;
		}
		return super.attackCanBeExecuted(attackName);
	};

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Rage"))
			this.Rage();
		else
			this.PlayingwithFire();
	}

	private void Rage() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard attackingPokemon = (PokemonCard) gameModel.getPosition(attacker).getTopCard();

		int damageMarks = attackingPokemon.getDamageMarks();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10 + damageMarks, true);
	}

	private void PlayingwithFire() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			// Discard a fire energy:
			List<Element> costs = new ArrayList<>();
			costs.add(Element.FIRE);
			gameModel.getAttackAction().playerPaysEnergy(getCardOwner(), costs, attacker);
			gameModel.sendGameModelToAllPlayers("");
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);
		} else {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);
		}
	}

	private List<Card> realFireEnergy() {
		List<Card> erg = new ArrayList<>();
		List<Card> cardList = this.card.getCurrentPosition().getEnergyCards();
		for (Card c : cardList)
			if (c.getCardId().equals("00098"))
				erg.add(c);
		return erg;
	}
}
