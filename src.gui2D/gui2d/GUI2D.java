package gui2d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import network.client.GuiToPlayerCommunication;
import network.client.PokemonGameView;
import model.database.Card;
import model.database.Database;
import model.database.EnergyCard;
import model.enums.Color;
import model.enums.DistributionMode;
import model.enums.Element;
import model.enums.GameState;
import model.enums.PositionID;
import model.game.LocalPokemonGameModel;
import model.interfaces.Position;
import gui2d.abstracts.SelectableNode;
import gui2d.animations.AnimateableObject;
import gui2d.animations.Animation;
import gui2d.controller.AnimationController;
import gui2d.controller.CameraController;
import gui2d.controller.DeckEditController;
import gui2d.controller.EffectController;
import gui2d.controller.GUI2DController;
import gui2d.controller.IOController;
import gui2d.controller.IngameController;
import gui2d.controller.LobbyController;
import gui2d.controller.MusicController;
import gui2d.controller.TitleController;
import gui2d.controller.MusicController.MusicType;
import gui2d.geometries.HandCardManager2D;
import gui2d.geometries.chooser.AttackChooseWindow;
import gui2d.geometries.chooser.CardChooseWindow;
import gui2d.geometries.chooser.ChooseGeometryChecker;
import gui2d.geometries.chooser.DistributionChooser;
import gui2d.geometries.chooser.ElementChooseWindow;
import gui2d.geometries.chooser.FileChooseWindow;
import gui2d.geometries.chooser.FileNameChooseWindow;
import gui2d.geometries.chooser.QuestionChooseWindow;
import gui2d.geometries.messages.CardPanel2D;
import gui2d.geometries.messages.TextPanel2D;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import arenaMode.gui.ArenaChooseController;
import arenaMode.gui.MamoriaArenaController;
import common.utilities.Pair;
import common.utilities.Threads;

public class GUI2D extends SimpleApplication implements PokemonGameView {
	private static GUI2D GUI2D;// public variable for the gui
	private static final long MESSAGE_TIME = 1000;

	/**
	 * Returns the current instance of this gui.
	 * 
	 * @return
	 */
	public static GUI2D getInstance() {
		return GUI2D;
	}

	/** Controller for key/mouse-input */
	private IOController ioController;
	/** Controller for the camera */
	private CameraController camController;
	/** Stores all ingame elements */
	private TitleController titleController;
	private IngameController ingameController;
	private LobbyController lobbyController;
	private ArenaChooseController arenaController;
	private MamoriaArenaController mamoriaArenaController;
	private DeckEditController deckEditController;
	/** True if this gui is running */
	private boolean isStarted;
	/** The player to communicate with */
	private GuiToPlayerCommunication player;
	/** Resolution of the screen in form of (width, height) */
	private Pair<Integer, Integer> resolution;
	private GUI2DController currentActiveController;

	private MusicController musicController;
	private AnimationController animationController;

	public GUI2D() {
		isStarted = false;
	}

	@Override
	public void simpleInitApp() {
		this.setPauseOnLostFocus(false);
		Thread.currentThread().setName(Threads.RENDER_THREAD.toString());
		GUI2D = this;
		this.setDisplayStatView(false);
		this.setDisplayFps(false);
		this.resolution = new Pair<>(this.settings.getWidth(), this.settings.getHeight());
		camController = new CameraController(cam, flyCam);
		camController.initCam();
		ioController = new IOController(inputManager);
		ioController.initKeys();
		ingameController = new IngameController();
		ingameController.initSceneGraph();
		guiNode.attachChild(ingameController);

		titleController = new TitleController();
		titleController.initSceneGraph();
		guiNode.attachChild(titleController);
		titleController.restart();
		currentActiveController = this.titleController;

		lobbyController = new LobbyController();
		lobbyController.initSceneGraph();
		guiNode.attachChild(lobbyController);

		arenaController = new ArenaChooseController();
		arenaController.initSceneGraph();
		guiNode.attachChild(arenaController);

		mamoriaArenaController = new MamoriaArenaController();
		mamoriaArenaController.initSceneGraph();
		guiNode.attachChild(mamoriaArenaController);

		deckEditController = new DeckEditController();
		deckEditController.initSceneGraph();
		guiNode.attachChild(deckEditController);

		// Make background
		Box box = new Box(7.37f, 4.2f, 0.0f);
		Spatial wall = new Geometry("Box", box);
		Material mat_brick = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat_brick.setTexture("ColorMap", assetManager.loadTexture(Database.getAssetKey("background")));
		wall.setMaterial(mat_brick);
		wall.setLocalTranslation(0.0f, 0.0f, 0.0f);
		rootNode.attachChild(wall);

		this.musicController = new MusicController();
		this.musicController.switchMusic(MusicType.TITLE_MUSIC);

		this.animationController = new AnimationController();
		isStarted = true;
	}

