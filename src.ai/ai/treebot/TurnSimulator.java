package ai.treebot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jme3.network.HostedConnection;

import model.database.Card;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.Coin;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PokemonCondition;
import model.enums.PositionID;
import model.enums.Sounds;
import model.enums.TurnState;
import model.game.LocalPokemonGameModel;
import model.interfaces.GameModelUpdate;
import model.interfaces.Position;
import model.scripting.abstracts.CardScript;
import model.scripting.abstracts.PokemonCardScript;
import network.client.Player;
import network.server.PokemonGameManager;
import network.tcp.borders.ServerListener;

/**
 * A server that is able to simulate a move locally.
 * 
 * @author Michael
 *
 */
public class TurnSimulator implements PokemonGameManager {

	private LocalPokemonGameModel gameModel;

	private TurnState turnState;

	public TurnSimulator(LocalPokemonGameModel localGameModel) {
		this.gameModel = localGameModel;
		this.turnState = TurnState.TURN_BEGIN;
	}

	@Override
	public boolean connectAsPlayer(HostedConnection player, String name, String password) {
		throw new UnsupportedOperationException("connectAsPlayer");
	}

	@Override
	public boolean connectAsLocalPlayer(Player player, String password) {
		throw new UnsupportedOperationException("connectAsLocalPlayer");
	}

	@Override
	public List<String> getPlayerActions(int positionIndex, PositionID position, Player player) {
		boolean handCard = position == PositionID.BLUE_HAND || position == PositionID.RED_HAND;
		Card c = handCard ? gameModel.getPosition(position).getCardAtIndex(positionIndex) : gameModel.getPosition(position).getTopCard();
		ArrayList<PlayerAction> actionList = new ArrayList<PlayerAction>();
		Position pos = gameModel.getPosition(position);
		// If player on turn and position doesn't belong to enemy
		boolean playerOnTurn = gameModel.getPlayerOnTurn().getColor() == player.getColor();
		boolean playerOnTurnBlue = gameModel.getPlayerOnTurn().getColor() == Color.BLUE;
		boolean playerOnTurnRed = gameModel.getPlayerOnTurn().getColor() == Color.RED;
		boolean positionColorBlue = pos.getColor() == Color.BLUE;
		if (playerOnTurn && ((positionColorBlue && playerOnTurnBlue) || (!positionColorBlue && playerOnTurnRed))) {
			actionList = getActionsForSelectedPosition(actionList, c, position, player);
		}
		ArrayList<String> stringActionList = new ArrayList<String>();
		for (int i = 0; i < actionList.size(); i++)
			stringActionList.add(actionList.get(i).toString());
		return stringActionList;
	}

	/**
	 * Adds actions to the given list, which are dependent to the position, the selected card is on. Only is called, if the given player is on turn.
	 * 
	 * @param actionList
	 * @param game
	 * @param c
	 * @param posID
	 * @param player
	 * @return
	 */
	private ArrayList<PlayerAction> getActionsForSelectedPosition(ArrayList<PlayerAction> actionList, Card c, PositionID posID, Player player) {
		CardScript script = c.getCardScript();

		// Check playedFromHand
		if (c instanceof PokemonCard || c instanceof TrainerCard || c instanceof EnergyCard) {
			if (script.canBePlayedFromHand() != null)
				actionList.add(script.canBePlayedFromHand());
		} else
			throw new IllegalArgumentException("Error: Card is not valid!");

		// Check attack1/2, pokemonPower, retreat:
		if (c instanceof PokemonCard) {
			PokemonCardScript pScript = (PokemonCardScript) script;

			// Check retreat:
			if (pScript.retreatCanBeExecuted())
				actionList.add(PlayerAction.RETREAT_POKEMON);

			// Check attacks:
			if (c.getCurrentPosition().getPositionID() == PositionID.BLUE_ACTIVEPOKEMON || c.getCurrentPosition().getPositionID() == PositionID.RED_ACTIVEPOKEMON) {
				for (String attName : pScript.getAttackNames()) {
					if (pScript.attackCanBeExecuted(attName)) {
						switch (pScript.getAttackNumber(attName)) {
						case 0:
							actionList.add(PlayerAction.ATTACK_1);
							break;
						case 1:
							actionList.add(PlayerAction.ATTACK_2);
							break;
						case -1:
							throw new IllegalArgumentException("Error: AttackName" + attName + " is not valid!");
						default:
							throw new IllegalArgumentException("Error: AttackName " + attName + " is out of range in the list");
						}
					}
				}
			}
			// Check pokemon power:
			for (String powerName : pScript.getPokemonPowerNames()) {
				if (pScript.pokemonPowerCanBeExecuted(powerName)) {
					switch (pScript.getPokemonPowerNumber(powerName)) {
					case 0:
						actionList.add(PlayerAction.POKEMON_POWER);
						break;
					case -1:
						throw new IllegalArgumentException("Error: powerName" + powerName + " is not valid!");
					default:
						throw new IllegalArgumentException("Error: powerName " + powerName + " is out of range in the list");
					}
				}
			}

		}
		return actionList;
	}

