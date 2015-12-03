package arenaMode.gui;

import java.util.ArrayList;
import java.util.List;

import org.msgpack.core.Preconditions;

import com.jme3.scene.Node;

import arenaMode.model.ArenaFighter;
import arenaMode.model.ArenaFighterCode;
import gui2d.GUI2D;
import gui2d.GUI2DMode;
import gui2d.abstracts.SelectableNode;
import gui2d.controller.GUI2DController;
import gui2d.controller.MusicController.MusicType;
import gui2d.geometries.Image2D;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.messages.TextPanel2D;
import model.database.Database;
import network.client.Account;

public class ArenaChooseController extends Node implements GUI2DController, ArenaReward {
	/** Resolution variable */
	private int screenWidth, screenHeight;
	private TextButton2D backButton, enterArenaButton, scrollLeftButton, scrollRightButton;
	private TextPanel2D chooseArenaPanel;
	private Image2D arenaImage;
	private List<String> enabledArenaNames;
	private int currentArenaIndex;
	private Account account;

	public ArenaChooseController() {
		screenWidth = GUI2D.getInstance().getResolution().getKey();
		screenHeight = GUI2D.getInstance().getResolution().getValue();
		enabledArenaNames = new ArrayList<>();
		this.enabledArenaNames.add("Marmoria City Arena");
		currentArenaIndex = 0;
	}

