package executables.clients;

import gui2d.GUI2D;
import model.database.Database;
import network.tcp.messages.MessageRegister;

public class ClientMain {
	public static void main(String[] args) {
		Database.init();
		MessageRegister.registerSerializables();

		GUI2D view = new GUI2D();
		view.start();
		while (!view.isStarted())
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
