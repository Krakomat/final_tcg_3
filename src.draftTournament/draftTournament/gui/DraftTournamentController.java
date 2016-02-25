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
			GUI2D.getInstance().switchMode(GUI2DMode.LOBBY, true);
			break;
		case FIGHT_1:
		case FIGHT_2:
		case FIGHT_3:
			if (GUI2D.getInstance().userAnswersQuestion("Do you really want to surrender this Draft Tournament?")) {
				chosenElements.clear();
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveDeckButtonClicked() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void confirmButtonClicked() {
		switch (state) {
		case CHOOSE_2_ELEMENTS:
			this.state = DraftTournamentState.CHOOSE_CARDS;
			List<Card> randomCards = draftDatabase.getRandomCardList(3);
			for (int i = 0; i < randomCards.size(); i++)
				elementImages.get(i).setTexture(Database.getTextureKey(randomCards.get(i).getCardId()));
			this.updateGUI();
			break;
		case FIGHT_1:
		case FIGHT_2:
		case FIGHT_3:
			// TODO
			break;
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void cardChooseImageSelected(int h) {
		// TODO Auto-generated method stub

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
			setVisible(headPanel, true);
			setVisible(backButton, true);
			setVisible(cardChooseImages, false);
			setVisible(basicEnergyChooseImages, false);
			setVisible(deckImages, false);
			List<Element> randomElements = draftDatabase.getRandomElementList(4);
			for (int i = 0; i < randomElements.size(); i++){
				elementImages.get(i).setTexture(Database.getAssetKey(randomElements.get(i).toString()));
				elementImages.get(i).setCardId(randomElements.get(i).toString());
			}
			setVisible(elementImages, true);
			break;
		case CHOOSE_CARDS:
			cardCountPanel.setText("0/40");
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
			setVisible(headPanel, false);
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
			setVisible(opponentImage, true);
			setVisible(exitButton, true);
			setVisible(headPanel, true);
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
			setVisible(opponentImage, true);
			setVisible(exitButton, true);
			setVisible(headPanel, true);
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
			setVisible(opponentImage, true);
			setVisible(exitButton, true);
			setVisible(headPanel, true);
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
			setVisible(headPanel, false);
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