	public void simpleUpdate(float tpf) {
		this.musicController.update(tpf * 1000); // Transform in ms
		try {
			this.animationController.simpleUpdate(tpf * 1000); // Transform in
																// ms
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ArrayList<Card> userChoosesCards(List<Card> cards, int amount, boolean exact, String message) {
		CardChooseWindow cardChooseWindow = this.ingameController.getCardChooseWindow();
		/*
		 * No need to wait for update queue or game model update, since waiting
		 * for update queue is being done by #addToUpdateQueue and the game
		 * model is irrelevant for card choose models.
		 */
		ChooseGeometryChecker checker = new ChooseGeometryChecker() {
			@Override
			public boolean checkSelectionIsOk() {
				return (cardChooseWindow.getChosenIndices().size() == cardChooseWindow.getChooseAmount())
						|| (cardChooseWindow.getChosenIndices().size() <= cardChooseWindow.getChooseAmount() && !cardChooseWindow.isChooseExactly());
			}
		};
		cardChooseWindow.setVisible(true);
		cardChooseWindow.setData(message, cards, amount, exact, checker);

		this.addToUpdateQueue(cardChooseWindow); // waits for update queue here
		this.getIOController().storeShootables();

		while (!cardChooseWindow.choosingFinished()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.getIOController().restoreShootables();

		List<Integer> indices = cardChooseWindow.getChosenIndices();
		ArrayList<Card> chosenCards = new ArrayList<Card>();
		for (Integer i : indices)
			chosenCards.add(cards.get(i));

		cardChooseWindow.unregisterShootables(ioController);
		cardChooseWindow.setVisible(false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				addToUpdateQueue(cardChooseWindow); // waits for update queue
													// here
			}
		}).start();
		return chosenCards;
	}

	@Override
	public List<PositionID> userChoosesPositions(List<PositionID> positionList, int amount, boolean exact, String message) {
		TextPanel2D messagePanel = this.ingameController.getMessagePanel();
		/*
		 * Currently only one position is being chosen here. This is valid,
		 * since no card script in this game forces the player to select
		 * multiple positions at once. If there is a card added to the game that
		 * does this, this method has to be rewritten.
		 */

		// Set all positions in the list glowing & selectable
		this.ingameController.setPositionSelectionMode(true);
		for (PositionID posID : positionList) {
			SelectableNode node = this.ingameController.getPositionGeometry(posID, this.getPlayer().getColor());
			node.setGlowing(true);
			this.addToUpdateQueue(node);
			this.ioController.addShootable(node);
		}

		messagePanel.setText(message);
		messagePanel.setVisible(true);
		addToUpdateQueue(messagePanel);

		// Wait until player chooses a position:
		while (this.ingameController.getCurrentlySelected() == null) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		messagePanel.setVisible(false);
		this.addToUpdateQueue(messagePanel);
		this.ingameController.setPositionSelectionMode(false);
		final SelectableNode node = this.ingameController.getCurrentlySelected(); // Node
																					// that
																					// was
																					// selected
		this.ingameController.resetGlowingSelected();

		// Get the selected position:
		PositionID chosenPosition = this.ingameController.getPositionIDForArenaGeometry(node, this.getPlayer().getColor());
		List<PositionID> chosenPositionList = new ArrayList<PositionID>();
		chosenPositionList.add(chosenPosition);

		return chosenPositionList;
	}

	@Override
	public List<Element> userChoosesElements(List<Element> elements, int amount, boolean exact, String message) {
		ElementChooseWindow elementChooseWindow = this.ingameController.getElementChooseWindow();
		/*
		 * No need to wait for update queue or game model update, since waiting
		 * for update queue is being done by #addToUpdateQueue and the game
		 * model is irrelevant for card choose models.
		 */
		ChooseGeometryChecker checker = new ChooseGeometryChecker() {
			@Override
			public boolean checkSelectionIsOk() {
				return (elementChooseWindow.getChosenIndices().size() == elementChooseWindow.getChooseAmount())
						|| (elementChooseWindow.getChosenIndices().size() <= elementChooseWindow.getChooseAmount() && !elementChooseWindow.isChooseExactly());
			}
		};
		elementChooseWindow.setVisible(true);
		elementChooseWindow.setData(message, elements, amount, exact, checker);

		this.addToUpdateQueue(elementChooseWindow); // waits for update queue
													// here
		this.getIOController().storeShootables();

		while (!elementChooseWindow.choosingFinished()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.getIOController().restoreShootables();

		List<Integer> indices = elementChooseWindow.getChosenIndices();
		ArrayList<Element> chosenElements = new ArrayList<Element>();
		for (Integer i : indices)
			chosenElements.add(elements.get(i));

		elementChooseWindow.unregisterShootables(ioController);
		elementChooseWindow.setVisible(false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				addToUpdateQueue(elementChooseWindow); // waits for update queue
														// here
			}
		}).start();
		return chosenElements;
	}

