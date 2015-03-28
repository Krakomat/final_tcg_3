package gui2d.geometries.chooser;

import gui2d.GUI2D;
import gui2d.controller.IOController;
import gui2d.geometries.Image2D;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Lock;
import model.database.Database;
import model.enums.Element;

public class ElementChooseWindow extends ChooseWindow {

	private List<Element> elements;
	private List<Image2D> imageList;
	private Lock lock;

	public ElementChooseWindow(String name, String text, float width, float height) {
		super(name, text, width, height);
		imageList = new ArrayList<>();
		this.elements = new ArrayList<>();
		lock = new Lock();
		this.imageList = new ArrayList<Image2D>();
		float imageWidth = this.width * 0.15f;
		float imageHeight = imageWidth * 1.141f;
		// Create new images:
		for (int i = 0; i < 15; i++) {
			float imageXPos = this.width * 0.05f + (imageWidth * 1.2f) * (i % 5);
			float imageYPos = this.height * 0.19f + (imageHeight * 1.40f) * (2 - (i / 5));

			// Create image:
			final int index = i;
			Image2D image = new Image2D("ChooseImage", Database.getTextureKey("00001"), imageWidth, imageHeight) {
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

	public void setData(String title, List<Element> chooseList, int chooseAmount, boolean chooseExactly, ChooseGeometryChecker checker) {
		super.setData(title, chooseAmount, chooseExactly, checker, chooseList.size());
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.elements = chooseList;
		for (int i = 0; i < elements.size(); i++) {
			Image2D image = this.imageList.get(i);
			image.setTexture(Database.getAssetKey(elements.get(i).toString()));
			image.setSelected(false);
		}
		for (int i = elements.size(); i < imageList.size(); i++) {
			Image2D image = this.imageList.get(i);
			image.setTexture(Database.getTextureKey("00001"));
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
				Image2D image = this.imageList.get(i);
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
		if (this.indexList.contains(index))
			this.indexList.remove(new Integer(index));
		else
			this.indexList.add(index);

		// Update image:
		Image2D image = imageList.get(index);
		image.setSelected(!image.isSelected());

		ElementChooseWindow self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self);
			}
		}).start();
		this.lock.unlock();
	}
}
