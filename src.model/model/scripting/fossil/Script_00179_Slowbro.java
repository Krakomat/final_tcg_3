package model.scripting.fossil;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.interfaces.PokemonGame;
import model.interfaces.Position;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00179_Slowbro extends PokemonCardScript {

	public Script_00179_Slowbro(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.PSYCHIC);
		att1Cost.add(Element.PSYCHIC);
		this.addAttack("Psyshock", att1Cost);

		this.addPokemonPower("Strange Behavior");
	}

	@Override
	public boolean pokemonPowerCanBeExecuted(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;
		Player player = this.getCardOwner();

		if (pCard.hasCondition(PokemonCondition.ASLEEP) || pCard.hasCondition(PokemonCondition.CONFUSED) || pCard.hasCondition(PokemonCondition.PARALYZED))
			return false;
		if (!gameModel.getAttackCondition().pokemonIsInPlay(pCard))
			return false;
		if (gameModel.getPlayerOnTurn().getColor() != this.getCardOwner().getColor())
			return false;
		if (gameModel.getFullArenaPositions(player.getColor()).size() < 2)
			return false;
		if (!gameModel.getGameModelParameters().getPower_Active_00164_Muk().isEmpty())
			return false;
		if (pCard.getHitpoints() == pCard.getDamageMarks() + 10) // Slowbro would get knocked out
			return false;
		for (PositionID posID : gameModel.getFullArenaPositions(player.getColor())) {
			Position pos = gameModel.getPosition(posID);
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			if (pokemon.getDamageMarks() > 0 && pokemon.getGameID() != this.card.getGameID())
				return true;
		}
		return false;
	}

	@Override
	public void executePokemonPower(String powerName) {
		PokemonCard pCard = (PokemonCard) this.card;
		Player player = this.getCardOwner();
		List<Card> cardList = new ArrayList<>();
		cardList.add(card);

		// Create a list with all of the players damaged position ids except slowbro:
		List<PositionID> damagedPositions = getDamagedPositions();
		damagedPositions.remove(this.card.getCurrentPosition().getPositionID()); // filter slowbros position out

		PositionID chosenPosition = player.playerChoosesPositions(damagedPositions, 1, true, "Choose a Pokemon to move a damage counter to Slowbro!").get(0);

		// Distribute damage:
		Position position = gameModel.getPosition(chosenPosition);
		PokemonCard pokemon = (PokemonCard) position.getTopCard();
		cardList.add(pokemon);
		this.gameModel.sendCardMessageToAllPlayers(player.getName() + " moves a damage counter from " + pokemon.getName() + " to " + this.card.getName() + "!",
				cardList, "");
		pokemon.setDamageMarks(pokemon.getDamageMarks() - 10);
		pCard.setDamageMarks(pCard.getDamageMarks() + 10);

		gameModel.sendGameModelToAllPlayers("");
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Psyshock"))
			this.psyshock();
	}

	private void psyshock() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 20, true);

		// Flip coin to check if defending pokemon is poisoned:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private List<PositionID> getDamagedPositions() {
		Player player = this.getCardOwner();
		List<PositionID> arenaPositions = gameModel.getFullArenaPositions(player.getColor());
		List<PositionID> damagedPositions = new ArrayList<>();
		for (PositionID posID : arenaPositions) {
			PokemonCard topCard = (PokemonCard) gameModel.getPosition(posID).getTopCard();
			if (topCard.getDamageMarks() > 0)
				damagedPositions.add(posID);
		}
		return damagedPositions;
	}
}
