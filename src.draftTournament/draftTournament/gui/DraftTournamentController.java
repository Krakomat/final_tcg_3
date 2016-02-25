package draftTournament.gui;

import draftTournament.model.DraftTournamentDatabase;
import draftTournament.model.DraftTournamentModel;
import draftTournament.model.DraftTournamentState;
import gui2d.GUI2D;
import gui2d.GUI2DMode;

public class DraftTournamentController extends DraftTournamentGUI {
	private DraftTournamentModel draftTournamentModel;
	private DraftTournamentState state;
	private DraftTournamentDatabase draftDatabase;

	public DraftTournamentController() {
		draftTournamentModel = new DraftTournamentModel();
		state = DraftTournamentState.CHOOSE_2_ELEMENTS;
		draftDatabase = new DraftTournamentDatabase();
	}

	@Override
	protected void backButtonClicked() {
		switch (state) {
		case CHOOSE_2_ELEMENTS:
			if (GUI2D.getInstance().userAnswersQuestion("Do you really want to surrender this Draft Tournament?"))
				GUI2D.getInstance().switchMode(GUI2DMode.LOBBY, true);
			break;
		case FIGHT_1:
		case FIGHT_2:
		case FIGHT_3:
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (GUI2D.getInstance().userAnswersQuestion("Do you really want to surrender this Draft Tournament?"))
						GUI2D.getInstance().switchMode(GUI2DMode.LOBBY, true);
				}
			}).start();
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void elementImageSelected(int h) {
		// TODO Auto-generated method stub

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
		case CHOOSE_2_ELEMENTS:
			setVisible(cardCountPanel, false);
			setVisible(saveDeckButton, false);
			setVisible(confirmButton, true);
			setVisible(opponentImage, false);
			setVisible(exitButton, true);
			setVisible(deckPanel, false);
			setVisible(headPanel, true);
			setVisible(backButton, true);
			setVisible(cardChooseImages, false);
			setVisible(basicEnergyChooseImages, false);
			setVisible(deckImages, false);
			setVisible(elementImages, true);
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
			setVisible(exitButton, false);
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
			setVisible(exitButton, false);
			setVisible(headPanel, false);
			setVisible(backButton, false);
			break;
		default:
			break;
		}
	}

	public void restart() {
		this.updateGUI();
	}
}
