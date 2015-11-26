package gui2d.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import model.database.Card;
import model.database.Database;
import model.database.Deck;
import model.database.EnergyCard;
import model.database.PokemonCard;
import model.database.TrainerCard;
import model.enums.CardType;
import model.enums.Edition;
import model.enums.Element;
import model.game.GameParameters;
import network.client.Account;
import gui2d.GUI2D;
import gui2d.GUI2DMode;
import gui2d.abstracts.SelectableNode;
import gui2d.controller.MusicController.MusicType;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.Image2D;
import gui2d.geometries.chooser.CardViewer;

import com.jme3.scene.Node;

public class DeckEditController extends Node implements GUI2DController {

	private TextButton2D backButton, pageLeftButton, pageRightButton, saveDeckButton, loadDeckButton, clearDeckButton;
	private List<Image2D> filterButtons;
	private List<Image2D> editionFilterButtons;
	/** Resolution variable */
	private int screenWidth, screenHeight;
	private int currentStartIndex;
	private Account account;
	private List<String> fullLibraryCards, shownLibraryCards, deckCards;
	private List<Image2D> deckImages;
	private List<Image2D> libraryImages;

	public DeckEditController() {
		currentStartIndex = 0;
		screenWidth = GUI2D.getInstance().getResolution().getKey();
		screenHeight = GUI2D.getInstance().getResolution().getValue();
	}

