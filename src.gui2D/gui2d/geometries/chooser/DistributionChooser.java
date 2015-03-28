package gui2d.geometries.chooser;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Lock;
import model.database.Database;
import model.database.PokemonCard;
import model.enums.DistributionMode;
import model.enums.Element;
import model.interfaces.Position;
import gui2d.GUI2D;
import gui2d.controller.IOController;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.WindowGeometry;
import gui2d.geometries.ArenaGeometry2D;

public class DistributionChooser extends WindowGeometry {
	protected DistributionMode mode;
	private List<ArenaGeometry2D> arenaPositions;
	private List<TextButton2D> plusButtons, minusButtons;
	private TextButton2D okButton;
	private Lock lock;
	protected boolean correctlyChosen, choosingFinished;
	protected ChooseGeometryChecker checker;
	protected List<Integer> distributionList, maxDistributionList;
	protected int balance;

	public DistributionChooser(String name, String text, float width, float height) {
		super(name, text, width, height);
		lock = new Lock();
		correctlyChosen = true;
		balance = 0;
		arenaPositions = new ArrayList<>();
		float posWidth = width * 0.15f;
		float posHeight = posWidth * 0.732f;
		for (int i = 0; i < 6; i++) {
			ArenaGeometry2D arenaGeo = new ArenaGeometry2D("ArenaPosChooser", Database.getPokemonThumbnailKey("00001"), posWidth, posHeight, true) {

				@Override
				public void mouseSelect() {
					// Do nothing!
				}

				@Override
				public void mouseSelectRightClick() {
					// nothing to do here
				}
			};
			arenaGeo.setLocalTranslation(width * 0.5f, height * 0.8f - (height * 0.13f) * i, 0.00001f);
			this.arenaPositions.add(arenaGeo);
			this.attachChild(arenaGeo);
		}

		plusButtons = new ArrayList<>();
		float buttonWidth = posHeight / 2;
		float buttonHeight = buttonWidth;
		for (int i = 0; i < 6; i++) {
			final int index = i;
			TextButton2D buttonGeo = new TextButton2D("ButtonPosChooser", "+", buttonWidth, buttonHeight) {

				@Override
				public void mouseSelect() {
					plusClicked(index);
				}

				@Override
				public void mouseSelectRightClick() {
					// nothing to do here
				}
			};
			buttonGeo.setLocalTranslation(width * 0.5f - buttonWidth, height * 0.8f - (height * 0.13f) * i + buttonHeight, 0.00001f);
			this.plusButtons.add(buttonGeo);
			this.attachChild(buttonGeo);
		}

		minusButtons = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			final int index = i;
			TextButton2D buttonGeo = new TextButton2D("ButtonPosChooser", "-", buttonWidth, buttonHeight) {
				@Override
				public void mouseSelect() {
					minusClicked(index);
				}

				@Override
				public void mouseSelectRightClick() {
					// nothing to do here
				}
			};
			buttonGeo.setLocalTranslation(width * 0.5f - buttonWidth, height * 0.8f - (height * 0.13f) * i, 0.00001f);
			this.minusButtons.add(buttonGeo);
			this.attachChild(buttonGeo);
		}

