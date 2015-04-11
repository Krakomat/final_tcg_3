package gui2d.controller;

import java.io.File;

import model.database.Database;
import model.database.Deck;
import model.game.GameParameters;
import network.client.Player;
import network.tcp.borders.ClientBorder;
import network.tcp.borders.ServerMain;
import gui2d.GUI2D;
import gui2d.GUI2DMode;
import gui2d.abstracts.SelectableNode;
import gui2d.controller.MusicController.MusicType;
import gui2d.geometries.ImageButton2D;
import gui2d.geometries.TextButton2D;

import com.jme3.scene.Node;

public class BotController extends Node implements GUI2DController {
	private TextButton2D backButton;
	private ImageButton2D overgrowthButton, zappButton, brushfireButton, blackoutButton;
	private int screenWidth, screenHeight;

	public BotController() {
		screenWidth = GUI2D.getInstance().getResolution().getKey();
		screenHeight = GUI2D.getInstance().getResolution().getValue();
	}

	public void initSceneGraph() {
		float buttonWidth = screenWidth * 0.1f;
		float buttonHeight = buttonWidth * 1.426f;

		overgrowthButton = new ImageButton2D("overgrowthButton", Database.getAssetKey("overgrowth"), buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Overgrowth.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		overgrowthButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth * 2, screenHeight * 0.35f + buttonHeight / 2, 0);
		overgrowthButton.setVisible(false);
		dropInUpdateQueue(overgrowthButton);
		this.attachChild(overgrowthButton);

		zappButton = new ImageButton2D("zappButton", Database.getAssetKey("zapp!"), buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Zap!.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		zappButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth * 1, screenHeight * 0.35f + buttonHeight / 2, 0);
		zappButton.setVisible(false);
		dropInUpdateQueue(zappButton);
		this.attachChild(zappButton);

		brushfireButton = new ImageButton2D("brushfireButton", Database.getAssetKey("brushfire"), buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Brushfire.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		brushfireButton.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.35f + buttonHeight / 2, 0);
		brushfireButton.setVisible(false);
		dropInUpdateQueue(brushfireButton);
		this.attachChild(brushfireButton);

		blackoutButton = new ImageButton2D("blackoutButton", Database.getAssetKey("blackout"), buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Blackout.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		blackoutButton.setLocalTranslation(screenWidth * 0.5f + buttonWidth, screenHeight * 0.35f + buttonHeight / 2, 0);
		blackoutButton.setVisible(false);
		dropInUpdateQueue(blackoutButton);
		this.attachChild(blackoutButton);

		backButton = new TextButton2D("backButton", "Back", screenWidth * 0.15f, (screenWidth * 0.15f) / 4) {

			@Override
			public void mouseSelect() {
				backButtonClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		backButton.setLocalTranslation(screenWidth * 0.5f - (screenWidth * 0.15f) / 2, 0, 0);
		backButton.setVisible(false);
		dropInUpdateQueue(backButton);
		this.attachChild(backButton);
	}

	private void dropInUpdateQueue(SelectableNode node) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(node);
			}
		});
		t.setName("Helper Thread dropInUpdateQueue");
		t.start();
	}

	protected void backButtonClicked() {
		GUI2D.getInstance().switchMode(GUI2DMode.LOBBY);
		GUI2D.getInstance().getMusicController().switchMusic(MusicType.LOBBY_MUSIC);
	}

	protected void botClicked(String deckName) {
		GUI2D.getInstance().switchMode(GUI2DMode.INGAME);
		GUI2D.getInstance().getPlayer().createGame();

		// Create tree bot and connect him to the server that was created in createGame:
		new Thread(new Runnable() {
			@Override
			public void run() {
				Player bot = Database.getBot("TreeBot");
				bot.setDeck(Deck.readFromDatabaseFile(new File(GameParameters.DECK_PATH + deckName)));
				ClientBorder botBorder = new ClientBorder(bot);
				bot.setServer(botBorder);

				// Register at server:
				botBorder.connectAsPlayer(bot, ServerMain.SERVER_LOCALHOST, ServerMain.GAME_PW);
			}
		}).start();
	}

	@Override
	public void hide() {
		this.backButton.setVisible(false);
		this.dropInUpdateQueue(backButton);
		this.overgrowthButton.setVisible(false);
		this.dropInUpdateQueue(overgrowthButton);
		this.zappButton.setVisible(false);
		this.dropInUpdateQueue(zappButton);
		this.brushfireButton.setVisible(false);
		this.dropInUpdateQueue(brushfireButton);
		this.blackoutButton.setVisible(false);
		this.dropInUpdateQueue(blackoutButton);
	}

	@Override
	public void restart() {
		this.hide();
		this.backButton.setVisible(true);
		this.dropInUpdateQueue(backButton);
		this.overgrowthButton.setVisible(true);
		this.dropInUpdateQueue(overgrowthButton);
		this.zappButton.setVisible(true);
		this.dropInUpdateQueue(zappButton);
		this.brushfireButton.setVisible(true);
		this.dropInUpdateQueue(brushfireButton);
		this.blackoutButton.setVisible(true);
		this.dropInUpdateQueue(blackoutButton);
	}
}