	public void initSceneGraph() {
		float buttonWidth = screenWidth * 0.15f;
		float buttonHeight = buttonWidth / 4;

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

		pageLeftButton = new TextButton2D("pageLeftButton", "<<", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				pageLeftButtonClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		pageLeftButton.setLocalTranslation(screenWidth * 0.125f - buttonWidth / 2, screenHeight * 0.12f, 0);
		pageLeftButton.setVisible(false);
		dropInUpdateQueue(pageLeftButton);
		this.attachChild(pageLeftButton);

		pageRightButton = new TextButton2D("pageRightButton", ">>", buttonWidth, buttonHeight) {

			@Override
			public void mouseSelect() {
				pageRightButtonClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		pageRightButton.setLocalTranslation(screenWidth * 0.575f - buttonWidth / 2, screenHeight * 0.12f, 0);
		pageRightButton.setVisible(false);
		dropInUpdateQueue(pageRightButton);
		this.attachChild(pageRightButton);

		float elementButtonWidth = screenWidth * 0.03f;
		filterButtons = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			String texture = "";
			switch (i) {
			case 0:
				texture = Element.COLORLESS.toString();
				break;
			case 1:
				texture = Element.FIRE.toString();
				break;
			case 2:
				texture = Element.GRASS.toString();
				break;
			case 3:
				texture = Element.LIGHTNING.toString();
				break;
			case 4:
				texture = Element.PSYCHIC.toString();
				break;
			case 5:
				texture = Element.ROCK.toString();
				break;
			case 6:
				texture = Element.WATER.toString();
				break;
			case 7:
				texture = "Trainer";
				break;
			case 8:
				texture = "Energy";
				break;
			}
			final int index = i;
			Image2D filterButton = new Image2D("filterButton" + i, Database.getAssetKey(texture), elementButtonWidth, elementButtonWidth) {

				@Override
				public void mouseSelect() {
					filterButtonClicked(index);
				}

				@Override
				public void mouseSelectRightClick() {
					// nothing to do here
				}
			};
			filterButton.setLocalTranslation(screenWidth * 0.29f - buttonWidth / 2 + elementButtonWidth * i, screenHeight * 0.12f, 0);
			filterButton.setVisible(false);
			dropInUpdateQueue(filterButton);
			this.attachChild(filterButton);
			this.filterButtons.add(filterButton);
		}

		editionFilterButtons = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			String texture = "";
			switch (i) {
			case 0:
				texture = Edition.BASE.toString();
				break;
			case 1:
				texture = Edition.JUNGLE.toString();
				break;
			case 2:
				texture = Edition.FOSSIL.toString();
				break;
			case 3:
				texture = Edition.ROCKET.toString();
				break;
			}
			final int index = i;
			Image2D filterButton = new Image2D("editionFilterButtons" + i, Database.getAssetKey(texture), elementButtonWidth, elementButtonWidth) {

				@Override
				public void mouseSelect() {
					elementFilterButtonClicked(index);
				}

				@Override
				public void mouseSelectRightClick() {
					// nothing to do here
				}
			};
			filterButton.setLocalTranslation(screenWidth * 0.01f, screenHeight * 0.82f - elementButtonWidth * i, 0);
			filterButton.setVisible(false);
			dropInUpdateQueue(filterButton);
			this.attachChild(filterButton);
			this.editionFilterButtons.add(filterButton);
		}

		saveDeckButton = new TextButton2D("saveDeckButton", "Save", buttonWidth / 2, buttonHeight / 2) {

			@Override
			public void mouseSelect() {
				saveDeckButtonClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		saveDeckButton.setLocalTranslation(screenWidth * 0.775f - buttonWidth / 2, screenHeight * 0.93f, 0);
		saveDeckButton.setVisible(false);
		dropInUpdateQueue(saveDeckButton);
		this.attachChild(saveDeckButton);

		loadDeckButton = new TextButton2D("loadDeckButton", "Load", buttonWidth / 2, buttonHeight / 2) {

			@Override
			public void mouseSelect() {
				loadDeckButtonClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		loadDeckButton.setLocalTranslation(screenWidth * 0.87f - buttonWidth / 2, screenHeight * 0.93f, 0);
		loadDeckButton.setVisible(false);
		dropInUpdateQueue(loadDeckButton);
		this.attachChild(loadDeckButton);

		clearDeckButton = new TextButton2D("clearDeckButton", "Clear", buttonWidth / 2, buttonHeight / 2) {

			@Override
			public void mouseSelect() {
				clearDeckButtonClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		clearDeckButton.setLocalTranslation(screenWidth * 0.965f - buttonWidth / 2, screenHeight * 0.93f, 0);
		clearDeckButton.setVisible(false);
		dropInUpdateQueue(clearDeckButton);
		this.attachChild(clearDeckButton);

		float deckImageWidth = screenWidth * 0.04f;
		float deckImageHeight = deckImageWidth * 1.141f;
		float deckImageBorder = screenWidth * 0.005f;
		this.deckImages = new ArrayList<>();
		for (int i = 0; i < GameParameters.DECK_SIZE; i++) {
			final int h = i;
			Image2D deckImage = new Image2D("DeckImage!" + i, Database.getCardThumbnailKey("00000"), deckImageWidth, deckImageHeight) {

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
							viewer.setData("Cards", cardList);
							GUI2D.getInstance().addToUpdateQueue(viewer);
						}
					}).start();
				}
			};
			deckImage.setLocalTranslation(screenWidth * 0.70f + (deckImageWidth + deckImageBorder) * (i % 6), screenHeight * 0.85f
					- (deckImageHeight + deckImageBorder) * (i / 6), 0);
			deckImage.setVisible(false);
			dropInUpdateQueue(deckImage);
			this.attachChild(deckImage);
			this.deckImages.add(deckImage);
		}

		float libraryImageWidth = screenWidth * 0.08f;
		float libraryImageHeight = libraryImageWidth * 1.141f;
		float libraryImageBorder = screenWidth * 0.05f;
		this.libraryImages = new ArrayList<>();
		for (int i = 0; i < GameParameters.DECK_EDITOR_LIBRARY_SIZE; i++) {
			final int h = i;
			Image2D libraryImage = new Image2D("LibraryImage!" + i, Database.getCardThumbnailKey("00000"), libraryImageWidth, libraryImageHeight) {

				@Override
				public void mouseSelect() {
					libraryImageSelected(h);
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
			libraryImage.setLocalTranslation(screenWidth * 0.05f + (libraryImageWidth + libraryImageBorder) * (i % 5), screenHeight * 0.7f
					- (libraryImageHeight + libraryImageBorder) * (i / 5), 0);
			libraryImage.setVisible(false);
			dropInUpdateQueue(libraryImage);
			this.attachChild(libraryImage);
			this.libraryImages.add(libraryImage);
		}
	}

	protected void elementFilterButtonClicked(int index) {
		Image2D button = this.editionFilterButtons.get(index);
		button.setSelected(!button.isSelected());
		dropInUpdateQueue(button);
		filterLibrary();
	}

	protected void filterButtonClicked(int index) {
		Image2D button = this.filterButtons.get(index);
		button.setSelected(!button.isSelected());
		dropInUpdateQueue(button);
		filterLibrary();
	}

	/**
	 * Filters the library.
	 */
	private void filterLibrary() {
		this.shownLibraryCards.clear();

		// Collect selected buttons:
		List<Integer> selectedButtonIndices = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			Image2D button = this.filterButtons.get(i);
			if (button.isSelected())
				selectedButtonIndices.add(i);
		}

		for (int i = 0; i < 3; i++) {
			Image2D button = this.editionFilterButtons.get(i);
			if (button.isSelected())
				selectedButtonIndices.add(i + 9);
		}

		if (selectedButtonIndices.isEmpty())
			for (int i = 0; i < this.fullLibraryCards.size(); i++)
				this.shownLibraryCards.add(this.fullLibraryCards.get(i));
		else {
			// Collect elements:
			List<Element> selectedButtonElements = new ArrayList<>();
			List<Edition> selectedButtonEditions = new ArrayList<>();
			boolean trainerChecked = false;
			boolean energyChecked = false;
			for (int i = 0; i < selectedButtonIndices.size(); i++) {
				Integer index = selectedButtonIndices.get(i);
				switch (index) {
				case 0:
					selectedButtonElements.add(Element.COLORLESS);
					break;
				case 1:
					selectedButtonElements.add(Element.FIRE);
					break;
				case 2:
					selectedButtonElements.add(Element.GRASS);
					break;
				case 3:
					selectedButtonElements.add(Element.LIGHTNING);
					break;
				case 4:
					selectedButtonElements.add(Element.PSYCHIC);
					break;
				case 5:
					selectedButtonElements.add(Element.ROCK);
					break;
				case 6:
					selectedButtonElements.add(Element.WATER);
					break;
				case 7:
					trainerChecked = true;
					break;
				case 8:
					energyChecked = true;
					break;
				case 9:
					selectedButtonEditions.add(Edition.BASE);
					break;
				case 10:
					selectedButtonEditions.add(Edition.JUNGLE);
					break;
				case 11:
					selectedButtonEditions.add(Edition.FOSSIL);
					break;
				}
			}

			// Filter all cards now:
			for (String id : this.fullLibraryCards) {
				Card c = Database.createCard(id);
				if (c instanceof PokemonCard) {
					PokemonCard p = (PokemonCard) c;
					if (((selectedButtonElements.isEmpty() && !trainerChecked && !energyChecked) || selectedButtonElements.contains(p.getElement()))
							&& (selectedButtonEditions.contains(c.getEdition()) || selectedButtonEditions.isEmpty()))
						this.shownLibraryCards.add(id);
				} else if (c instanceof TrainerCard && (trainerChecked || (selectedButtonElements.isEmpty() && !energyChecked))
						&& (selectedButtonEditions.contains(c.getEdition()) || selectedButtonEditions.isEmpty()))
					this.shownLibraryCards.add(id);
				else if (c instanceof EnergyCard && (energyChecked || (selectedButtonElements.isEmpty() && !trainerChecked))
						&& (selectedButtonEditions.contains(c.getEdition()) || selectedButtonEditions.isEmpty()))
					this.shownLibraryCards.add(id);
			}
		}

		this.currentStartIndex = 0;
		this.updateLibraryImages();
		this.updateButtons();
	}

	protected void clearDeckButtonClicked() {
		this.clearDeck();
		// Visual update:
		this.updateDeckImages();
		this.updateLibraryImages();
		this.updateButtons();
	}

	protected void loadDeckButtonClicked() {
		Deck loadedDeck = Deck.loadDeckDialog();
		if (loadedDeck != null && this.checkDeckForCorrectness(loadedDeck.getCards())) {
			this.clearDeck();
			this.createDeckFromLibrary(loadedDeck);
			this.account.setDeck(loadedDeck);
			Account.saveAccount(this.account);
		} else if (loadedDeck != null && !this.checkDeckForCorrectness(loadedDeck.getCards()))
			JOptionPane.showMessageDialog(null, "Failed on loading the deck - File is not a valid deck!", "Error", JOptionPane.ERROR_MESSAGE);
	}

	protected void saveDeckButtonClicked() {
		String deckName = JOptionPane.showInputDialog("Deck Name:");
		if (deckName != null) {
			if (!deckName.equals("")) {
				Deck d = new Deck();
				d.setCards(deckCards);
				d.setName(deckName);
				d.saveDeck(GameParameters.DECK_PATH);
				this.account.setDeck(d);
				Account.saveAccount(this.account);
				JOptionPane.showMessageDialog(null, "Saved deck under name " + deckName + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
			} else
				JOptionPane.showMessageDialog(null, "Deck name is not valid!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void pageRightButtonClicked() {
		currentStartIndex = currentStartIndex + 15;
		this.updateLibraryImages();
		this.updateButtons();
	}

	protected void pageLeftButtonClicked() {
		currentStartIndex = currentStartIndex - 15;
		this.updateLibraryImages();
		this.updateButtons();
	}

	protected void libraryImageSelected(int h) {
		this.moveCardFromLibraryToDeck(currentStartIndex + h);
	}

	protected void deckImageSelected(int h) {
		this.moveCardFromDeckToLibrary(h);
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

	protected void backButtonClicked() {
		GUI2D.getInstance().switchMode(GUI2DMode.LOBBY);
		GUI2D.getInstance().getMusicController().switchMusic(MusicType.LOBBY_MUSIC);
	}

	protected void deckEditorButtonClicked() {
		GUI2D.getInstance().switchMode(GUI2DMode.DECK_EDIT);
	}

	/**
	 * Moves the given card from the library into the deck.
	 * 
	 * @param index
	 *            index of the card in the libraryCard list.
	 */
	private void moveCardFromLibraryToDeck(int index) {
		String cardID = this.shownLibraryCards.get(index);

		// Only transfer if deck is not full and card is contained no more than 4 times
		if (this.deckCards.size() < 60 && checkCardCanBeAddedToDeck(cardID)) {
			if (index >= this.shownLibraryCards.size())
				System.err.println("Error: Index too big in moveCardFromLibraryToDeck: " + index);

			this.deckCards.add(cardID);

			// Visual update:
			this.updateDeckImages();
			this.updateLibraryImages();
			this.updateButtons();
		}
	}

	private boolean checkCardCanBeAddedToDeck(String cardId) {
		Card c = Database.createCard(cardId);

		// No restriction on basic energy cards:
		if (c instanceof EnergyCard && ((EnergyCard) c).isBasisEnergy())
			return true;

		int counter = 0;
		for (String id : this.deckCards)
			if (id.equals(cardId))
				counter++;
		if (counter >= 4)
			return false;
		return true;
	}

	/**
	 * Moves all cards of the given deck from the library into the deck. Only call, if deckCards is empty and the size of the given deck is <= 60.
	 * 
	 */
	private void createDeckFromLibrary(Deck deck) {
		if (deck.size() != GameParameters.DECK_SIZE)
			JOptionPane.showMessageDialog(null, "Deck file is not valid!", "Error", JOptionPane.ERROR_MESSAGE);
		else {
			for (int i = 0; i < deck.size(); i++) {
				String card = deck.get(i);
				this.deckCards.add(card);
			}

			// Visual update:
			this.updateDeckImages();
			this.updateLibraryImages();
			this.updateButtons();
		}
	}

	/**
	 * Moves the given card from the deck into the library.
	 * 
	 * @param index
	 *            index of the card in the deckCard list.
	 */
	private void moveCardFromDeckToLibrary(int index) {
		if (index >= this.deckCards.size())
			System.err.println("Error: Index too big in moveCardFromDeckToLibrary: " + index);

		this.deckCards.remove(index);

		// Visual update:
		this.updateDeckImages();
		this.updateLibraryImages();
		this.updateButtons();
	}

	/**
	 * Moves all cards from the deck into the library. Does NOT update the visualization!
	 * 
	 */
	private void clearDeck() {
		int size = deckCards.size();
		for (int i = 0; i < size; i++)
			this.deckCards.remove(0);
	}

	/**
	 * Updates the deck images and drops them into the render queue.
	 */
	private void updateDeckImages() {
		this.sortCardIdList(deckCards);
		for (int i = 0; i < this.deckImages.size(); i++) {
			Image2D deckImage = this.deckImages.get(i);
			if (i < this.deckCards.size()) {
				deckImage.setCardId(this.deckCards.get(i));
				deckImage.setVisible(true);
				deckImage.setTexture(Database.getCardThumbnailKey(this.deckCards.get(i)));
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						GUI2D.getInstance().getIOController().addShootable(deckImage);
						GUI2D.getInstance().getIOController().addRightShootable(deckImage);
					}
				});
				t.start();
			} else {
				deckImage.setVisible(false);
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						GUI2D.getInstance().getIOController().removeShootable(deckImage);
						GUI2D.getInstance().getIOController().removeRightShootable(deckImage);
					}
				});
				t.start();
			}
			dropInUpdateQueue(deckImage);
		}
	}

	/**
	 * Updates the library images and drops them into the render queue.
	 */
	private void updateLibraryImages() {
		this.sortCardIdList(shownLibraryCards);
		for (int i = 0; i < GameParameters.DECK_EDITOR_LIBRARY_SIZE; i++) {
			Image2D libraryImage = this.libraryImages.get(i);

			if (this.currentStartIndex + i < this.shownLibraryCards.size()) {
				libraryImage.setCardId(this.shownLibraryCards.get(this.currentStartIndex + i));
				libraryImage.setTexture(Database.getCardThumbnailKey(this.shownLibraryCards.get(this.currentStartIndex + i)));
				libraryImage.setVisible(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						GUI2D.getInstance().getIOController().addShootable(libraryImage);
						GUI2D.getInstance().getIOController().addRightShootable(libraryImage);
					}
				}).start();
			} else {
				libraryImage.setVisible(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						GUI2D.getInstance().getIOController().removeShootable(libraryImage);
						GUI2D.getInstance().getIOController().removeRightShootable(libraryImage);
					}
				}).start();
			}
			dropInUpdateQueue(libraryImage);
		}
	}

	private List<String> sortCardIdList(List<String> list) {
		list.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				Card c1 = Database.createCard(o1);
				Card c2 = Database.createCard(o2);
				if (c1 instanceof PokemonCard && !(c2 instanceof PokemonCard))
					return -1;
				if (!(c1 instanceof PokemonCard) && c2 instanceof PokemonCard)
					return 1;
				if (c1 instanceof TrainerCard && !(c2 instanceof TrainerCard))
					return -1;
				if (!(c1 instanceof TrainerCard) && c2 instanceof TrainerCard)
					return 1;
				if (c1 instanceof PokemonCard && c2 instanceof PokemonCard) {
					Element e1 = ((PokemonCard) c1).getElement();
					Element e2 = ((PokemonCard) c2).getElement();
					int result = Element.compareElements(e1, e2);
					if (result != 0)
						return result;
				}

				int i = Integer.parseInt(o1);
				int j = Integer.parseInt(o2);
				return i < j ? -1 : (i > j) ? 1 : 0;
			}
		});
		return list;
	}

	/**
	 * Updates the visibility of the buttons.
	 */
	private void updateButtons() {
		if (this.currentStartIndex == 0)
			this.pageLeftButton.setVisible(false);
		else
			this.pageLeftButton.setVisible(true);
		this.dropInUpdateQueue(pageLeftButton);

		if (this.currentStartIndex + 15 >= this.shownLibraryCards.size())
			this.pageRightButton.setVisible(false);
		else
			this.pageRightButton.setVisible(true);
		this.dropInUpdateQueue(pageRightButton);

		if (checkDeckForCorrectness(this.deckCards))
			this.saveDeckButton.setVisible(true);
		else
			this.saveDeckButton.setVisible(false);
		this.dropInUpdateQueue(saveDeckButton);
	}

	private boolean checkDeckForCorrectness(List<String> deck) {
		// Exactly 60 cards in the deck
		if (deck.size() != 60)
			return false;

		List<Card> cardList = new ArrayList<>();
		// At least one basic pokemon in the deck:
		boolean basicPkmnFound = false;
		for (int i = 0; i < deck.size(); i++) {
			Card c = Database.createCard(deck.get(i));
			cardList.add(c);
			if (c.getCardType() == CardType.BASICPOKEMON)
				basicPkmnFound = true;
		}
		if (!basicPkmnFound)
			return false;

		// No card contained more than 4 times(except for basic energy cards):
		Map<String, Integer> cardMap = new HashMap<>();
		for (int i = 0; i < deck.size(); i++) {
			String id = deck.get(i);
			if (cardMap.containsKey(id))
				cardMap.replace(id, cardMap.get(id) + 1);
			else
				cardMap.put(id, 1);
			if (cardMap.get(id) > 4) {
				if (cardList.get(i) instanceof EnergyCard && !((EnergyCard) cardList.get(i)).isBasisEnergy())
					return false;
				if (!(cardList.get(i) instanceof EnergyCard))
					return false;
			}
		}

		return true;
	}

	/**
	 * (Re-)Starts this node by setting the two main buttons visible and setting the rest invisible.
	 */
	public void restart() {
		this.currentStartIndex = 0;
		this.backButton.setVisible(true);
		this.dropInUpdateQueue(backButton);

		this.pageLeftButton.setVisible(true);
		this.dropInUpdateQueue(pageLeftButton);
		this.pageRightButton.setVisible(true);
		this.dropInUpdateQueue(pageRightButton);

		this.saveDeckButton.setVisible(true);
		this.dropInUpdateQueue(saveDeckButton);
		this.loadDeckButton.setVisible(true);
		this.dropInUpdateQueue(loadDeckButton);
		this.clearDeckButton.setVisible(true);
		this.dropInUpdateQueue(clearDeckButton);

		for (int i = 0; i < GameParameters.DECK_SIZE; i++) {
			Image2D deckImage = this.deckImages.get(i);
			deckImage.setVisible(true);
		}
		updateDeckImages();

		for (int i = 0; i < GameParameters.DECK_EDITOR_LIBRARY_SIZE; i++) {
			Image2D libraryImage = this.libraryImages.get(i);
			libraryImage.setVisible(true);
		}
		for (int i = 0; i < this.filterButtons.size(); i++) {
			Image2D button = this.filterButtons.get(i);
			button.setVisible(true);
			this.filterButtons.get(i).setSelected(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().addShootable(button);
				}
			}).start();
			dropInUpdateQueue(this.filterButtons.get(i));
		}

		for (int i = 0; i < this.editionFilterButtons.size(); i++) {
			Image2D button = this.editionFilterButtons.get(i);
			button.setVisible(true);
			this.editionFilterButtons.get(i).setSelected(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().addShootable(button);
				}
			}).start();
			dropInUpdateQueue(this.editionFilterButtons.get(i));
		}

		this.filterLibrary(); // updateLibraryImages(); updateButtons();
	}

	/**
	 * Hides all buttons.
	 */
	public void hide() {
		this.backButton.setVisible(false);
		this.dropInUpdateQueue(backButton);

		this.pageLeftButton.setVisible(false);
		this.dropInUpdateQueue(pageLeftButton);
		this.pageRightButton.setVisible(false);
		this.dropInUpdateQueue(pageRightButton);

		this.saveDeckButton.setVisible(false);
		this.dropInUpdateQueue(saveDeckButton);
		this.loadDeckButton.setVisible(false);
		this.dropInUpdateQueue(loadDeckButton);
		this.clearDeckButton.setVisible(false);
		this.dropInUpdateQueue(clearDeckButton);

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

		for (int i = 0; i < GameParameters.DECK_EDITOR_LIBRARY_SIZE; i++) {
			Image2D libraryImage = this.libraryImages.get(i);
			libraryImage.setVisible(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().removeShootable(libraryImage);
					GUI2D.getInstance().getIOController().removeRightShootable(libraryImage);
				}
			}).start();
			dropInUpdateQueue(libraryImage);
		}

		for (int i = 0; i < this.filterButtons.size(); i++) {
			Image2D button = this.filterButtons.get(i);
			button.setVisible(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().removeShootable(button);
				}
			}).start();
			dropInUpdateQueue(this.filterButtons.get(i));
		}

		for (int i = 0; i < this.editionFilterButtons.size(); i++) {
			Image2D button = this.editionFilterButtons.get(i);
			button.setVisible(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					GUI2D.getInstance().getIOController().removeShootable(button);
				}
			}).start();
			dropInUpdateQueue(this.editionFilterButtons.get(i));
		}
	}

	public void setAccount(Account account) {
		this.account = account;

		// Clone deck:
		this.deckCards = cloneStringList(this.account.getDeck().getCards());

		this.fullLibraryCards = Database.getFullCardLibrary().getUniqueCards();
		this.shownLibraryCards = Database.getFullCardLibrary().getUniqueCards();
		this.sortCardIdList(shownLibraryCards);
	}

	private List<String> cloneStringList(List<String> list) {
		List<String> clone = new ArrayList<>();
		for (int i = 0; i < list.size(); i++)
			clone.add(new String(list.get(i).toString()));
		return clone;
	}
}
