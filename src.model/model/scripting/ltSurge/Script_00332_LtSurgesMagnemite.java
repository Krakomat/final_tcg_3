package model.scripting.ltSurge;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;

public class Script_00332_LtSurgesMagnemite extends PokemonCardScript {

	public Script_00332_LtSurgesMagnemite(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.LIGHTNING);
		this.addAttack("Removal Pulse", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.LIGHTNING);
		att2Cost.add(Element.LIGHTNING);
		this.addAttack("Confusion Pulse", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Removal Pulse"))
			this.RemovalPulse();
		else
			this.ConfusionPulse();
	}

	private void RemovalPulse() {
		Player player = this.getCardOwner();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Position defendingPosition = gameModel.getPosition(defender);
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		// Check for energy at enemy position:
		if (defendingPosition.getEnergyCards().size() > 0 && !((PokemonCard) defendingPosition.getTopCard()).hasCondition(PokemonCondition.BROCKS_PROTECTION)) {
			if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
				// Choose one of them and remove it:
				List<Card> cardList = new ArrayList<>();
				for (Card c : defendingPosition.getEnergyCards())
					cardList.add(c);

				Card chosenEnergyCard = player.playerChoosesCards(cardList, 1, true, "Choose one energy card to remove!").get(0);
				PositionID discardPile = enemyDiscardPile();

				gameModel.getAttackAction().moveCard(defender, discardPile, chosenEnergyCard.getGameID(), true);
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}

	private void ConfusionPulse() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		// Flip coin to check if defending pokemon is confused:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is confused!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is confused!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.CONFUSED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
