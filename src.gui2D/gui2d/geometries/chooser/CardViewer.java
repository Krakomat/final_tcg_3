package gui2d.geometries.chooser;

import gui2d.GUI2D;
import gui2d.controller.IOController;
import gui2d.geometries.Image2D;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.WindowGeometry;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import common.utilities.Lock;
import model.database.Card;
import model.database.Database;

public class CardViewer extends WindowGeometry {

	private List<Card> cards;
	private Image2D cardImage;
	private int currentCardIndex;
	private Lock lock;
	private TextButton2D goLeftButton, goRightButton, okButton;

	public CardViewer(String name, String text, float width, float height) {
		super(name, text, width, height);
		this.cards = new ArrayList<>();
		this.currentCardIndex = -1;
		lock = new Lock();

		goLeftButton = new TextButton2D(name, "<<", width * 0.1f, height * 0.1f) {
			@Override
			public void mouseSelect() {
				goLeftClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		goLeftButton.setLocalTranslation(width * 0.03f, yPos + height * 0.05f, level + 0.00001f);
		this.attachChild(goLeftButton);

		goRightButton = new TextButton2D(name, ">>", width * 0.1f, height * 0.1f) {
			@Override
			public void mouseSelect() {
				goRightClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		goRightButton.setLocalTranslation(xPos + width * 0.87f, yPos + height * 0.05f, level + 0.00001f);
		this.attachChild(goRightButton);

		okButton = new TextButton2D(name, "OK", width * 0.20f, height * 0.1f) {
			@Override
			public void mouseSelect() {
				goOkClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		okButton.setLocalTranslation(xPos + width * 0.40f, yPos + height * 0.05f, level + 0.00001f);
		this.attachChild(okButton);

		this.cardImage = new Image2D(name, Database.getTextureKey("00000"), width * 0.50f, height * 0.7f) {
			@Override
			public void mouseSelect() {
			}

			@Override
			public void mouseSelectRightClick() {
			}
		};
		cardImage.setLocalTranslation(xPos + width * 0.25f, yPos + height * 0.2f, level + 0.00001f);
		this.attachChild(cardImage);
	}

	protected void goOkClicked() {
		CardViewer self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				unregisterShootables(GUI2D.getInstance().getIOController());
				setVisible(false);
				GUI2D.getInstance().getIOController().restoreShootables();
				GUI2D.getInstance().addToUpdateQueue(self); // waits for update queue here
			}
		}).start();
	}

	protected void goRightClicked() {
		CardViewer self = this;
		this.currentCardIndex++;
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self); // waits for update queue here
			}
		}).start();
	}

	protected void goLeftClicked() {
		CardViewer self = this;
		this.currentCardIndex--;
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self); // waits for update queue here
			}
		}).start();
	}

	public void setData(String title, List<Card> cardList) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Preconditions.checkArgument(cardList.size() > 0, "Error: Called setData of class CardViewer with an empty card list!");
		this.setText(title);
		this.cards = cardList;
		this.currentCardIndex = 0;
		this.cardImage.setTexture(Database.getTextureKey(cards.get(currentCardIndex).getCardId()));
		this.goLeftButton.setVisible(false);
		this.goRightButton.setVisible(cards.size() > 1 ? true : false);
		this.okButton.setVisible(true);
		GUI2D.getInstance().getIOController().storeShootables();
		this.lock.unlock();
	}

	public void update() {
		super.update();

		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (this.isVisible()) {
			// Call update on image:
			cardImage.setTexture(Database.getTextureKey(cards.get(currentCardIndex).getCardId()));
			cardImage.setVisible(true);

			if (this.currentCardIndex > 0)
				this.goLeftButton.setVisible(true);
			else
				this.goLeftButton.setVisible(false);

			if (this.currentCardIndex < this.cards.size() - 1)
				this.goRightButton.setVisible(true);
			else
				this.goRightButton.setVisible(false);

			this.registerShootables(GUI2D.getInstance().getIOController());
		} else {
			cardImage.setVisible(false);
			this.goLeftButton.setVisible(false);
			this.goRightButton.setVisible(false);
			this.okButton.setVisible(false);
		}
		// Update components:
		this.cardImage.update();
		this.goLeftButton.update();
		this.goRightButton.update();
		this.okButton.update();

		this.lock.unlock();
	}

	public void registerShootables(IOController ioController) {
		if (this.okButton.isVisible()) {
			if (!ioController.hasShootable(okButton))
				ioController.addShootable(okButton);
		} else {
			if (ioController.hasShootable(okButton))
				ioController.removeShootable(okButton);
		}

		if (this.goLeftButton.isVisible()) {
			if (!ioController.hasShootable(goLeftButton))
				ioController.addShootable(goLeftButton);
		} else {
			if (ioController.hasShootable(goLeftButton))
				ioController.removeShootable(goLeftButton);
		}

		if (this.goRightButton.isVisible()) {
			if (!ioController.hasShootable(goRightButton))
				ioController.addShootable(goRightButton);
		} else {
			if (ioController.hasShootable(goRightButton))
				ioController.removeShootable(goRightButton);
		}
	}

	public void unregisterShootables(IOController ioController) {
		if (ioController.hasShootable(okButton))
			ioController.removeShootable(okButton);

		if (ioController.hasShootable(goLeftButton))
			ioController.removeShootable(goLeftButton);

		if (ioController.hasShootable(goRightButton))
			ioController.removeShootable(goRightButton);
	}

	public void setVisible(boolean value) {
		super.setVisible(value);
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cardImage.setVisible(value);
		this.lock.unlock();
	}
}
