package network.tcp.borders;

import java.io.IOException;
import java.util.Map;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;

import network.server.PokemonGameManagerFactory;
import network.tcp.messages.QueryMessage;
import network.tcp.messages.RespondMessage;

/**
 * Representation of a Server that is created by the main application of the server program. Maintains a single game currently.
 * 
 * @author Michael
 *
 */
public class ServerMain extends SimpleApplication {
	public static final String GAME_NAME = "TestGame";
	public static final String GAME_PW = "testPW";
	public static final int SERVER_PORT = 6143;
	public static final String SERVER_LOCALHOST = "localhost";

	private ServerListener serverListener;
	private Server myServer;
	private boolean isStarted;
	private int prizeCards;

	public ServerMain(int prizeCards) {
		isStarted = false;
		this.prizeCards = prizeCards;
	}

	@Override
	public void simpleInitApp() {
		try {
			myServer = Network.createServer(SERVER_PORT);
			myServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long id = PokemonGameManagerFactory.createNewGame(GAME_NAME, GAME_PW, this, this.prizeCards);
		serverListener = new ServerListener(PokemonGameManagerFactory.getGame(id));
		myServer.addMessageListener(serverListener, QueryMessage.class);
		myServer.addMessageListener(serverListener, RespondMessage.class);
		serverListener.setServer(myServer);
		this.isStarted = true;
	}

	public void exit() {
		Map<Integer, ServerBorder> connections = serverListener.getConnections();
		for (Integer i : connections.keySet()) {
			ServerBorder client = connections.get(i);
			client.getHostedConnection().close("Game is finished. You've been disconnected automatically!");
		}

		this.stop();
	}

	@Override
	public void destroy() {
		System.out.println("Destroy");
		myServer.close();
		super.destroy();
	}

	public boolean isStarted() {
		return isStarted;
	}
}
