package network.tcp.borders;

import network.client.Player;

import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;

public class ClientConnectionListener implements ClientStateListener {
	private Player player;

	public ClientConnectionListener(Player player) {
		super();
		this.player = player;
	}

	@Override
	public void clientConnected(Client c) {

	}

	@Override
	public void clientDisconnected(Client c, DisconnectInfo info) {
		player.exit();
	}
}
