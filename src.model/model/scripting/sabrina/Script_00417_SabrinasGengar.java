package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00417_SabrinasGengar extends PokemonCardScript {

	public Script_00417_SabrinasGengar(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Dark Wave", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.PSYCHIC);
		this.addAttack("Shadow Bind", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Dark Wave"))
			this.DarkWave();
		else
			this.ShadowBind();
	}

	private void DarkWave() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		gameModel.getGameModelParameters().setAllowedToPlayPokemonPower((short) 2);
		gameModel.sendGameModelToAllPlayers("");
	}

	private void ShadowBind() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 40, true);

		// Flip coin to check if defending pokemon is allowed to retreat the next turn:
		gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is not allowed to retreat the next turn!", "");
		gameModel.getGameModelParameters().activateEffect("00417", cardGameID());
	}

	public void executePreTurnActions(Player player) {
		if (gameModel.getGameModelParameters().activeEffect("00417", cardGameID())) {
			gameModel.setRetreatExecuted(true);
			gameModel.getGameModelParameters().deactivateEffect("00417", cardGameID());
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
