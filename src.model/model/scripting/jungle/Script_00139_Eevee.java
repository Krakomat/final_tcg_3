package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00139_Eevee extends PokemonCardScript {

	public Script_00139_Eevee(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.COLORLESS);
		this.addAttack("Tail Wag", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Quick Attack", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Tail Wag"))
			this.tailWag();
		else
			this.quickAttack();
	}

	private void tailWag() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Card attackingPokemon = gameModel.getPosition(attacker).getTopCard();

		// Flip coin to check if active pokemon is protected from attacks:
		gameModel.sendTextMessageToAllPlayers("If heads then " + attackingPokemon.getName() + " can't be attacked next turn!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
			PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
			if (defendingPokemon.getHitpoints() <= 50) {
				gameModel.getGameModelParameters().activateEffect("00139", defendingPokemon.getGameID());
				gameModel.getGameModelParameters().addEffectParameter("00139", this.cardGameID(), 2);
			}
		}
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().getValueForEffectParameterKeyPair("00139", this.cardGameID()) != null) {
			if (gameModel.getGameModelParameters().getValueForEffectParameterKeyPair("00139", this.cardGameID()) == 2)
				gameModel.getGameModelParameters().replaceEffectParameter("00139", this.cardGameID(), 1);
			else {
				gameModel.getGameModelParameters().deactivateEffect("00139");
				gameModel.getGameModelParameters().removeEffectParameter("00139", this.cardGameID());
			}
		}
	}

	private void quickAttack() {
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
