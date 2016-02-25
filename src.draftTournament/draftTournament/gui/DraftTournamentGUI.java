package draftTournament.gui;

import java.util.ArrayList;
import java.util.List;

import com.jme3.material.RenderState.BlendMode;
import com.jme3.scene.Node;

import arenaMode.model.ArenaFighterCode;
import gui2d.GUI2D;
import gui2d.GUI2DMode;
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

	protected abstract void basicEnergyChooseImageSelected(int h);

	protected abstract void saveDeckButtonClicked();

	protected abstract void confirmButtonClicked();

	protected abstract void elementImageSelected(int h);

	protected abstract void deckImageSelected(int h);

	protected abstract void cardChooseImageSelected(int h);

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
		this.cardCountPanel.setVisible(false);
		this.dropInUpdateQueue(cardCountPanel);
		this.saveDeckButton.setVisible(false);
		this.dropInUpdateQueue(saveDeckButton);
		this.confirmButton.setVisible(false);
		this.dropInUpdateQueue(confirmButton);
		this.opponentImage.setVisible(false);
		this.dropInUpdateQueue(opponentImage);
		this.exitButton.setVisible(false);
		this.dropInUpdateQueue(exitButton);
		this.deckPanel.setVisible(false);
		this.dropInUpdateQueue(deckPanel);
		this.headPanel.setVisible(false);
		this.dropInUpdateQueue(headPanel);
		this.backButton.setVisible(false);
		this.dropInUpdateQueue(backButton);
		for (int i = 0; i < 3; i++) {
			Image2D cardChooseImage = this.cardChooseImages.get(i);
			cardChooseImage.setVisible(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().removeShootable(cardChooseImage);
					GUI2D.getInstance().getIOController().removeRightShootable(cardChooseImage);
				}
			}).start();
			dropInUpdateQueue(cardChooseImage);
		}

		for (int i = 0; i < 6; i++) {
			Image2D basicEnergyChooseImage = this.basicEnergyChooseImages.get(i);
			basicEnergyChooseImage.setVisible(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().removeShootable(basicEnergyChooseImage);
					GUI2D.getInstance().getIOController().removeRightShootable(basicEnergyChooseImage);
				}
			}).start();
			dropInUpdateQueue(basicEnergyChooseImage);
		}

		for (int i = 0; i < GameParameters.DECK_SIZE; i++) {
			Image2D deckImage = this.deckImages.get(i);
			deckImage.setVisible(false);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().removeShootable(deckImage);
					GUI2D.getInstance().getIOController().removeRightShootable(deckImage);
				}
			});
			t.start();
			dropInUpdateQueue(deckImage);
		}

		for (int i = 0; i < 4; i++) {
			Image2D elementImage = this.elementImages.get(i);
			elementImage.setVisible(false);
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().removeShootable(elementImage);
				}
			});
			t.start();
			dropInUpdateQueue(elementImage);
		}
	}

	@Override
	public void restart() {
		this.cardCountPanel.setVisible(true);
		this.dropInUpdateQueue(cardCountPanel);
		this.saveDeckButton.setVisible(true);
		this.dropInUpdateQueue(saveDeckButton);
		this.confirmButton.setVisible(true);
		this.dropInUpdateQueue(confirmButton);
		this.opponentImage.setVisible(true);
		this.dropInUpdateQueue(opponentImage);
		this.exitButton.setVisible(true);
		this.dropInUpdateQueue(exitButton);
		this.deckPanel.setVisible(true);
		this.dropInUpdateQueue(deckPanel);
		this.headPanel.setVisible(true);
		this.dropInUpdateQueue(headPanel);
		this.backButton.setVisible(true);
		this.dropInUpdateQueue(backButton);
		for (int i = 0; i < 3; i++) {
			Image2D cardChooseImage = this.cardChooseImages.get(i);
			cardChooseImage.setVisible(true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().addShootable(cardChooseImage);
					GUI2D.getInstance().getIOController().addRightShootable(cardChooseImage);
				}
			}).start();
			dropInUpdateQueue(cardChooseImage);
		}

		for (int i = 0; i < 6; i++) {
			Image2D basicEnergyChooseImage = this.basicEnergyChooseImages.get(i);
			basicEnergyChooseImage.setVisible(true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().addShootable(basicEnergyChooseImage);
					GUI2D.getInstance().getIOController().addRightShootable(basicEnergyChooseImage);
				}
			}).start();
			dropInUpdateQueue(basicEnergyChooseImage);
		}

		for (int i = 0; i < GameParameters.DECK_SIZE; i++) {
			Image2D deckImage = this.deckImages.get(i);
			deckImage.setVisible(true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().addShootable(deckImage);
					GUI2D.getInstance().getIOController().addRightShootable(deckImage);
				}
			}).start();
			dropInUpdateQueue(deckImage);
		}

		for (int i = 0; i < 4; i++) {
			Image2D elementImage = this.elementImages.get(i);
			elementImage.setVisible(true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().addShootable(elementImage);
				}
			}).start();
			dropInUpdateQueue(elementImage);
		}
	}

	@Override
	public MusicType getAmbientMusic() {
		return MusicType.DRAFT_TOURNAMENT_MUSIC;
	}

}
