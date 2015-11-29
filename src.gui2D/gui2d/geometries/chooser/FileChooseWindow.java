package gui2d.geometries.chooser;

import gui2d.GUI2D;
import gui2d.controller.IOController;
import gui2d.geometries.TextButton2D;
import java.util.ArrayList;
import java.util.List;
import common.utilities.Lock;

public class FileChooseWindow extends ChooseWindow {

	private List<String> attacks;
	private List<TextButton2D> imageList;
	private Lock lock;

	public FileChooseWindow(String name, String text, float width, float height) {
		super(name, text, width, height);
		imageList = new ArrayList<>();
		this.attacks = new ArrayList<>();
		lock = new Lock();
		this.imageList = new ArrayList<TextButton2D>();
		float imageWidth = this.width * 0.35f;
		float imageHeight = imageWidth * 0.15f;
		// Create new images:
		for (int i = 0; i < 20; i++) {
			float imageXPos = this.width * 0.05f + (imageWidth * 1.05f) * (i / 10);
			float imageYPos = this.height * 0.30f + (imageHeight * 1.10f) * (i%10);

			// Create image:
			final int index = i;
			TextButton2D image = new TextButton2D("ChooseImage", "", imageWidth, imageHeight) {
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
		this.okButton.setText("Cancel");
		this.registerShootables(GUI2D.getInstance().getIOController());
	}

	public void setData(String title, List<String> chooseList, int chooseAmount, boolean chooseExactly, ChooseGeometryChecker checker) {
		super.setData(title, chooseAmount, chooseExactly, checker, chooseList.size());
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.attacks = chooseList;
		for (int i = 0; i < attacks.size(); i++) {
			TextButton2D image = this.imageList.get(i);
			image.setText(attacks.get(i));
			image.setSelected(false);
		}
		for (int i = attacks.size(); i < imageList.size(); i++) {
			TextButton2D image = this.imageList.get(i);
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
				TextButton2D image = this.imageList.get(i);
				image.update();
			}

			this.registerShootables(GUI2D.getInstance().getIOController());
		} else {
			for (TextButton2D image : imageList) {
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
		for (TextButton2D image : imageList) {
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
		for (TextButton2D image : imageList) {
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
			TextButton2D image = this.imageList.get(i);
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
		TextButton2D image = imageList.get(index);
		image.setSelected(!image.isSelected());
		this.choosingFinished = true;
		FileChooseWindow self = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self);
			}
		}).start();
		this.lock.unlock();
	}
}
