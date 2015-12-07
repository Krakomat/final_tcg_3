package arenaMode.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jme3.scene.Node;

import arenaMode.model.ArenaFighter;
import arenaMode.model.ArenaFighterCode;
import arenaMode.model.ArenaFighterFactory;
import gui2d.GUI2D;
import gui2d.GUI2DMode;
import gui2d.abstracts.SelectableNode;
import gui2d.controller.GUI2DController;
import gui2d.controller.MusicController.MusicType;
import gui2d.geometries.Image2D;
import gui2d.geometries.Text2D;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.messages.TextPanel2D;
import model.database.Database;
import model.database.Deck;
import model.game.GameParameters;
import network.client.Account;
import network.client.Player;
import network.server.PokemonGameManagerFactory;
import network.tcp.borders.ServerMain;

public class AzuriaArenaController extends Node implements GUI2DController {
	public static List<ArenaFighter> getArenaFighters() {
		List<ArenaFighter> list = new ArrayList<>();
		list.add(ArenaFighterFactory.createFighter(ArenaFighterCode.AZURIA_LYRA));
		list.add(ArenaFighterFactory.createFighter(ArenaFighterCode.AZURIA_MAY));
		list.add(ArenaFighterFactory.createFighter(ArenaFighterCode.AZURIA_MISTY));
		return list;
	}

	/** Resolution variable */
	private int screenWidth, screenHeight;
	private TextButton2D backButton, fightButton;
	private Image2D redThumb, brendanThumb, brockThumb, arenaImage, fighterImage;
	private TextPanel2D fighterNamePanel;
	private Text2D deckDescription, unlockedCardsDescription;
	private Account account;
	private ArenaFighter currentSelectedFighter, red, brendan, brock;

	public AzuriaArenaController() {
		screenWidth = GUI2D.getInstance().getResolution().getKey();
		screenHeight = GUI2D.getInstance().getResolution().getValue();
		currentSelectedFighter = null;
		red = ArenaFighterFactory.createFighter(ArenaFighterCode.AZURIA_LYRA);
		brendan = ArenaFighterFactory.createFighter(ArenaFighterCode.AZURIA_MAY);
		brock = ArenaFighterFactory.createFighter(ArenaFighterCode.AZURIA_MISTY);
	}