	@Override
	public ArrayList<String> userChoosesAttacks(List<Card> attackOwner, List<String> attacks, int amount, boolean exact, String message) {
		AttackChooseWindow attackChooseWindow = this.ingameController.getAttackChooseWindow();
		/*
		 * No need to wait for update queue or game model update, since waiting
		 * for update queue is being done by #addToUpdateQueue and the game
		 * model is irrelevant for card choose models.
		 */
		ChooseGeometryChecker checker = new ChooseGeometryChecker() {
			@Override
			public boolean checkSelectionIsOk() {
				return (attackChooseWindow.getChosenIndices().size() == attackChooseWindow.getChooseAmount())
						|| (attackChooseWindow.getChosenIndices().size() <= attackChooseWindow.getChooseAmount() && !attackChooseWindow.isChooseExactly());
			}
		};
		attackChooseWindow.setVisible(true);
		attackChooseWindow.setData(message, attackOwner, attacks, amount, exact, checker);
		this.getIOController().storeShootables();

		this.addToUpdateQueue(attackChooseWindow); // waits for update queue
													// here

		while (!attackChooseWindow.choosingFinished()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.getIOController().restoreShootables();

		List<Integer> indices = attackChooseWindow.getChosenIndices();
		ArrayList<String> chosenAttacks = new ArrayList<String>();
		for (Integer i : indices)
			chosenAttacks.add(attacks.get(i));

		attackChooseWindow.unregisterShootables(ioController);
		attackChooseWindow.setVisible(false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				addToUpdateQueue(attackChooseWindow); // waits for update queue
														// here
			}
		}).start();
		return chosenAttacks;
	}

	@Override
	public List<String> userChoosesStrings(List<String> possibilities, int amount, boolean exact, String message) {
		FileChooseWindow fileChooseWindow = this.ingameController.getFileChooseWindow();

		ChooseGeometryChecker checker = new ChooseGeometryChecker() {
			@Override
			public boolean checkSelectionIsOk() {
				return (fileChooseWindow.getChosenIndices().size() == fileChooseWindow.getChooseAmount())
						|| (fileChooseWindow.getChosenIndices().size() <= fileChooseWindow.getChooseAmount() && !fileChooseWindow.isChooseExactly());
			}
		};
		fileChooseWindow.setVisible(true);
		fileChooseWindow.setData(message, possibilities, amount, exact, checker);
		this.getIOController().storeShootables();

		this.addToUpdateQueue(fileChooseWindow); // waits for update queue here

		while (!fileChooseWindow.choosingFinished()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.getIOController().restoreShootables();
		List<Integer> indices = fileChooseWindow.getChosenIndices();
		ArrayList<String> chosenAttacks = new ArrayList<String>();
		for (Integer i : indices)
			chosenAttacks.add(possibilities.get(i));

		fileChooseWindow.unregisterShootables(ioController);
		fileChooseWindow.setVisible(false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				addToUpdateQueue(fileChooseWindow); // waits for update queue
													// here
			}
		}).start();
		return chosenAttacks;
	}

	@Override
	public String userTypesName(String defaultString, String questionText) {
		FileNameChooseWindow fileNameChooseWindow = this.ingameController.getFileNameChooseWindow();

		ChooseGeometryChecker checker = new ChooseGeometryChecker() {
			@Override
			public boolean checkSelectionIsOk() {
				return (fileNameChooseWindow.getChosenIndices().size() == fileNameChooseWindow.getChooseAmount())
						|| (fileNameChooseWindow.getChosenIndices().size() <= fileNameChooseWindow.getChooseAmount() && !fileNameChooseWindow.isChooseExactly());
			}
		};
		fileNameChooseWindow.setVisible(true);
		fileNameChooseWindow.setData("", defaultString, questionText, checker);
		this.getIOController().storeShootables();

		this.addToUpdateQueue(fileNameChooseWindow); // waits for update queue
														// here

		while (!fileNameChooseWindow.choosingFinished()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.getIOController().restoreShootables();

		String name = fileNameChooseWindow.getChosenText();
		fileNameChooseWindow.unregisterShootables(ioController);
		fileNameChooseWindow.setVisible(false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				addToUpdateQueue(fileNameChooseWindow); // waits for update
														// queue here
			}
		}).start();
		return name;
	}

	@Override
	public boolean userAnswersQuestion(String question) {
		QuestionChooseWindow questionChooseWindow = this.ingameController.getQuestionChooseWindow();
		questionChooseWindow.setVisible(true);
		questionChooseWindow.setData(question);
		this.getIOController().storeShootables();

		this.addToUpdateQueue(questionChooseWindow); // waits for update queue
														// here

		while (!questionChooseWindow.choosingFinished()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.getIOController().restoreShootables();

		boolean chosenAnswer = questionChooseWindow.getAnswer();
		questionChooseWindow.unregisterShootables(ioController);
		questionChooseWindow.setVisible(false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				addToUpdateQueue(questionChooseWindow); // waits for update
														// queue here
			}
		}).start();
		return chosenAnswer;
	}

	@Override
	public List<Card> userPaysEnergyCosts(List<Element> costs, List<Card> energyCards) {
		CardChooseWindow cardChooseWindow = this.ingameController.getCardChooseWindow();
		/*
		 * No need to wait for update queue or game model update, since waiting
		 * for update queue is being done by #addToUpdateQueue and the game
		 * model is irrelevant for card choose models.
		 */
		ChooseGeometryChecker checker = new ChooseGeometryChecker() {
			public boolean checkSelectionIsOk() {
				// Get the amount of energy that is chosen:
				List<Integer> indices = cardChooseWindow.getChosenIndices();
				if (indices.size() > costs.size())
					return false; // when too much energy was selected!

				ArrayList<Element> chosenEnergy = new ArrayList<Element>();
				for (Integer i : indices) {
					List<Element> energy = ((EnergyCard) energyCards.get(i)).getProvidedEnergy();
					for (Element ele : energy)
						chosenEnergy.add(ele);
				}

				// Get a copy of the color- and colorless costs:
				List<Element> colorCosts = new ArrayList<>();
				int colorless = 0;
				for (Element element : costs)
					if (element != Element.COLORLESS)
						colorCosts.add(element);
					else
						colorless++;

				// Try to pay color costs:
				for (Element element : chosenEnergy) {
					boolean payed = false;
					for (int i = 0; i < colorCosts.size() && !payed; i++) {
						Element costElement = colorCosts.get(i);
						if (costElement == element) {
							colorCosts.remove(costElement);
							payed = true;
						}
					}
				}
				boolean colorPayed = colorCosts.size() == 0;

				// Try to pay the rest costs(colorless):
				boolean colorlessPayed = chosenEnergy.size() >= colorless;

				return colorPayed && colorlessPayed;
			}
		};
		cardChooseWindow.setVisible(true);
		List<Card> cards = new ArrayList<>();
		for (Card c : energyCards)
			cards.add(c);
		cardChooseWindow.setData("Select cards to pay the energy costs!", cards, 0, true, checker);

		this.addToUpdateQueue(cardChooseWindow); // waits for update queue here

		while (!cardChooseWindow.choosingFinished()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		List<Integer> indices = cardChooseWindow.getChosenIndices();
		ArrayList<Card> chosenCards = new ArrayList<Card>();
		for (Integer i : indices)
			chosenCards.add(energyCards.get(i));

		cardChooseWindow.unregisterShootables(ioController);
		cardChooseWindow.setVisible(false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				addToUpdateQueue(cardChooseWindow); // waits for update queue
													// here
			}
		}).start();
		return chosenCards;
	}

	@Override
	public List<Integer> userDistributesDamage(List<Position> positionList, List<Integer> damageList, List<Integer> maxDistList, DistributionMode mode) {
		DistributionChooser distributionChooser = this.ingameController.getDistributionChooser();
		/*
		 * No need to wait for update queue or game model update, since waiting
		 * for update queue is being done by #addToUpdateQueue and the game
		 * model is irrelevant for card choose models.
		 */
		ChooseGeometryChecker checker = new ChooseGeometryChecker() {
			@Override
			public boolean checkSelectionIsOk() {
				return distributionChooser.getBalance() == 0;
			}
		};
		distributionChooser.setData("Distribute damage!", positionList, checker, damageList, maxDistList, mode);
		distributionChooser.setVisible(true);

		this.addToUpdateQueue(distributionChooser); // waits for update queue
													// here

		while (!distributionChooser.choosingFinished()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		List<Integer> indices = distributionChooser.getDistributionList();

		distributionChooser.unregisterShootables(ioController);
		distributionChooser.setVisible(false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				addToUpdateQueue(distributionChooser); // waits for update queue
														// here
			}
		}).start();
		return indices;
	}

	@Override
	public void userUpdatesGameModel(LocalPokemonGameModel gameModel, Color ownColor, String sound) {
		// System.out.println("Started Update from " +
		// Thread.currentThread().getName());
		for (Position p : gameModel.getGameField().getAllPositions()) {
			SelectableNode n = ingameController.getPositionGeometry(p.getPositionID(), ownColor);
			if (n != null) {
				n = ingameController.updateGeometry(n, p);
				this.addToUpdateQueue(n);
			}
		}
		EffectController.playSound(sound);

		// Check if game is finished:
		if (gameModel.getGameState() == GameState.BLUE_WON || gameModel.getGameState() == GameState.RED_WON) {
			if ((this.getPlayer().getColor() == Color.BLUE && gameModel.getGameState() == GameState.BLUE_WON)
					|| (this.getPlayer().getColor() == Color.RED && gameModel.getGameState() == GameState.RED_WON)) {
				this.getIngameController().playerWon();
			} else {
				this.getIngameController().playerLost();
			}
		}
		// System.out.println("Finished Update");
	}

	@Override
	public void userReceivesGameTextMessage(String message, String sound) {
		TextPanel2D messagePanel = this.ingameController.getMessagePanel();
		messagePanel.setText(message);
		messagePanel.setVisible(true);
		this.addToUpdateQueue(messagePanel);
		EffectController.playSound(sound);
		try {
			Thread.sleep(MESSAGE_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		messagePanel.setVisible(false);
		this.addToUpdateQueue(messagePanel);
	}

	@Override
	public void userReceivesCardMessage(String message, Card card, String sound) {
		TextPanel2D messagePanel = this.ingameController.getMessagePanel();
		CardPanel2D cardMessagePanel = this.ingameController.getCardMessagePanel();

		messagePanel.setText(message);
		messagePanel.setVisible(true);
		List<Card> cList = new ArrayList<>();
		cList.add(card);
		cardMessagePanel.setCard(cList);
		cardMessagePanel.setVisible(true);
		List<SelectableNode> nodeList = new ArrayList<>();
		nodeList.add(cardMessagePanel);
		nodeList.add(messagePanel);
		this.addToUpdateQueue(nodeList);
		EffectController.playSound(sound);
		try {
			Thread.sleep(MESSAGE_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		messagePanel.setVisible(false);
		cardMessagePanel.setVisible(false);
		this.addToUpdateQueue(nodeList);
	}

	@Override
	public void userReceivesCardMessage(String message, List<Card> cardList, String sound) {
		TextPanel2D messagePanel = this.ingameController.getMessagePanel();
		CardPanel2D cardMessagePanel = this.ingameController.getCardMessagePanel();

		messagePanel.setText(message);
		messagePanel.setVisible(true);
		cardMessagePanel.setCard(cardList);
		cardMessagePanel.setVisible(true);
		List<SelectableNode> nodeList = new ArrayList<>();
		nodeList.add(cardMessagePanel);
		nodeList.add(messagePanel);
		this.addToUpdateQueue(nodeList);
		EffectController.playSound(sound);
		try {
			Thread.sleep(MESSAGE_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		messagePanel.setVisible(false);
		cardMessagePanel.setVisible(false);
		this.addToUpdateQueue(nodeList);
	}

	@Override
	public void setCardChoosable(Position position, int i, boolean value) {
		// System.out.println("SetCardChoosable for (" +
		// position.getPositionID() + ", " + i + ")" + "
		// from " + Thread.currentThread().getName());

		SelectableNode node = ingameController.getPositionGeometry(position.getPositionID(), this.player.getColor());
		if (node instanceof HandCardManager2D) {
			// It may happen that the hand card manager is not ready for being
			// updated, so we have to wait
			// until he is.
			while (((HandCardManager2D) node).getHandCard2Ds().size() <= i && i < HandCardManager2D.MAX_HAND_CARDS) {
				try {
					System.err.println("Warning: HandCardManager is not updated for index " + i + "!");
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			((HandCardManager2D) node).setIndexGlowing(i);
		} else {
			node.setGlowing(value);
			ioController.addShootable(node);
			this.addToUpdateQueue(node);
		}
	}

	@Override
	public void setEndTurnButtonVisible(boolean b) {
		// System.out.println("Setting endTurnButton visible: " + b);

		SelectableNode endTurnButton = this.ingameController.getEndTurnButton();
		endTurnButton.setVisible(b); // button adds/removes itself to shootables
										// in update()!
		// Start writing:
		this.addToUpdateQueue(endTurnButton);

		SelectableNode surrenderButton = this.ingameController.getSurrenderButton();
		surrenderButton.setVisible(b); // button adds/removes itself to
										// shootables in update()!
		// Start writing:
		this.addToUpdateQueue(surrenderButton);

		HandCardManager2D ownHand = this.ingameController.getOwnHand();
		ownHand.setScrollButtonsActivated(b);
		this.addToUpdateQueue(ownHand);

		HandCardManager2D enemyHand = this.ingameController.getEnemyHand();
		enemyHand.setScrollButtonsActivated(b);
		this.addToUpdateQueue(enemyHand);
	}

	public void setButtonVisible(SelectableNode button, boolean value) {
		button.setVisible(value); // button adds/removes itself to shootables in
									// update()!
		// Start writing:
		this.addToUpdateQueue(button);
	}

	@Override
	public void startGame() {
		this.lobbyController.hide();
	}

	@Override
	public void stopGame() {
		// this.switchMode(GUI2DMode.LOBBY);
	}

	@Override
	public boolean isStarted() {
		return isStarted;
	}

	public IOController getIOController() {
		return this.ioController;
	}

	public MusicController getMusicController() {
		return musicController;
	}

	public IngameController getIngameController() {
		return this.ingameController;
	}

	public synchronized void addToUpdateQueue(SelectableNode node) {
		if (Thread.currentThread().getName().equals(Threads.RENDER_THREAD.toString()))
			System.err.println("[RENDER] Error: Called addToUpdateQueue from render thread.");

		// Start writing:
		if (node != null)
			this.enqueue(new Callable<Spatial>() {
				public Spatial call() throws Exception {
					node.update();
					return null;
				}
			});
	}

	public synchronized void addToUpdateQueue(List<SelectableNode> nodeList) {
		if (Thread.currentThread().getName().equals(Threads.RENDER_THREAD.toString()))
			System.err.println("[RENDER] Error: Called addToUpdateQueue from render thread.");

		for (SelectableNode node : nodeList) {
			// Start writing:
			if (node != null)
				this.enqueue(new Callable<Spatial>() {
					public Spatial call() throws Exception {
						node.update();
						return null;
					}
				});
		}
	}

	public Pair<Integer, Integer> getResolution() {
		return resolution;
	}

	public BitmapFont getGuiFont() {
		return guiFont;
	}

	public GuiToPlayerCommunication getPlayer() {
		return player;
	}

	/**
	 * Switches between the controllers.
	 * 
	 * @param newMode
	 */
	public void switchMode(GUI2DMode newMode) {
		this.currentActiveController.hide();
		switch (newMode) {
		case START:
			this.currentActiveController = this.titleController;
			break;
		case DECK_EDIT:
			this.currentActiveController = this.deckEditController;
			this.musicController.switchMusic(MusicType.DECK_EDIT_MUSIC);
			break;
		case INGAME:
			this.currentActiveController = this.ingameController;
			this.musicController.switchMusic(MusicType.INGAME_MUSIC);
			break;
		case LOBBY:
			if (this.currentActiveController != this.titleController && this.currentActiveController != this.arenaController)
				this.musicController.switchMusic(MusicType.LOBBY_MUSIC);
			this.currentActiveController = this.lobbyController;
			break;
		case ARENA_CHOOSE_LOBBY:
			if (this.currentActiveController != this.lobbyController)
				this.musicController.switchMusic(MusicType.LOBBY_MUSIC);
			this.currentActiveController = this.arenaController;
			break;
		case MAMORIA_CITY_ARENA:
			this.currentActiveController = this.mamoriaArenaController;
			// TODO make mamoria music
			// this.musicController.switchMusic(MusicType.LOBBY_MUSIC);
			break;
		default:
			break;
		}
		this.currentActiveController.restart();
	}

	/**
	 * Only call after gui has been started.
	 * 
	 * @param player
	 */
	public void setPlayer(GuiToPlayerCommunication player) {
		this.player = player;
		this.lobbyController.setAccount(this.player.asAccount());
		this.arenaController.setAccount(this.player.asAccount());
		this.mamoriaArenaController.setAccount(this.player.asAccount());
		this.deckEditController.setAccount(this.player.asAccount());
		this.animationController.setPlayerColor(this.player.getColor());
	}

	/**
	 * Fades out the music of this audio node.
	 * 
	 * @param audio
	 * @param fadeOutTimeMilis
	 */
	public void fadeOutMusic(AudioNode audio, long fadeOutTimeMilis, long loopNumber) {
		float initVolume = audio.getVolume();
		long waitingTime = fadeOutTimeMilis / loopNumber;
		float reduceValue = initVolume / loopNumber;
		while (audio.getVolume() > 0) {
			try {
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			audio.setVolume((audio.getVolume() - reduceValue) >= 0 ? audio.getVolume() - reduceValue : 0);
		}
		audio.stop();
		audio.setVolume(initVolume);
	}

	@Override
	public void destroy() {
		super.destroy();
		System.exit(0);
	}

	@Override
	public void playSound(String sound) {
		EffectController.playSound(sound);
	}

	@Override
	public void playAnimation(Animation animation) {
		try {
			this.animationController.setPlayerColor(this.player.getColor());
			AnimateableObject[] animObjects = this.animationController.addAnimation(animation);

			boolean animationDone = false;
			while (!animationDone) {
				animationDone = true;
				for (AnimateableObject animObj : animObjects) {
					if (!animObj.animationDone()) {
						animationDone = false;
					}
				}
				if (!animationDone)
					Thread.sleep(10);
			}
			for (AnimateableObject animObj : animObjects) {
				animObj.resetAnimation();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
}
