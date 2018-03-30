package debugging;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.jme3.system.AppSettings;

import executables.clients.FinalTCG3;
import gui2d.GUI2D;
import gui2d.GUI2DMode;
import model.database.Database;
import model.database.Deck;
import model.game.GameParameters;
import model.game.PokemonGameModelImpl;
import network.client.Player;
import network.client.PlayerImpl;
import network.server.PokemonGameManagerFactory;
import network.server.PokemonGameManagerImpl;
import network.tcp.borders.ServerMain;
import network.tcp.messages.MessageRegister;

public class SetupGame {

	protected PokemonGameManagerImpl setup() {
		try {
			Database.init();
			MessageRegister.registerSerializables();
			GUI2D view = new GUI2D();

			view.setShowSettings(false);
			AppSettings settings = new AppSettings(true);
			settings.put("Width", (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.85f));
			settings.put("Height", (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.85f));
			settings.setTitle("Final TCG 3 Version " + FinalTCG3.VERSION);
			settings.put("VSync", true);
			settings.put("Samples", 16);
			settings.setFullscreen(false);
			try {
				BufferedImage newimg = ImageIO.read(GUI2D.class.getClass().getResourceAsStream("/tilesets/other/pokeball16x16.png"));
				BufferedImage newimg2 = ImageIO.read(GUI2D.class.getClass().getResourceAsStream("/tilesets/other/pokeball32x32.png"));
				settings.setIcons(new BufferedImage[] { newimg, newimg2 });
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			view.setSettings(settings);
			view.start();
			while (!view.isStarted())
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			view.switchMode(GUI2DMode.INGAME, false);
			PlayerImpl player = (PlayerImpl) Database.readAccountFolder(); // has to be != null
			view.setPlayer(player);
			player.setGUI(view);

			view.setPlayer(player);
			player.createLocalGame(4);

			PokemonGameManagerImpl server = (PokemonGameManagerImpl) player.getServer();
			server.activateDebugMode();

			// Create tree bot and connect him to the server that was created in createGame:
			Player bot = Database.getBot("Computer");
			bot.setDeck(Deck.readFromDatabaseFile(new File(GameParameters.BOT_DECK_PATH + "Overgrowth.xml")));
			bot.setServer(PokemonGameManagerFactory.CURRENT_RUNNING_LOCAL_GAME);
			// Register bot at server:
			PokemonGameManagerFactory.CURRENT_RUNNING_LOCAL_GAME.connectAsLocalPlayer(bot, ServerMain.GAME_PW);

			PokemonGameModelImpl gameModel = (PokemonGameModelImpl) server.getGameModel();
			gameModel.activateDebugMode();
			gameModel.initNewGame();
			return server;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
