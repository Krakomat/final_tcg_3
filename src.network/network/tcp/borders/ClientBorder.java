package network.tcp.borders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;

import model.enums.PositionID;
import model.interfaces.GameModelUpdate;
import network.client.Player;
import network.serialization.TCGSerializer;
import network.server.PokemonGameManager;
import network.tcp.messages.ByteString;
import network.tcp.messages.Method;
import network.tcp.messages.QueryMessage;
import network.tcp.messages.RespondMessage;

/**
 * Simulates an artificial server for the client, so messages from the client to the server are being transfered by this class.
 * 
 * @author Michael
 *
 */
public class ClientBorder implements PokemonGameManager {

	/** Responsible for sending messages to the server */
	private Client myClient;
	private ClientListener clientListener;
	private ClientStateListener clientStateListener;
	private TCGSerializer serializer;
	private RespondMessage respondMessage;

	public ClientBorder(Player player) {
		this.clientListener = new ClientListener(player, this);
		this.clientStateListener = new ClientConnectionListener(player);
		this.serializer = new TCGSerializer();
		this.setRespondMessage(null);
	}

	public boolean connectAsPlayer(Player player, String ipAdress, String password) {
		try {
			this.myClient = Network.connectToServer(ipAdress, ServerMain.SERVER_PORT);
			this.myClient.start();
			this.myClient.addMessageListener(clientListener);
			this.myClient.addClientStateListener(clientStateListener);
			System.out.println("[ClientBorder] Connected to server!");
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<ByteString> parameters = new ArrayList<>();
		parameters.add(serializer.packString(player.getName()));
		parameters.add(serializer.packString(password));
		QueryMessage message = new QueryMessage(Method.SERVER_CONNECT_AS_PLAYER, parameters);
		message.logSendMessage("ClientBorder");
		this.respondMessage = null;
		myClient.send(message);

		this.waitForResponse();

		RespondMessage response = this.respondMessage;
		response.logSendMessage("ClientBorder");

		boolean returnStatement = serializer.unpackBool(response.getParameters().get(0));

		return returnStatement;
	}

	@Override
	public boolean connectAsPlayer(HostedConnection player, String name, String password) {
		try {
			throw new IOException("Don't call connectAsPlayer on network");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<String> getPlayerActions(int positionIndex, PositionID position, Player player) {
		List<ByteString> parameters = new ArrayList<>();
		parameters.add(serializer.packInt(positionIndex));
		parameters.add(serializer.packPositionID(position));
		QueryMessage message = new QueryMessage(Method.SERVER_GET_PLAYER_ACTIONS, parameters);
		message.logSendMessage("ClientBorder");
		this.respondMessage = null;
		myClient.send(message);

		this.waitForResponse();

		RespondMessage response = this.respondMessage;
		response.logSendMessage("ClientBorder");
		try {
			List<String> returnStatement = serializer.unpackStringList(response.getParameters().get(0));
			return returnStatement;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void playerPlaysCard(Player player, int index) {
		QueryMessage message = new QueryMessage(Method.SERVER_PLAYER_PLAYS_CARD, serializer.packInt(index));
		message.logSendMessage("ClientBorder");
		myClient.send(message);
	}

	@Override
	public List<String> getAttacksForPosition(PositionID position) {
		QueryMessage message = new QueryMessage(Method.SERVER_GET_ATTACKS_FOR_POSITION, serializer.packPositionID(position));
		message.logSendMessage("ClientBorder");
		this.respondMessage = null;
		myClient.send(message);

		this.waitForResponse();

		RespondMessage response = this.respondMessage;
		response.logSendMessage("ClientBorder");
		try {
			List<String> returnStatement = serializer.unpackStringList(response.getParameters().get(0));
			return returnStatement;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void executeAttack(Player player, String attackName) {
		QueryMessage message = new QueryMessage(Method.SERVER_EXECUTE_ATTACK, serializer.packString(attackName));
		message.logSendMessage("ClientBorder");
		myClient.send(message);
	}

	@Override
	public List<String> getPokePowerForPosition(PositionID posID) {
		QueryMessage message = new QueryMessage(Method.SERVER_GET_POKEPOWER_FOR_POSITION, serializer.packPositionID(posID));
		message.logSendMessage("ClientBorder");
		this.respondMessage = null;
		myClient.send(message);

		this.waitForResponse();

		RespondMessage response = this.respondMessage;
		response.logSendMessage("ClientBorder");
		try {
			List<String> returnStatement = serializer.unpackStringList(response.getParameters().get(0));
			return returnStatement;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void executePokemonPower(Player player, String name, PositionID posID) {
		List<ByteString> parameters = new ArrayList<ByteString>();
		parameters.add(serializer.packString(name));
		parameters.add(serializer.packPositionID(posID));
		QueryMessage message = new QueryMessage(Method.SERVER_EXECUTE_POKEMON_POWER, parameters);
		message.logSendMessage("ClientBorder");
		myClient.send(message);
	}

	@Override
	public void retreatPokemon(Player player) {
		QueryMessage message = new QueryMessage(Method.SERVER_RETREAT_POKEMON);
		message.logSendMessage("ClientBorder");
		myClient.send(message);
	}

	@Override
	public void endTurn(Player player) {
		QueryMessage message = new QueryMessage(Method.SERVER_END_TURN);
		message.logSendMessage("ClientBorder");
		myClient.send(message);
	}

	@Override
	public GameModelUpdate getGameModelForPlayer(Player player) {
		List<ByteString> parameters = new ArrayList<>();
		QueryMessage message = new QueryMessage(Method.SERVER_GET_GAME_MODEL_FOR_PLAYER, parameters);
		message.logSendMessage("ClientBorder");
		this.respondMessage = null;
		myClient.send(message);

		this.waitForResponse();

		RespondMessage response = this.respondMessage;
		response.logSendMessage("ClientBorder");
		try {
			GameModelUpdate returnStatement = serializer.unpackGameModelUpdate(response.getParameters().get(0));
			return returnStatement;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setRespondMessage(RespondMessage respondMessage) {
		this.respondMessage = respondMessage;
	}

	private void waitForResponse() {
		while (this.respondMessage == null) {
			sleep(100);
		}
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setListener(ServerListener listener) {
		throw new UnsupportedOperationException("Client Border does not support setListener!");
	}

	public Client getClient() {
		return this.myClient;
	}
}
