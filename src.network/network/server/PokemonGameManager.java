package network.server;

import java.util.List;

import com.jme3.network.HostedConnection;

import network.client.Player;
import network.tcp.borders.ServerListener;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.GameModelUpdate;

/**
 * The pokemon game manager provides the api to the clients and controls the
 * game flow. This is by convention the only class, that can execute a
 * wait()-operation.
 * 
 * @author Michael
 *
 */
public interface PokemonGameManager {

	/**
	 * The given player connects to this game and gets assigned to a
	 * {@link Color}. Returns false, if the game is currently full, or the
	 * password was wrong. Only for network applications.
	 * 
	 * @param player
	 * @param name
	 * @param password
	 * @return
	 */
	public boolean connectAsPlayer(HostedConnection player, String name, String password);

	/**
	 * The given player connects to this game and gets assigned to a
	 * {@link Color}. Returns false, if the game is currently full, or the
	 * password was wrong. Only for local applications.
	 * 
	 * @param player
	 * @param password
	 * @return
	 */
	public boolean connectAsLocalPlayer(Player player, String password);

	/**
	 * Determines which actions the player on turn can execute for the card that
	 * can be found at the given position with the given index
	 * 
	 * @param positionIndex
	 *            index, at which the card can be found at the position
	 * @param position
	 *            position of the card
	 * @param player
	 *            player, who wants to execute actions with this card
	 * @return an ArrayList<String>, out of which each entry can be transformed
	 *         to a {@link PlayerAction} via
	 *         {@link PlayerAction.valueOf(String)}
	 */
	public List<String> getPlayerActions(int positionIndex, PositionID position, Player player);

	/**
	 * The given player plays the given card from his hand. The server has to
	 * check, whether this move is correct according to the rules. If this is
	 * not the case, then the player loses the game.
	 * 
	 * @param player
	 *            player, who enforces the action
	 * @param index
	 *            index of the card on which the action is enforced. It has to
	 *            be in the hand of the respective player. The action executed
	 *            is determined by the card type(e.g. play energy card, if
	 *            CardType == ENERGY).
	 */
	public void playerPlaysCard(Player player, int index);

	/**
	 * Returns the list of attacks for the pokemon on the given position or an
	 * empty list if the position is empty.
	 * 
	 * @param position
	 * @return
	 */
	public List<String> getAttacksForPosition(PositionID position);

	/**
	 * The given player executes the given attack. The attacker is the players
	 * active pokemon, the defender is the enemies active pokemon. After
	 * executing the attack, the field is checked for defeated pokemon and the
	 * players turn ends.
	 * 
	 * @param player
	 *            player executing the given attack
	 * @param attackName
	 *            attack to be executed
	 */
	public void executeAttack(Player player, String attackName);

	/**
	 * Returns the list of pokemon power names for the pokemon on the given
	 * position or an empty list if the position is empty.
	 * 
	 * @param position
	 * @return
	 */
	public List<String> getPokePowerForPosition(PositionID posID);

	/**
	 * Executes the given pokemon power.
	 * 
	 * @param player
	 * @param name
	 * @param posID
	 */
	public void executePokemonPower(Player player, String name, PositionID posID);

	/**
	 * The given player retreats his active pokemon. First it is checked if
	 * retreat is allowed. Also, if the active pokemon is confused then a coin
	 * is flipped and the active pokemon can't retreat if the coin shows tails.
	 * On retreat the player is prompted to pay the neccessary energy costs.
	 * After that he is promted to choose a pokemon on his bench to swap.
	 * 
	 * @param player
	 *            player who gave the command
	 */
	public void retreatPokemon(Player player);

	/**
	 * The given player ends his turn. If the given player doesn't match the
	 * player that is currently on turn, then the given player loses the game.
	 * 
	 * @param player
	 */
	public void endTurn(Player player);

	/**
	 * Returns the gameModel.
	 * 
	 * @param player
	 * @return
	 */
	public GameModelUpdate getGameModelForPlayer(Player player);

	/**
	 * Sets the listener for this game manager. Only used at the server side.
	 * 
	 * @param listener
	 */
	public void setListener(ServerListener listener);

	/**
	 * The player with the given color surrenders.
	 * 
	 * @param player
	 */
	public void surrender(Player player);
}
