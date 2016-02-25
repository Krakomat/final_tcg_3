package draftTournament.gui;

import java.util.ArrayList;
import java.util.List;

import com.jme3.material.RenderState.BlendMode;
import com.jme3.scene.Node;

import arenaMode.model.ArenaFighterCode;
import gui2d.GUI2D;
import gui2d.abstracts.SelectableNode;
import gui2d.controller.GUI2DController;
import gui2d.controller.MusicController.MusicType;
import gui2d.geometries.Image2D;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.chooser.CardViewer;
import gui2d.geometries.messages.TextPanel2D;
import model.database.Card;
import model.database.Database;
import model.enums.Element;
import model.game.GameParameters;

public abstract class DraftTournamentGUI extends Node implements GUI2DController {
	private int screenWidth, screenHeight;
	protected TextButton2D backButton, confirmButton, saveDeckButton, exitButton;
	protected List<Image2D> basicEnergyChooseImages, cardChooseImages, deckImages, elementImages;
	protected Image2D opponentImage;
	protected TextPanel2D headPanel, cardCountPanel, deckPanel;

	public DraftTournamentGUI() {
		screenWidth = GUI2D.getInstance().getResolution().getKey();
		screenHeight = GUI2D.getInstance().getResolution().getValue();
	}

	public void initSceneGraph() {
		cardChooseImages = new ArrayList<>();
		float chooseImageWidth = screenWidth * 0.13f;
		float chooseImageHeight = chooseImageWidth * 1.141f;
		float chooseImageBorder = screenWidth * 0.02f;
		for (int i = 0; i < 3; i++) {
			final int h = i;
			Image2D cardChooseImage = new Image2D("CardChooseImage(" + i + ")", Database.getTextureKey("00000"), chooseImageWidth, chooseImageHeight, BlendMode.Alpha) {

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
							viewer.setData("", cardList);
							GUI2D.getInstance().addToUpdateQueue(viewer);
						}
					}).start();
				}

				@Override
				public void mouseSelect() {
					cardChooseImageSelected(h);
				}
			};
			cardChooseImage.setCardId("00000");
			cardChooseImage.setLocalTranslation(screenWidth * 0.15f + (chooseImageWidth + chooseImageBorder) * (i % 3),
					screenHeight * 0.6f - (chooseImageHeight + chooseImageBorder) / 2, 0);
			cardChooseImage.setVisible(false);
			dropInUpdateQueue(cardChooseImage);
			this.attachChild(cardChooseImage);
			this.cardChooseImages.add(cardChooseImage);
		}

		basicEnergyChooseImages = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			final int h = i;
			Image2D basicEnergyChooseImage = new Image2D("BasicEnergyChooseImage(" + i + ")", Database.getTextureKey("00000"), chooseImageWidth, chooseImageHeight,
					BlendMode.Alpha) {

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
							viewer.setData("", cardList);
							GUI2D.getInstance().addToUpdateQueue(viewer);
						}
					}).start();
				}

				@Override
				public void mouseSelect() {
					basicEnergyChooseImageSelected(h);
				}
			};
			basicEnergyChooseImage.setCardId("00000");
			basicEnergyChooseImage.setLocalTranslation(screenWidth * 0.15f + (chooseImageWidth + chooseImageBorder) * (i % 3),
					screenHeight * 0.6f - (chooseImageHeight + chooseImageBorder) * (i / 3), 0);
			basicEnergyChooseImage.setVisible(false);
			dropInUpdateQueue(basicEnergyChooseImage);
			this.attachChild(basicEnergyChooseImage);
			this.basicEnergyChooseImages.add(basicEnergyChooseImage);
		}

		deckImages = new ArrayList<>();
		float deckImageWidth = screenWidth * 0.04f;
		float deckImageHeight = deckImageWidth * 1.141f;
		float deckImageBorder = screenWidth * 0.005f;
		this.deckImages = new ArrayList<>();
		for (int i = 0; i < GameParameters.DECK_SIZE; i++) {
			final int h = i;
			Image2D deckImage = new Image2D("DeckImage(" + i + ")", Database.getCardThumbnailKey("00000"), deckImageWidth, deckImageHeight, BlendMode.Alpha) {

				@Override
				public void mouseSelect() {
					deckImageSelected(h);
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
							viewer.setData("", cardList);
							GUI2D.getInstance().addToUpdateQueue(viewer);
						}
					}).start();
				}
			};
			deckImage.setCardId("00000");
			deckImage.setLocalTranslation(screenWidth * 0.70f + (deckImageWidth + deckImageBorder) * (i % 6), screenHeight * 0.85f - (deckImageHeight + deckImageBorder) * (i / 6),
					0);
			deckImage.setVisible(false);
			dropInUpdateQueue(deckImage);
			this.attachChild(deckImage);
			this.deckImages.add(deckImage);
		}

		elementImages = new ArrayList<>();
		float elementImagesWidth = screenWidth * 0.1f;
		float elementImagesHeight = elementImagesWidth;
		float elementImagesBorder = screenWidth * 0.008f;
		for (int i = 0; i < 4; i++) {
			final int h = i;
			Image2D elementImage = new Image2D("ElementImage(" + i + ")", Database.getAssetKey(Element.COLORLESS.toString()), elementImagesWidth, elementImagesHeight,
					BlendMode.Alpha) {

				@Override
				public void mouseSelect() {
					elementImageSelected(h);
				}

				@Override
				public void mouseSelectRightClick() {

				}
			};
			elementImage.setLocalTranslation(screenWidth * 0.15f + (elementImagesWidth + elementImagesBorder) * (i % 6), screenHeight * 0.5f, 0);
			elementImage.setVisible(false);
			elementImage.setCardId("00001");
			dropInUpdateQueue(elementImage);
			this.attachChild(elementImage);
			this.elementImages.add(elementImage);
		}

		float buttonWidth = screenWidth * 0.15f;
		float buttonHeight = buttonWidth / 4;

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

		saveDeckButton = new TextButton2D("saveDeckButton", "S", screenWidth * 0.03f, screenWidth * 0.03f) {

			@Override
			public void mouseSelect() {
				saveDeckButtonClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		saveDeckButton.setLocalTranslation(screenWidth * 0.97f, screenHeight - screenWidth * 0.07f, 0);
		saveDeckButton.setVisible(false);
		dropInUpdateQueue(saveDeckButton);
		this.attachChild(saveDeckButton);

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

		deckPanel = new TextPanel2D("DeckPanel", "Deck", (deckImageWidth + deckImageBorder) * 6 - deckImageBorder, screenWidth * 0.03f) {

			@Override
			public void mouseSelectRightClick() {
			}

			@Override
			public void mouseSelect() {
			}
		};
		deckPanel.setLocalTranslation(screenWidth * 0.7f, screenHeight - screenWidth * 0.03f, 0);
		deckPanel.setVisible(false);
		dropInUpdateQueue(deckPanel);
		this.attachChild(deckPanel);

		headPanel = new TextPanel2D("HeadPanel", "Choose two Elements", (chooseImageWidth + chooseImageBorder) * 3 - chooseImageBorder, screenWidth * 0.03f) {

			@Override
			public void mouseSelectRightClick() {
			}

			@Override
			public void mouseSelect() {
			}
		};
		headPanel.setLocalTranslation(screenWidth * 0.15f, screenHeight * 0.85f + deckImageHeight - screenWidth * 0.03f, 0);
		headPanel.setVisible(false);
		dropInUpdateQueue(headPanel);
		this.attachChild(headPanel);

		cardCountPanel = new TextPanel2D("CardCountPanel", "00/40", (chooseImageWidth + chooseImageBorder) * 3 - chooseImageBorder, screenWidth * 0.03f) {

			@Override
			public void mouseSelectRightClick() {
			}

			@Override
			public void mouseSelect() {
			}
		};
		cardCountPanel.setLocalTranslation(screenWidth * 0.15f, screenHeight * 0.2f, 0);
		cardCountPanel.setVisible(false);
		dropInUpdateQueue(cardCountPanel);
		this.attachChild(cardCountPanel);

		opponentImage = new Image2D("OpponentImage", Database.getAssetKey(ArenaFighterCode.SAFFRONIA_SABRINA.toString()), screenWidth * 0.15f, screenWidth * 0.15f * 2,
				BlendMode.Alpha) {

			@Override
			public void mouseSelect() {

			}

			@Override
			public void mouseSelectRightClick() {

			}
		};
		opponentImage.setLocalTranslation(screenWidth * 0.275f, screenHeight * 0.3f, 0);
		opponentImage.setVisible(false);
		dropInUpdateQueue(opponentImage);
		this.attachChild(opponentImage);

		confirmButton = new TextButton2D("confirmButton", "Confirm", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				confirmButtonClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		confirmButton.setLocalTranslation(screenWidth * 0.275f, screenHeight * 0.2f, 0);
		confirmButton.setVisible(false);
		dropInUpdateQueue(confirmButton);
		this.attachChild(confirmButton);
	}

	protected abstract void backButtonClicked();

	protected abstract void basicEnergyChooseImageSelected(int h);

	protected abstract void saveDeckButtonClicked();

	protected abstract void confirmButtonClicked();

	protected abstract void elementImageSelected(int h);

	protected abstract void deckImageSelected(int h);

	protected abstract void cardChooseImageSelected(int h);

	protected abstract void updateGUI();

	protected void setVisible(SelectableNode node, boolean flag) {
		node.setVisible(flag);
		this.dropInUpdateQueue(node);
	}

	protected void setVisible(List<Image2D> nodes, boolean flag) {
		for (int i = 0; i < nodes.size(); i++) {
			Image2D node = nodes.get(i);
			if (node.getCardId() == null || node.getCardId().equals("00000")) {
				node.setVisible(false);
				flag = false;
			} else
				node.setVisible(flag);
			final boolean visible = flag;
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (!visible) {
						GUI2D.getInstance().getIOController().removeShootable(node);
						GUI2D.getInstance().getIOController().removeRightShootable(node);
					} else {
						GUI2D.getInstance().getIOController().addShootable(node);
						GUI2D.getInstance().getIOController().addRightShootable(node);
					}
				}
			}).start();
			dropInUpdateQueue(node);
		}
	}

	protected void dropInUpdateQueue(SelectableNode node) {
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
	public void hide() {
		setVisible(cardCountPanel, false);
		setVisible(saveDeckButton, false);
		setVisible(confirmButton, false);
		setVisible(opponentImage, false);
		setVisible(exitButton, false);
		setVisible(deckPanel, false);
		setVisible(headPanel, false);
		setVisible(backButton, false);
		setVisible(cardChooseImages, false);
		setVisible(basicEnergyChooseImages, false);
		setVisible(deckImages, false);
		setVisible(elementImages, false);
	}

	@Override
	public void restart() {
		this.updateGUI();
	}

	@Override
	public MusicType getAmbientMusic() {
		return MusicType.DRAFT_TOURNAMENT_MUSIC;
	}

}
