package model.scripting.giovanni;

import java.util.ArrayList;
import java.util.List;

import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00480_GiovannisNidoking extends PokemonCardScript {

	public Script_00480_GiovannisNidoking(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Intimidate", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Tumbling Attack", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Intimidate"))
			this.Intimidate();
		else
			this.TumblingAttack();
	}

	private void Intimidate() {
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();
		if (defendingPokemon.getHitpoints() <= 50) {
			gameModel.getGameModelParameters().activateEffect("00480", defendingPokemon.getGameID());
			gameModel.getGameModelParameters().addEffectParameter("00480", this.cardGameID(), 2);
		}
	}

	public void executeEndTurnActions() {
		if (gameModel.getGameModelParameters().getValueForEffectParameterKeyPair("00480", this.cardGameID()) != null) {
			if (gameModel.getGameModelParameters().getValueForEffectParameterKeyPair("00480", this.cardGameID()) == 2)
				gameModel.getGameModelParameters().replaceEffectParameter("00480", this.cardGameID(), 1);
			else {
				gameModel.getGameModelParameters().deactivateEffect("00480");
				gameModel.getGameModelParameters().removeEffectParameter("00480", this.cardGameID());
			}
		}
	}

	private void TumblingAttack() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		gameModel.sendTextMessageToAllPlayers("If heads then this attack does 30 more damage!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 70, true);
		} else
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);
	}
}
