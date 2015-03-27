package gui2d.geometries.chooser;

import gui2d.GUI2D;
import gui2d.controller.IOController;
import gui2d.geometries.ImageText2D;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Lock;
import model.database.Card;
import model.database.Database;

public class AttackChooseWindow extends ChooseWindow {

	private List<String> attacks;
	private List<ImageText2D> imageList;
	private Lock lock;

	public AttackChooseWindow(String name, String text, float width, float height) {
		super(name, text, width, height);
		imageList = new ArrayList<>();
		this.attacks = new ArrayList<>();
		lock = new Lock();
		this.imageList = new ArrayList<ImageText2D>();
		float imageWidth = this.width * 0.15f;
		float imageHeight = imageWidth * 1.141f;
		// Create new images:
		for (int i = 0; i < 15; i++) {
			float imageXPos = this.width * 0.05f + (imageWidth * 1.2f) * (i % 5);
			float imageYPos = this.height * 0.19f + (imageHeight * 1.40f) * (2 - (i / 5));

			// Create image:
			final int index = i;
			ImageText2D image = new ImageText2D("ChooseImage", Database.getTextureKey("00001"), imageWidth, imageHeight, "Attack") {
				@Override
				public void mouseSelect() {
					imageSelected(index);
				}
			};
			image.setLocalTranslation(imageXPos, imageYPos, level + 0.00001f);

			imageList.add(image);
			this.attachChild(image);
		}

		this.registerShootables(GUI2D.getInstance().getIOController());
	}

	public void setData(String title, List<Card> attackOwner, List<String> chooseList, int chooseAmount, boolean chooseExactly, ChooseGeometryChecker checker) {
		super.setData(title, chooseAmount, chooseExactly, checker, chooseList.size());
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.attacks = chooseList;
		for (int i = 0; i < attacks.size(); i++) {
			ImageText2D image = this.imageList.get(i);
			image.setTexture(Database.getTextureKey(attackOwner.get(i).getCardId()));
			image.setText(attacks.get(i));
			image.setSelected(false);
		}
		for (int i = attacks.size(); i < imageList.size(); i++) {
			ImageText2D image = this.imageList.get(i);
			image.setVisible(false);
		}
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
			for (int i = 0; i < imageList.size(); i++) {
				ImageText2D image = this.imageList.get(i);
				image.update();
			}

			this.registerShootables(GUI2D.getInstance().getIOController());
		} else {
			for (ImageText2D image : imageList) {
				image.setVisible(false);
				image.update();
			}
			this.unregisterShootables(GUI2D.getInstance().getIOController());
		}
		this.lock.unlock();
	}

	/**
	 * Registers the objects that are shootable by the mouse at the given IOController.
	 * 
	 * @param ioController
	 */
	public void registerShootables(IOController ioController) {
		super.registerShootables(ioController);
		for (ImageText2D image : imageList) {
			if (image.isVisible())
				ioController.addShootable(image);
			else
				ioController.removeShootable(image);
		}
	}

	/**
	 * Unregisters the objects that were shootable by the mouse at the given IOController.
	 * 
	 * @param ioController
	 */
	public void unregisterShootables(IOController ioController) {
		super.unregisterShootables(ioController);
		for (ImageText2D image : imageList) {
			if (ioController.hasShootable(image))
				ioController.removeShootable(image);
		}
	}

	public void setVisible(boolean value) {
		super.setVisible(value);
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < imageList.size(); i++) {
			ImageText2D image = this.imageList.get(i);
			image.setVisible(value);
		}
		this.lock.unlock();
	}

	/**
	 * Is called when an image is being selected. The image can be found in the list by subtracting startIndex form the given index.
	 * 
	 * @param index
	 */
	public void imageSelected(int index) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (this.indexList.contains(index))
			this.indexList.remove(new Integer(index));
		else
			this.indexList.add(index);

		// Update image:
		ImageText2D image = imageList.get(index);
		image.setSelected(!image.isSelected());

		AttackChooseWindow self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self);
			}
		}).start();
		this.lock.unlock();
	}
}
