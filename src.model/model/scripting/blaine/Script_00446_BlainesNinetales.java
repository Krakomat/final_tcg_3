package model.scripting.blaine;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00446_BlainesNinetales extends PokemonCardScript {

	public Script_00446_BlainesNinetales(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.FIRE);
		att1Cost.add(Element.FIRE);
		this.addAttack("Burn Up", att1Cost);

		this.addPokemonPower("Healing Fire");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 50, true);

		if (gameModel.getAttackAction().flipACoin() == Coin.TAILS) {
			gameModel.sendTextMessageToAllPlayers(this.card.getName() + " loses all its fire energy!", "");
			// Discard all fire energy cards:
			List<Card> fireEnergy = getFireEnergyOnPosition();
			for (Card c : fireEnergy) {
				gameModel.getAttackAction().discardCardToDiscardPile(getCurrentPositionID(), c.getGameID(), true);
			}
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private List<Card> getFireEnergyOnPosition() {
		Position pos = gameModel.getPosition(getCurrentPositionID());
		List<Card> cardList = new ArrayList<>();
		for (Card c : pos.getEnergyCards())
			if (c.getCardId().equals("00098"))
				cardList.add(c);
		return cardList;
	}

	private boolean HealingFireCanBeExecuted() {
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() == this.getCardOwner().getColor())
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		return true;
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	public void pokemonGotEnergy(EnergyCard energyCard) {
		if (energyCard.getCardId().equals("00098")) {
			if (HealingFireCanBeExecuted()) {
				gameModel.getAttackAction().healPosition(getCurrentPositionID(), 10);
				gameModel.sendGameModelToAllPlayers("");
			}
		}
	}
}
