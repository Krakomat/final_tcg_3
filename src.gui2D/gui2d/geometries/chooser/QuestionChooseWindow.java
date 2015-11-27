package gui2d.geometries.chooser;

import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.math.ColorRGBA;

import gui2d.GUI2D;
import gui2d.controller.IOController;
import gui2d.geometries.TextButton2D;
import gui2d.geometries.WindowGeometry;
import common.utilities.Lock;

public class QuestionChooseWindow extends WindowGeometry {

	protected TextButton2D yesButton, noButton;
	private BitmapText question;
	private boolean choosingFinished, answer;
	private String questionText;
	private Lock lock;

	public QuestionChooseWindow(String name, String question, float width, float height) {
		super(name, "", width, height);
		lock = new Lock();
		choosingFinished = false;
		answer = false;
		this.questionText = question;
		yesButton = new TextButton2D(name, "Yes", width * 0.4f, height * 0.3f) {
			@Override
			public void mouseSelect() {
				yesClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		yesButton.setLocalTranslation(width * 0.03f, yPos + height * 0.05f, level + 0.00001f);
		this.attachChild(yesButton);

		noButton = new TextButton2D(name, "No", width * 0.4f, height * 0.3f) {
			@Override
			public void mouseSelect() {
				noClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		noButton.setLocalTranslation(xPos + width * 0.57f, yPos + height * 0.05f, level + 0.00001f);
		this.attachChild(noButton);

		this.registerShootables(GUI2D.getInstance().getIOController());
		
		// Window text:
		this.question = new BitmapText(GUI2D.getInstance().getGuiFont(), false);
		this.question.setSize(GUI2D.getInstance().getGuiFont().getCharSet().getRenderedSize()); // font size
		this.question.setText(this.titleText); // the text
		this.question.setColor(ColorRGBA.Black); // font color

		this.question.setLocalTranslation(width * 0.03f, height * 0.77f + this.question.getLineHeight() / 2, 0.00001f);
		this.question.setLineWrapMode(LineWrapMode.Word);
		this.attachChild(this.question);
	}

	protected void noClicked() {
		answer = false;
		this.choosingFinished = true;
	}

	protected void yesClicked() {
		answer = true;
		this.choosingFinished = true;
	}

	public void setData(String question) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.questionText = question;
		this.answer = false;
		this.choosingFinished = false;
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
			this.yesButton.setVisible(true);
			this.noButton.setVisible(true);
			this.registerShootables(GUI2D.getInstance().getIOController());
			this.question.setText(this.questionText);
		} else {
			this.yesButton.setVisible(false);
			this.noButton.setVisible(false);
			this.unregisterShootables(GUI2D.getInstance().getIOController());
			this.question.setText("");
		}
		this.yesButton.update();
		this.noButton.update();

		this.lock.unlock();
	}

	/**
	 * Registers the objects that are shootable by the mouse at the given IOController.
	 * 
	 * @param ioController
	 */
	public void registerShootables(IOController ioController) {
		if (this.yesButton.isVisible()) {
			if (!ioController.hasShootable(yesButton))
				ioController.addShootable(yesButton);
		} else {
			if (ioController.hasShootable(yesButton))
				ioController.removeShootable(yesButton);
		}

		if (this.noButton.isVisible()) {
			if (!ioController.hasShootable(noButton))
				ioController.addShootable(noButton);
		} else {
			if (ioController.hasShootable(noButton))
				ioController.removeShootable(noButton);
		}
	}

	/**
	 * Unregisters the objects that were shootable by the mouse at the given IOController.
	 * 
	 * @param ioController
	 */
	public void unregisterShootables(IOController ioController) {
		if (ioController.hasShootable(yesButton))
			ioController.removeShootable(yesButton);

		if (ioController.hasShootable(noButton))
			ioController.removeShootable(noButton);
	}

	public void setVisible(boolean value) {
		super.setVisible(value);
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.lock.unlock();
	}

	public boolean choosingFinished() {
		return choosingFinished;
	}

	public boolean getAnswer() {
		return answer;
	}
}
