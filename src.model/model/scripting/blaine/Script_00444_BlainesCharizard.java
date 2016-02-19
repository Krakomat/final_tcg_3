package model.scripting.blaine;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00444_BlainesCharizard extends PokemonCardScript {

	public Script_00444_BlainesCharizard(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		this.addAttack("Roaring Flames", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.FIRE);
		att2Cost.add(Element.FIRE);
		this.addAttack("Flame Jet", att2Cost);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Roaring Flames"))
			this.RoaringFlames();
		else
			this.FlameJet();
	}

	private void RoaringFlames() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();

		// Discard all rock energy cards:
		List<Card> rockEnergy = getRockEnergyOnPosition();
		for (Card c : rockEnergy) {
			gameModel.getAttackAction().discardCardToDiscardPile(getCurrentPositionID(), c.getGameID(), true);
		}
		gameModel.sendGameModelToAllPlayers("");

		int bonusDamage = 0;
		if (costsAvailable()) {
			bonusDamage = 20 * getFireEnergyOnPosition().size();
			// Discard all energy cards:
			gameModel.getAttackAction().removeAllEnergyFromPosition(attacker);
			gameModel.sendGameModelToAllPlayers("");
		}
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20 + bonusDamage, true);
	}

	private List<Card> getRockEnergyOnPosition() {
		Position pos = gameModel.getPosition(getCurrentPositionID());
		List<Card> cardList = new ArrayList<>();
		for (Card c : pos.getEnergyCards())
			if (c.getCardId().equals("00097"))
				cardList.add(c);
		return cardList;
	}

	private List<Card> getFireEnergyOnPosition() {
		Position pos = gameModel.getPosition(getCurrentPositionID());
		List<Card> cardList = new ArrayList<>();
		for (Card c : pos.getEnergyCards())
			if (c.getCardId().equals("00098"))
				cardList.add(c);
		return cardList;
	}

	private boolean costsAvailable() {
		List<Element> attackCosts = new ArrayList<Element>();
		attackCosts.add(Element.FIRE);
		attackCosts.add(Element.FIRE);

		// Check if costs are available at the position:
		ArrayList<Element> onField = (ArrayList<Element>) this.card.getCurrentPosition().getEnergy();
		@SuppressWarnings("unchecked")
		List<Element> copy = (ArrayList<Element>) onField.clone();
		List<Element> colors = new ArrayList<Element>();
		List<Element> colorless = new ArrayList<Element>();
		for (int i = 0; i < attackCosts.size(); i++) {
			if (attackCosts.get(i).equals(Element.COLORLESS))
				colorless.add(attackCosts.get(i));
			else
				colors.add(attackCosts.get(i));
		}

		for (int i = 0; i < colors.size(); i++) {
			Element e = colors.get(i);
			boolean found = false;
			for (int j = 0; j < copy.size(); j++) {
				if (e.equals(copy.get(j)) && !found) {
					copy.remove(j);
					found = true;
				}
			}
			// Try to pay with rainbow energy:
			if (!found) {
				for (int j = 0; j < copy.size(); j++) {
					if (copy.get(j).equals(Element.RAINBOW) && !found) {
						copy.remove(j);
						found = true;
					}
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	private void FlameJet() {
		Player player = this.getCardOwner();
		Player enemy = this.getEnemyPlayer();
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		Element attackerElement = ((PokemonCard) this.card).getElement();

		if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
			if (gameModel.getFullArenaPositions(enemy.getColor()).size() > 0) {
				PositionID benchDefender = player.playerChoosesPositions(gameModel.getFullArenaPositions(enemy.getColor()), 1, true, "Choose a pokemon that receives the damage!")
						.get(0);
				this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, benchDefender, 40, false);
			} else
				gameModel.sendTextMessageToAllPlayers(getEnemyPlayer().getName() + " has no bench Pokemon!", "");
		}
	}
}