	@Override
	public void playerPlaysCard(Player player, int index) {
		if (this.turnState == TurnState.TURN_END)
			try {
				throw new IOException("Error: Turn was ended already in local game model");
			} catch (IOException e) {
				e.printStackTrace();
			}

		// Get the hand position:
		Position handPos = null;
		if (player.getColor() == Color.BLUE)
			handPos = gameModel.getPosition(PositionID.BLUE_HAND);
		else
			handPos = gameModel.getPosition(PositionID.RED_HAND);

		// Get the card:
		Card c = handPos.getCardAtIndex(index);

		// Check if card is allowed to be played:
		if (c.getCardScript().canBePlayedFromHand() == null)
			gameModel.playerLoses(player);

		// If card is a trainer card then send card message beforehand, so scripts don't need to implement this:
		if (c instanceof TrainerCard)
			gameModel.sendCardMessageToAllPlayers(player.getName() + " plays " + c.getName(), c, Sounds.ACTIVATE_TRAINER);

		// Set the turn number for the card:
		c.setPlayedInTurn(gameModel.getTurnNumber());

		// Execute the card script:
		c.getCardScript().playFromHand();

		gameModel.sendGameModelToAllPlayers("");
		gameModel.cleanDefeatedPositions();
	}

	@Override
	public List<String> getAttacksForPosition(PositionID position) {
		Position pos = this.gameModel.getPosition(position);
		if (pos.isEmpty() || !(pos.getTopCard() instanceof PokemonCard))
			return new ArrayList<String>(); // position empty or no arena position

		// Get the attacks from the card script:
		PokemonCard pokemon = (PokemonCard) pos.getTopCard();
		PokemonCardScript script = (PokemonCardScript) pokemon.getCardScript();
		return script.getAttackNames();
	}

	@Override
	public void executeAttack(Player player, String attackName) {
		if (this.turnState == TurnState.TURN_END)
			try {
				throw new IOException("Error: Turn was ended already in local game model");
			} catch (IOException e) {
				e.printStackTrace();
			}

		// Get the script:
		PokemonCard active = null;
		if (player.getColor() == Color.BLUE)
			active = (PokemonCard) gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard();
		else
			active = (PokemonCard) gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard();
		PokemonCardScript pScript = (PokemonCardScript) active.getCardScript();

		// Get the attack name:
		if (pScript.getAttackNumber(attackName) == -1)
			throw new IllegalArgumentException("Error: attack name " + attackName + " is not valid in executeAttack()");

		// Check is attacking is allowed:
		if (!pScript.attackCanBeExecuted(attackName))
			gameModel.playerLoses(player);

		// Check if pokemon is confused and flip a coin, if attacking is allowed or the pokemon hurts itself:
		boolean attackAllowed = true;
		if (active.hasCondition(PokemonCondition.CONFUSED)) {
			gameModel.sendTextMessageToAllPlayers("Coinflip: On TAILS " + active.getName() + " hurts itself!", "");
			Coin c = gameModel.getAttackAction().flipACoin();
			if (c == Coin.TAILS) {
				attackAllowed = false;
				// Damage attacker:
				gameModel.sendTextMessageToAllPlayers(active.getName() + " hurts itself!", "");
				gameModel.getAttackAction().inflictDamageToPosition(active.getElement(), active.getCurrentPosition().getPositionID(), active.getCurrentPosition().getPositionID(),
						20, true);
			}
		}

		// Check if pokemon is blind and flip a coin, if attacking is allowed:
		if (attackAllowed && active.hasCondition(PokemonCondition.BLIND)) {
			gameModel.sendTextMessageToAllPlayers("Coinflip: " + active.getName() + " can't attack when tails", "");
			Coin c = gameModel.getAttackAction().flipACoin();
			if (c == Coin.TAILS)
				attackAllowed = false;
		}

		if (attackAllowed) {
			// Execute the attack:
			gameModel.sendTextMessageToAllPlayers(active.getName() + " attacks with " + attackName, "");

			// Check Vermillion City Gym:
			boolean selfDamage = false;
			if (!gameModel.getPosition(PositionID.STADIUM).isEmpty() && gameModel.getPosition(PositionID.STADIUM).getTopCard().getCardId().equals("00342") && active.getName().contains("Lt. Surge")) {
				boolean answer = player.playerDecidesYesOrNo("Do you want to use the effect of Vermillion City Gym?");
				if (answer) {
					if (gameModel.getAttackAction().flipACoin() == Coin.HEADS) {
						gameModel.getGameModelParameters().setVermillionCityGymAttackModifier(true);
					} else {
						selfDamage = true;
					}
				}
			}
			pScript.executeAttack(attackName);
			gameModel.getGameModelParameters().setVermillionCityGymAttackModifier(false);

			// Do 10 damage to yourself:
			if (selfDamage) {
				gameModel.getAttackAction().inflictDamageToPosition(active.getElement(), active.getCurrentPosition().getPositionID(), active.getCurrentPosition().getPositionID(),
						10, true);
			}
		}
		gameModel.cleanDefeatedPositions();
		endTurn(player);
	}

