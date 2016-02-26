package draftTournament.gui;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import draftTournament.model.DraftTournamentDatabase;
import draftTournament.model.DraftTournamentState;
import gui2d.GUI2D;
import gui2d.GUI2DMode;
import gui2d.geometries.Image2D;
import model.database.Card;
import model.database.Database;
import model.database.Deck;
import model.enums.Element;
import model.enums.Rarity;
import model.game.GameParameters;
import network.client.Account;

public class DraftTournamentController extends DraftTournamentGUI {
	private DraftTournamentState state;
	private DraftTournamentDatabase draftDatabase;
	private Deck deck, normalDeck;
	private Account account;
	private List<Element> chosenElements;

	public DraftTournamentController() {
		state = DraftTournamentState.CHOOSE_2_ELEMENTS;
		draftDatabase = new DraftTournamentDatabase();
		deck = new Deck();
		chosenElements = new ArrayList<>();
		account = null;
		normalDeck = null;
	}

	@Override
	protected void backButtonClicked() {
		switch (state) {
		case CHOOSE_2_ELEMENTS:
			chosenElements.clear();
			deck = new Deck();
			for (int i = 0; i < 60; i++) {
				Image2D image = this.deckImages.get(i);
				image.setTexture(Database.getTextureKey("00000"));
				image.setCardId(null);
			}
			draftDatabase.initOpponents();
			GUI2D.getInstance().switchMode(GUI2DMode.LOBBY, true);
			break;
		case FIGHT_1:
		case FIGHT_2:
		case FIGHT_3:
			if (GUI2D.getInstance().userAnswersQuestion("Do you really want to surrender this Draft Tournament?")) {
				chosenElements.clear();
				deck = new Deck();
				state = DraftTournamentState.CHOOSE_2_ELEMENTS;
				for (int i = 0; i < 60; i++) {
					Image2D image = this.deckImages.get(i);
					image.setTexture(Database.getTextureKey("00000"));
					image.setCardId(null);
				}
				draftDatabase.initOpponents();
				GUI2D.getInstance().switchMode(GUI2DMode.LOBBY, true);
				Preconditions.checkArgument(normalDeck != null);
				this.account.setDeck(normalDeck); // Restore deck
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void basicEnergyChooseImageSelected(int h) {
		if (deck.size() < 60) {
			String cardID = basicEnergyChooseImages.get(h).getCardId();
			this.deck.addCard(cardID);
			int maxIndex = deck.size() - 1;
			deckImages.get(maxIndex).setTexture(Database.getTextureKey(cardID));
			deckImages.get(maxIndex).setCardId(cardID);
			updateGUI();

			if (deck.size() == 60) {
				setVisible(confirmButton, true);
				setVisible(saveDeckButton, true);
			}
		}
	}

	@Override
	protected void saveDeckButtonClicked() {
		String deckName = GUI2D.getInstance().userTypesName("Name", "Type in a name:");
		if (deckName != null) {
			Deck d = new Deck();
			d.setCards(deck.getCards());
			d.setName(deckName);
			d.saveDeck(GameParameters.DECK_PATH);
		}
	}

	@Override
	protected void confirmButtonClicked() {
		switch (state) {
		case CHOOSE_2_ELEMENTS:
			for (Image2D image : this.elementImages)
				if (image.isSelected())
					chosenElements.add(Element.valueOf(image.getCardId()));
			chosenElements.add(Element.COLORLESS);
			this.state = DraftTournamentState.CHOOSE_CARDS;
			draftDatabase.initializeCardSet(chosenElements, true);
			List<Card> randomCards = draftDatabase.getRandomCardList(3, null);
			for (int i = 0; i < randomCards.size(); i++) {
				cardChooseImages.get(i).setTexture(Database.getTextureKey(randomCards.get(i).getCardId()));
				cardChooseImages.get(i).setCardId(randomCards.get(i).getCardId());
				cardChooseImages.get(i).setGlowing(true);
			}
			this.updateGUI();
			break;
		case CHOOSE_BASIC_ENERGY:
			this.account.setDeck(deck); // Set Deck
			state = DraftTournamentState.FIGHT_1;
			updateGUI();
			break;
		case FIGHT_1:
			state = DraftTournamentState.FIGHT_2;
			updateGUI();
			break;
		case FIGHT_2:
			state = DraftTournamentState.FIGHT_3;
			updateGUI();
			break;
		case FIGHT_3:
			state = DraftTournamentState.VICTORY;
			updateGUI();
			break;
		// TODO
		case DEFEAT:
		case VICTORY:
			chosenElements.clear();
			deck = new Deck();
			state = DraftTournamentState.CHOOSE_2_ELEMENTS;
			for (int i = 0; i < 60; i++) {
				Image2D image = this.deckImages.get(i);
				image.setTexture(Database.getTextureKey("00000"));
				image.setCardId(null);
			}
			draftDatabase.initOpponents();
			GUI2D.getInstance().switchMode(GUI2DMode.LOBBY, true);
			Preconditions.checkArgument(normalDeck != null);
			this.account.setDeck(normalDeck); // Restore deck
		default:
			break;
		}
	}

	@Override
	protected void elementImageSelected(int h) {
		Image2D element = this.elementImages.get(h);

		// Count selected elements:
		int selectedElements = 0;
		for (Image2D image : this.elementImages)
			if (image.isSelected())
				selectedElements++;

		if (element.isSelected()) {
			element.setSelected(false);
			selectedElements--;
		} else {
			if (selectedElements < 2) {
				element.setSelected(true);
				selectedElements++;
			}
		}
		this.dropInUpdateQueue(element);

		if (selectedElements == 2)
			setVisible(confirmButton, true);
		else
			setVisible(confirmButton, false);
	}

	@Override
	protected void deckImageSelected(int h) {
		if (state == DraftTournamentState.CHOOSE_BASIC_ENERGY) {
			if (h >= 40) {
				this.deck.removeCard(h);
				for (int i = 40; i < 60; i++) {
					Image2D image = this.deckImages.get(i);
					String id = deck.get(i);
					if (id != null) {
						image.setTexture(Database.getTextureKey(id));
						image.setCardId(id);
					} else {
						image.setTexture(Database.getTextureKey("00000"));
						image.setCardId(null);
					}
				}
				updateGUI();
			}
		}
	}

	@Override
	protected void cardChooseImageSelected(int h) {
		String cardID = cardChooseImages.get(h).getCardId();
		this.deck.addCard(cardID);
		int maxIndex = deck.size() - 1;
		deckImages.get(maxIndex).setTexture(Database.getTextureKey(cardID));
		deckImages.get(maxIndex).setCardId(cardID);

		// Next round:
		if (maxIndex < 39) {
			List<Card> randomCards = draftDatabase.getRandomCardList(3, (maxIndex == 13 || maxIndex == 33) ? Rarity.RARE : null);
			for (int i = 0; i < randomCards.size(); i++) {
				cardChooseImages.get(i).setTexture(Database.getTextureKey(randomCards.get(i).getCardId()));
				cardChooseImages.get(i).setCardId(randomCards.get(i).getCardId());
				cardChooseImages.get(i).setGlowing(true);
			}
		} else {
			for (int i = 0; i < 3; i++) {
				cardChooseImages.get(i).setTexture(Database.getTextureKey("00000"));
				cardChooseImages.get(i).setCardId(null);
			}
			this.state = DraftTournamentState.CHOOSE_BASIC_ENERGY;
		}
		updateGUI();
	}

	@Override
	protected void updateGUI() {
		switch (this.state) {
		case CHOOSE_2_ELEMENTS: // Occurs at start
			setVisible(cardCountPanel, false);
			setVisible(saveDeckButton, false);
			setVisible(confirmButton, false);
			setVisible(opponentImage, false);
			setVisible(exitButton, true);
			setVisible(deckPanel, false);
			headPanel.setText("Choose two Elements");
			setVisible(headPanel, true);
			backButton.setText("Back");
			setVisible(backButton, true);
			setVisible(cardChooseImages, false);
			setVisible(basicEnergyChooseImages, false);
			setVisible(deckImages, false);
			List<Element> randomElements = draftDatabase.getRandomElementList(4);
			for (int i = 0; i < randomElements.size(); i++) {
				elementImages.get(i).setTexture(Database.getAssetKey(randomElements.get(i).toString()));
				elementImages.get(i).setCardId(randomElements.get(i).toString());
			}
			setVisible(elementImages, true);
			break;
		case CHOOSE_CARDS:
			cardCountPanel.setText(deck.size() + "/40");
			setVisible(cardCountPanel, true);
			setVisible(saveDeckButton, false);
			setVisible(deckPanel, true);
			setVisible(cardChooseImages, true);
			setVisible(basicEnergyChooseImages, false);
			setVisible(deckImages, true);
			setVisible(elementImages, false);
			setVisible(confirmButton, false);
			setVisible(opponentImage, false);
			setVisible(exitButton, true);
			headPanel.setText("Choose a card for your deck!");
			setVisible(headPanel, true);
			setVisible(backButton, false);
			break;
		case CHOOSE_BASIC_ENERGY:
			setVisible(cardCountPanel, false);
			setVisible(saveDeckButton, false);
			setVisible(deckPanel, true);
			setVisible(cardChooseImages, false);
			setVisible(basicEnergyChooseImages, true);
			setVisible(deckImages, true);
			setVisible(elementImages, false);
			setVisible(confirmButton, false);
			setVisible(opponentImage, false);
			setVisible(exitButton, true);
			headPanel.setText("Fill up the rest of your deck with Basic Energy Cards!");
			setVisible(headPanel, true);
			setVisible(backButton, false);
			break;
		case DEFEAT:
			setVisible(cardCountPanel, false);
			setVisible(saveDeckButton, true);
			setVisible(deckPanel, true);
			setVisible(cardChooseImages, false);
			setVisible(basicEnergyChooseImages, false);
			setVisible(deckImages, true);
			setVisible(elementImages, false);
			this.confirmButton.setText("Confirm");
			setVisible(confirmButton, true);
			setVisible(opponentImage, false);
			setVisible(exitButton, true);
			headPanel.setText("Defeat!");
			setVisible(headPanel, true);
			setVisible(backButton, false);
			break;
		case FIGHT_1:
			setVisible(cardCountPanel, false);
			setVisible(saveDeckButton, true);
			setVisible(deckPanel, true);
			setVisible(cardChooseImages, false);
			setVisible(basicEnergyChooseImages, false);
			setVisible(deckImages, true);
			setVisible(elementImages, false);
			confirmButton.setText("Fight");
			setVisible(confirmButton, true);
			opponentImage.setTexture(Database.getAssetKey(draftDatabase.getOpponent(0).getCode().toString()));
			setVisible(opponentImage, true);
			setVisible(exitButton, true);
			headPanel.setText("First Opponent: " + draftDatabase.getOpponent(0).getName());
			setVisible(headPanel, true);
			backButton.setText("Surrender");
			setVisible(backButton, true);
			break;
		case FIGHT_2:
			setVisible(cardCountPanel, false);
			setVisible(saveDeckButton, true);
			setVisible(deckPanel, true);
			setVisible(cardChooseImages, false);
			setVisible(basicEnergyChooseImages, false);
			setVisible(deckImages, true);
			setVisible(elementImages, false);
			confirmButton.setText("Fight");
			setVisible(confirmButton, true);
			opponentImage.setTexture(Database.getAssetKey(draftDatabase.getOpponent(1).getCode().toString()));
			setVisible(opponentImage, true);
			setVisible(exitButton, true);
			headPanel.setText("Second Opponent: " + draftDatabase.getOpponent(1).getName());
			setVisible(headPanel, true);
			backButton.setText("Surrender");
			setVisible(backButton, true);
			break;
		case FIGHT_3:
			setVisible(cardCountPanel, false);
			setVisible(saveDeckButton, true);
			setVisible(deckPanel, true);
			setVisible(cardChooseImages, false);
			setVisible(basicEnergyChooseImages, false);
			setVisible(deckImages, true);
			setVisible(elementImages, false);
			confirmButton.setText("Fight");
			setVisible(confirmButton, true);
			opponentImage.setTexture(Database.getAssetKey(draftDatabase.getOpponent(2).getCode().toString()));
			setVisible(opponentImage, true);
			setVisible(exitButton, true);
			headPanel.setText("Third Opponent: " + draftDatabase.getOpponent(2).getName());
			setVisible(headPanel, true);
			backButton.setText("Surrender");
			setVisible(backButton, true);
			break;
		case VICTORY:
			setVisible(cardCountPanel, false);
			setVisible(saveDeckButton, true);
			setVisible(deckPanel, true);
			setVisible(cardChooseImages, false);
			setVisible(basicEnergyChooseImages, false);
			setVisible(deckImages, true);
			setVisible(elementImages, false);
			this.confirmButton.setText("Confirm");
			setVisible(confirmButton, true);
			setVisible(opponentImage, false);
			setVisible(exitButton, true);
			headPanel.setText("Victory!");
			setVisible(headPanel, true);
			setVisible(backButton, false);
			break;
		default:
			break;
		}
	}

	public void setAccount(Account asAccount) {
		this.account = asAccount;
		this.normalDeck = account.getDeck();
	}

	public void restart() {
		this.updateGUI();
	}
}