	public void initSceneGraph() {
		float buttonWidth = screenWidth * 0.15f;
		float buttonHeight = buttonWidth / 4;

		chooseArenaPanel = new TextPanel2D("chooseArenaPanel", this.enabledArenaNames.get(0), screenWidth * 0.35f, buttonHeight) {

			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		chooseArenaPanel.setLocalTranslation(screenWidth * 0.5f - (screenWidth * 0.35f) / 2, screenHeight * 0.75f + buttonHeight / 2, 0);
		chooseArenaPanel.setVisible(false);
		dropInUpdateQueue(chooseArenaPanel);
		this.attachChild(chooseArenaPanel);

		backButton = new TextButton2D("backButton", "Back", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				GUI2D.getInstance().switchMode(GUI2DMode.LOBBY);
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

		enterArenaButton = new TextButton2D("enterArenaButton", "Enter", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				GUI2D.getInstance().switchMode(GUI2DMode.MAMORIA_CITY_ARENA);
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		enterArenaButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, screenHeight * 0.25f, 0);
		enterArenaButton.setVisible(false);
		dropInUpdateQueue(enterArenaButton);
		this.attachChild(enterArenaButton);

		scrollLeftButton = new TextButton2D("scrollLeftButton", "<<", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				currentArenaIndex--;
				arenaImage.setTexture(Database.getAssetKey(enabledArenaNames.get(currentArenaIndex)));
				dropInUpdateQueue(arenaImage);
				chooseArenaPanel.setText(enabledArenaNames.get(currentArenaIndex));
				dropInUpdateQueue(chooseArenaPanel);
				scrollRightButton.setVisible(true);
				dropInUpdateQueue(scrollRightButton);
				if (currentArenaIndex == 0) {
					scrollLeftButton.setVisible(false);
					dropInUpdateQueue(scrollLeftButton);
				}
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		scrollLeftButton.setLocalTranslation(screenWidth * 0.2f - buttonWidth / 2, screenHeight * 0.25f, 0);
		scrollLeftButton.setVisible(false);
		dropInUpdateQueue(scrollLeftButton);
		this.attachChild(scrollLeftButton);

		scrollRightButton = new TextButton2D("scrollRightButton", ">>", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				currentArenaIndex++;
				arenaImage.setTexture(Database.getAssetKey(enabledArenaNames.get(currentArenaIndex)));
				dropInUpdateQueue(arenaImage);
				chooseArenaPanel.setText(enabledArenaNames.get(currentArenaIndex));
				dropInUpdateQueue(chooseArenaPanel);
				scrollLeftButton.setVisible(true);
				dropInUpdateQueue(scrollLeftButton);
				if (enabledArenaNames.size() == currentArenaIndex + 1) {
					scrollRightButton.setVisible(false);
					dropInUpdateQueue(scrollRightButton);
				}
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		scrollRightButton.setLocalTranslation(screenWidth * 0.8f - buttonWidth / 2, screenHeight * 0.25f, 0);
		scrollRightButton.setVisible(false);
		dropInUpdateQueue(scrollRightButton);
		this.attachChild(scrollRightButton);

		float imageWidth = screenWidth * 0.45f;
		float imageHeight = screenHeight * 0.35f;
		arenaImage = new Image2D("arenaImage", Database.getAssetKey(this.enabledArenaNames.get(0)), imageWidth, imageHeight) {

			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		arenaImage.setLocalTranslation(screenWidth * 0.5f - imageWidth / 2, screenHeight * 0.75f - imageHeight, 0);
		arenaImage.setVisible(false);
		dropInUpdateQueue(arenaImage);
		this.attachChild(arenaImage);
	}

	@Override
	public void hide() {
		this.backButton.setVisible(false);
		this.dropInUpdateQueue(backButton);
		this.enterArenaButton.setVisible(false);
		this.dropInUpdateQueue(enterArenaButton);
		this.scrollLeftButton.setVisible(false);
		this.dropInUpdateQueue(scrollLeftButton);
		this.scrollRightButton.setVisible(false);
		this.dropInUpdateQueue(scrollRightButton);
		this.chooseArenaPanel.setVisible(false);
		this.dropInUpdateQueue(chooseArenaPanel);
		this.arenaImage.setVisible(false);
		this.dropInUpdateQueue(arenaImage);
	}

	@Override
	public void restart() {
		currentArenaIndex = 0;
		this.arenaImage.setTexture(Database.getAssetKey(this.enabledArenaNames.get(currentArenaIndex)));
		this.arenaImage.setVisible(true);
		this.dropInUpdateQueue(arenaImage);
		this.chooseArenaPanel.setText(this.enabledArenaNames.get(currentArenaIndex));
		this.dropInUpdateQueue(chooseArenaPanel);
		this.backButton.setVisible(true);
		this.dropInUpdateQueue(backButton);
		this.enterArenaButton.setVisible(true);
		this.dropInUpdateQueue(enterArenaButton);
		this.scrollLeftButton.setVisible(false);
		this.dropInUpdateQueue(scrollLeftButton);
		if (enabledArenaNames.size() > currentArenaIndex + 1)
			this.scrollRightButton.setVisible(true);
		else
			this.scrollRightButton.setVisible(false);
		this.dropInUpdateQueue(scrollRightButton);
		this.chooseArenaPanel.setVisible(true);
		this.dropInUpdateQueue(chooseArenaPanel);
	}

	public void setAccount(Account asAccount) {
		this.account = asAccount;
		// TODO only activate enabled arenas!
		currentArenaIndex = 0;
		this.enabledArenaNames = new ArrayList<>();
		this.enabledArenaNames.add("Marmoria City Arena");
		// this.enabledArenaNames.add("Azuria City Arena");
		// this.enabledArenaNames.add("Orania City Arena");
		// this.enabledArenaNames.add("Prismania City Arena");
		// this.enabledArenaNames.add("Fuchsania City Arena");
		// this.enabledArenaNames.add("Saffronia City Arena");
		// this.enabledArenaNames.add("Zinnoberinsel Arena");
		// this.enabledArenaNames.add("Vertania City Arena");
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

	@Override
	public List<String> unlockReward(ArenaFighter fighter) {
		List<String> rewards = new ArrayList<>();
		switch (fighter.getCode()) {
		case MAMORIA_BRENDAN:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.MAMORIA_BROCK)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.MAMORIA_BRENDAN))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.MAMORIA_BRENDAN);
			break;
		case MAMORIA_BROCK:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.MAMORIA_BROCK)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.MAMORIA_BROCK)) {
				account.getDefeatedArenaFighters().add(ArenaFighterCode.MAMORIA_BROCK);
				// 3 rewards for beating the master the first time!
				for (int i = 0; i < 3; i++) {
					String reward = fighter.createReward(account);
					if (reward != null) {
						rewards.add(reward);
						account.getUnlockedCards().add(reward);
					}
				}
			}
			break;
		case MAMORIA_RED:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.MAMORIA_BROCK)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.MAMORIA_RED))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.MAMORIA_RED);
			break;
		default:
			break;
		}
		GUI2D.getInstance().getDeckEditController().setAccount(account);
		Account.saveAccount(account);
		int rewardSize = rewards.size();
		Preconditions.checkArgument(rewardSize == 0 || rewardSize == 1 || rewardSize == 3);
		return rewards;
	}
}
