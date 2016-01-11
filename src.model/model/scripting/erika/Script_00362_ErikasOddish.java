package model.scripting.erika;

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
import network.client.Player;

public class Script_00362_ErikasOddish extends PokemonCardScript {

	public Script_00362_ErikasOddish(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		att1Cost.add(Element.GRASS);
		this.addAttack("Poisonpowder", att1Cost);

		this.addPokemonPower("Photosynthesis");
	}

	@Override
	public void executeAttack(String attackName) {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is poisoned:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is poisoned!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is poisoned!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.POISONED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		// Cannot be manually activated!
		return false;
	}

	public void moveToPosition(PositionID targetPosition) {
		super.moveToPosition(targetPosition);
		checkPhotosynthesis();
	}

	public void playFromHand() {
		super.playFromHand();
		checkPhotosynthesis();
	}

	public void pokemonGotCondition(int turnNumber, PokemonCondition condition) {
		super.pokemonGotCondition(turnNumber, condition);
		checkPhotosynthesis();
	}

	public void pokemonGotConditionsRemoved(int turnNumber) {
		super.pokemonGotConditionsRemoved(turnNumber);
		checkPhotosynthesis();
	}

	public void executeEndTurnActions() {
		super.executeEndTurnActions();
		checkPhotosynthesis();
	}

	public void executePreTurnActions() {
		super.executePreTurnActions();
		checkPhotosynthesis();
	}

	private void checkPhotosynthesis() {
		boolean powerAllowed = true;
		if (gameModel.getGameModelParameters().isAllowedToPlayPokemonPower() > 0)
			powerAllowed = false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			powerAllowed = false;
		if (((PokemonCard) this.card).hasCondition(PokemonCondition.POKEMON_POWER_BLOCK))
			powerAllowed = false;
		if (this.card.getCurrentPosition().getPositionID() != null && !PositionID.isArenaPosition(this.card.getCurrentPosition().getPositionID()))
			powerAllowed = false;

		if (powerAllowed) {
			if (!gameModel.getGameModelParameters().getPower_Activated_00362_ErikasOddish().contains(this.card.getGameID()))
				gameModel.getGameModelParameters().getPower_Activated_00362_ErikasOddish().add(new Integer(this.card.getGameID()));
		} else {
			gameModel.getGameModelParameters().getPower_Activated_00362_ErikasOddish().remove(new Integer(this.card.getGameID()));
		}

		boolean playerHasPower = false;
		Player player = this.getCardOwner();

		for (Integer gameID : gameModel.getGameModelParameters().getPower_Activated_00362_ErikasOddish()) {
			Card amonite = gameModel.getCard(gameID);
			if (amonite.getCurrentPosition().getColor() == player.getColor())
				playerHasPower = true;
		}

		if (playerHasPower) {
			// Turn all energy attached to ErikasOddish into grass energy:
			PokemonCard card = (PokemonCard) this.card;
			Position pos = card.getCurrentPosition();

			for (Card e : pos.getEnergyCards()) {
				EnergyCard eCard = (EnergyCard) e;
				List<Element> originalEnergy = new ArrayList<>();
				for (int i = 0; i < eCard.getProvidedEnergy().size(); i++) {
					originalEnergy.add(eCard.getProvidedEnergy().remove(i));
					eCard.getProvidedEnergy().add(i, Element.GRASS);
				}
			}

			gameModel.sendGameModelToAllPlayers("");
		} else {
			// Restore energy cards:
			PokemonCard card = (PokemonCard) this.card;
			Position pos = card.getCurrentPosition();

			for (Card e : pos.getEnergyCards()) {
				EnergyCard eCard = (EnergyCard) e;
				List<Element> originalEnergy = new ArrayList<>();
				for (int i = 0; i < eCard.getProvidedEnergy().size(); i++) {
					originalEnergy.add(eCard.getProvidedEnergy().remove(i));
					eCard.getProvidedEnergy().add(i, EnergyCard.getOriginalEnergy(eCard.getCardId()));
				}
			}

			gameModel.sendGameModelToAllPlayers("");
		}
	}

}
