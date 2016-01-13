package network.tcp.borders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.enums.PositionID;
import model.game.GameModelUpdate;
import network.client.Player;
import network.serialization.TCGSerializer;
import network.server.PokemonGameManager;
import network.tcp.messages.ByteString;
import network.tcp.messages.QueryMessage;
import network.tcp.messages.RespondMessage;

import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;

/**
 * Listens to incoming messages to the server and delegates them to the game manager.
 * 
 * @author Michael
 *
 */
public class ServerListener implements MessageListener<HostedConnection> {

	private PokemonGameManager gameManager;
	/** Only used when a new ServerBorder is created! */
	private Server server;
	private TCGSerializer serializer;
	private Map<Integer, ServerBorder> connectionMap;

	public ServerListener(PokemonGameManager gameManager) {
		this.gameManager = gameManager;
		this.gameManager.setListener(this);
		this.serializer = new TCGSerializer();
		this.connectionMap = new HashMap<>();
	}

	@Override
	public void messageReceived(HostedConnection source, Message m) {
		if (m instanceof QueryMessage) {
			QueryMessage qMessage = (QueryMessage) m;
			System.out.println("[ServerListener] Received QueryMessage: " + qMessage.getMethod());
			try {
				switch (qMessage.getMethod()) {
				case SERVER_CONNECT_AS_PLAYER:
					String name = serializer.unpackString(qMessage.getParameters().get(0));
					String password = serializer.unpackString(qMessage.getParameters().get(1));
					boolean result = gameManager.connectAsPlayer(source, name, password);

					// Send answer:
					List<ByteString> parameters = new ArrayList<>();
					parameters.add(serializer.packBool(result));
					RespondMessage response = new RespondMessage(qMessage.getMethod(), parameters);
					server.broadcast(Filters.in(source), response);
					break;
				case SERVER_END_TURN:
					Player player = this.connectionMap.get(source.getId());
					this.gameManager.endTurn(player);
					break;
				case SERVER_EXECUTE_ATTACK:
					String attackName = serializer.unpackString(qMessage.getParameters().get(0));
					player = this.connectionMap.get(source.getId());
					this.gameManager.executeAttack(player, attackName);
					break;
				case SERVER_EXECUTE_POKEMON_POWER:
					attackName = serializer.unpackString(qMessage.getParameters().get(0));
					PositionID posID = serializer.unpackPositionID(qMessage.getParameters().get(1));
					player = this.connectionMap.get(source.getId());
					this.gameManager.executePokemonPower(player, attackName, posID);
					break;
				case SERVER_GET_ATTACKS_FOR_POSITION:
					posID = serializer.unpackPositionID(qMessage.getParameters().get(0));

					List<String> stringList = gameManager.getAttacksForPosition(posID);

					response = new RespondMessage(qMessage.getMethod(), serializer.packStringList(stringList));
					server.broadcast(Filters.in(source), response);
					break;
				case SERVER_GET_GAME_MODEL_FOR_PLAYER:
					player = this.connectionMap.get(source.getId());
					int version = serializer.unpackInt(qMessage.getParameters().get(0));

					GameModelUpdate gameModel = gameManager.getGameModelForPlayer(player, version);

					response = new RespondMessage(qMessage.getMethod(), serializer.packGameModelUpdate(gameModel));
					server.broadcast(Filters.in(source), response);
					break;
				case SERVER_GET_PLAYER_ACTIONS:
					int positionIndex = serializer.unpackInt(qMessage.getParameters().get(0));
					posID = serializer.unpackPositionID(qMessage.getParameters().get(1));
					player = this.connectionMap.get(source.getId());

					stringList = gameManager.getPlayerActions(positionIndex, posID, player);

					response = new RespondMessage(qMessage.getMethod(), serializer.packStringList(stringList));
					server.broadcast(Filters.in(source), response);
					break;
				case SERVER_GET_POKEPOWER_FOR_POSITION:
					posID = serializer.unpackPositionID(qMessage.getParameters().get(0));

					stringList = gameManager.getPokePowerForPosition(posID);

					response = new RespondMessage(qMessage.getMethod(), serializer.packStringList(stringList));
					server.broadcast(Filters.in(source), response);
					break;
				case SERVER_PLAYER_PLAYS_CARD:
					int index = serializer.unpackInt(qMessage.getParameters().get(0));
					player = this.connectionMap.get(source.getId());
					this.gameManager.playerPlaysCard(player, index);
					break;
				case SERVER_RETREAT_POKEMON:
					player = this.connectionMap.get(source.getId());
					this.gameManager.retreatPokemon(player);
					break;
				case SERVER_ACTIVATE_STADIUM:
					player = this.connectionMap.get(source.getId());
					this.gameManager.activateStadium(player);
					break;
				case SERVER_SURRENDER:
					player = this.connectionMap.get(source.getId());
					this.gameManager.surrender(player);
					break;
				default:
					try {
						throw new IOException("Wrong Method type at ServerListener: " + qMessage.getMethod());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (m instanceof RespondMessage) {
			RespondMessage rMessage = (RespondMessage) m;
			ServerBorder border = this.connectionMap.get(source.getId());
			border.setRespondMessage(rMessage);
		}
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public void addConnection(HostedConnection connection, ServerBorder border) {
		System.out.println("[ServerListener] Added connection with id: " + connection.getId());
		this.connectionMap.put(connection.getId(), border);
	}

	public Map<Integer, ServerBorder> getConnections() {
		return this.connectionMap;
	}
}