		okButton = new TextButton2D(name, "OK", width * 0.20f, height * 0.1f) {
			@Override
			public void mouseSelect() {
				okClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		okButton.setLocalTranslation(width * 0.40f, height * 0.04f, level + 0.00001f);
		this.attachChild(okButton);
	}

	protected void minusClicked(int index) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ArenaGeometry2D arenaPos = this.arenaPositions.get(index);
		if (mode == DistributionMode.DAMAGE) {
			Integer damage = this.distributionList.remove(index);
			damage = damage - 1;
			this.distributionList.add(index, damage);
			arenaPos.setDamageMarks(damage * 10);
			arenaPos.setHitPoints(maxDistributionList.get(index) * 10 + 10);
			balance = balance + 1;
		} else {
			List<Element> energyList = arenaPos.getEnergyList();
			energyList.remove(Element.GRASS);
			arenaPos.setEnergyList(energyList);

			Integer energy = this.distributionList.remove(index);
			energy = energy - 1;
			this.distributionList.add(index, energy);
			balance = balance + 1;
		}
		DistributionChooser self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self);
			}
		}).start();

		this.lock.unlock();
	}

	protected void plusClicked(int index) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ArenaGeometry2D arenaPos = this.arenaPositions.get(index);
		if (mode == DistributionMode.DAMAGE) {
			Integer damage = this.distributionList.remove(index);
			damage = damage + 1;
			this.distributionList.add(index, damage);
			arenaPos.setDamageMarks(damage * 10);
			arenaPos.setHitPoints(maxDistributionList.get(index) * 10 + 10);
			balance = balance - 1;
		} else {
			List<Element> energyList = arenaPos.getEnergyList();
			energyList.add(Element.GRASS);
			arenaPos.setEnergyList(energyList);

			Integer energy = this.distributionList.remove(index);
			energy = energy + 1;
			this.distributionList.add(index, energy);
			balance = balance - 1;
		}

		DistributionChooser self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self);
			}
		}).start();

		this.lock.unlock();
	}

	protected void okClicked() {
		this.choosingFinished = true;
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
			if (correctlyChosen)
				this.okButton.setVisible(true);
			else
				this.okButton.setVisible(false);

			// Plus-Buttons:
			for (int i = 0; i < distributionList.size(); i++) {
				TextButton2D plusButton = this.plusButtons.get(i);
				int damage = distributionList.get(i);
				int maxDamage = maxDistributionList.get(i);
				if (balance > 0 && damage < (maxDamage))
					plusButton.setVisible(true);
				else
					plusButton.setVisible(false);
			}
			for (int i = distributionList.size(); i < plusButtons.size(); i++)
				this.plusButtons.get(i).setVisible(false);

			// Minus-Buttons:
			for (int i = 0; i < distributionList.size(); i++) {
				TextButton2D minusButton = this.minusButtons.get(i);
				int damage = distributionList.get(i);
				if (balance <= 0 && damage > 0)
					minusButton.setVisible(true);
				else
					minusButton.setVisible(false);
			}
			for (int i = distributionList.size(); i < minusButtons.size(); i++)
				this.minusButtons.get(i).setVisible(false);

			// Arena positions:
			for (int i = 0; i < distributionList.size(); i++) {
				ArenaGeometry2D arenaPos = this.arenaPositions.get(i);
				arenaPos.setVisible(true);
			}
			for (int i = distributionList.size(); i < arenaPositions.size(); i++)
				this.arenaPositions.get(i).setVisible(false);

			this.registerShootables(GUI2D.getInstance().getIOController());
		} else {
			this.okButton.setVisible(false);
			for (TextButton2D button : this.plusButtons)
				button.setVisible(false);
			for (TextButton2D button : this.minusButtons)
				button.setVisible(false);
			for (ArenaGeometry2D arenaPos : this.arenaPositions)
				arenaPos.setVisible(false);
		}
		this.okButton.update();
		for (TextButton2D button : this.plusButtons)
			button.update();
		for (TextButton2D button : this.minusButtons)
			button.update();
		for (ArenaGeometry2D arenaPos : this.arenaPositions)
			arenaPos.update();

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

		// Plus-Buttons:
		for (TextButton2D plusButton : this.plusButtons) {
			if (plusButton.isVisible()) {
				if (!ioController.hasShootable(plusButton))
					ioController.addShootable(plusButton);
			} else {
				if (ioController.hasShootable(plusButton))
					ioController.removeShootable(plusButton);
			}
		}

		// Minus-Buttons:
		for (TextButton2D minusButton : this.minusButtons) {
			if (minusButton.isVisible()) {
				if (!ioController.hasShootable(minusButton))
					ioController.addShootable(minusButton);
			} else {
				if (ioController.hasShootable(minusButton))
					ioController.removeShootable(minusButton);
			}
		}
	}

	public void unregisterShootables(IOController ioController) {
		if (ioController.hasShootable(okButton))
			ioController.removeShootable(okButton);

		// Plus-Buttons:
		for (TextButton2D plusButton : this.plusButtons) {
			if (ioController.hasShootable(plusButton))
				ioController.removeShootable(plusButton);
		}

		// Minus-Buttons:
		for (TextButton2D minusButton : this.minusButtons) {
			if (ioController.hasShootable(minusButton))
				ioController.removeShootable(minusButton);
		}
	}

	/**
	 * Updates the data on this ChooseGeometry.
	 * 
	 * @param title
	 * @param chooseAmount
	 * @param chooseExactly
	 * @param checker
	 */
	public void setData(String title, List<Position> positionList, ChooseGeometryChecker checker, List<Integer> initialDistribution, List<Integer> maxDistribution,
			DistributionMode mode) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.setText(title);
		this.correctlyChosen = false;
		this.checker = checker;
		this.distributionList = initialDistribution;
		this.maxDistributionList = maxDistribution;
		this.choosingFinished = false;
		this.mode = mode;
		balance = 0;

		for (int i = 0; i < positionList.size(); i++) {
			Position pos = positionList.get(i);
			PokemonCard pokemon = (PokemonCard) pos.getTopCard();
			ArenaGeometry2D arenaGeo = this.arenaPositions.get(i);
			arenaGeo.setTexture(Database.getPokemonThumbnailKey(pokemon.getCardId()));
			arenaGeo.setConditionList(pokemon.getConditions());
			arenaGeo.setDamageMarks(pokemon.getDamageMarks());
			arenaGeo.setHitPoints(pokemon.getHitpoints());
			arenaGeo.setEnergyList(pos.getEnergy());
		}
		this.lock.unlock();
	}

	public boolean choosingFinished() {
		return choosingFinished;
	}

	public List<Integer> getDistributionList() {
		return distributionList;
	}

	public int getBalance() {
		return balance;
	}

}
