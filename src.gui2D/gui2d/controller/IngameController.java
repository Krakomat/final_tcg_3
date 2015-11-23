package gui2d.controller;

import gui2d.GUI2D;
import gui2d.abstracts.SelectableNode;
import gui2d.geometries.ArenaGeometry2D;
import gui2d.geometries.HandCard2D;
import gui2d.geometries.HandCardManager2D;
import gui2d.geometries.Image2D;
import gui2d.geometries.ImageCounter2D;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.chooser.AttackChooseWindow;
import gui2d.geometries.chooser.CardChooseWindow;
import gui2d.geometries.chooser.CardViewer;
import gui2d.geometries.chooser.DistributionChooser;
import gui2d.geometries.chooser.ElementChooseWindow;
import gui2d.geometries.messages.CardPanel2D;
import gui2d.geometries.messages.TextPanel2D;

import java.util.ArrayList;
import java.util.List;

import model.database.Card;
import model.database.Database;
import model.database.PokemonCard;
import model.enums.Color;
import model.enums.PlayerAction;
import model.enums.PositionID;
import model.interfaces.Position;

import com.jme3.scene.Node;

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
	private TextButton2D endTurnButton, attack1Button, attack2Button, retreatButton, playButton, pokePowerButton;
	/** Resolution variable */
	private int screenWidth, screenHeight;

	/** Currently selected node */
	private SelectableNode currentlySelected;
	/** True if a position has to be selected */
	private boolean positionSelectionMode;

	/* Choose windows: */
	private CardChooseWindow cardChooseWindow;
	private ElementChooseWindow elementChooseWindow;
	private AttackChooseWindow attackChooseWindow;
	private DistributionChooser distributionChooser;

	/* View Window: */
	private CardViewer cardViewer;

	/* Message Panels: */
	private TextPanel2D messagePanel;
	private CardPanel2D cardMessagePanel;

	public IngameController() {
		screenWidth = GUI2D.getInstance().getResolution().getKey();
		screenHeight = GUI2D.getInstance().getResolution().getValue();
		currentlySelected = null;
		positionSelectionMode = false;
	}

	public void initSceneGraph() {
		float handCardWidth = screenWidth * 0.06f; // Size of one single hand card
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
			Image2D ownPriceImage = new Image2D("Prize", Database.getTextureKey("00000"), prizePosWidth, prizePosHeight) {
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

			Image2D enemyPriceImage = new Image2D("Prize", Database.getTextureKey("00000"), prizePosWidth, prizePosHeight) {
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
		float buttonHeight = activePosHeight / 4;
		endTurnButton = new TextButton2D("EndTurnButton", "End Turn", buttonWidth, buttonHeight) {

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

		attack1Button = new TextButton2D("Attack1Button", "Attack1", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				attack1Clicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		attack1Button.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.48f - activePosHeight + buttonHeight * 3, 0);
		attack1Button.setVisible(false);
		dropInUpdateQueue(attack1Button);
		this.attachChild(attack1Button);

		attack2Button = new TextButton2D("Attack2Button", "Attack2", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				attack2Clicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		attack2Button.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.48f - activePosHeight + buttonHeight * 2, 0);
		attack2Button.setVisible(false);
		dropInUpdateQueue(attack2Button);
		this.attachChild(attack2Button);

		retreatButton = new TextButton2D("RetreatButton", "Retreat", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				retreatClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		retreatButton.setLocalTranslation(screenWidth * 0.5f, screenHeight * 0.48f - activePosHeight + buttonHeight, 0);
		retreatButton.setVisible(false);
		dropInUpdateQueue(retreatButton);
		this.attachChild(retreatButton);

		playButton = new TextButton2D("PlayButton", "Play", buttonWidth, buttonHeight) {

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

		pokePowerButton = new TextButton2D("PokemonPowerButton", "Activate", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				pokePowerClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		pokePowerButton.setLocalTranslation(screenWidth * 0.5f - buttonWidth / 2, screenHeight * 0.17f, 0);
		pokePowerButton.setVisible(false);
		dropInUpdateQueue(pokePowerButton);
		this.attachChild(pokePowerButton);

		// Init Choose windows:
		cardChooseWindow = new CardChooseWindow("CardChooseWindow", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f, GUI2D.getInstance()
				.getResolution().getValue() * 0.8f);
		cardChooseWindow.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.15f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(cardChooseWindow);
		cardChooseWindow.setVisible(false);

		elementChooseWindow = new ElementChooseWindow("ElementChooseWindow", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f, GUI2D.getInstance()
				.getResolution().getValue() * 0.8f);
		elementChooseWindow.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.15f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(elementChooseWindow);
		elementChooseWindow.setVisible(false);

		attackChooseWindow = new AttackChooseWindow("AttackChooseWindow", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f, GUI2D.getInstance()
				.getResolution().getValue() * 0.8f);
		attackChooseWindow.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.15f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(attackChooseWindow);
		attackChooseWindow.setVisible(false);

		distributionChooser = new DistributionChooser("DistributionChooseWindow", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f, GUI2D.getInstance()
				.getResolution().getValue() * 0.8f);
		distributionChooser.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.15f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(distributionChooser);
		distributionChooser.setVisible(false);

		cardViewer = new CardViewer("CardViewer", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f,
				GUI2D.getInstance().getResolution().getValue() * 0.8f);
		cardViewer.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.15f, 1);
		GUI2D.getInstance().getGuiNode().attachChild(cardViewer);
		cardViewer.setVisible(false);

		messagePanel = new TextPanel2D("MessagePanel", "Middle", GUI2D.getInstance().getResolution().getKey() * 0.5f,
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
		messagePanel.setLocalTranslation(GUI2D.getInstance().getResolution().getKey() * 0.25f, GUI2D.getInstance().getResolution().getValue() * 0.73f, 0);
		GUI2D.getInstance().getGuiNode().attachChild(messagePanel);
		messagePanel.setVisible(false);

		cardMessagePanel = new CardPanel2D("CardMessaPanel", GUI2D.getInstance().getResolution().getKey() * 0.24f,
				GUI2D.getInstance().getResolution().getValue() * 0.22f) {
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

		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(cardChooseWindow);
				GUI2D.getInstance().addToUpdateQueue(elementChooseWindow);
				GUI2D.getInstance().addToUpdateQueue(attackChooseWindow);
				GUI2D.getInstance().addToUpdateQueue(messagePanel);
				GUI2D.getInstance().addToUpdateQueue(cardMessagePanel);
				GUI2D.getInstance().addToUpdateQueue(distributionChooser);
				GUI2D.getInstance().addToUpdateQueue(cardViewer);
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
				final HandCard2D handGeo = (HandCard2D) currentlySelected; // has to be a handcard!
				GUI2D.getInstance().setEndTurnButtonVisible(false);
				resetGlowingSelected();
				resetButtons();
				GUI2D.getInstance().getPlayer().playHandCard(handGeo.getIndex());
			}
		});
		t.setName("PlayButtonThread");
		t.start();
	}

	/**
	 * Actions, when the pokemonPowerButton is clicked.
	 */
	protected void pokePowerClicked() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				final SelectableNode arenaNode = currentlySelected;
				Color color = GUI2D.getInstance().getPlayer().getColor();
				GUI2D.getInstance().setEndTurnButtonVisible(false);
				resetGlowingSelected();
				resetButtons();
				GUI2D.getInstance().getPlayer().pokemonPower(getPositionIDForArenaGeometry(arenaNode, color));
			}
		});
		t.setName("PokePowerButtonThread");
		t.start();
	}

	protected void retreatClicked() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().setEndTurnButtonVisible(false);
				resetGlowingSelected();
				resetButtons();
				GUI2D.getInstance().getPlayer().retreatPokemon();
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
				GUI2D.getInstance().getPlayer().attack(1);
			}
		});
		t.setName("Attack2ButtonThread");
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
				GUI2D.getInstance().getPlayer().sendEndTurnToServer();
			}
		});
		t.setName("EndTurnButtonThread");
		t.start();
	}

	public void resetGlowingSelected() {
		currentlySelected = null;
		for (SelectableNode node : this.getSelectableNodes()) {
			if (node.isGlowing())
				GUI2D.getInstance().getIOController().removeShootable(node);

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
		GUI2D.getInstance().setButtonVisible(attack1Button, false);
		GUI2D.getInstance().setButtonVisible(attack2Button, false);
		GUI2D.getInstance().setButtonVisible(playButton, false);
		GUI2D.getInstance().setButtonVisible(retreatButton, false);
		GUI2D.getInstance().setButtonVisible(pokePowerButton, false);
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
				List<PlayerAction> actionList = GUI2D.getInstance().getPlayer().getPlayerActionsForHandCard(handGeo.getIndex());
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

	private void makeButtonForActionVisible(PlayerAction action, SelectableNode selectedNode) {
		switch (action) {
		case ATTACK_1:
			Color color = GUI2D.getInstance().getPlayer().getColor();
			List<String> attackNames = GUI2D.getInstance().getPlayer()
					.getAttackNames(color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON);
			attack1Button.setText(attackNames.get(0));
			GUI2D.getInstance().setButtonVisible(attack1Button, true);
			break;
		case ATTACK_2:
			color = GUI2D.getInstance().getPlayer().getColor();
			attackNames = GUI2D.getInstance().getPlayer().getAttackNames(color == Color.BLUE ? PositionID.BLUE_ACTIVEPOKEMON : PositionID.RED_ACTIVEPOKEMON);
			attack2Button.setText(attackNames.get(1));
			GUI2D.getInstance().setButtonVisible(attack2Button, true);
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
			color = GUI2D.getInstance().getPlayer().getColor();
			attackNames = GUI2D.getInstance().getPlayer().getPokePowerNames(this.getPositionIDForArenaGeometry(selectedNode, color));
			pokePowerButton.setText(attackNames.get(0));
			GUI2D.getInstance().setButtonVisible(pokePowerButton, true);
			break;
		case PUT_ON_BENCH:
			playButton.setText("Play");
			GUI2D.getInstance().setButtonVisible(playButton, true);
			break;
		case RETREAT_POKEMON:
			GUI2D.getInstance().setButtonVisible(retreatButton, true);
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
					// Only execute when no positions have to be selected by the user
					Color ownColor = GUI2D.getInstance().getPlayer().getColor();
					List<PlayerAction> actionList = GUI2D.getInstance().getPlayer()
							.getPlayerActionsForArenaPosition(getPositionIDForArenaGeometry(arenaGeo, ownColor));
					resetButtons();
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
					deck.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					discardPile.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					deck.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					discardPile.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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
					price.setTexture(Database.getTextureKey("00000")); // Top Card not visible
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

		endTurnButton.setVisible(false);
		dropInUpdateQueue(endTurnButton);

		attack1Button.setVisible(false);
		dropInUpdateQueue(attack1Button);

		attack2Button.setVisible(false);
		dropInUpdateQueue(attack2Button);

		retreatButton.setVisible(false);
		dropInUpdateQueue(retreatButton);

		playButton.setVisible(false);
		dropInUpdateQueue(playButton);

		pokePowerButton.setVisible(false);
		dropInUpdateQueue(pokePowerButton);
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
}
