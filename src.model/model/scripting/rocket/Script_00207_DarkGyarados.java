package model.scripting.rocket;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00207_DarkGyarados extends PokemonCardScript {

	public Script_00207_DarkGyarados(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		att1Cost.add(Element.WATER);
		this.addAttack("Ice Beam", att1Cost);

		this.addPokemonPower("Final Beam");
	}

	public void pokemonIsDamaged(int turnNumber, int damage, PositionID source) {
		if (finalBeamCanBeExecuted()) {
			if (source != null) {
				gameModel.sendCardMessageToAllPlayers(this.card.getName() + " activates Final Beam!", card, "");
				if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
					// Inflict damage:
					PositionID ownPosition = this.card.getCurrentPosition().getPositionID();
					gameModel.getAttackAction().inflictDamageToPosition(Element.WATER, ownPosition, source, realWaterEnergy().size() * 20, false);
				} else
					gameModel.sendCardMessageToAllPlayers("Final Beam was not successful!", card, "");
			}
		}
	}

	private List<Card> realWaterEnergy() {
		List<Card> erg = new ArrayList<>();
		List<Card> cardList = this.card.getCurrentPosition().getEnergyCards();
		for (Card c : cardList)
			if (c.getCardId().equals("00102"))
				erg.add(c);
		return erg;
	}

	/*
	 * Only is executed when DarkGyarados is knocked out. This automatically implies that DarkGyarados has to be in play.
	 */
	private boolean finalBeamCanBeExecuted() {
		// Can be executed when DarkGyarados isn't asleep, confused or paralyzed. Also can only be executed when the owner of DarkGyarados is not on turn, to prevent chain
		// reactions between two opposing DarkGyarados.
		PokemonCard pCard = (PokemonCard) this.card;
		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			return false;
		if (this.gameModel.getGameModelParameters().activeEffect("00164"))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() == this.getCardOwner().getColor())
			return false;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			return false;
		return pCard.hasCondition(PokemonCondition.KNOCKOUT);
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 30, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}
}
