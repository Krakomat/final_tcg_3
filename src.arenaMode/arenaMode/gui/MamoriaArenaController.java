package arenaMode.gui;

import com.jme3.scene.Node;

import arenaMode.model.ArenaFighterCode;
import gui2d.GUI2D;
import gui2d.GUI2DMode;
import gui2d.abstracts.SelectableNode;
import gui2d.controller.GUI2DController;
import gui2d.geometries.Image2D;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.messages.TextPanel2D;
import model.database.Database;
import network.client.Account;

public class MamoriaArenaController extends Node implements GUI2DController {
	/** Resolution variable */
	private int screenWidth, screenHeight;
	private TextButton2D backButton, fightButton;
	private Image2D redThumb, brendanThumb, brockThumb, arenaImage, fighterImage;
	private TextPanel2D fighterNamePanel;
	private Account account;
	private ArenaFighterCode currentSelectedCode;

	public MamoriaArenaController() {
		screenWidth = GUI2D.getInstance().getResolution().getKey();
		screenHeight = GUI2D.getInstance().getResolution().getValue();
		currentSelectedCode = null;
	}

	public void initSceneGraph() {
		float buttonWidth = screenWidth * 0.15f;
		float buttonHeight = buttonWidth / 4;

		backButton = new TextButton2D("backButton", "Back", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				GUI2D.getInstance().switchMode(GUI2DMode.ARENA_CHOOSE_LOBBY);
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

		fightButton = new TextButton2D("fightButton", "Fight", screenWidth * 0.25f, buttonHeight) {

			@Override
			public void mouseSelect() {
				// TODO
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		fightButton.setLocalTranslation(screenWidth * 0.7f, 0, 0);
		fightButton.setVisible(false);
		dropInUpdateQueue(fightButton);
		this.attachChild(fightButton);

		float imageWidth = screenWidth * 0.35f;
		float thumbWidth = screenWidth * 0.125f;
		float thumbHeight = screenWidth * 0.125f;
		arenaImage = new Image2D("arenaImage", Database.getAssetKey("Mamoria City Arena"), imageWidth, thumbHeight) {

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

		redThumb = new Image2D("redThumb", Database.getAssetKey(ArenaFighterCode.MAMORIA_RED + "THUMB"), thumbWidth, thumbHeight) {

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
					updateFighterImage(ArenaFighterCode.MAMORIA_RED);
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

		brendanThumb = new Image2D("brendanThumb", Database.getAssetKey(ArenaFighterCode.MAMORIA_BRENDAN + "THUMB"), thumbWidth, thumbHeight) {

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
					updateFighterImage(ArenaFighterCode.MAMORIA_BRENDAN);
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

		brockThumb = new Image2D("brockThumb", Database.getAssetKey(ArenaFighterCode.MAMORIA_BROCK + "THUMB"), thumbWidth, thumbHeight) {

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
					updateFighterImage(ArenaFighterCode.MAMORIA_BROCK);
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

		fighterImage = new Image2D("fighterImage", Database.getAssetKey(ArenaFighterCode.MAMORIA_RED.toString()), fighterImageWidth, fighterImageHeight) {

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
	}

	@Override
	public void hide() {
		this.backButton.setVisible(false);
		this.dropInUpdateQueue(backButton);
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
		currentSelectedCode = null;
		this.backButton.setVisible(true);
		this.dropInUpdateQueue(backButton);
		this.fighterNamePanel.setVisible(false);
		this.dropInUpdateQueue(fighterNamePanel);
		this.fightButton.setVisible(false);
		this.dropInUpdateQueue(fightButton);
		this.arenaImage.setVisible(true);
		this.dropInUpdateQueue(arenaImage);
		this.redThumb.setVisible(true);
		this.redThumb.setSelected(false);
		this.dropInUpdateQueue(redThumb);
		this.brendanThumb.setVisible(true);
		this.brendanThumb.setSelected(false);
		this.dropInUpdateQueue(brendanThumb);
		this.brockThumb.setVisible(true);
		this.brockThumb.setSelected(false);
		this.dropInUpdateQueue(brockThumb);
		this.fighterImage.setVisible(false);
		this.dropInUpdateQueue(fighterImage);
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().getIOController().addShootable(redThumb);
				GUI2D.getInstance().getIOController().addShootable(brendanThumb);
				GUI2D.getInstance().getIOController().addShootable(brockThumb);
			}
		}).start();
	}

	private void updateFighterImage(ArenaFighterCode code) {
		currentSelectedCode = code;
		if (currentSelectedCode != null) {
			fighterImage.setTexture(Database.getAssetKey(currentSelectedCode.toString()));
			this.fighterImage.setVisible(true);
			dropInUpdateQueue(fighterImage);
			this.fightButton.setVisible(true);
			this.dropInUpdateQueue(fightButton);
			// TODO set name
			this.fighterNamePanel.setVisible(true);
			this.dropInUpdateQueue(fighterNamePanel);
		} else {
			this.fighterImage.setVisible(false);
			dropInUpdateQueue(fighterImage);
			this.fightButton.setVisible(false);
			this.dropInUpdateQueue(fightButton);
			this.fighterNamePanel.setVisible(false);
			this.dropInUpdateQueue(fighterNamePanel);
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
}
