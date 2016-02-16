package model.scripting.sabrina;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00421_SabrinasJynx extends PokemonCardScript {

	public Script_00421_SabrinasJynx(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Helping Hand", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.PSYCHIC);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Hug", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Helping Hand"))
			this.HelpingHand();
		else
			this.Hug();
	}

	private void HelpingHand() {
		PositionID defender = getCardOwner().playerChoosesPositions(gameModel.getFullArenaPositions(getEnemyPlayer().getColor()), 1, true, "Choose a Pokemon to heal!").get(0);
		PokemonCard defendingPokemon = (PokemonCard) gameModel.getPosition(defender).getTopCard();

		if (defendingPokemon.getDamageMarks() > 0) {
			boolean heal = getCardOwner().playerDecidesYesOrNo("Do you want to remove a damage mark from " + defendingPokemon.getName() + " in order to draw a card?");
			while (heal && defendingPokemon.getDamageMarks() > 0) {
				this.gameModel.getAttackAction().healPosition(defender, 10);
				this.gameModel.getAttackAction().playerDrawsCards(1, getCardOwner());
				heal = getCardOwner().playerDecidesYesOrNo("Do you want to remove a damage mark from " + defendingPokemon.getName() + " in order to draw a card?");
			}
		} else {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " has no damage marks! Helping Hand missed!", "");
		}
	}

	private void Hug() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is allowed to retreat the next turn:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is not allowed to retreat the next turn!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is not allowed to retreat the next turn!", "");
			gameModel.getGameModelParameters().activateEffect("00421", cardGameID());
		}
	}

	public void executePreTurnActions() {
		if (gameModel.getGameModelParameters().activeEffect("00421", cardGameID())) {
			gameModel.setRetreatExecuted(true);
			gameModel.getGameModelParameters().deactivateEffect("00421", cardGameID());
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
