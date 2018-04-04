package gui2d.controller;

import gui2d.GUI2D;
import gui2d.GUI2DMode;
import gui2d.abstracts.SelectableNode;
import gui2d.controller.MusicController.MusicType;
import gui2d.geometries.ArenaGeometry2D;
import gui2d.geometries.HandCard2D;
import gui2d.geometries.HandCardManager2D;
import gui2d.geometries.Image2D;
import gui2d.geometries.ImageButton2D;
import gui2d.geometries.ImageCounter2D;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.chooser.AttackChooseWindow;
import gui2d.geometries.chooser.CardChooseWindow;
import gui2d.geometries.chooser.CardViewer;
import gui2d.geometries.chooser.DistributionChooser;
import gui2d.geometries.chooser.ElementChooseWindow;
import gui2d.geometries.chooser.FileChooseWindow;
import gui2d.geometries.chooser.FileNameChooseWindow;
import gui2d.geometries.chooser.QuestionChooseWindow;
import gui2d.geometries.messages.CardPanel2D;
import gui2d.geometries.messages.TextPanel2D;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import model.database.Card;
import model.database.Database;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.Position;

import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * Manages all elements ingame.
 * 
 * @author Michael
 *
 */
public class IngameController extends Node implements GUI2DController {

	private HandCardManager2D ownHand, enemyHand;
	private ArenaGeometry2D ownActive, enemyActive;
	private List<ArenaGeometry2D> ownBench, enemyBench;
	private ImageCounter2D ownDeck, enemyDeck, ownGraveyard, enemyGraveyard;
	private List<Image2D> ownPrize, enemyPrize;
	private Spatial stadiumNode;
	private Material stadiumMat;
	private TextButton2D endTurnButton, attack1Button, attack2Button, attack3Button, playButton, pokePower1Button, returnToLobbyButton, backButton;
	private ImageButton2D surrenderButton, attackButton, retreatButton, pokePowerButton, stadiumButton;
	/** Resolution variable */
	private int screenWidth, screenHeight;
	private Image2D resultScreen, reward1, reward2, reward3, player_avatar, opponent_avatar;
	/** Currently selected node */
	private SelectableNode currentlySelected;
	/** True if a position has to be selected */
	private boolean positionSelectionMode;

	/* Choose windows: */
	private CardChooseWindow cardChooseWindow;
	private ElementChooseWindow elementChooseWindow;
	private AttackChooseWindow attackChooseWindow;
	private QuestionChooseWindow questionChooseWindow;
	private DistributionChooser distributionChooser;
	private FileChooseWindow fileChooseWindow;
	private FileNameChooseWindow fileNameChooseWindow;

	/* View Window: */
	private CardViewer cardViewer;

	/* Message Panels: */
	private TextPanel2D messagePanel, rewardMessagePanel;
	private CardPanel2D cardMessagePanel;

	public IngameController() {
		screenWidth = GUI2D.getInstance().getResolution().getKey();
		screenHeight = GUI2D.getInstance().getResolution().getValue();
		currentlySelected = null;
		positionSelectionMode = false;
	}

