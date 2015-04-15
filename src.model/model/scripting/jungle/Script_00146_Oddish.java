package model.scripting.jungle;

import java.util.ArrayList;
import java.util.List;

import network.client.Player;
import model.database.Card;
import model.database.PokemonCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.Element;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.interfaces.PokemonGame;
import model.scripting.abstracts.PokemonCardScript;

public class Script_00146_Oddish extends PokemonCardScript {

	public Script_00146_Oddish(PokemonCard card, PokemonGame gameModel) {
		super(card, gameModel);
		List<Element> att1Cost = new ArrayList<>();
		att1Cost.add(Element.GRASS);
		this.addAttack("Stun Spore", att1Cost);

		List<Element> att2Cost = new ArrayList<>();
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.GRASS);
		att2Cost.add(Element.COLORLESS);
		att2Cost.add(Element.COLORLESS);
		this.addAttack("Sprout", att2Cost);
	}

	@Override
	public boolean attackCanBeExecuted(String attackName) {
		if (attackName.equals("Sprout") && gameModel.getFullBenchPositions(this.getCardOwner().getColor()).size() == 5)
			return false;
		else
			return super.attackCanBeExecuted(attackName);
	}

	@Override
	public void executeAttack(String attackName) {
		if (attackName.equals("Stun Spore"))
			this.stunSpore();
		else
			this.sprout();
	}

	private void stunSpore() {
		PositionID attacker = this.card.getCurrentPosition().getPositionID();
		PositionID defender = this.gameModel.getDefendingPosition(this.card.getCurrentPosition().getColor());
		Card defendingPokemon = gameModel.getPosition(defender).getTopCard();
		Element attackerElement = ((PokemonCard) this.card).getElement();
		this.gameModel.getAttackAction().inflictDamageToPosition(attackerElement, attacker, defender, 10, true);

		// Flip coin to check if defending pokemon is paralyzed:
		gameModel.sendTextMessageToAllPlayers("If heads then " + defendingPokemon.getName() + " is paralyzed!", "");
		Coin c = gameModel.getAttackAction().flipACoin();
		gameModel.sendTextMessageToAllPlayers("Coin showed " + c, "");
		if (c == Coin.HEADS) {
			gameModel.sendTextMessageToAllPlayers(defendingPokemon.getName() + " is paralyzed!", "");
			gameModel.getAttackAction().inflictConditionToPosition(defender, PokemonCondition.PARALYZED);
			gameModel.sendGameModelToAllPlayers("");
		}
	}

	private void sprout() {
		ArrayList<Card> basicPokemon = gameModel.getBasicPokemonOnPosition(ownDeck());
		List<Card> bellsproutBasicPokemon = new ArrayList<>();
		for (Card c : basicPokemon)
			if (c instanceof PokemonCard && c.getName().equals("Oddish"))
				bellsproutBasicPokemon.add(c);

		if (bellsproutBasicPokemon.size() > 0) {
			Card chosenCard = this.getCardOwner().playerChoosesCards(bellsproutBasicPokemon, 1, true, "Choose a pokemon to put on your bench!").get(0);
			gameModel.getAttackAction().putBasicPokemonOnBench(getCardOwner(), (PokemonCard) chosenCard);
		} else
			gameModel.sendTextMessageToAllPlayers(this.getCardOwner().getName() + "'s deck contains no Bellsprout!", "");

		// Shuffle deck:
		gameModel.sendTextMessageToAllPlayers(getCardOwner().getName() + " shuffles his deck!", Sounds.SHUFFLE);
		gameModel.getAttackAction().shufflePosition(ownDeck());
	}

	private PositionID ownDeck() {
		Player player = this.getCardOwner();
		if (player.getColor() == Color.BLUE)
			return PositionID.BLUE_DECK;
		else
			return PositionID.RED_DECK;
	}
}
