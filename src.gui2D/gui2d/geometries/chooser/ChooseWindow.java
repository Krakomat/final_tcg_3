package gui2d.geometries.chooser;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Lock;
import gui2d.GUI2D;
import gui2d.controller.IOController;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.WindowGeometry;

public class ChooseWindow extends WindowGeometry {

	protected int chooseAmount, startIndex;
	protected boolean chooseExactly, correctlyChosen, choosingFinished;
	protected TextButton2D goLeftButton, goRightButton, okButton;
	protected ChooseGeometryChecker checker;
	/** Chosen indices */
	protected List<Integer> indexList;
	protected int chooseListSize;
	private Lock lock;
	protected int elementsPerPage;

	public ChooseWindow(String name, String text, float width, float height, int elementsPerPage) {
		super(name, text, width, height);
		this.indexList = new ArrayList<>();
		this.choosingFinished = false;
		this.elementsPerPage = elementsPerPage;
		chooseListSize = 0;
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
	}

	@Override
	public void update() {
		super.update();

		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (this.checker != null)
			this.correctlyChosen = this.checker.checkSelectionIsOk();
		this.unregisterShootables(GUI2D.getInstance().getIOController());

		if (this.isVisible()) {
			if (startIndex > 0)
				this.goLeftButton.setVisible(true);
			else
				this.goLeftButton.setVisible(false);

			if (startIndex < (chooseListSize - this.elementsPerPage))
				this.goRightButton.setVisible(true);
			else
				this.goRightButton.setVisible(false);

			if (correctlyChosen)
				this.okButton.setVisible(true);
			else
				this.okButton.setVisible(false);

			this.registerShootables(GUI2D.getInstance().getIOController());
		} else {
			this.goLeftButton.setVisible(false);
			this.goRightButton.setVisible(false);
			this.okButton.setVisible(false);
		}
		this.goLeftButton.update();
		this.goRightButton.update();
		this.okButton.update();

		this.lock.unlock();
	}

	protected void goOkClicked() {
		this.choosingFinished = true;
	}

	protected void goRightClicked() {
		this.startIndex += this.elementsPerPage;
		ChooseWindow self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self);
			}
		}).start();
	}

	protected void goLeftClicked() {
		this.startIndex -= this.elementsPerPage;
		ChooseWindow self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self);
			}
		}).start();
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

	/**
	 * Updates the data on this ChooseGeometry.
	 * 
	 * @param title
	 * @param chooseAmount
	 * @param chooseExactly
	 * @param checker
	 */
	public void setData(String title, int chooseAmount, boolean chooseExactly, ChooseGeometryChecker checker, int chooseListSize) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.setText(title);
		this.chooseAmount = chooseAmount;
		this.startIndex = 0;
		this.chooseExactly = chooseExactly;
		this.correctlyChosen = false;
		this.checker = checker;
		this.indexList.clear();
		this.choosingFinished = false;
		this.chooseListSize = chooseListSize;
		this.lock.unlock();
	}

	public boolean choosingFinished() {
		return choosingFinished;
	}

	public List<Integer> getChosenIndices() {
		return indexList;
	}

	public int getChooseAmount() {
		return chooseAmount;
	}

	public boolean isChooseExactly() {
		return chooseExactly;
	}
}