	public void initSceneGraph() {
		float handCardWidth = screenWidth * 0.06f; // Size of one single hand
													// card
		float handCardHeight = handCardWidth * 1.141f;

		ownHand = new HandCardManager2D("HandCardManager", screenWidth * 0.5f, screenHeight * 0.03f, 0, false);
		ownHand.setVisible(false);
		this.attachChild(ownHand);

		enemyHand = new HandCardManager2D("HandCardManager", screenWidth * 0.5f, screenHeight * 0.85f, 0, true);
		enemyHand.setVisible(false);
		this.attachChild(enemyHand);

		float activePosWidth = screenWidth * 0.15f; // Size of activePosition
		float activePosHeight = activePosWidth * 0.732f;

		ownActive = new ArenaGeometry2D("Active", Database.getPokemonThumbnailKey("00001"), activePosWidth, activePosHeight, false) {
			@Override
			public void mouseSelect() {
				arenaPositionSelected(this);
			}

			@Override
			public void mouseSelectRightClick() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						CardViewer viewer = GUI2D.getInstance().getIngameController().getCardViewer();
						viewer.setVisible(true);
						List<Card> cardList = new ArrayList<>();
						cardList.add(Database.createCard(ownActive.getTopCardID()));
						viewer.setData("Cards", cardList);
						GUI2D.getInstance().addToUpdateQueue(viewer);
					}
				}).start();
			}
		};
		ownActive.setLocalTranslation(screenWidth * 0.5f - activePosWidth, screenHeight * 0.48f - activePosHeight, 0);
		ownActive.setVisible(false);
		dropInUpdateQueue(ownActive);
		this.attachChild(ownActive);

		enemyActive = new ArenaGeometry2D("Active", Database.getPokemonThumbnailKey("00001"), activePosWidth, activePosHeight, true) {
			@Override
			public void mouseSelect() {
				arenaPositionSelected(this);
			}

			@Override
			public void mouseSelectRightClick() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						CardViewer viewer = GUI2D.getInstance().getIngameController().getCardViewer();
						viewer.setVisible(true);
						List<Card> cardList = new ArrayList<>();
						cardList.add(Database.createCard(enemyActive.getTopCardID()));
						viewer.setData("Cards", cardList);
						GUI2D.getInstance().addToUpdateQueue(viewer);
					}
				}).start();
			}
		};
		enemyActive.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.48f, 0);
		enemyActive.setVisible(false);
		dropInUpdateQueue(enemyActive);
		this.attachChild(enemyActive);

		float benchPosWidth = screenWidth * 0.09f; // Size of benchPosition
		float benchPosHeight = benchPosWidth * 0.732f;
		ownBench = new ArrayList<>();
		enemyBench = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			ArenaGeometry2D ownBenchPos = new ArenaGeometry2D("Bench", Database.getPokemonThumbnailKey("00001"), benchPosWidth, benchPosHeight, false) {
				@Override
				public void mouseSelect() {
					arenaPositionSelected(this);
				}

				@Override
				public void mouseSelectRightClick() {
					ArenaGeometry2D self = this;
					new Thread(new Runnable() {
						@Override
						public void run() {
							CardViewer viewer = GUI2D.getInstance().getIngameController().getCardViewer();
							viewer.setVisible(true);
							List<Card> cardList = new ArrayList<>();
							cardList.add(Database.createCard(self.getTopCardID()));
							viewer.setData("Cards", cardList);
							GUI2D.getInstance().addToUpdateQueue(viewer);
						}
					}).start();
				}
			};
			ownBenchPos.setLocalTranslation(screenWidth * 0.1f, screenHeight * 0.69f - benchPosHeight * i, 0);
			ownBenchPos.setVisible(false);
			dropInUpdateQueue(ownBenchPos);
			this.attachChild(ownBenchPos);

			ownBench.add(ownBenchPos);

			ArenaGeometry2D enemyBenchPos = new ArenaGeometry2D("Bench", Database.getPokemonThumbnailKey("00001"), benchPosWidth, benchPosHeight, true) {
				@Override
				public void mouseSelect() {
					arenaPositionSelected(this);
				}

				@Override
				public void mouseSelectRightClick() {
					ArenaGeometry2D self = this;
					new Thread(new Runnable() {
						@Override
						public void run() {
							CardViewer viewer = GUI2D.getInstance().getIngameController().getCardViewer();
							viewer.setVisible(true);
							List<Card> cardList = new ArrayList<>();
							cardList.add(Database.createCard(self.getTopCardID()));
							viewer.setData("Cards", cardList);
							GUI2D.getInstance().addToUpdateQueue(viewer);
						}
					}).start();
				}
			};
			enemyBenchPos.setLocalTranslation(screenWidth * 0.81f, screenHeight * 0.69f - benchPosHeight * i, 0);
			enemyBenchPos.setVisible(false);
			dropInUpdateQueue(enemyBenchPos);
			this.attachChild(enemyBenchPos);
			enemyBench.add(enemyBenchPos);
		}

		ownDeck = new ImageCounter2D("Deck", Database.getTextureKey("00000"), handCardWidth, handCardHeight, 0) {

			@Override
			public void mouseSelect() {
				stackGeometrySelected(this);
			}

			@Override
			public void mouseSelectRightClick() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						CardViewer viewer = GUI2D.getInstance().getIngameController().getCardViewer();
						viewer.setVisible(true);
						List<Card> cardList = new ArrayList<>();
						for (int i = 0; i < ownDeck.getCardIds().size(); i++)
							cardList.add(Database.createCard(ownDeck.getCardIds().get(i)));
						viewer.setData("Cards", cardList);
						GUI2D.getInstance().addToUpdateQueue(viewer);
					}
				}).start();
			}
		};
		ownDeck.setLocalTranslation(screenWidth * 0.85f, screenHeight * 0.03f, 0);
		ownDeck.setVisible(false);
		dropInUpdateQueue(ownDeck);
		this.attachChild(ownDeck);

		enemyDeck = new ImageCounter2D("Deck", Database.getTextureKey("00000"), handCardWidth, handCardHeight, 0) {

			@Override
			public void mouseSelect() {
				stackGeometrySelected(this);
			}

			@Override
			public void mouseSelectRightClick() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						CardViewer viewer = GUI2D.getInstance().getIngameController().getCardViewer();
						viewer.setVisible(true);
						List<Card> cardList = new ArrayList<>();
						for (int i = 0; i < enemyDeck.getCardIds().size(); i++)
							cardList.add(Database.createCard(enemyDeck.getCardIds().get(i)));
						viewer.setData("Cards", cardList);
						GUI2D.getInstance().addToUpdateQueue(viewer);
					}
				}).start();
			}
		};
		enemyDeck.setLocalTranslation(screenWidth * 0.10f, screenHeight * 0.85f, 0);
		enemyDeck.setVisible(false);
		dropInUpdateQueue(enemyDeck);
		this.attachChild(enemyDeck);

		ownGraveyard = new ImageCounter2D("DiscardPile", Database.getTextureKey("00000"), handCardWidth, handCardHeight, 0) {

			@Override
			public void mouseSelect() {
				stackGeometrySelected(this);
			}

			@Override
			public void mouseSelectRightClick() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						CardViewer viewer = GUI2D.getInstance().getIngameController().getCardViewer();
						viewer.setVisible(true);
						List<Card> cardList = new ArrayList<>();
						for (int i = 0; i < ownGraveyard.getCardIds().size(); i++)
							cardList.add(Database.createCard(ownGraveyard.getCardIds().get(i)));
						viewer.setData("Cards", cardList);
						GUI2D.getInstance().addToUpdateQueue(viewer);
					}
				}).start();
			}
		};
		ownGraveyard.setLocalTranslation(screenWidth * 0.92f, screenHeight * 0.03f, 0);
		ownGraveyard.setVisible(false);
		dropInUpdateQueue(ownGraveyard);
		this.attachChild(ownGraveyard);

		enemyGraveyard = new ImageCounter2D("DiscardPile", Database.getTextureKey("00000"), handCardWidth, handCardHeight, 0) {

			@Override
			public void mouseSelect() {
				stackGeometrySelected(this);
			}

			@Override
			public void mouseSelectRightClick() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						CardViewer viewer = GUI2D.getInstance().getIngameController().getCardViewer();
						viewer.setVisible(true);
						List<Card> cardList = new ArrayList<>();
						for (int i = 0; i < enemyGraveyard.getCardIds().size(); i++)
							cardList.add(Database.createCard(enemyGraveyard.getCardIds().get(i)));
						viewer.setData("Cards", cardList);
						GUI2D.getInstance().addToUpdateQueue(viewer);
					}
				}).start();
			}
		};
		enemyGraveyard.setLocalTranslation(screenWidth * 0.03f, screenHeight * 0.85f, 0);
		enemyGraveyard.setVisible(false);
		dropInUpdateQueue(enemyGraveyard);
		this.attachChild(enemyGraveyard);

		float prizePosWidth = activePosWidth / 6; // Size of activePosition
		float prizePosHeight = prizePosWidth * 1.141f;
		ownPrize = new ArrayList<>();
		enemyPrize = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			Image2D ownPriceImage = new Image2D("Prize", Database.getTextureKey("00000"), prizePosWidth, prizePosHeight, BlendMode.Alpha) {
				@Override
				public void mouseSelect() {
					prizeGeometrySelected(this);
				}

				@Override
				public void mouseSelectRightClick() {
					Image2D self = this;
					new Thread(new Runnable() {
						@Override
						public void run() {
							CardViewer viewer = GUI2D.getInstance().getIngameController().getCardViewer();
							viewer.setVisible(true);
							List<Card> cardList = new ArrayList<>();
							cardList.add(Database.createCard(self.getCardId()));
							viewer.setData("Cards", cardList);
							GUI2D.getInstance().addToUpdateQueue(viewer);
						}
					}).start();
				}
			};
			ownPriceImage.setLocalTranslation(screenWidth * 0.5f - activePosWidth + prizePosWidth * i, screenHeight * 0.48f - activePosHeight - prizePosHeight, 0);
			ownPriceImage.setVisible(false);
			dropInUpdateQueue(ownPriceImage);
			this.attachChild(ownPriceImage);
			ownPrize.add(ownPriceImage);

			Image2D enemyPriceImage = new Image2D("Prize", Database.getTextureKey("00000"), prizePosWidth, prizePosHeight, BlendMode.Alpha) {
				@Override
				public void mouseSelect() {
					prizeGeometrySelected(this);
				}

				@Override
				public void mouseSelectRightClick() {
					Image2D self = this;
					new Thread(new Runnable() {
						@Override
						public void run() {
							CardViewer viewer = GUI2D.getInstance().getIngameController().getCardViewer();
							viewer.setVisible(true);
							List<Card> cardList = new ArrayList<>();
							cardList.add(Database.createCard(self.getCardId()));
							viewer.setData("Cards", cardList);
							GUI2D.getInstance().addToUpdateQueue(viewer);
						}
					}).start();
				}
			};
			enemyPriceImage.setLocalTranslation(screenWidth * 0.5f + prizePosWidth * i, screenHeight * 0.48f + activePosHeight, 0);
			enemyPriceImage.setVisible(false);
			dropInUpdateQueue(enemyPriceImage);
			this.attachChild(enemyPriceImage);
			enemyPrize.add(enemyPriceImage);
		}

		float buttonWidth = screenWidth * 0.15f;
		float buttonHeight = activePosHeight * 0.75f / 2;
		endTurnButton = new TextButton2D("EndTurnButton", "End Turn", buttonWidth, activePosHeight / 4) {

			@Override
			public void mouseSelect() {
				endTurnClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		endTurnButton.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.48f - activePosHeight, 0);
		endTurnButton.setVisible(false);
		dropInUpdateQueue(endTurnButton);
		this.attachChild(endTurnButton);

		backButton = new TextButton2D("backButton", "Back", buttonWidth, activePosHeight / 4) {

			@Override
			public void mouseSelect() {
				backClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		backButton.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.48f - activePosHeight, 0);
		backButton.setVisible(false);
		dropInUpdateQueue(backButton);
		this.attachChild(backButton);

		pokePowerButton = new ImageButton2D("pokePowerButton", Database.getAssetKey("pokemonPower"), buttonWidth / 2, buttonHeight) {

			@Override
			public void mouseSelect() {
				pokePowerClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		pokePowerButton.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.455f - activePosHeight + buttonHeight * 1, 0);
		pokePowerButton.setVisible(false);
		dropInUpdateQueue(pokePowerButton);
		this.attachChild(pokePowerButton);

		stadiumButton = new ImageButton2D("StadiumButton", Database.getAssetKey("stadium"), buttonWidth / 2, buttonHeight) {

			@Override
			public void mouseSelect() {
				stadiumClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		stadiumButton.setLocalTranslation(screenWidth * 0.5f + buttonWidth / 2, screenHeight * 0.455f - activePosHeight + buttonHeight * 1, 0);
		stadiumButton.setVisible(false);
		dropInUpdateQueue(stadiumButton);
		this.attachChild(stadiumButton);

		attackButton = new ImageButton2D("AttackButton", Database.getAssetKey("attack"), buttonWidth / 2, buttonHeight) {

			@Override
			public void mouseSelect() {
				attackClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		attackButton.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.455f - activePosHeight + buttonHeight * 2, 0);
		attackButton.setVisible(false);
		dropInUpdateQueue(attackButton);
		this.attachChild(attackButton);

		float attackButtonHeight = activePosHeight * 0.75f / 3;
		attack1Button = new TextButton2D("Attack1Button", "Attack1", buttonWidth, attackButtonHeight) {

			@Override
			public void mouseSelect() {
				attack1Clicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		attack1Button.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.48f - activePosHeight + attackButtonHeight * 3, 0);
		attack1Button.setVisible(false);
		dropInUpdateQueue(attack1Button);
		this.attachChild(attack1Button);

		attack2Button = new TextButton2D("Attack2Button", "Attack2", buttonWidth, attackButtonHeight) {

			@Override
			public void mouseSelect() {
				attack2Clicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		attack2Button.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.48f - activePosHeight + attackButtonHeight * 2, 0);
		attack2Button.setVisible(false);
		dropInUpdateQueue(attack2Button);
		this.attachChild(attack2Button);

		attack3Button = new TextButton2D("Attack3Button", "Attack3", buttonWidth, attackButtonHeight) {

			@Override
			public void mouseSelect() {
				attack3Clicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		attack3Button.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.48f - activePosHeight + attackButtonHeight * 1, 0);
		attack3Button.setVisible(false);
		dropInUpdateQueue(attack3Button);
		this.attachChild(attack3Button);

		retreatButton = new ImageButton2D("RetreatButton", Database.getAssetKey("swap"), buttonWidth / 2, buttonHeight) {

			@Override
			public void mouseSelect() {
				retreatClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		retreatButton.setLocalTranslation(screenWidth * 0.5f + buttonWidth / 2, screenHeight * 0.455f - activePosHeight + buttonHeight * 2, 0);
		retreatButton.setVisible(false);
		dropInUpdateQueue(retreatButton);
		this.attachChild(retreatButton);

		playButton = new TextButton2D("PlayButton", "Play", buttonWidth, activePosHeight / 4) {

			@Override
			public void mouseSelect() {
				playClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		playButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, screenHeight * 0.17f, 0);
		playButton.setVisible(false);
		dropInUpdateQueue(playButton);
		this.attachChild(playButton);

		pokePower1Button = new TextButton2D("PokemonPowerButton", "Activate", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				pokePower1Clicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		pokePower1Button.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.455f - activePosHeight + buttonHeight * 2, 0);
		pokePower1Button.setVisible(false);
		dropInUpdateQueue(pokePower1Button);
		this.attachChild(pokePower1Button);

		surrenderButton = new ImageButton2D("surrenderButton", Database.getAssetKey("surrender"), screenWidth * 0.03f, screenWidth * 0.03f) {

			@Override
			public void mouseSelectRightClick() {
			}

			@Override
			public void mouseSelect() {
				surrenderButtonClicked();
			}
		};
		surrenderButton.setLocalTranslation(screenWidth * 0.1f, 0, 0);
		surrenderButton.setVisible(false);
		dropInUpdateQueue(surrenderButton);
		this.attachChild(surrenderButton);

		// Init Choose windows:
		cardChooseWindow = new CardChooseWindow("CardChooseWindow", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f,
				GUI2D.getInstance().getResolution().getValue() * 0.8f, 15);
		cardChooseWindow.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.15f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(cardChooseWindow);
		cardChooseWindow.setVisible(false);

		elementChooseWindow = new ElementChooseWindow("ElementChooseWindow", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f,
				GUI2D.getInstance().getResolution().getValue() * 0.8f, 15);
		elementChooseWindow.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.15f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(elementChooseWindow);
		elementChooseWindow.setVisible(false);

		attackChooseWindow = new AttackChooseWindow("AttackChooseWindow", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f,
				GUI2D.getInstance().getResolution().getValue() * 0.8f, 15);
		attackChooseWindow.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.15f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(attackChooseWindow);
		attackChooseWindow.setVisible(false);

		fileChooseWindow = new FileChooseWindow("FileChooseWindow", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f,
				GUI2D.getInstance().getResolution().getValue() * 0.8f, 30);
		fileChooseWindow.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.15f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(fileChooseWindow);
		fileChooseWindow.setVisible(false);

		fileNameChooseWindow = new FileNameChooseWindow("FileNameChooseWindow", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.3f,
				GUI2D.getInstance().getResolution().getValue() * 0.3f, 15);
		fileNameChooseWindow.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.35f, GUI2D.getInstance().getResolution().getValue() * 0.35f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(fileNameChooseWindow);
		fileNameChooseWindow.setVisible(false);

		questionChooseWindow = new QuestionChooseWindow("QuestionChooseWindow", "Question", GUI2D.getInstance().getResolution().getKey() * 0.5f,
				GUI2D.getInstance().getResolution().getValue() * 0.2f);
		questionChooseWindow.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.45f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(questionChooseWindow);
		questionChooseWindow.setVisible(false);

		distributionChooser = new DistributionChooser("DistributionChooseWindow", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f,
				GUI2D.getInstance().getResolution().getValue() * 0.8f);
		distributionChooser.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.15f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(distributionChooser);
		distributionChooser.setVisible(false);

		cardViewer = new CardViewer("CardViewer", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f, GUI2D.getInstance().getResolution().getValue() * 0.8f);
		cardViewer.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.15f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(cardViewer);
		cardViewer.setVisible(false);

		messagePanel = new TextPanel2D("MessagePanel", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f, GUI2D.getInstance().getResolution().getValue() * 0.10f) {
			@Override
			public void mouseSelect() {
				// Do nothing here!
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		messagePanel.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.73f, 0);
		GUI2D.getInstance().getGuiNode().attachChild(messagePanel);
		messagePanel.setVisible(false);

		rewardMessagePanel = new TextPanel2D("rewardMessagePanel", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f,
				GUI2D.getInstance().getResolution().getValue() * 0.10f) {
			@Override
			public void mouseSelect() {
				// Do nothing here!
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		rewardMessagePanel.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.73f, 0);
		GUI2D.getInstance().getGuiNode().attachChild(rewardMessagePanel);
		rewardMessagePanel.setVisible(false);

		cardMessagePanel = new CardPanel2D("CardMessaPanel", GUI2D.getInstance().getResolution().getKey() * 0.24f, GUI2D.getInstance().getResolution().getValue() * 0.22f) {
			@Override
			public void mouseSelect() {
				// Do nothing here!
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		cardMessagePanel.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.50f, 0);
		GUI2D.getInstance().getGuiNode().attachChild(cardMessagePanel);
		cardMessagePanel.setVisible(false);

		this.resultScreen = new Image2D("Result", Database.getAssetKey("win"), this.screenWidth * 0.3f, this.screenHeight * 0.3f, BlendMode.Alpha) {
			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {

			}
		};
		this.resultScreen.setLocalTranslation(screenWidth * 0.35f, screenHeight * 0.35f, 0.2f);
		this.resultScreen.setVisible(false);
		dropInUpdateQueue(this.resultScreen);
		this.attachChild(this.resultScreen);

		this.reward1 = new Image2D("reward1", Database.getAssetKey("win"), this.screenWidth * 0.1f, this.screenHeight * 0.3f, BlendMode.Alpha) {
			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {

			}
		};
		this.reward1.setLocalTranslation(screenWidth * 0.35f, screenHeight * 0.35f, 0.2f);
		this.reward1.setVisible(false);
		dropInUpdateQueue(this.reward1);
		this.attachChild(this.reward1);

		this.reward2 = new Image2D("reward2", Database.getAssetKey("win"), this.screenWidth * 0.1f, this.screenHeight * 0.3f, BlendMode.Alpha) {
			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {

			}
		};
		this.reward2.setLocalTranslation(screenWidth * 0.45f, screenHeight * 0.35f, 0.2f);
		this.reward2.setVisible(false);
		dropInUpdateQueue(this.reward2);
		this.attachChild(this.reward2);

		this.reward3 = new Image2D("reward3", Database.getAssetKey("win"), this.screenWidth * 0.1f, this.screenHeight * 0.3f, BlendMode.Alpha) {
			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {

			}
		};
		this.reward3.setLocalTranslation(screenWidth * 0.55f, screenHeight * 0.35f, 0.2f);
		this.reward3.setVisible(false);
		dropInUpdateQueue(this.reward3);
		this.attachChild(this.reward3);

		returnToLobbyButton = new TextButton2D("returnToLobbyButton", "Return to Lobby", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				returnToLobbyButtonClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		returnToLobbyButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, screenHeight * 0.25f, 0);
		returnToLobbyButton.setVisible(false);
		dropInUpdateQueue(returnToLobbyButton);
		this.attachChild(returnToLobbyButton);

		this.player_avatar = new Image2D("Player_Avatar", Database.getAssetKey("PLAYER_AVATAR"), this.screenWidth * 0.1f, this.screenWidth * 0.1f, BlendMode.Alpha) {
			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {

			}
		};
		this.player_avatar.setLocalTranslation(screenWidth * 0.0f, screenHeight * 0.0f, 0.2f);
		this.player_avatar.setVisible(false);
		dropInUpdateQueue(this.player_avatar);
		this.attachChild(this.player_avatar);

		this.opponent_avatar = new Image2D("Opponent_avatar", Database.getAssetKey("COMPUTER_AVATAR"), this.screenWidth * 0.1f, this.screenWidth * 0.1f, BlendMode.Alpha) {
			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {

			}
		};
		this.opponent_avatar.setLocalTranslation(screenWidth * 0.9f, screenHeight * 0.822f, 0.2f);
		this.opponent_avatar.setVisible(false);
		dropInUpdateQueue(this.opponent_avatar);
		this.attachChild(this.opponent_avatar);

		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(cardChooseWindow);
				GUI2D.getInstance().addToUpdateQueue(elementChooseWindow);
				GUI2D.getInstance().addToUpdateQueue(attackChooseWindow);
				GUI2D.getInstance().addToUpdateQueue(fileChooseWindow);
				GUI2D.getInstance().addToUpdateQueue(fileNameChooseWindow);
				GUI2D.getInstance().addToUpdateQueue(questionChooseWindow);
				GUI2D.getInstance().addToUpdateQueue(messagePanel);
				GUI2D.getInstance().addToUpdateQueue(rewardMessagePanel);
				GUI2D.getInstance().addToUpdateQueue(cardMessagePanel);
				GUI2D.getInstance().addToUpdateQueue(distributionChooser);
				GUI2D.getInstance().addToUpdateQueue(cardViewer);
			}
		}).start();

		// Make stadium
		Box stadium = new Box(4.52f, 2.4f, 0.0f);
		stadiumNode = new Geometry("Box", stadium);
		stadiumMat = new Material(GUI2D.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		stadiumMat.setTexture("ColorMap", GUI2D.getInstance().getAssetManager().loadTexture(Database.getPokemonThumbnailKey("00286")));
		stadiumNode.setMaterial(stadiumMat);
		stadiumNode.setLocalTranslation(0.0f, 0.12f, 0.1f);
	}

	protected void backClicked() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (attack1Enabled)
					GUI2D.getInstance().setButtonVisible(attack1Button, false);
				if (attack2Enabled)
					GUI2D.getInstance().setButtonVisible(attack2Button, false);
				if (attack3Enabled)
					GUI2D.getInstance().setButtonVisible(attack3Button, false);
				if (attackEnabled)
					GUI2D.getInstance().setButtonVisible(attackButton, true);
				if (stadiumEnabled)
					GUI2D.getInstance().setButtonVisible(stadiumButton, true);
				if (retreatEnabled)
					GUI2D.getInstance().setButtonVisible(retreatButton, true);
				if (pokemonPowerEnabled) {
					GUI2D.getInstance().setButtonVisible(pokePower1Button, false);
					GUI2D.getInstance().setButtonVisible(pokePowerButton, true);
				}
				GUI2D.getInstance().setButtonVisible(endTurnButton, true);
				GUI2D.getInstance().setButtonVisible(backButton, false);
			}
		}).start();
	}

	protected void attackClicked() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (attack1Enabled)
					GUI2D.getInstance().setButtonVisible(attack1Button, true);
				if (attack2Enabled)
					GUI2D.getInstance().setButtonVisible(attack2Button, true);
				if (attack3Enabled)
					GUI2D.getInstance().setButtonVisible(attack3Button, true);
				GUI2D.getInstance().setButtonVisible(attackButton, false);
				GUI2D.getInstance().setButtonVisible(pokePowerButton, false);
				GUI2D.getInstance().setButtonVisible(retreatButton, false);
				if (stadiumButton.isVisible()) {
					stadiumEnabled = true;
					GUI2D.getInstance().setButtonVisible(stadiumButton, false);
				}
				GUI2D.getInstance().setButtonVisible(endTurnButton, false);
				GUI2D.getInstance().setButtonVisible(backButton, true);
			}
		}).start();
	}

	protected void returnToLobbyButtonClicked() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().setButtonVisible(resultScreen, false);
				GUI2D.getInstance().setButtonVisible(reward1, false);
				GUI2D.getInstance().setButtonVisible(reward2, false);
				GUI2D.getInstance().setButtonVisible(reward3, false);
				GUI2D.getInstance().setButtonVisible(messagePanel, false);
				GUI2D.getInstance().setButtonVisible(rewardMessagePanel, false);
				GUI2D.getInstance().setButtonVisible(returnToLobbyButton, false);
				GUI2D.getInstance().switchMode(GUI2DMode.LOBBY, true);
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

	/**
	 * Actions, when the playButton is clicked.
	 */
	protected void playClicked() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final HandCard2D handGeo = (HandCard2D) currentlySelected;
				int modeledIndex = handGeo.getIndex() + handGeo.getCurrentScrollIndex();
				GUI2D.getInstance().setEndTurnButtonVisible(false);
				resetGlowingSelected();
				resetButtons();
				GUI2D.getInstance().getPlayer().playHandCard(modeledIndex);
			}
		});
		t.setName("PlayButtonThread");
		t.start();
	}

	/**
	 * Actions, when the pokemonPowerButton is clicked.
	 */
	protected void pokePowerClicked() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (pokemonPowerEnabled)
					GUI2D.getInstance().setButtonVisible(pokePower1Button, true);
				GUI2D.getInstance().setButtonVisible(attackButton, false);
				GUI2D.getInstance().setButtonVisible(pokePowerButton, false);
				GUI2D.getInstance().setButtonVisible(retreatButton, false);
				GUI2D.getInstance().setButtonVisible(endTurnButton, false);
				if (stadiumButton.isVisible()) {
					stadiumEnabled = true;
					GUI2D.getInstance().setButtonVisible(stadiumButton, false);
				}
				GUI2D.getInstance().setButtonVisible(backButton, true);
			}
		}).start();
	}

	/**
	 * Actions, when the pokemonPowerButton is clicked.
	 */
	protected void pokePower1Clicked() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final SelectableNode arenaNode = currentlySelected;
				Color color = GUI2D.getInstance().getPlayer().getColor();
				GUI2D.getInstance().setEndTurnButtonVisible(false);
				resetGlowingSelected();
				resetButtons();
				if (stadiumButton.isVisible()) {
					stadiumEnabled = false;
					GUI2D.getInstance().setButtonVisible(stadiumButton, false);
				}
				GUI2D.getInstance().getPlayer().pokemonPower(getPositionIDForArenaGeometry(arenaNode, color));
			}
		});
		t.setName("PokePowerButtonThread");
		t.start();
	}

	protected void stadiumClicked() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean answer = GUI2D.getInstance().userAnswersQuestion("Do you want to activate the stadiums effect?");
				if (answer) {
					GUI2D.getInstance().setEndTurnButtonVisible(false);
					resetGlowingSelected();
					resetButtons();
					if (stadiumButton.isVisible()) {
						stadiumEnabled = false;
						GUI2D.getInstance().setButtonVisible(stadiumButton, false);
					}
					GUI2D.getInstance().getPlayer().activateStadium();
				}
			}
		});
		t.setName("RetreatButtonThread");
		t.start();
	}

	protected void retreatClicked() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean answer = GUI2D.getInstance().userAnswersQuestion("Do you want to retreat your active pokemon?");
				if (answer) {
					GUI2D.getInstance().setEndTurnButtonVisible(false);
					resetGlowingSelected();
					resetButtons();
					GUI2D.getInstance().getPlayer().retreatPokemon();
				}
			}
		});
		t.setName("RetreatButtonThread");
		t.start();
	}

	/**
	 * Executes the first attack of the ownActive pokemon. Note that the attack has to exist as a precondition.
	 */
	protected void attack1Clicked() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().setEndTurnButtonVisible(false);
				resetGlowingSelected();
				resetButtons();
				if (stadiumButton.isVisible()) {
					stadiumEnabled = false;
					GUI2D.getInstance().setButtonVisible(stadiumButton, false);
				}
				GUI2D.getInstance().getPlayer().attack(0);
			}
		});
		t.setName("Attack1ButtonThread");
		t.start();
	}

	/**
	 * Executes the second attack of the ownActive pokemon. Note that the attack has to exist as a precondition.
	 */
	protected void attack2Clicked() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().setEndTurnButtonVisible(false);
				resetGlowingSelected();
				resetButtons();
				if (stadiumButton.isVisible()) {
					stadiumEnabled = false;
					GUI2D.getInstance().setButtonVisible(stadiumButton, false);
				}
				GUI2D.getInstance().getPlayer().attack(1);
			}
		});
		t.setName("Attack2ButtonThread");
		t.start();
	}

	/**
	 * Executes the second attack of the ownActive pokemon. Note that the attack has to exist as a precondition.
	 */
	protected void attack3Clicked() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().setEndTurnButtonVisible(false);
				resetGlowingSelected();
				resetButtons();
				if (stadiumButton.isVisible()) {
					stadiumEnabled = false;
					GUI2D.getInstance().setButtonVisible(stadiumButton, false);
				}
				GUI2D.getInstance().getPlayer().attack(2);
			}
		});
		t.setName("Attack3ButtonThread");
		t.start();
	}

	/**
	 * Actions, when the endTurnButton is clicked.
	 */
	protected void endTurnClicked() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().setEndTurnButtonVisible(false);
				resetGlowingSelected();
				resetButtons();
				if (stadiumButton.isVisible()) {
					stadiumEnabled = false;
					GUI2D.getInstance().setButtonVisible(stadiumButton, false);
				}
				GUI2D.getInstance().getPlayer().sendEndTurnToServer();
			}
		});
		t.setName("EndTurnButtonThread");
		t.start();
	}

	protected void surrenderButtonClicked() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean answer = GUI2D.getInstance().userAnswersQuestion("Do you really want to surrender?");
				if (answer) {
					GUI2D.getInstance().setEndTurnButtonVisible(false);
					resetGlowingSelected();
					resetButtons();
					if (stadiumButton.isVisible()) {
						stadiumEnabled = false;
						GUI2D.getInstance().setButtonVisible(stadiumButton, false);
					}
					GUI2D.getInstance().getPlayer().sendSurrenderToServer();
				}
			}
		});
		t.setName("SurrenderButtonThread");
		t.start();
	}

	public void playerWon() {
		List<String> unlockedCards = GUI2D.getInstance().playerWon();
		if (unlockedCards.isEmpty()) {
			this.resultScreen.setTexture(Database.getAssetKey("win"));
			GUI2D.getInstance().setButtonVisible(this.resultScreen, true);
		} else if (unlockedCards.size() == 1) {
			this.rewardMessagePanel.setText("Victory! You unlocked the following cards:");
			this.rewardMessagePanel.setVisible(true);
			GUI2D.getInstance().setButtonVisible(this.rewardMessagePanel, true);
			this.reward2.setTexture(Database.getTextureKey(unlockedCards.get(0)));
			GUI2D.getInstance().setButtonVisible(this.reward2, true);
		} else if (unlockedCards.size() == 3) {
			this.rewardMessagePanel.setText("Victory! You unlocked the following cards:");
			this.rewardMessagePanel.setVisible(true);
			GUI2D.getInstance().setButtonVisible(this.rewardMessagePanel, true);
			this.reward1.setTexture(Database.getTextureKey(unlockedCards.get(0)));
			GUI2D.getInstance().setButtonVisible(this.reward1, true);
			this.reward2.setTexture(Database.getTextureKey(unlockedCards.get(1)));
			GUI2D.getInstance().setButtonVisible(this.reward2, true);
			this.reward3.setTexture(Database.getTextureKey(unlockedCards.get(2)));
			GUI2D.getInstance().setButtonVisible(this.reward3, true);
		}
		GUI2D.getInstance().setButtonVisible(returnToLobbyButton, true);
		GUI2D.getInstance().getMusicController().switchMusic(MusicType.VICTORY_MUSIC);
	}

	public void playerLost() {
		GUI2D.getInstance().playerLost();
		this.resultScreen.setTexture(Database.getAssetKey("lose"));
		GUI2D.getInstance().setButtonVisible(this.resultScreen, true);
		GUI2D.getInstance().setButtonVisible(returnToLobbyButton, true);
		GUI2D.getInstance().getMusicController().switchMusic(MusicType.LOSS_MUSIC);
	}

	public ImageButton2D getSurrenderButton() {
		return surrenderButton;
	}

	public void resetGlowingSelected() {
		currentlySelected = null;
		for (SelectableNode node : this.getSelectableNodes()) {
			if (node.isGlowing())
				GUI2D.getInstance().getIOController().removeShootable(node);
			ownHand.clearGlowing();
			node.setSelected(false);
			node.setGlowing(false);
			GUI2D.getInstance().getIOController().removeRightShootable(node);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().addToUpdateQueue(node);
				}
			}).start();
		}
	}

	public void resetButtons() {
		pokemonPowerEnabled = false;
		attack1Enabled = false;
		attack2Enabled = false;
		attack3Enabled = false;
		attackEnabled = false;
		retreatEnabled = false;
		GUI2D.getInstance().setButtonVisible(backButton, false);
		GUI2D.getInstance().setButtonVisible(attackButton, false);
		GUI2D.getInstance().setButtonVisible(attack1Button, false);
		GUI2D.getInstance().setButtonVisible(attack2Button, false);
		GUI2D.getInstance().setButtonVisible(attack3Button, false);
		GUI2D.getInstance().setButtonVisible(playButton, false);
		GUI2D.getInstance().setButtonVisible(retreatButton, false);
		GUI2D.getInstance().setButtonVisible(pokePowerButton, false);
		GUI2D.getInstance().setButtonVisible(pokePower1Button, false);
		GUI2D.getInstance().setButtonVisible(returnToLobbyButton, false);
	}

	/**
	 * This method is called whenever the mouseSelect() method is invoked at any {@link HandCard2D} in the scenegraph.
	 * 
	 * @param handGeo
	 */
	public void handCardGeometrySelected(HandCard2D handGeo) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				List<PlayerAction> actionList = GUI2D.getInstance().getPlayer().getPlayerActionsForHandCard(handGeo.getIndex() + handGeo.getCurrentScrollIndex());
				resetButtons();
				for (PlayerAction action : actionList)
					makeButtonForActionVisible(action, handGeo);
				if (currentlySelected == handGeo) {
					resetButtons();
					currentlySelected.setSelected(false);
					GUI2D.getInstance().addToUpdateQueue(handGeo);
					currentlySelected = null;
				} else if (currentlySelected != null) {
					currentlySelected.setSelected(false);
					GUI2D.getInstance().addToUpdateQueue(currentlySelected);
					currentlySelected = handGeo;
					currentlySelected.setSelected(true);
					GUI2D.getInstance().addToUpdateQueue(currentlySelected);
				} else {
					currentlySelected = handGeo;
					currentlySelected.setSelected(true);
					GUI2D.getInstance().addToUpdateQueue(currentlySelected);
				}
			}
		});
		t.setName("HandCardClickedThread");
		t.start();
	}

	private boolean attack1Enabled, attack2Enabled, attack3Enabled, attackEnabled, retreatEnabled, pokemonPowerEnabled, stadiumEnabled;

	private void makeButtonForActionVisible(PlayerAction action, SelectableNode selectedNode) {
		GUI2D.getInstance().setButtonVisible(endTurnButton, true);
		switch (action) {
		case ATTACK_1:
			attackEnabled = true;
			attack1Enabled = true;
			Color color = GUI2D.getInstance().getPlayer().getColor();
			List<String> attackNames = GUI2D.getInstance().getPlayer().getAttackNames(color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON);
			attack1Button.setText(attackNames.get(0));
			GUI2D.getInstance().setButtonVisible(attackButton, true);
			break;
		case ATTACK_2:
			attackEnabled = true;
			attack2Enabled = true;
			color = GUI2D.getInstance().getPlayer().getColor();
			attackNames = GUI2D.getInstance().getPlayer().getAttackNames(color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON);
			attack2Button.setText(attackNames.get(1));
			GUI2D.getInstance().setButtonVisible(attackButton, true);
			break;
		case ATTACK_3:
			attackEnabled = true;
			attack3Enabled = true;
			color = GUI2D.getInstance().getPlayer().getColor();
			attackNames = GUI2D.getInstance().getPlayer().getAttackNames(color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON);
			attack3Button.setText(attackNames.get(2));
			GUI2D.getInstance().setButtonVisible(attackButton, true);
			break;
		case EVOLVE_POKEMON:
			playButton.setText("Evolve");
			GUI2D.getInstance().setButtonVisible(playButton, true);
			break;
		case PLAY_ENERGY_CARD:
			playButton.setText("Play");
			GUI2D.getInstance().setButtonVisible(playButton, true);
			break;
		case PLAY_TRAINER_CARD:
			playButton.setText("Play");
			GUI2D.getInstance().setButtonVisible(playButton, true);
			break;
		case POKEMON_POWER:
			pokemonPowerEnabled = true;
			color = GUI2D.getInstance().getPlayer().getColor();
			attackNames = GUI2D.getInstance().getPlayer().getPokePowerNames(this.getPositionIDForArenaGeometry(selectedNode, color));
			pokePower1Button.setText(attackNames.get(0));
			GUI2D.getInstance().setButtonVisible(pokePowerButton, true);
			break;
		case PUT_ON_BENCH:
			playButton.setText("Play");
			GUI2D.getInstance().setButtonVisible(playButton, true);
			break;
		case RETREAT_POKEMON:
			retreatEnabled = true;
			GUI2D.getInstance().setButtonVisible(retreatButton, true);
			break;
		case ACTIVATE_STADIUM_EFFECT:
			stadiumEnabled = true;
			GUI2D.getInstance().setButtonVisible(stadiumButton, true);
			break;
		case SHOW_CARDS_ON_POSITION:
			break;
		default:
			break;
		}
	}

	/**
	 * This method is called whenever the mouseSelect() method is invoked at any {@link ArenaGeometry2D} in the scenegraph.
	 * 
	 * @param arenaGeo
	 */
	public void arenaPositionSelected(ArenaGeometry2D arenaGeo) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (!isPositionSelectionMode()) {
					// Only execute when no positions have to be selected by the
					// user
					Color ownColor = GUI2D.getInstance().getPlayer().getColor();
					List<PlayerAction> actionList = GUI2D.getInstance().getPlayer().getPlayerActionsForArenaPosition(getPositionIDForArenaGeometry(arenaGeo, ownColor));
					resetButtons();
					// stadiumEnabled = false;
					// GUI2D.getInstance().setButtonVisible(stadiumButton, false);
					for (PlayerAction action : actionList)
						makeButtonForActionVisible(action, arenaGeo);
				}

				if (currentlySelected == arenaGeo) {
					resetButtons();
					currentlySelected.setSelected(false);
					GUI2D.getInstance().addToUpdateQueue(arenaGeo);
					currentlySelected = null;
				} else if (currentlySelected != null) {
					currentlySelected.setSelected(false);
					GUI2D.getInstance().addToUpdateQueue(currentlySelected);
					currentlySelected = arenaGeo;
					currentlySelected.setSelected(true);
					GUI2D.getInstance().addToUpdateQueue(currentlySelected);
				} else {
					currentlySelected = arenaGeo;
					currentlySelected.setSelected(true);
					GUI2D.getInstance().addToUpdateQueue(currentlySelected);
				}
			}
		});
		t.setName("ArenaPosClickedThread");
		t.start();
	}

	/**
	 * This method is called whenever the mouseSelect() method is invoked at any {@link ImageButtonCounter2D} in the scenegraph.
	 * 
	 * @param stackGeo
	 */
	public void stackGeometrySelected(ImageCounter2D stackGeo) {
		// nothing to do here!
	}

	/**
	 * This method is called whenever the mouseSelect() method is invoked at any {@link Image2D} in the scenegraph.
	 * 
	 * @param stackGeo
	 */
	public void prizeGeometrySelected(Image2D prizeGeo) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (currentlySelected == prizeGeo) {
					resetButtons();
					currentlySelected.setSelected(false);
					GUI2D.getInstance().addToUpdateQueue(prizeGeo);
					currentlySelected = null;
				} else if (currentlySelected != null) {
					currentlySelected.setSelected(false);
					GUI2D.getInstance().addToUpdateQueue(currentlySelected);
					currentlySelected = prizeGeo;
					currentlySelected.setSelected(true);
					GUI2D.getInstance().addToUpdateQueue(currentlySelected);
				} else {
					currentlySelected = prizeGeo;
					currentlySelected.setSelected(true);
					GUI2D.getInstance().addToUpdateQueue(currentlySelected);
				}
			}
		});
		t.setName("PrizePosClickedThread");
		t.start();
	}

	/**
	 * Returns the geometry of the given positionId.
	 * 
	 * @param p
	 * @return
	 */
	public SelectableNode getPositionGeometry(PositionID p, Color ownColor) {
		switch (p) {
		case BLUE_ACTIVEPOKEMON:
			if (ownColor == Color.BLUE)
				return ownActive;
			else
				return enemyActive;
		case BLUE_BENCH_1:
			if (ownColor == Color.BLUE)
				return ownBench.get(0);
			else
				return enemyBench.get(0);
		case BLUE_BENCH_2:
			if (ownColor == Color.BLUE)
				return ownBench.get(1);
			else
				return enemyBench.get(1);
		case BLUE_BENCH_3:
			if (ownColor == Color.BLUE)
				return ownBench.get(2);
			else
				return enemyBench.get(2);
		case BLUE_BENCH_4:
			if (ownColor == Color.BLUE)
				return ownBench.get(3);
			else
				return enemyBench.get(3);
		case BLUE_BENCH_5:
			if (ownColor == Color.BLUE)
				return ownBench.get(4);
			else
				return enemyBench.get(4);
		case BLUE_DECK:
			if (ownColor == Color.BLUE)
				return ownDeck;
			else
				return enemyDeck;
		case BLUE_DISCARDPILE:
			if (ownColor == Color.BLUE)
				return ownGraveyard;
			else
				return enemyGraveyard;
		case BLUE_HAND:
			if (ownColor == Color.BLUE)
				return ownHand;
			else
				return enemyHand;
		case BLUE_PRICE_1:
			if (ownColor == Color.BLUE)
				return ownPrize.get(0);
			else
				return enemyPrize.get(0);
		case BLUE_PRICE_2:
			if (ownColor == Color.BLUE)
				return ownPrize.get(1);
			else
				return enemyPrize.get(1);
		case BLUE_PRICE_3:
			if (ownColor == Color.BLUE)
				return ownPrize.get(2);
			else
				return enemyPrize.get(2);
		case BLUE_PRICE_4:
			if (ownColor == Color.BLUE)
				return ownPrize.get(3);
			else
				return enemyPrize.get(3);
		case BLUE_PRICE_5:
			if (ownColor == Color.BLUE)
				return ownPrize.get(4);
			else
				return enemyPrize.get(4);
		case BLUE_PRICE_6:
			if (ownColor == Color.BLUE)
				return ownPrize.get(5);
			else
				return enemyPrize.get(5);
		case RED_ACTIVEPOKEMON:
			if (ownColor == Color.RED)
				return ownActive;
			else
				return enemyActive;
		case RED_BENCH_1:
			if (ownColor == Color.RED)
				return ownBench.get(0);
			else
				return enemyBench.get(0);
		case RED_BENCH_2:
			if (ownColor == Color.RED)
				return ownBench.get(1);
			else
				return enemyBench.get(1);
		case RED_BENCH_3:
			if (ownColor == Color.RED)
				return ownBench.get(2);
			else
				return enemyBench.get(2);
		case RED_BENCH_4:
			if (ownColor == Color.RED)
				return ownBench.get(3);
			else
				return enemyBench.get(3);
		case RED_BENCH_5:
			if (ownColor == Color.RED)
				return ownBench.get(4);
			else
				return enemyBench.get(4);
		case RED_DECK:
			if (ownColor == Color.RED)
				return ownDeck;
			else
				return enemyDeck;
		case RED_DISCARDPILE:
			if (ownColor == Color.RED)
				return ownGraveyard;
			else
				return enemyGraveyard;
		case RED_HAND:
			if (ownColor == Color.RED)
				return ownHand;
			else
				return enemyHand;
		case RED_PRICE_1:
			if (ownColor == Color.RED)
				return ownPrize.get(0);
			else
				return enemyPrize.get(0);
		case RED_PRICE_2:
			if (ownColor == Color.RED)
				return ownPrize.get(1);
			else
				return enemyPrize.get(1);
		case RED_PRICE_3:
			if (ownColor == Color.RED)
				return ownPrize.get(2);
			else
				return enemyPrize.get(2);
		case RED_PRICE_4:
			if (ownColor == Color.RED)
				return ownPrize.get(3);
			else
				return enemyPrize.get(3);
		case RED_PRICE_5:
			if (ownColor == Color.RED)
				return ownPrize.get(4);
			else
				return enemyPrize.get(4);
		case RED_PRICE_6:
			if (ownColor == Color.RED)
				return ownPrize.get(5);
			else
				return enemyPrize.get(5);
		default:
			System.err.println("Wrong position : " + p);
			return null;
		}
	}

	/**
	 * Returns the positionID for the given arena position. Positions allowed here are active, bench and price position nodes!
	 * 
	 * @param node
	 * @param color
	 * @return
	 */
	public PositionID getPositionIDForArenaGeometry(SelectableNode node, Color color) {
		if (node == ownActive)
			return color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
		if (node == ownBench.get(0))
			return color == Color.BLUE ? PositionID.BLUE_BENCH_1 : PositionID.RED_BENCH_1;
		if (node == ownBench.get(1))
			return color == Color.BLUE ? PositionID.BLUE_BENCH_2 : PositionID.RED_BENCH_2;
		if (node == ownBench.get(2))
			return color == Color.BLUE ? PositionID.BLUE_BENCH_3 : PositionID.RED_BENCH_3;
		if (node == ownBench.get(3))
			return color == Color.BLUE ? PositionID.BLUE_BENCH_4 : PositionID.RED_BENCH_4;
		if (node == ownBench.get(4))
			return color == Color.BLUE ? PositionID.BLUE_BENCH_5 : PositionID.RED_BENCH_5;
		if (node == ownPrize.get(0))
			return color == Color.BLUE ? PositionID.BLUE_PRICE_1 : PositionID.RED_PRICE_1;
		if (node == ownPrize.get(1))
			return color == Color.BLUE ? PositionID.BLUE_PRICE_2 : PositionID.RED_PRICE_2;
		if (node == ownPrize.get(2))
			return color == Color.BLUE ? PositionID.BLUE_PRICE_3 : PositionID.RED_PRICE_3;
		if (node == ownPrize.get(3))
			return color == Color.BLUE ? PositionID.BLUE_PRICE_4 : PositionID.RED_PRICE_4;
		if (node == ownPrize.get(4))
			return color == Color.BLUE ? PositionID.BLUE_PRICE_5 : PositionID.RED_PRICE_5;
		if (node == ownPrize.get(5))
			return color == Color.BLUE ? PositionID.BLUE_PRICE_6 : PositionID.RED_PRICE_6;

		if (node == enemyActive)
			return color == Color.RED ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON;
		if (node == enemyBench.get(0))
			return color == Color.RED ? PositionID.BLUE_BENCH_1 : PositionID.RED_BENCH_1;
		if (node == enemyBench.get(1))
			return color == Color.RED ? PositionID.BLUE_BENCH_2 : PositionID.RED_BENCH_2;
		if (node == enemyBench.get(2))
			return color == Color.RED ? PositionID.BLUE_BENCH_3 : PositionID.RED_BENCH_3;
		if (node == enemyBench.get(3))
			return color == Color.RED ? PositionID.BLUE_BENCH_4 : PositionID.RED_BENCH_4;
		if (node == enemyBench.get(4))
			return color == Color.RED ? PositionID.BLUE_BENCH_5 : PositionID.RED_BENCH_5;
		if (node == enemyPrize.get(0))
			return color == Color.RED ? PositionID.BLUE_PRICE_1 : PositionID.RED_PRICE_1;
		if (node == enemyPrize.get(1))
			return color == Color.RED ? PositionID.BLUE_PRICE_2 : PositionID.RED_PRICE_2;
		if (node == enemyPrize.get(2))
			return color == Color.RED ? PositionID.BLUE_PRICE_3 : PositionID.RED_PRICE_3;
		if (node == enemyPrize.get(3))
			return color == Color.RED ? PositionID.BLUE_PRICE_4 : PositionID.RED_PRICE_4;
		if (node == enemyPrize.get(4))
			return color == Color.RED ? PositionID.BLUE_PRICE_5 : PositionID.RED_PRICE_5;
		if (node == enemyPrize.get(5))
			return color == Color.RED ? PositionID.BLUE_PRICE_6 : PositionID.RED_PRICE_6;

		throw new IllegalArgumentException("Node is not an arena node in getPositionIDForArenaGeometry()");
	}

	public TextButton2D getEndTurnButton() {
		return endTurnButton;
	}

	public SelectableNode updateGeometry(SelectableNode n, Position p) {
		switch (p.getPositionID()) {
		case BLUE_ACTIVEPOKEMON:
			ArenaGeometry2D active = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				active.setDamageMarks(pokemon.getDamageMarks());
				active.setHitPoints(pokemon.getHitpoints());
				active.setConditionList(pokemon.getConditions());
				active.setEnergyList(p.getEnergy());
				active.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				active.setTopCardID(pokemon.getCardId());
				active.setVisible(true);
			} else
				active.setVisible(false);
			break;
		case BLUE_BENCH_1:
			ArenaGeometry2D bench = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				bench.setDamageMarks(pokemon.getDamageMarks());
				bench.setHitPoints(pokemon.getHitpoints());
				bench.setConditionList(pokemon.getConditions());
				bench.setEnergyList(p.getEnergy());
				bench.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				bench.setTopCardID(pokemon.getCardId());
				bench.setVisible(true);
			} else
				bench.setVisible(false);
			break;
		case BLUE_BENCH_2:
			bench = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				bench.setDamageMarks(pokemon.getDamageMarks());
				bench.setHitPoints(pokemon.getHitpoints());
				bench.setConditionList(pokemon.getConditions());
				bench.setEnergyList(p.getEnergy());
				bench.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				bench.setTopCardID(pokemon.getCardId());
				bench.setVisible(true);
			} else
				bench.setVisible(false);
			break;
		case BLUE_BENCH_3:
			bench = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				bench.setDamageMarks(pokemon.getDamageMarks());
				bench.setHitPoints(pokemon.getHitpoints());
				bench.setConditionList(pokemon.getConditions());
				bench.setEnergyList(p.getEnergy());
				bench.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				bench.setTopCardID(pokemon.getCardId());
				bench.setVisible(true);
			} else
				bench.setVisible(false);
			break;
		case BLUE_BENCH_4:
			bench = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				bench.setDamageMarks(pokemon.getDamageMarks());
				bench.setHitPoints(pokemon.getHitpoints());
				bench.setConditionList(pokemon.getConditions());
				bench.setEnergyList(p.getEnergy());
				bench.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				bench.setTopCardID(pokemon.getCardId());
				bench.setVisible(true);
			} else
				bench.setVisible(false);
			break;
		case BLUE_BENCH_5:
			bench = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				bench.setDamageMarks(pokemon.getDamageMarks());
				bench.setHitPoints(pokemon.getHitpoints());
				bench.setConditionList(pokemon.getConditions());
				bench.setEnergyList(p.getEnergy());
				bench.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				bench.setTopCardID(pokemon.getCardId());
				bench.setVisible(true);
			} else
				bench.setVisible(false);
			break;
		case BLUE_DECK:
			ImageCounter2D deck = (ImageCounter2D) n;
			if (p.size() > 0) {
				deck.setCounter(p.size());
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					deck.setTexture(Database.getTextureKey(c.getCardId()));
				else
					deck.setTexture(Database.getTextureKey("00000"));
				List<String> cardIds = new ArrayList<>();
				for (int i = 0; i < p.getCards().size(); i++)
					cardIds.add(p.getCards().get(i).getCardId());
				deck.setCardIds(cardIds);
				deck.setVisible(true);
			} else
				deck.setVisible(false);
			break;
		case BLUE_DISCARDPILE:
			ImageCounter2D discardPile = (ImageCounter2D) n;
			if (p.size() > 0) {
				discardPile.setCounter(p.size());
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					discardPile.setTexture(Database.getTextureKey(c.getCardId()));
				else
					discardPile.setTexture(Database.getTextureKey("00000"));
				List<String> cardIds = new ArrayList<>();
				for (int i = 0; i < p.getCards().size(); i++)
					cardIds.add(p.getCards().get(i).getCardId());
				discardPile.setCardIds(cardIds);
				discardPile.setVisible(true);
			} else
				discardPile.setVisible(false);
			break;
		case BLUE_HAND:
			HandCardManager2D hand = (HandCardManager2D) n;
			if (p.size() > 0) {
				hand.setCards(p.getCards());
				hand.setVisible(true);
			} else {
				hand.setCards(new ArrayList<Card>());
				hand.setVisible(false);
			}
			break;
		case BLUE_PRICE_1:
			Image2D price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		case BLUE_PRICE_2:
			price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		case BLUE_PRICE_3:
			price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		case BLUE_PRICE_4:
			price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		case BLUE_PRICE_5:
			price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		case BLUE_PRICE_6:
			price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		case RED_ACTIVEPOKEMON:
			active = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				active.setDamageMarks(pokemon.getDamageMarks());
				active.setHitPoints(pokemon.getHitpoints());
				active.setConditionList(pokemon.getConditions());
				active.setEnergyList(p.getEnergy());
				active.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				active.setTopCardID(pokemon.getCardId());
				active.setVisible(true);
			} else
				active.setVisible(false);
			break;
		case RED_BENCH_1:
			bench = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				bench.setDamageMarks(pokemon.getDamageMarks());
				bench.setHitPoints(pokemon.getHitpoints());
				bench.setConditionList(pokemon.getConditions());
				bench.setEnergyList(p.getEnergy());
				bench.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				bench.setTopCardID(pokemon.getCardId());
				bench.setVisible(true);
			} else
				bench.setVisible(false);
			break;
		case RED_BENCH_2:
			bench = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				bench.setDamageMarks(pokemon.getDamageMarks());
				bench.setHitPoints(pokemon.getHitpoints());
				bench.setConditionList(pokemon.getConditions());
				bench.setEnergyList(p.getEnergy());
				bench.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				bench.setTopCardID(pokemon.getCardId());
				bench.setVisible(true);
			} else
				bench.setVisible(false);
			break;
		case RED_BENCH_3:
			bench = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				bench.setDamageMarks(pokemon.getDamageMarks());
				bench.setHitPoints(pokemon.getHitpoints());
				bench.setConditionList(pokemon.getConditions());
				bench.setEnergyList(p.getEnergy());
				bench.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				bench.setTopCardID(pokemon.getCardId());
				bench.setVisible(true);
			} else
				bench.setVisible(false);
			break;
		case RED_BENCH_4:
			bench = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				bench.setDamageMarks(pokemon.getDamageMarks());
				bench.setHitPoints(pokemon.getHitpoints());
				bench.setConditionList(pokemon.getConditions());
				bench.setEnergyList(p.getEnergy());
				bench.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				bench.setTopCardID(pokemon.getCardId());
				bench.setVisible(true);
			} else
				bench.setVisible(false);
			break;
		case RED_BENCH_5:
			bench = (ArenaGeometry2D) n;
			if (p.size() > 0) {
				PokemonCard pokemon = (PokemonCard) p.getTopCard();
				bench.setDamageMarks(pokemon.getDamageMarks());
				bench.setHitPoints(pokemon.getHitpoints());
				bench.setConditionList(pokemon.getConditions());
				bench.setEnergyList(p.getEnergy());
				bench.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
				bench.setTopCardID(pokemon.getCardId());
				bench.setVisible(true);
			} else
				bench.setVisible(false);
			break;
		case RED_DECK:
			deck = (ImageCounter2D) n;
			if (p.size() > 0) {
				deck.setCounter(p.size());
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					deck.setTexture(Database.getTextureKey(c.getCardId()));
				else
					deck.setTexture(Database.getTextureKey("00000"));
				List<String> cardIds = new ArrayList<>();
				for (int i = 0; i < p.getCards().size(); i++)
					cardIds.add(p.getCards().get(i).getCardId());
				deck.setCardIds(cardIds);
				deck.setVisible(true);
			} else
				deck.setVisible(false);
			break;
		case RED_DISCARDPILE:
			discardPile = (ImageCounter2D) n;
			if (p.size() > 0) {
				discardPile.setCounter(p.size());
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					discardPile.setTexture(Database.getTextureKey(c.getCardId()));
				else
					discardPile.setTexture(Database.getTextureKey("00000"));
				List<String> cardIds = new ArrayList<>();
				for (int i = 0; i < p.getCards().size(); i++)
					cardIds.add(p.getCards().get(i).getCardId());
				discardPile.setCardIds(cardIds);
				discardPile.setVisible(true);
			} else
				discardPile.setVisible(false);
			break;
		case RED_HAND:
			hand = (HandCardManager2D) n;
			if (p.size() > 0) {
				hand.setCards(p.getCards());
				hand.setVisible(true);
			} else {
				hand.setCards(new ArrayList<Card>());
				hand.setVisible(false);
			}
			break;
		case RED_PRICE_1:
			price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		case RED_PRICE_2:
			price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		case RED_PRICE_3:
			price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		case RED_PRICE_4:
			price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		case RED_PRICE_5:
			price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		case RED_PRICE_6:
			price = (Image2D) n;
			if (p.size() > 0) {
				Card c = p.getTopCard();
				if (c.getGameID() != -1)
					price.setTexture(Database.getTextureKey(c.getCardId()));
				else
					price.setTexture(Database.getTextureKey("00000"));
				price.setCardId(c.getCardId());
				price.setVisible(true);
			} else
				price.setVisible(false);
			break;
		default:
			break;
		}
		return n;
	}

	public void updateStadium(Position p) {
		if (p.isEmpty()) {
			GUI2D.getInstance().enqueue(new Callable<Spatial>() {
				@Override
				public Spatial call() throws Exception {
					GUI2D.getInstance().getRootNode().detachChild(stadiumNode);
					return null;
				}
			});
		} else {
			GUI2D.getInstance().enqueue(new Callable<Spatial>() {
				@Override
				public Spatial call() throws Exception {
					Card stadium = p.getTopCard();
					stadiumMat.setTexture("ColorMap", GUI2D.getInstance().getAssetManager().loadTexture(Database.getPokemonThumbnailKey(stadium.getCardId())));
					GUI2D.getInstance().getRootNode().attachChild(stadiumNode);
					return null;
				}
			});
		}
	}

	public List<SelectableNode> getSelectableNodes() {
		List<SelectableNode> list = new ArrayList<SelectableNode>();
		list.add(this.ownActive);
		list.add(this.ownDeck);
		list.add(this.ownGraveyard);
		list.add(this.ownHand);
		for (int i = 0; i < this.ownHand.getHandCard2Ds().size(); i++) {
			HandCard2D handCard = this.ownHand.getHandCard(i);
			list.add(handCard);
		}
		for (int i = 0; i < 5; i++) {
			list.add(this.ownBench.get(i));
			list.add(this.enemyBench.get(i));
		}
		for (int i = 0; i < 6; i++) {
			list.add(this.ownPrize.get(i));
			list.add(this.enemyPrize.get(i));
		}
		list.add(this.ownActive);
		list.add(this.ownDeck);
		list.add(this.ownGraveyard);
		list.add(this.enemyActive);
		list.add(this.enemyDeck);
		list.add(this.enemyGraveyard);
		list.add(this.enemyHand);
		for (int i = 0; i < this.enemyHand.getHandCard2Ds().size(); i++) {
			HandCard2D handCard = this.enemyHand.getHandCard(i);
			list.add(handCard);
		}
		return list;
	}

	public SelectableNode getCurrentlySelected() {
		return this.currentlySelected;
	}

	public boolean isPositionSelectionMode() {
		return positionSelectionMode;
	}

	public void setPositionSelectionMode(boolean positionSelectionMode) {
		this.positionSelectionMode = positionSelectionMode;
	}

	public void hide() {
		for (int i = 0; i < ownHand.getHandCard2Ds().size(); i++) {
			HandCard2D handCard = ownHand.getHandCard2Ds().get(i);
			handCard.setVisible(false);
			dropInUpdateQueue(handCard);
		}
		for (int i = 0; i < enemyHand.getHandCard2Ds().size(); i++) {
			HandCard2D handCard = enemyHand.getHandCard2Ds().get(i);
			handCard.setVisible(false);
			dropInUpdateQueue(handCard);
		}

		ownActive.setVisible(false);
		dropInUpdateQueue(ownActive);

		enemyActive.setVisible(false);
		dropInUpdateQueue(enemyActive);

		for (int i = 0; i < 5; i++) {
			ArenaGeometry2D ownBenchPos = ownBench.get(i);
			ownBenchPos.setVisible(false);
			dropInUpdateQueue(ownBenchPos);

			ArenaGeometry2D enemyBenchPos = enemyBench.get(i);
			enemyBenchPos.setVisible(false);
			dropInUpdateQueue(enemyBenchPos);
		}

		ownDeck.setVisible(false);
		dropInUpdateQueue(ownDeck);

		enemyDeck.setVisible(false);
		dropInUpdateQueue(enemyDeck);

		ownGraveyard.setVisible(false);
		dropInUpdateQueue(ownGraveyard);

		enemyGraveyard.setVisible(false);
		dropInUpdateQueue(enemyGraveyard);

		for (int i = 0; i < 6; i++) {
			Image2D ownPriceImage = ownPrize.get(i);
			ownPriceImage.setVisible(false);
			dropInUpdateQueue(ownPriceImage);

			Image2D enemyPriceImage = enemyPrize.get(i);
			enemyPriceImage.setVisible(false);
			dropInUpdateQueue(enemyPriceImage);
		}

		GUI2D.getInstance().enqueue(new Callable<Spatial>() {
			@Override
			public Spatial call() throws Exception {
				GUI2D.getInstance().getRootNode().detachChild(stadiumNode);
				return null;
			}
		});

		endTurnButton.setVisible(false);
		dropInUpdateQueue(endTurnButton);

		backButton.setVisible(false);
		dropInUpdateQueue(backButton);

		pokePowerButton.setVisible(false);
		dropInUpdateQueue(pokePowerButton);

		attackButton.setVisible(false);
		dropInUpdateQueue(attackButton);

		attack1Button.setVisible(false);
		dropInUpdateQueue(attack1Button);

		attack2Button.setVisible(false);
		dropInUpdateQueue(attack2Button);

		attack2Button.setVisible(false);
		dropInUpdateQueue(attack3Button);

		retreatButton.setVisible(false);
		dropInUpdateQueue(retreatButton);

		playButton.setVisible(false);
		dropInUpdateQueue(playButton);

		pokePower1Button.setVisible(false);
		dropInUpdateQueue(pokePower1Button);

		surrenderButton.setVisible(false);
		dropInUpdateQueue(surrenderButton);

		player_avatar.setVisible(false);
		dropInUpdateQueue(player_avatar);

		opponent_avatar.setVisible(false);
		dropInUpdateQueue(opponent_avatar);
	}

	public CardChooseWindow getCardChooseWindow() {
		return this.cardChooseWindow;
	}

	public ElementChooseWindow getElementChooseWindow() {
		return elementChooseWindow;
	}

	public AttackChooseWindow getAttackChooseWindow() {
		return attackChooseWindow;
	}

	public QuestionChooseWindow getQuestionChooseWindow() {
		return questionChooseWindow;
	}

	public DistributionChooser getDistributionChooser() {
		return distributionChooser;
	}

	public CardViewer getCardViewer() {
		return this.cardViewer;
	}

	public TextPanel2D getMessagePanel() {
		return messagePanel;
	}

	public CardPanel2D getCardMessagePanel() {
		return cardMessagePanel;
	}

	@Override
	public void restart() {
		// nothing to do here
	}

	public FileChooseWindow getFileChooseWindow() {
		return fileChooseWindow;
	}

	public FileNameChooseWindow getFileNameChooseWindow() {
		return fileNameChooseWindow;
	}

	public HandCardManager2D getOwnHand() {
		return ownHand;
	}

	public HandCardManager2D getEnemyHand() {
		return enemyHand;
	}

	@Override
	public MusicType getAmbientMusic() {
		return MusicType.INGAME_MUSIC;
	}

	public SelectableNode getStadiumButton() {
		return this.stadiumButton;
	}

	public void updateOpponentAvatar(TextureKey characterThumb) {
		this.opponent_avatar.setTexture(characterThumb);
	}

	public void updatePlayerAvatar(TextureKey characterThumb) {
		this.player_avatar.setTexture(characterThumb);
	}

	public void showAvatars() {
		this.player_avatar.setVisible(true);
		this.opponent_avatar.setVisible(true);
		dropInUpdateQueue(this.player_avatar);
		dropInUpdateQueue(this.opponent_avatar);
	}
}
