package gui2d.geometries.chooser;

import gui2d.GUI2D;
import gui2d.controller.IOController;
import gui2d.geometries.Image2D;

import java.util.ArrayList;
import java.util.List;

import com.jme3.material.RenderState.BlendMode;

import common.utilities.Lock;
import model.database.Card;
import model.database.Database;

public class CardChooseWindow extends ChooseWindow {

	private List<Card> cards;
	private List<Image2D> imageList;
	private Lock lock;

	public CardChooseWindow(String name, String text, float width, float height, int elementsPerPage) {
		super(name, text, width, height, elementsPerPage);
		imageList = new ArrayList<>();
		this.cards = new ArrayList<>();
		lock = new Lock();
		float imageWidth = this.width * 0.15f;
		float imageHeight = imageWidth * 1.141f;
		// Create new images:
		for (int i = 0; i < 15; i++) {
			float imageXPos = this.width * 0.05f + (imageWidth * 1.2f) * (i % 5);
			float imageYPos = this.height * 0.19f + (imageHeight * 1.40f) * (2 - (i / 5));

			// Create image:
			final int index = i;
			Image2D image = new Image2D("ChooseImage", Database.getTextureKey("00001"), imageWidth, imageHeight, BlendMode.PremultAlpha) {
				@Override
				public void mouseSelect() {
					imageSelected(index);
				}

				@Override
				public void mouseSelectRightClick() {
					// nothing to do here
				}
			};
			image.setLocalTranslation(imageXPos, imageYPos, level + 0.00001f);

			imageList.add(image);
			this.attachChild(image);
		}

		this.registerShootables(GUI2D.getInstance().getIOController());
	}

	public void setData(String title, List<Card> chooseList, int chooseAmount, boolean chooseExactly, ChooseGeometryChecker checker) {
		super.setData(title, chooseAmount, chooseExactly, checker, chooseList.size());
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.cards = chooseList;
		for (int i = 0; i < cards.size() && i < 15; i++) {
			Image2D image = this.imageList.get(i);
			image.setTexture(Database.getTextureKey(cards.get(i).getCardId()));
			image.setSelected(false);
		}
		for (int i = cards.size(); i < imageList.size(); i++) {
			Image2D image = this.imageList.get(i);
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
			for (int i = 0; i < imageList.size() && (i + startIndex) < cards.size(); i++) {
				Image2D image = this.imageList.get(i);
				image.setTexture(Database.getTextureKey(cards.get(startIndex + i).getCardId()));
				image.setVisible(true);
				if (this.indexList.contains(startIndex + i))
					image.setSelected(true);
				else
					image.setSelected(false);
				image.update();
			}
			for (int i = cards.size() - startIndex; i < imageList.size(); i++) {
				Image2D image = this.imageList.get(i);
				image.setVisible(false);
				image.update();
			}

			this.registerShootables(GUI2D.getInstance().getIOController());
		} else {
			for (Image2D image : imageList) {
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
		for (Image2D image : imageList) {
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
		for (Image2D image : imageList) {
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
			Image2D image = this.imageList.get(i);
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
		if (this.indexList.contains(startIndex + index))
			this.indexList.remove(Integer.valueOf(startIndex + index));
		else
			this.indexList.add(startIndex + index);

		// Update image:
		Image2D image = imageList.get(index);
		image.setSelected(!image.isSelected());

		CardChooseWindow self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self);
			}
		}).start();
		this.lock.unlock();
	}
}
