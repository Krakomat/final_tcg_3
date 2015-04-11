package gui2d.controller;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import model.database.Database;
import model.database.Deck;
import model.game.GameParameters;
import network.client.Account;
import network.client.Player;
import network.tcp.borders.ClientBorder;
import network.tcp.borders.ServerMain;
import gui2d.GUI2D;
import gui2d.GUI2DMode;
import gui2d.abstracts.SelectableNode;
import gui2d.geometries.ImageButton2D;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.messages.TextPanel2D;

import com.jme3.scene.Node;

public class LobbyController extends Node implements GUI2DController {

	private TextButton2D singlePlayerButton, multiPlayerButton, multiPlayerCreateButton, multiPlayerConnectButton, backButton, deckEditorButton, exitButton;
	private ImageButton2D overgrowthButton, zappButton, brushfireButton, blackoutButton;
	/** Resolution variable */
	private int screenWidth, screenHeight;
	private TextPanel2D ipAdressPanel, usernamePanel, equipedDeckPanel;
	private Account account;

	public LobbyController() {
		screenWidth = GUI2D.getInstance().getResolution().getKey();
		screenHeight = GUI2D.getInstance().getResolution().getValue();
	}

	public void initSceneGraph() {
		float buttonWidth = screenWidth * 0.15f;
		float buttonHeight = buttonWidth / 4;

		singlePlayerButton = new TextButton2D("singlePlayerButton", "Singleplayer", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				singlePlayerClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		singlePlayerButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, screenHeight * 0.55f + buttonHeight / 2, 0);
		singlePlayerButton.setVisible(false);
		dropInUpdateQueue(singlePlayerButton);
		this.attachChild(singlePlayerButton);

		multiPlayerButton = new TextButton2D("multiPlayerButton", "Multiplayer", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				multiPlayerClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		multiPlayerButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, screenHeight * 0.45f + buttonHeight / 2, 0);
		multiPlayerButton.setVisible(false);
		dropInUpdateQueue(multiPlayerButton);
		this.attachChild(multiPlayerButton);

		multiPlayerCreateButton = new TextButton2D("multiPlayerCreateButton", "Create Game", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				multiPlayerCreateClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		multiPlayerCreateButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, screenHeight * 0.55f + buttonHeight / 2, 0);
		multiPlayerCreateButton.setVisible(false);
		dropInUpdateQueue(multiPlayerCreateButton);
		this.attachChild(multiPlayerCreateButton);

		multiPlayerConnectButton = new TextButton2D("multiPlayerConnectButton", "Connect to Game", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				multiPlayerConnectClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		multiPlayerConnectButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, screenHeight * 0.45f + buttonHeight / 2, 0);
		multiPlayerConnectButton.setVisible(false);
		dropInUpdateQueue(multiPlayerConnectButton);
		this.attachChild(multiPlayerConnectButton);

		backButton = new TextButton2D("backButton", "Back", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				backButtonClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		backButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, 0, 0);
		backButton.setVisible(false);
		dropInUpdateQueue(backButton);
		this.attachChild(backButton);

		exitButton = new TextButton2D("exitButton", "X", screenWidth * 0.03f, screenWidth * 0.03f) {

			@Override
			public void mouseSelect() {
				System.exit(0);
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		exitButton.setLocalTranslation(screenWidth * 0.97f, screenHeight - screenWidth * 0.03f, 0);
		exitButton.setVisible(false);
		dropInUpdateQueue(exitButton);
		this.attachChild(exitButton);

		deckEditorButton = new TextButton2D("deckEditorButton", "Edit Deck", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				deckEditorButtonClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		deckEditorButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, 0, 0);
		deckEditorButton.setVisible(false);
		dropInUpdateQueue(deckEditorButton);
		this.attachChild(deckEditorButton);

		try {
			ipAdressPanel = new TextPanel2D("ipAdressPanel", InetAddress.getLocalHost().getHostAddress(), screenWidth * 0.2f, screenWidth * 0.2f / 8) {
				@Override
				public void mouseSelect() {

				}

				@Override
				public void mouseSelectRightClick() {
					// nothing to do here
				}
			};
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		ipAdressPanel.setLocalTranslation(screenWidth * 0.8f, 0, 0);
		ipAdressPanel.setVisible(false);
		dropInUpdateQueue(ipAdressPanel);
		this.attachChild(ipAdressPanel);

		usernamePanel = new TextPanel2D("usernamePanel", "User", screenWidth * 0.2f, screenWidth * 0.2f / 8) {
			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		usernamePanel.setLocalTranslation(0, 0, 0);
		usernamePanel.setVisible(false);
		dropInUpdateQueue(usernamePanel);
		this.attachChild(usernamePanel);

		equipedDeckPanel = new TextPanel2D("equipedDeckPanel", "Deck", screenWidth * 0.2f, screenWidth * 0.2f / 8) {
			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		equipedDeckPanel.setLocalTranslation(0, screenHeight - (screenWidth * 0.2f / 8), 0);
		equipedDeckPanel.setVisible(false);
		dropInUpdateQueue(equipedDeckPanel);
		this.attachChild(equipedDeckPanel);

		float botButtonWidth = screenWidth * 0.1f;
		float botButtonHeight = botButtonWidth * 1.426f;

		overgrowthButton = new ImageButton2D("overgrowthButton", Database.getAssetKey("overgrowth"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Overgrowth.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		overgrowthButton.setLocalTranslation(screenWidth * 0.5f - botButtonWidth * 2, screenHeight * 0.35f + botButtonHeight / 2, 0);
		overgrowthButton.setVisible(false);
		dropInUpdateQueue(overgrowthButton);
		this.attachChild(overgrowthButton);

		zappButton = new ImageButton2D("zappButton", Database.getAssetKey("zapp!"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Zap!.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		zappButton.setLocalTranslation(screenWidth * 0.5f - botButtonWidth * 1, screenHeight * 0.35f + botButtonHeight / 2, 0);
		zappButton.setVisible(false);
		dropInUpdateQueue(zappButton);
		this.attachChild(zappButton);

		brushfireButton = new ImageButton2D("brushfireButton", Database.getAssetKey("brushfire"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Brushfire.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		brushfireButton.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.35f + botButtonHeight / 2, 0);
		brushfireButton.setVisible(false);
		dropInUpdateQueue(brushfireButton);
		this.attachChild(brushfireButton);

		blackoutButton = new ImageButton2D("blackoutButton", Database.getAssetKey("blackout"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Blackout.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		blackoutButton.setLocalTranslation(screenWidth * 0.5f + botButtonWidth, screenHeight * 0.35f + botButtonHeight / 2, 0);
		blackoutButton.setVisible(false);
		dropInUpdateQueue(blackoutButton);
		this.attachChild(blackoutButton);
	}

	protected void botClicked(String deckName) {
		GUI2D.getInstance().switchMode(GUI2DMode.INGAME);
		GUI2D.getInstance().getPlayer().createGame();

		// Create tree bot and connect him to the server that was created in createGame:
		new Thread(new Runnable() {
			@Override
			public void run() {
				Player bot = Database.getBot("TreeBot");
				bot.setDeck(Deck.readFromDatabaseFile(new File(GameParameters.BOT_DECK_PATH + deckName)));
				ClientBorder botBorder = new ClientBorder(bot);
				bot.setServer(botBorder);

				// Register at server:
				botBorder.connectAsPlayer(bot, ServerMain.SERVER_LOCALHOST, ServerMain.GAME_PW);
			}
		}).start();
	}

	protected void standardBotClicked() {
		GUI2D.getInstance().switchMode(GUI2DMode.INGAME);
		GUI2D.getInstance().getPlayer().createGame();

		// Create standard bot and connect him to the server that was created in createGame:
		new Thread(new Runnable() {
			@Override
			public void run() {
				Player bot = Database.getBot("StandardBot");
				ClientBorder botBorder = new ClientBorder(bot);
				bot.setServer(botBorder);

				// Register at server:
				botBorder.connectAsPlayer(bot, ServerMain.SERVER_LOCALHOST, ServerMain.GAME_PW);
			}
		}).start();
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

	protected void multiPlayerClicked() {
		this.singlePlayerButton.setVisible(false);
		this.dropInUpdateQueue(singlePlayerButton);
		this.multiPlayerButton.setVisible(false);
		this.dropInUpdateQueue(multiPlayerButton);
		this.deckEditorButton.setVisible(false);
		this.dropInUpdateQueue(deckEditorButton);

		this.backButton.setVisible(true);
		this.dropInUpdateQueue(backButton);
		this.multiPlayerCreateButton.setVisible(true);
		this.dropInUpdateQueue(multiPlayerCreateButton);
		this.multiPlayerConnectButton.setVisible(true);
		this.dropInUpdateQueue(multiPlayerConnectButton);
	}

	protected void singlePlayerClicked() {
		this.singlePlayerButton.setVisible(false);
		this.dropInUpdateQueue(singlePlayerButton);
		this.multiPlayerButton.setVisible(false);
		this.dropInUpdateQueue(multiPlayerButton);
		this.deckEditorButton.setVisible(false);
		this.dropInUpdateQueue(deckEditorButton);

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

	protected void multiPlayerCreateClicked() {
		GUI2D.getInstance().switchMode(GUI2DMode.INGAME);
		GUI2D.getInstance().getPlayer().createGame();
	}

	protected void multiPlayerConnectClicked() {
		String ipAdress = JOptionPane.showInputDialog("IP4 adress:");
		if (ipAdress != null) {
			GUI2D.getInstance().switchMode(GUI2DMode.INGAME);
			GUI2D.getInstance().getPlayer().connectToGame(ipAdress);
		}
	}

	protected void dummyBotClicked() {
		GUI2D.getInstance().switchMode(GUI2DMode.INGAME);
		GUI2D.getInstance().getPlayer().createGame();

		// Create dummy bot and connect him to the server that was created in createGame:
		new Thread(new Runnable() {
			@Override
			public void run() {
				Player bot = Database.getBot("DummyBot");
				ClientBorder botBorder = new ClientBorder(bot);
				bot.setServer(botBorder);

				// Register at server:
				botBorder.connectAsPlayer(bot, ServerMain.SERVER_LOCALHOST, ServerMain.GAME_PW);
			}
		}).start();
	}

	protected void backButtonClicked() {
		this.restart();
	}

	protected void deckEditorButtonClicked() {
		GUI2D.getInstance().switchMode(GUI2DMode.DECK_EDIT);
	}

	/**
	 * (Re-)Starts this node by setting the two main buttons visible and setting the rest invisible.
	 */
	public void restart() {
		this.hide();
		this.singlePlayerButton.setVisible(true);
		this.dropInUpdateQueue(singlePlayerButton);
		this.multiPlayerButton.setVisible(true);
		this.dropInUpdateQueue(multiPlayerButton);
		this.ipAdressPanel.setVisible(true);
		this.dropInUpdateQueue(ipAdressPanel);
		this.exitButton.setVisible(true);
		this.dropInUpdateQueue(exitButton);
		this.usernamePanel.setVisible(true);
		this.equipedDeckPanel.setVisible(true);
		this.deckEditorButton.setVisible(true);
		this.dropInUpdateQueue(deckEditorButton);
		this.setAccount(this.account);
	}

	/**
	 * Hides all buttons.
	 */
	public void hide() {
		this.singlePlayerButton.setVisible(false);
		this.dropInUpdateQueue(singlePlayerButton);
		this.multiPlayerButton.setVisible(false);
		this.dropInUpdateQueue(multiPlayerButton);
		this.multiPlayerCreateButton.setVisible(false);
		this.dropInUpdateQueue(multiPlayerCreateButton);
		this.multiPlayerConnectButton.setVisible(false);
		this.dropInUpdateQueue(multiPlayerConnectButton);
		this.backButton.setVisible(false);
		this.dropInUpdateQueue(backButton);
		this.exitButton.setVisible(false);
		this.dropInUpdateQueue(exitButton);
		this.ipAdressPanel.setVisible(false);
		this.dropInUpdateQueue(ipAdressPanel);
		this.usernamePanel.setVisible(false);
		this.dropInUpdateQueue(usernamePanel);
		this.deckEditorButton.setVisible(false);
		this.dropInUpdateQueue(deckEditorButton);
		this.equipedDeckPanel.setVisible(false);
		this.dropInUpdateQueue(equipedDeckPanel);
		this.overgrowthButton.setVisible(false);
		this.dropInUpdateQueue(overgrowthButton);
		this.zappButton.setVisible(false);
		this.dropInUpdateQueue(zappButton);
		this.brushfireButton.setVisible(false);
		this.dropInUpdateQueue(brushfireButton);
		this.blackoutButton.setVisible(false);
		this.dropInUpdateQueue(blackoutButton);
	}

	public void setAccount(Account account) {
		if (account != null) {
			this.account = account;
			this.usernamePanel.setText(account.getName());
			this.dropInUpdateQueue(usernamePanel);

			this.equipedDeckPanel.setText("Deck: " + account.getDeck().getName());
			this.dropInUpdateQueue(equipedDeckPanel);
		}
	}
}