	@Override
	public List<String> getPokePowerForPosition(PositionID posID) {
		Position pos = this.gameModel.getPosition(posID);
		if (pos.isEmpty() || !(pos.getTopCard() instanceof PokemonCard))
			return new ArrayList<String>(); // position empty or no arena position

		// Get the pokemon power from the card script:
		PokemonCard pokemon = (PokemonCard) pos.getTopCard();
		PokemonCardScript script = (PokemonCardScript) pokemon.getCardScript();
		return script.getPokemonPowerNames();
	}

	@Override
	public void executePokemonPower(Player player, String powerName, PositionID posID) {
		if (this.turnState == TurnState.TURN_END)
			try {
				throw new IOException("Error: Turn was ended already in local game model");
			} catch (IOException e) {
				e.printStackTrace();
			}

		// Get the script:
		PokemonCard pokemon = null;
		if (player.getColor() == Color.BLUE)
			pokemon = (PokemonCard) gameModel.getPosition(posID).getTopCard();
		else
			pokemon = (PokemonCard) gameModel.getPosition(posID).getTopCard();
		PokemonCardScript pScript = (PokemonCardScript) pokemon.getCardScript();

		// Check the power name:
		if (pScript.getPokemonPowerNumber(powerName) == -1)
			throw new IllegalArgumentException("Error: attack name " + powerName + " is not valid in executePokemonPower()");

		// Check is activating is allowed:
		if (!pScript.pokemonPowerCanBeExecuted(powerName))
			gameModel.playerLoses(player);

		// Execute the power:
		gameModel.sendCardMessageToAllPlayers(pokemon.getName() + " activates " + powerName + "!", pokemon, "");
		pScript.executePokemonPower(powerName);
	}

	@Override
	public void retreatPokemon(Player player) {
		if (this.turnState == TurnState.TURN_END)
			try {
				throw new IOException("Error: Turn was ended already in local game model");
			} catch (IOException e) {
				e.printStackTrace();
			}
		// Get the script:
		PokemonCard active = null;
		if (player.getColor() == Color.BLUE)
			active = (PokemonCard) gameModel.getPosition(PositionID.BLUE_ACTIVEPOKEMON).getTopCard();
		else
			active = (PokemonCard) gameModel.getPosition(PositionID.RED_ACTIVEPOKEMON).getTopCard();
		PokemonCardScript pScript = (PokemonCardScript) active.getCardScript();

		// Check is retreat is allowed:
		if (!pScript.retreatCanBeExecuted())
			gameModel.playerLoses(player);

		// Retreat:
		pScript.executeRetreat();
	}

	@Override
	public void endTurn(Player player) {
		this.turnState = TurnState.TURN_END;
	}

	@Override
	public GameModelUpdate getGameModelForPlayer(Player player) {
		throw new UnsupportedOperationException("getGameModelForPlayer");
	}

	public LocalPokemonGameModel getGameModel() {
		return this.gameModel;
	}

	@Override
	public void setListener(ServerListener listener) {
		throw new UnsupportedOperationException("setListener");
	}

	@Override
	public void surrender(Player player) {

	}
}
