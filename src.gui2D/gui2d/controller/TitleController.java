package gui2d.controller;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import network.client.Account;
import network.client.PlayerImpl;
import model.database.Database;
import model.database.Deck;
import model.game.GameParameters;
import gui2d.GUI2D;
import gui2d.GUI2DMode;
import gui2d.abstracts.SelectableNode;
import gui2d.controller.MusicController.MusicType;
import gui2d.geometries.TextButton2D;

import com.jme3.scene.Node;

import gui2d.geometries.Image2D;

public class TitleController extends Node implements GUI2DController {

	private TextButton2D startButton;
	private Image2D titleImage;
	/** Resolution variable */
	private int screenWidth, screenHeight;

	public TitleController() {
		screenWidth = GUI2D.getInstance().getResolution().getKey();
		screenHeight = GUI2D.getInstance().getResolution().getValue();
	}

	public void initSceneGraph() {
		float buttonWidth = screenWidth * 0.15f;
		float buttonHeight = buttonWidth / 4;

		startButton = new TextButton2D("startButton", "START", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				startButtonClicked();
			}
		};
		startButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, screenHeight * 0.25f + buttonHeight / 2, 0);
		startButton.setVisible(false);
		dropInUpdateQueue(startButton);
		this.attachChild(startButton);

		titleImage = new Image2D("titleImage!", Database.getAssetKey("logo"), screenWidth * 0.4f, screenWidth * 0.2f) {

			@Override
			public void mouseSelect() {
				// nothing to do here
			}
		};
		titleImage.setLocalTranslation(screenWidth * 0.3f, screenHeight * 0.4f, 0);
		titleImage.setVisible(false);
		dropInUpdateQueue(titleImage);
		this.attachChild(titleImage);
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

	protected void startButtonClicked() {
		try {
			PlayerImpl player = (PlayerImpl) Database.readAccountFolder();
			if (player == null) {
				// Make a new account:
				String accountName = JOptionPane.showInputDialog("Enter your name:");
				if (accountName != null) {
					if (!accountName.equals("")) {
						player = (PlayerImpl) PlayerImpl.createNewPlayer(0, accountName, "");
						File deckFile = new File(GameParameters.DECK_PATH + "Starter Deck.xml");
						Deck deck = Deck.readFromDatabaseFile(deckFile);
						player.setDeck(deck);
						Account.saveAccount(player);
						JOptionPane.showMessageDialog(null, "Saved your account with the name " + accountName + " on this PC!", "Success",
								JOptionPane.INFORMATION_MESSAGE);
					} else
						JOptionPane.showMessageDialog(null, "Account name is not valid!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			if (player != null) {
				GUI2D.getInstance().setPlayer(player);
				player.setGUI(GUI2D.getInstance());
				GUI2D.getInstance().switchMode(GUI2DMode.LOBBY);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * (Re-)Starts this node by setting the two main buttons visible and setting the rest invisible.
	 */
	public void restart() {
		this.hide();
		this.startButton.setVisible(true);
		this.dropInUpdateQueue(startButton);
		this.titleImage.setVisible(true);
		this.dropInUpdateQueue(titleImage);
	}

	/**
	 * Hides all buttons.
	 */
	public void hide() {
		this.startButton.setVisible(false);
		this.dropInUpdateQueue(startButton);
		this.titleImage.setVisible(false);
		this.dropInUpdateQueue(titleImage);
	}
}
