package gui2d.controller;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import model.database.Database;
import model.database.Deck;
import model.game.GameParameters;
import network.client.Account;
import network.client.Player;
import network.server.PokemonGameManagerFactory;
import network.tcp.borders.ClientBorder;
import network.tcp.borders.ServerMain;
import gui2d.GUI2D;
import gui2d.GUI2DMode;
import gui2d.abstracts.SelectableNode;
import gui2d.controller.MusicController.MusicType;
import gui2d.geometries.ImageButton2D;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.messages.TextPanel2D;

import com.jme3.scene.Node;

public class LobbyController extends Node implements GUI2DController {

	private TextButton2D singlePlayerButton, multiPlayerButton, arenaButton, multiPlayerCreateButton, multiPlayerConnectButton, backButton, deckEditorButton, exitButton;
	private ImageButton2D overgrowthButton, zappButton, brushfireButton, blackoutButton, lightningBugButton, kraftreserveButton, grassChopperButton, hotWaterButton, psychOutButton,
			wasserschwallButton, leibwaechterButton, schlossUndRiegelButton, aergerButton, verwuestungButton;
	private TextPanel2D chooseBotDeckPanel;
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

		chooseBotDeckPanel = new TextPanel2D("chooseBotDeckPanel", "Choose a deck for your enemy:", screenWidth * 0.35f, buttonHeight) {

			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		chooseBotDeckPanel.setLocalTranslation(screenWidth * 0.5f - (screenWidth * 0.35f) / 2, screenHeight * 0.75f + buttonHeight / 2, 0);
		chooseBotDeckPanel.setVisible(false);
		dropInUpdateQueue(chooseBotDeckPanel);
		this.attachChild(chooseBotDeckPanel);

		singlePlayerButton = new TextButton2D("singlePlayerButton", "Practice Match", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				singlePlayerClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		singlePlayerButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, screenHeight * 0.65f + buttonHeight / 2, 0);
		singlePlayerButton.setVisible(false);
		dropInUpdateQueue(singlePlayerButton);
		this.attachChild(singlePlayerButton);

		arenaButton = new TextButton2D("arenaButton", "Gym Challenge", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				arenaModeClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		arenaButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, screenHeight * 0.55f + buttonHeight / 2, 0);
		arenaButton.setVisible(false);
		dropInUpdateQueue(arenaButton);
		this.attachChild(arenaButton);

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
				new Thread(new Runnable() {
					@Override
					public void run() {
						multiPlayerConnectClicked();
					}
				}).start();
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
		overgrowthButton.setLocalTranslation(screenWidth * 0.5f - botButtonWidth * 3.5f, screenHeight * 0.35f + botButtonHeight / 2, 0);
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
		zappButton.setLocalTranslation(screenWidth * 0.5f - botButtonWidth * 2.5f, screenHeight * 0.35f + botButtonHeight / 2, 0);
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
		brushfireButton.setLocalTranslation(screenWidth * 0.5f - botButtonWidth * 1.5f, screenHeight * 0.35f + botButtonHeight / 2, 0);
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
		blackoutButton.setLocalTranslation(screenWidth * 0.5f - botButtonWidth * 0.5f, screenHeight * 0.35f + botButtonHeight / 2, 0);
		blackoutButton.setVisible(false);
		dropInUpdateQueue(blackoutButton);
		this.attachChild(blackoutButton);

		lightningBugButton = new ImageButton2D("lightningBugButton", Database.getAssetKey("lightningBug"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Lightning Bug.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		lightningBugButton.setLocalTranslation(screenWidth * 0.5f + botButtonWidth * 0.5f, screenHeight * 0.35f + botButtonHeight / 2, 0);
		lightningBugButton.setVisible(false);
		dropInUpdateQueue(lightningBugButton);
		this.attachChild(lightningBugButton);

		aergerButton = new ImageButton2D("aergerButton", Database.getAssetKey("aerger"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Aerger.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		aergerButton.setLocalTranslation(screenWidth * 0.5f + botButtonWidth * 1.5f, screenHeight * 0.35f + botButtonHeight / 2, 0);
		aergerButton.setVisible(false);
		dropInUpdateQueue(aergerButton);
		this.attachChild(aergerButton);

		verwuestungButton = new ImageButton2D("verwuestungButton", Database.getAssetKey("verwuestung"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Verwuestung.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		verwuestungButton.setLocalTranslation(screenWidth * 0.5f + botButtonWidth * 2.5f, screenHeight * 0.35f + botButtonHeight / 2, 0);
		verwuestungButton.setVisible(false);
		dropInUpdateQueue(verwuestungButton);
		this.attachChild(verwuestungButton);

		schlossUndRiegelButton = new ImageButton2D("schlossUndRiegelButton", Database.getAssetKey("schlossUndRiegel"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("SchlossUndRiegel.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		schlossUndRiegelButton.setLocalTranslation(screenWidth * 0.5f + botButtonWidth * 2.5f, screenHeight * 0.097f + botButtonHeight / 2, 0);
		schlossUndRiegelButton.setVisible(false);
		dropInUpdateQueue(schlossUndRiegelButton);
		this.attachChild(schlossUndRiegelButton);

		kraftreserveButton = new ImageButton2D("kraftreserveButton", Database.getAssetKey("kraftreserve"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Kraftreserve.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		kraftreserveButton.setLocalTranslation(screenWidth * 0.5f - botButtonWidth * 3.5f, screenHeight * 0.097f + botButtonHeight / 2, 0);
		kraftreserveButton.setVisible(false);
		dropInUpdateQueue(kraftreserveButton);
		this.attachChild(kraftreserveButton);

		hotWaterButton = new ImageButton2D("hotWaterButton", Database.getAssetKey("hotWater"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Hot Water.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		hotWaterButton.setLocalTranslation(screenWidth * 0.5f - botButtonWidth * 2.5f, screenHeight * 0.097f + botButtonHeight / 2, 0);
		hotWaterButton.setVisible(false);
		dropInUpdateQueue(hotWaterButton);
		this.attachChild(hotWaterButton);

		psychOutButton = new ImageButton2D("psychOutButton", Database.getAssetKey("psychOut"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Psych Out.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		psychOutButton.setLocalTranslation(screenWidth * 0.5f - botButtonWidth * 1.5f, screenHeight * 0.097f + botButtonHeight / 2, 0);
		psychOutButton.setVisible(false);
		dropInUpdateQueue(psychOutButton);
		this.attachChild(psychOutButton);

		grassChopperButton = new ImageButton2D("grassChopperButton", Database.getAssetKey("grassChopper"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Grass Chopper.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		grassChopperButton.setLocalTranslation(screenWidth * 0.5f - botButtonWidth * 0.5f, screenHeight * 0.097f + botButtonHeight / 2, 0);
		grassChopperButton.setVisible(false);
		dropInUpdateQueue(grassChopperButton);
		this.attachChild(grassChopperButton);

		wasserschwallButton = new ImageButton2D("wasserschwallButton", Database.getAssetKey("wasserSchwall"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Wasserschwall.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		wasserschwallButton.setLocalTranslation(screenWidth * 0.5f + botButtonWidth * 0.5f, screenHeight * 0.097f + botButtonHeight / 2, 0);
		wasserschwallButton.setVisible(false);
		dropInUpdateQueue(wasserschwallButton);
		this.attachChild(wasserschwallButton);

		leibwaechterButton = new ImageButton2D("leibwaechterButton", Database.getAssetKey("leibwächter"), botButtonWidth, botButtonHeight) {

			@Override
			public void mouseSelect() {
				botClicked("Leibwaechter.xml");
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		leibwaechterButton.setLocalTranslation(screenWidth * 0.5f + botButtonWidth * 1.5f, screenHeight * 0.097f + botButtonHeight / 2, 0);
		leibwaechterButton.setVisible(false);
		dropInUpdateQueue(leibwaechterButton);
		this.attachChild(leibwaechterButton);
	}

	protected void botClicked(String deckName) {
		GUI2D.getInstance().switchMode(GUI2DMode.INGAME, true);
		Player bot = Database.getBot("TreeBot");
		GUI2D.getInstance().getPlayer().createLocalGame(bot.getPrizeCards());

		// Create tree bot and connect him to the server that was created in
		// createGame:
		new Thread(new Runnable() {
			@Override
			public void run() {
				bot.setDeck(Deck.readFromDatabaseFile(new File(GameParameters.BOT_DECK_PATH + deckName)));
				bot.setServer(PokemonGameManagerFactory.CURRENT_RUNNING_LOCAL_GAME);

				// Register at server:
				PokemonGameManagerFactory.CURRENT_RUNNING_LOCAL_GAME.connectAsLocalPlayer(bot, ServerMain.GAME_PW);
			}
		}).start();
	}

	protected void standardBotClicked() {
		GUI2D.getInstance().switchMode(GUI2DMode.INGAME, true);
		GUI2D.getInstance().getPlayer().createGame();

		// Create standard bot and connect him to the server that was created in
		// createGame:
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
		this.arenaButton.setVisible(false);
		this.dropInUpdateQueue(arenaButton);
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

	protected void arenaModeClicked() {
		GUI2D.getInstance().switchMode(GUI2DMode.ARENA_CHOOSE_LOBBY, true);
	}

	protected void singlePlayerClicked() {
		this.singlePlayerButton.setVisible(false);
		this.dropInUpdateQueue(singlePlayerButton);
		this.arenaButton.setVisible(false);
		this.dropInUpdateQueue(arenaButton);
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
		this.lightningBugButton.setVisible(true);
		this.dropInUpdateQueue(lightningBugButton);
		this.aergerButton.setVisible(true);
		this.dropInUpdateQueue(aergerButton);
		this.verwuestungButton.setVisible(true);
		this.dropInUpdateQueue(verwuestungButton);
		this.leibwaechterButton.setVisible(true);
		this.dropInUpdateQueue(leibwaechterButton);
		this.kraftreserveButton.setVisible(true);
		this.dropInUpdateQueue(kraftreserveButton);
		this.grassChopperButton.setVisible(true);
		this.dropInUpdateQueue(grassChopperButton);
		this.hotWaterButton.setVisible(true);
		this.dropInUpdateQueue(hotWaterButton);
		this.psychOutButton.setVisible(true);
		this.dropInUpdateQueue(psychOutButton);
		this.wasserschwallButton.setVisible(true);
		this.dropInUpdateQueue(wasserschwallButton);
		this.schlossUndRiegelButton.setVisible(true);
		this.dropInUpdateQueue(schlossUndRiegelButton);
		this.chooseBotDeckPanel.setVisible(true);
		this.dropInUpdateQueue(chooseBotDeckPanel);
	}

	protected void multiPlayerCreateClicked() {
		GUI2D.getInstance().switchMode(GUI2DMode.INGAME, true);
		GUI2D.getInstance().getPlayer().createGame();
	}

	protected void multiPlayerConnectClicked() {
		String ipAdress = GUI2D.getInstance().userTypesName("192.168.178.", "IP4 adress:");
		if (ipAdress != null) {
			GUI2D.getInstance().switchMode(GUI2DMode.INGAME, true);
			GUI2D.getInstance().getPlayer().connectToGame(ipAdress);
		}
	}

	protected void dummyBotClicked() {
		GUI2D.getInstance().switchMode(GUI2DMode.INGAME, true);
		GUI2D.getInstance().getPlayer().createGame();

		// Create dummy bot and connect him to the server that was created in
		// createGame:
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
		GUI2D.getInstance().switchMode(GUI2DMode.DECK_EDIT, true);
	}

	/**
	 * (Re-)Starts this node by setting the two main buttons visible and setting the rest invisible.
	 */
	public void restart() {
		this.hide();
		this.singlePlayerButton.setVisible(true);
		this.dropInUpdateQueue(singlePlayerButton);
		this.arenaButton.setVisible(true);
		this.dropInUpdateQueue(arenaButton);
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
		this.arenaButton.setVisible(false);
		this.dropInUpdateQueue(arenaButton);
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
		this.lightningBugButton.setVisible(false);
		this.dropInUpdateQueue(lightningBugButton);
		this.aergerButton.setVisible(false);
		this.dropInUpdateQueue(aergerButton);
		this.verwuestungButton.setVisible(false);
		this.dropInUpdateQueue(verwuestungButton);
		this.leibwaechterButton.setVisible(false);
		this.dropInUpdateQueue(leibwaechterButton);
		this.kraftreserveButton.setVisible(false);
		this.dropInUpdateQueue(kraftreserveButton);
		this.grassChopperButton.setVisible(false);
		this.dropInUpdateQueue(grassChopperButton);
		this.hotWaterButton.setVisible(false);
		this.dropInUpdateQueue(hotWaterButton);
		this.psychOutButton.setVisible(false);
		this.dropInUpdateQueue(psychOutButton);
		this.wasserschwallButton.setVisible(false);
		this.dropInUpdateQueue(wasserschwallButton);
		this.chooseBotDeckPanel.setVisible(false);
		this.dropInUpdateQueue(chooseBotDeckPanel);
		this.schlossUndRiegelButton.setVisible(false);
		this.dropInUpdateQueue(schlossUndRiegelButton);
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

	@Override
	public MusicType getAmbientMusic() {
		return MusicType.LOBBY_MUSIC;
	}
}