	public void initSceneGraph() {
		float buttonWidth = screenWidth * 0.15f;
		float buttonHeight = buttonWidth / 4;
		float imageWidth = screenWidth * 0.35f;
		float thumbWidth = screenWidth * 0.125f;
		float thumbHeight = screenWidth * 0.125f;

		backButton = new TextButton2D("backButton", "Back", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				GUI2D.getInstance().switchMode(GUI2DMode.ARENA_CHOOSE_LOBBY, false);
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

		fightButton = new TextButton2D("fightButton", "Fight", imageWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				GUI2D.getInstance().registerFighterAsOpponent(currentSelectedFighter);
				GUI2D.getInstance().switchMode(GUI2DMode.INGAME, false);
				if (currentSelectedFighter == brock)
					GUI2D.getInstance().getMusicController().switchMusic(MusicType.ARENA_MASTER_MUSIC);
				else
					GUI2D.getInstance().getMusicController().switchMusic(MusicType.ARENA_SERVANT_MUSIC);

				GUI2D.getInstance().setNextMode(GUI2DMode.AZURIA_CITY_ARENA);
				GUI2D.getInstance().getPlayer().createLocalGame();

				// Create tree bot and connect him to the server that was
				// created in createGame:
				new Thread(new Runnable() {
					@Override
					public void run() {
						Player bot = Database.getBot("TreeBot");
						if (currentSelectedFighter == brendan)
							bot.setBotDifficulty(3);
						else
							bot.setBotDifficulty(5);
						bot.setDeck(Deck.readFromDatabaseFile(new File(GameParameters.ARENA_DECK_PATH + currentSelectedFighter.getDeck().getName() + ".xml")));
						bot.setServer(PokemonGameManagerFactory.CURRENT_RUNNING_LOCAL_GAME);

						// Register at server:
						PokemonGameManagerFactory.CURRENT_RUNNING_LOCAL_GAME.connectAsLocalPlayer(bot, ServerMain.GAME_PW);
					}
				}).start();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		fightButton.setLocalTranslation(screenWidth * 0.2f - imageWidth / 2, screenHeight * 0.4f - thumbHeight, 0);
		fightButton.setVisible(false);
		dropInUpdateQueue(fightButton);
		this.attachChild(fightButton);

		arenaImage = new Image2D("arenaImage", Database.getAssetKey("Cerulean City Gym"), imageWidth, thumbHeight) {

			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		arenaImage.setLocalTranslation(screenWidth * 0.2f - imageWidth / 2, screenHeight * 0.9f - thumbHeight, 0);
		arenaImage.setVisible(false);
		dropInUpdateQueue(arenaImage);
		this.attachChild(arenaImage);

		redThumb = new Image2D("redThumb", Database.getAssetKey(ArenaFighterCode.AZURIA_LYRA + "THUMB"), thumbWidth, thumbHeight) {

			@Override
			public void mouseSelect() {
				if (redThumb.isSelected()) {
					redThumb.setSelected(false);
					dropInUpdateQueue(redThumb);
					updateFighterImage(null);
				} else {
					redThumb.setSelected(true);
					dropInUpdateQueue(redThumb);
					brendanThumb.setSelected(false);
					dropInUpdateQueue(brendanThumb);
					brockThumb.setSelected(false);
					dropInUpdateQueue(brockThumb);
					updateFighterImage(red);
				}
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		redThumb.setLocalTranslation(screenWidth * 0.5f - thumbWidth / 2, screenHeight * 0.4f - thumbHeight, 0);
		redThumb.setVisible(false);
		dropInUpdateQueue(redThumb);
		this.attachChild(redThumb);

		brendanThumb = new Image2D("brendanThumb", Database.getAssetKey(ArenaFighterCode.AZURIA_MAY + "THUMB"), thumbWidth, thumbHeight) {

			@Override
			public void mouseSelect() {
				if (brendanThumb.isSelected()) {
					brendanThumb.setSelected(false);
					dropInUpdateQueue(brendanThumb);
					updateFighterImage(null);
				} else {
					brendanThumb.setSelected(true);
					dropInUpdateQueue(brendanThumb);
					redThumb.setSelected(false);
					dropInUpdateQueue(redThumb);
					brockThumb.setSelected(false);
					dropInUpdateQueue(brockThumb);
					updateFighterImage(brendan);
				}
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		brendanThumb.setLocalTranslation(screenWidth * 0.5f - thumbWidth / 2, screenHeight * 0.65f - thumbHeight, 0);
		brendanThumb.setVisible(false);
		dropInUpdateQueue(brendanThumb);
		this.attachChild(brendanThumb);

		brockThumb = new Image2D("brockThumb", Database.getAssetKey(ArenaFighterCode.AZURIA_MISTY + "THUMB"), thumbWidth, thumbHeight) {

			@Override
			public void mouseSelect() {
				if (brockThumb.isSelected()) {
					brockThumb.setSelected(false);
					dropInUpdateQueue(brockThumb);
					updateFighterImage(null);
				} else {
					brockThumb.setSelected(true);
					dropInUpdateQueue(brockThumb);
					redThumb.setSelected(false);
					dropInUpdateQueue(redThumb);
					brendanThumb.setSelected(false);
					dropInUpdateQueue(brendanThumb);
					updateFighterImage(brock);
				}
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		brockThumb.setLocalTranslation(screenWidth * 0.5f - thumbWidth / 2, screenHeight * 0.9f - thumbHeight, 0);
		brockThumb.setVisible(false);
		dropInUpdateQueue(brockThumb);
		this.attachChild(brockThumb);

		float fighterImageWidth = screenWidth * 0.25f;
		float fighterImageHeight = screenHeight * 0.8f;

		fighterImage = new Image2D("fighterImage", Database.getAssetKey(ArenaFighterCode.AZURIA_LYRA.toString()), fighterImageWidth, fighterImageHeight) {

			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		fighterImage.setLocalTranslation(screenWidth * 0.95f - fighterImageWidth, screenHeight * 0.15f, 0);
		fighterImage.setVisible(false);
		dropInUpdateQueue(fighterImage);
		this.attachChild(fighterImage);

		fighterNamePanel = new TextPanel2D("fighterNamePanel", "Brock", imageWidth, buttonHeight) {

			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		fighterNamePanel.setLocalTranslation(screenWidth * 0.2f - imageWidth / 2, screenHeight * 0.805f - thumbHeight, 0);
		fighterNamePanel.setVisible(false);
		dropInUpdateQueue(fighterNamePanel);
		this.attachChild(fighterNamePanel);

		deckDescription = new Text2D("deckDescription", "Deck: ", imageWidth, buttonHeight) {

			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		deckDescription.setLocalTranslation(screenWidth * 0.2f - imageWidth / 2, screenHeight * 0.705f - thumbHeight, 0);
		deckDescription.setVisible(false);
		dropInUpdateQueue(deckDescription);
		this.attachChild(deckDescription);

		unlockedCardsDescription = new Text2D("unlockedCardsDescription", "Cards unlocked: ", imageWidth, buttonHeight) {

			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		unlockedCardsDescription.setLocalTranslation(screenWidth * 0.2f - imageWidth / 2, screenHeight * 0.605f - thumbHeight, 0);
		unlockedCardsDescription.setVisible(false);
		dropInUpdateQueue(unlockedCardsDescription);
		this.attachChild(unlockedCardsDescription);
	}

	@Override
	public void hide() {
		this.backButton.setVisible(false);
		this.dropInUpdateQueue(backButton);
		this.deckDescription.setVisible(false);
		this.dropInUpdateQueue(deckDescription);
		this.unlockedCardsDescription.setVisible(false);
		this.dropInUpdateQueue(unlockedCardsDescription);
		this.fighterNamePanel.setVisible(false);
		this.dropInUpdateQueue(fighterNamePanel);
		this.fightButton.setVisible(false);
		this.dropInUpdateQueue(fightButton);
		this.arenaImage.setVisible(false);
		this.dropInUpdateQueue(arenaImage);
		this.redThumb.setVisible(false);
		this.dropInUpdateQueue(redThumb);
		this.brendanThumb.setVisible(false);
		this.dropInUpdateQueue(brendanThumb);
		this.brockThumb.setVisible(false);
		this.dropInUpdateQueue(brockThumb);
		this.fighterImage.setVisible(false);
		this.dropInUpdateQueue(fighterImage);
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().getIOController().removeShootable(redThumb);
				GUI2D.getInstance().getIOController().removeShootable(brendanThumb);
				GUI2D.getInstance().getIOController().removeShootable(brockThumb);
			}
		}).start();
	}

	@Override
	public void restart() {
		currentSelectedFighter = null;
		red = ArenaFighterFactory.createFighter(ArenaFighterCode.AZURIA_LYRA);
		brendan = ArenaFighterFactory.createFighter(ArenaFighterCode.AZURIA_MAY);
		brock = ArenaFighterFactory.createFighter(ArenaFighterCode.AZURIA_MISTY);
		this.backButton.setVisible(true);
		this.dropInUpdateQueue(backButton);
		this.deckDescription.setVisible(false);
		this.dropInUpdateQueue(deckDescription);
		this.unlockedCardsDescription.setVisible(false);
		this.dropInUpdateQueue(unlockedCardsDescription);
		this.fighterNamePanel.setVisible(false);
		this.dropInUpdateQueue(fighterNamePanel);
		this.fightButton.setVisible(false);
		this.dropInUpdateQueue(fightButton);
		this.arenaImage.setVisible(true);
		this.dropInUpdateQueue(arenaImage);
		this.redThumb.setVisible(true);
		this.redThumb.setSelected(false);
		this.dropInUpdateQueue(redThumb);
		if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_LYRA))
			this.brendanThumb.setVisible(true);
		this.brendanThumb.setSelected(false);
		this.dropInUpdateQueue(brendanThumb);
		if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_MAY))
			this.brockThumb.setVisible(true);
		this.brockThumb.setSelected(false);
		this.dropInUpdateQueue(brockThumb);
		this.fighterImage.setVisible(false);
		this.dropInUpdateQueue(fighterImage);
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().getIOController().addShootable(redThumb);
				if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_LYRA))
					GUI2D.getInstance().getIOController().addShootable(brendanThumb);
				if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_MAY))
					GUI2D.getInstance().getIOController().addShootable(brockThumb);
			}
		}).start();
	}

	private void updateFighterImage(ArenaFighter fighter) {
		currentSelectedFighter = fighter;
		if (currentSelectedFighter != null) {
			fighterImage.setTexture(Database.getAssetKey(currentSelectedFighter.getCode().toString()));
			this.fighterImage.setVisible(true);
			dropInUpdateQueue(fighterImage);
			this.fightButton.setVisible(true);
			this.dropInUpdateQueue(fightButton);
			this.fighterNamePanel.setText(fighter.getName());
			this.fighterNamePanel.setVisible(true);
			this.dropInUpdateQueue(fighterNamePanel);
			this.deckDescription.setText("Deck: " + fighter.getDeck().getName());
			this.deckDescription.setVisible(true);
			this.dropInUpdateQueue(deckDescription);
			this.unlockedCardsDescription.setText("Cards unlocked: " + +fighter.getUnlockedCards(account).size() + "/" + fighter.getUnlockableCards().size());
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_MISTY))
				this.unlockedCardsDescription.setVisible(true);
			else
				this.unlockedCardsDescription.setVisible(false);
			this.dropInUpdateQueue(unlockedCardsDescription);
		} else {
			this.fighterImage.setVisible(false);
			dropInUpdateQueue(fighterImage);
			this.fightButton.setVisible(false);
			this.dropInUpdateQueue(fightButton);
			this.fighterNamePanel.setVisible(false);
			this.dropInUpdateQueue(fighterNamePanel);
			this.deckDescription.setVisible(false);
			this.dropInUpdateQueue(deckDescription);
			this.unlockedCardsDescription.setVisible(false);
			this.dropInUpdateQueue(unlockedCardsDescription);
		}
	}

	public void setAccount(Account asAccount) {
		this.account = asAccount;
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

	@Override
	public MusicType getAmbientMusic() {
		return MusicType.LOBBY_MUSIC;
	}
}
