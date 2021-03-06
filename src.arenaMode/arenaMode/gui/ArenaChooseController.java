package arenaMode.gui;

import java.util.ArrayList;
import java.util.List;

import org.msgpack.core.Preconditions;

import com.jme3.material.RenderState.BlendMode;
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
		this.enabledArenaNames.add("Pewter City Gym");
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
				GUI2D.getInstance().switchMode(GUI2DMode.LOBBY, true);
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
				if (enabledArenaNames.get(currentArenaIndex).equals("Pewter City Gym"))
					GUI2D.getInstance().switchMode(GUI2DMode.MAMORIA_CITY_ARENA, true);
				if (enabledArenaNames.get(currentArenaIndex).equals("Cerulean City Gym"))
					GUI2D.getInstance().switchMode(GUI2DMode.AZURIA_CITY_ARENA, true);
				if (enabledArenaNames.get(currentArenaIndex).equals("Vermilion City Gym"))
					GUI2D.getInstance().switchMode(GUI2DMode.ORANIA_CITY_ARENA, true);
				if (enabledArenaNames.get(currentArenaIndex).equals("Celadon City Gym"))
					GUI2D.getInstance().switchMode(GUI2DMode.PRISMANIA_CITY_ARENA, true);
				if (enabledArenaNames.get(currentArenaIndex).equals("Fuchsia City Gym"))
					GUI2D.getInstance().switchMode(GUI2DMode.FUCHSANIA_CITY_ARENA, true);
				if (enabledArenaNames.get(currentArenaIndex).equals("Saffron City Gym"))
					GUI2D.getInstance().switchMode(GUI2DMode.SAFFRONIA_CITY_ARENA, true);
				if (enabledArenaNames.get(currentArenaIndex).equals("Cinnabar City Gym"))
					GUI2D.getInstance().switchMode(GUI2DMode.ZINNOBERINSEL_ARENA, true);
				if (enabledArenaNames.get(currentArenaIndex).equals("Viridian City Gym"))
					GUI2D.getInstance().switchMode(GUI2DMode.VERTANIA_CITY_ARENA, true);
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
		arenaImage = new Image2D("arenaImage", Database.getAssetKey(this.enabledArenaNames.get(0)), imageWidth, imageHeight, BlendMode.Alpha) {

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
		// currentArenaIndex = 0;
		this.arenaImage.setTexture(Database.getAssetKey(this.enabledArenaNames.get(currentArenaIndex)));
		this.arenaImage.setVisible(true);
		this.dropInUpdateQueue(arenaImage);
		this.chooseArenaPanel.setText(this.enabledArenaNames.get(currentArenaIndex));
		if (enabledArenaNames.size() > currentArenaIndex + 1) {
			scrollRightButton.setVisible(true);
		} else
			this.scrollRightButton.setVisible(false);
		dropInUpdateQueue(scrollRightButton);
		if (currentArenaIndex > 0) {
			scrollLeftButton.setVisible(true);
		} else
			this.scrollLeftButton.setVisible(false);
		dropInUpdateQueue(scrollLeftButton);
		this.dropInUpdateQueue(chooseArenaPanel);
		this.backButton.setVisible(true);
		this.dropInUpdateQueue(backButton);
		this.enterArenaButton.setVisible(true);
		this.dropInUpdateQueue(enterArenaButton);
		this.dropInUpdateQueue(scrollLeftButton);
		// if (enabledArenaNames.size() > currentArenaIndex + 1)
		// this.scrollRightButton.setVisible(true);
		// else
		// this.scrollRightButton.setVisible(false);
		// this.dropInUpdateQueue(scrollRightButton);
		this.chooseArenaPanel.setVisible(true);
		this.dropInUpdateQueue(chooseArenaPanel);
	}

	public void setAccount(Account asAccount) {
		this.account = asAccount;
		currentArenaIndex = 0;
		this.enabledArenaNames = new ArrayList<>();
		this.enabledArenaNames.add("Pewter City Gym");
		if (this.account.getDefeatedArenaFighters().contains(ArenaFighterCode.MAMORIA_BROCK))
			this.enabledArenaNames.add("Cerulean City Gym");
		if (this.account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_MISTY))
			this.enabledArenaNames.add("Vermilion City Gym");
		if (this.account.getDefeatedArenaFighters().contains(ArenaFighterCode.ORANIA_LTSURGE))
			this.enabledArenaNames.add("Celadon City Gym");
		if (this.account.getDefeatedArenaFighters().contains(ArenaFighterCode.PRISMANIA_ERIKA))
			this.enabledArenaNames.add("Fuchsia City Gym");
		if (this.account.getDefeatedArenaFighters().contains(ArenaFighterCode.FUCHSANIA_KOGA))
			this.enabledArenaNames.add("Saffron City Gym");
		if (this.account.getDefeatedArenaFighters().contains(ArenaFighterCode.SAFFRONIA_SABRINA))
			this.enabledArenaNames.add("Cinnabar City Gym");
		if (this.account.getDefeatedArenaFighters().contains(ArenaFighterCode.ZINNOBER_PYRO))
			this.enabledArenaNames.add("Viridian City Gym");
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
		return MusicType.GYM_CHALLENGE_MUSIC;
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
				// 1 reward per fighter for beating the master the first time!
				for (ArenaFighter f : GUI2D.getInstance().getMamoriaArenaController().getArenaFighters()) {
					String reward = f.createReward(account);
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

		case AZURIA_LYRA:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_MISTY)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_LYRA))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.AZURIA_LYRA);
			break;
		case AZURIA_MISTY:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_MISTY)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_MISTY)) {
				account.getDefeatedArenaFighters().add(ArenaFighterCode.AZURIA_MISTY);
				// 1 reward per fighter for beating the master the first time!
				for (ArenaFighter f : GUI2D.getInstance().getAzuriaArenaController().getArenaFighters()) {
					String reward = f.createReward(account);
					if (reward != null) {
						rewards.add(reward);
						account.getUnlockedCards().add(reward);
					}
				}
			}
			break;
		case AZURIA_MAY:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_MISTY)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.AZURIA_MAY))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.AZURIA_MAY);
			break;
		case ORANIA_LEAF:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.ORANIA_LTSURGE)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.ORANIA_LEAF))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.ORANIA_LEAF);
			break;
		case ORANIA_LTSURGE:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.ORANIA_LTSURGE)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.ORANIA_LTSURGE)) {
				account.getDefeatedArenaFighters().add(ArenaFighterCode.ORANIA_LTSURGE);
				// 1 reward per fighter for beating the master the first time!
				for (ArenaFighter f : GUI2D.getInstance().getAzuriaArenaController().getArenaFighters()) {
					String reward = f.createReward(account);
					if (reward != null) {
						rewards.add(reward);
						account.getUnlockedCards().add(reward);
					}
				}
			}
			break;
		case ORANIA_NATE:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.ORANIA_LTSURGE)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.ORANIA_NATE))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.ORANIA_NATE);
			break;
		case PRISMANIA_ROSA:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.PRISMANIA_ERIKA)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.PRISMANIA_ROSA))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.PRISMANIA_ROSA);
			break;
		case PRISMANIA_ERIKA:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.PRISMANIA_ERIKA)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.PRISMANIA_ERIKA)) {
				account.getDefeatedArenaFighters().add(ArenaFighterCode.PRISMANIA_ERIKA);
				// 1 reward per fighter for beating the master the first time!
				for (ArenaFighter f : GUI2D.getInstance().getAzuriaArenaController().getArenaFighters()) {
					String reward = f.createReward(account);
					if (reward != null) {
						rewards.add(reward);
						account.getUnlockedCards().add(reward);
					}
				}
			}
			break;
		case PRISMANIA_SERENA:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.PRISMANIA_ERIKA)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.PRISMANIA_SERENA))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.PRISMANIA_SERENA);
			break;
		case FUCHSANIA_HILBERT:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.FUCHSANIA_KOGA)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.FUCHSANIA_HILBERT))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.FUCHSANIA_HILBERT);
			break;
		case FUCHSANIA_LUCAS:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.FUCHSANIA_KOGA)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.FUCHSANIA_LUCAS))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.FUCHSANIA_LUCAS);
			break;
		case FUCHSANIA_KOGA:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.FUCHSANIA_KOGA)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.FUCHSANIA_KOGA)) {
				account.getDefeatedArenaFighters().add(ArenaFighterCode.FUCHSANIA_KOGA);
				// 1 reward per fighter for beating the master the first time!
				for (ArenaFighter f : GUI2D.getInstance().getAzuriaArenaController().getArenaFighters()) {
					String reward = f.createReward(account);
					if (reward != null) {
						rewards.add(reward);
						account.getUnlockedCards().add(reward);
					}
				}
			}
			break;
		case SAFFRONIA_CALEM:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.SAFFRONIA_SABRINA)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.SAFFRONIA_CALEM))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.SAFFRONIA_CALEM);
			break;
		case SAFFRONIA_HILDA:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.SAFFRONIA_SABRINA)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.SAFFRONIA_HILDA))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.SAFFRONIA_HILDA);
			break;
		case SAFFRONIA_SABRINA:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.SAFFRONIA_SABRINA)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.SAFFRONIA_SABRINA)) {
				account.getDefeatedArenaFighters().add(ArenaFighterCode.SAFFRONIA_SABRINA);
				// 1 reward per fighter for beating the master the first time!
				for (ArenaFighter f : GUI2D.getInstance().getAzuriaArenaController().getArenaFighters()) {
					String reward = f.createReward(account);
					if (reward != null) {
						rewards.add(reward);
						account.getUnlockedCards().add(reward);
					}
				}
			}
			break;
		case ZINNOBER_DAWN:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.ZINNOBER_PYRO)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.ZINNOBER_DAWN))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.ZINNOBER_DAWN);
			break;
		case ZINNOBER_ETHAN:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.ZINNOBER_PYRO)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.ZINNOBER_ETHAN))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.ZINNOBER_ETHAN);
			break;
		case ZINNOBER_PYRO:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.ZINNOBER_PYRO)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.ZINNOBER_PYRO)) {
				account.getDefeatedArenaFighters().add(ArenaFighterCode.ZINNOBER_PYRO);
				// 1 reward per fighter for beating the master the first time!
				for (ArenaFighter f : GUI2D.getInstance().getAzuriaArenaController().getArenaFighters()) {
					String reward = f.createReward(account);
					if (reward != null) {
						rewards.add(reward);
						account.getUnlockedCards().add(reward);
					}
				}
			}
			break;
		case VERTANIA_ROCKET_FEMALE:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.VERTANIA_GIOVANNI)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.VERTANIA_ROCKET_FEMALE))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.VERTANIA_ROCKET_FEMALE);
			break;
		case VERTANIA_ROCKET_MALE:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.VERTANIA_GIOVANNI)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.VERTANIA_ROCKET_MALE))
				account.getDefeatedArenaFighters().add(ArenaFighterCode.VERTANIA_ROCKET_MALE);
			break;
		case VERTANIA_GIOVANNI:
			if (account.getDefeatedArenaFighters().contains(ArenaFighterCode.VERTANIA_GIOVANNI)) {
				// Eligible for rewards:
				String reward = fighter.createReward(account);
				if (reward != null) {
					rewards.add(reward);
					account.getUnlockedCards().add(reward);
				}
			}
			if (!account.getDefeatedArenaFighters().contains(ArenaFighterCode.VERTANIA_GIOVANNI)) {
				account.getDefeatedArenaFighters().add(ArenaFighterCode.VERTANIA_GIOVANNI);
				// 1 reward per fighter for beating the master the first time!
				for (ArenaFighter f : GUI2D.getInstance().getAzuriaArenaController().getArenaFighters()) {
					String reward = f.createReward(account);
					if (reward != null) {
						rewards.add(reward);
						account.getUnlockedCards().add(reward);
					}
				}
			}
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
