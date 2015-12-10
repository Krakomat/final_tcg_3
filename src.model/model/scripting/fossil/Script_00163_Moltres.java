package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00163_Moltres extends PokemonCardScript {

	public Script_00163_Moltres(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		this.addAttack("Wildfire", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		this.addAttack("Dive Bomb", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Wildfire"))
			this.wildfire();
		else
			this.diveBomb();
	}

	private void wildfire() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();

		List<Card> energy = this.card.getCurrentPosition().getEnergyCards();
		List<Card> fireEnergy = new ArrayList<>();
		for (Card c : energy) {
			EnergyCard eCard = (EnergyCard) c; // Cast is allowed
			if (eCard.isBasisEnergy() && eCard.getProvidedEnergy().contains(Element.FIRE))
				fireEnergy.add(eCard);
		}

		if (fireEnergy.size() > 0) {
			List<Card> chosenEnergy = player.playerChoosesCards(fireEnergy, 60, false, "Choose an arbitrary amount of fire energy:");
			if (chosenEnergy.size() > 0) {
				this.gameModel.sendTextMessageToAllPlayers(player.getName() + " discards " + chosenEnergy.size() + " fire energy from Moltres!", "");
				// Discard energy:
				for (Card c : chosenEnergy)
					this.gameModel.getAttackAction().discardCardToDiscardPile(this.card.getCurrentPosition().getPositionID(), c.getGameID(), true);
				this.gameModel.sendGameModelToAllPlayers("");

				// Discard cards from opponents deck:
				this.gameModel.sendTextMessageToAllPlayers(enemy.getName() + " discards " + chosenEnergy.size() + " from his deck!", "");
				for (int i = 0; i < chosenEnergy.size() && this.gameModel.getPosition(enemyDeck()).size() > 0; i++)
					this.gameModel.getAttackAction().discardCardToDiscardPile(enemyDeck(), gameModel.getPosition(enemyDeck()).getTopCard().getGameID(), true);
				this.gameModel.sendGameModelToAllPlayers("");
			} else
				this.gameModel.sendTextMessageToAllPlayers("Wildfire does nothing!", "");
		} else
			this.gameModel.sendTextMessageToAllPlayers("Wildfire does nothing!", "");
	}

	private void diveBomb() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Flip coin to check if damage is applied:
		gameModel.sendTextMessageToAllPlayers("If Tails then Dive Bomb does nothing!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS)
			this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 80, true);
		else
			gameModel.sendTextMessageToAllPlayers("Dive Bomb does nothing!", "");
	}
}
