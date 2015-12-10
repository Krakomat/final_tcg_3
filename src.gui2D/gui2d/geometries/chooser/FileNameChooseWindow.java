package gui2d.geometries.chooser;

import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.ColorRGBA;

import gui2d.GUI2D;
import gui2d.abstracts.KeyShootable;
import gui2d.controller.IOController;
import gui2d.geometries.TextButton2D;
import common.utilities.Lock;

public class FileNameChooseWindow extends ChooseWindow implements KeyShootable {

	private TextButton2D cancelButton;
	private Lock lock;
	private String chosenText, questionText;
	private BitmapText question, nameImage;
	private FileNameChooseWindow self;

	public FileNameChooseWindow(String name, String text, float width, float height) {
		super(name, text, width, height);
		lock = new Lock();
		self = this;

		this.chosenText = "";
		this.questionText = "";

		this.okButton.setText("OK");
		this.okButton.setLocalTranslation(xPos + width * 0.10f, yPos + height * 0.05f, level + 0.00001f);

		cancelButton = new TextButton2D(name, "Cancel", width * 0.20f, height * 0.1f) {
			@Override
			public void mouseSelect() {
				goCancelClicked();
			}

			@Override
			public void mouseSelectRightClick() {
				// nothing to do here
			}
		};
		cancelButton.setLocalTranslation(xPos + width * 0.60f, yPos + height * 0.05f, level + 0.00001f);
		this.attachChild(cancelButton);

		// Window text:
		this.question = new BitmapText(GUI2D.getInstance().getGuiFont(), false);
		this.question.setSize(GUI2D.getInstance().getGuiFont().getCharSet().getRenderedSize()); // font size
		this.question.setText(this.titleText); // the text
		this.question.setColor(ColorRGBA.Black); // font color

		this.question.setLocalTranslation(width * 0.03f, height * 0.77f + this.question.getLineHeight() / 2, 0.00001f);
		this.question.setLineWrapMode(LineWrapMode.Word);
		this.attachChild(this.question);

		// Window nameImage:
		this.nameImage = new BitmapText(GUI2D.getInstance().getGuiFont(), false);
		this.nameImage.setSize(GUI2D.getInstance().getGuiFont().getCharSet().getRenderedSize()); // font size
		this.nameImage.setText(this.titleText); // the text
		this.nameImage.setColor(ColorRGBA.Red); // font color

		this.nameImage.setLocalTranslation(width * 0.03f, height * 0.57f + this.question.getLineHeight() / 2, 0.00001f);
		this.nameImage.setLineWrapMode(LineWrapMode.Word);
		this.attachChild(this.nameImage);

		this.registerShootables(GUI2D.getInstance().getIOController());
	}

	protected void goCancelClicked() {
		chosenText = null;
		this.choosingFinished = true;
	}

	public void setData(String title, String defaultName, String question, ChooseGeometryChecker checker) {
		super.setData(title, chooseAmount, chooseExactly, checker, 1);
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.questionText = question;
		this.chosenText = defaultName;
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
			cancelButton.setVisible(true);
			this.question.setText(this.questionText);
			this.nameImage.setText(this.chosenText);

			if (this.chosenText.equals("") || this.chosenText.equals("Name"))
				this.okButton.setVisible(false);
			else
				this.okButton.setVisible(true);

			this.registerShootables(GUI2D.getInstance().getIOController());
		} else {
			cancelButton.setVisible(false);
			this.question.setText("");
			this.nameImage.setText("");
			this.unregisterShootables(GUI2D.getInstance().getIOController());
		}
		cancelButton.update();
		okButton.update();
		this.lock.unlock();
	}

	/**
	 * Registers the objects that are shootable by the mouse at the given IOController.
	 * 
	 * @param ioController
	 */
	public void registerShootables(IOController ioController) {
		super.registerShootables(ioController);
		if (this.cancelButton.isVisible()) {
			if (!ioController.hasShootable(cancelButton))
				ioController.addShootable(cancelButton);
		} else {
			if (ioController.hasShootable(cancelButton))
				ioController.removeShootable(cancelButton);
		}

		if (this.isVisible()) {
			if (!ioController.hasKeyShootable(this))
				ioController.addKeyShootable(this);
		} else {
			if (ioController.hasKeyShootable(this))
				ioController.removeKeyShootable(this);
		}
	}

	/**
	 * Unregisters the objects that were shootable by the mouse at the given IOController.
	 * 
	 * @param ioController
	 */
	public void unregisterShootables(IOController ioController) {
		super.unregisterShootables(ioController);
		if (ioController.hasShootable(cancelButton))
			ioController.removeShootable(cancelButton);

		if (ioController.hasKeyShootable(this))
			ioController.removeKeyShootable(this);
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

	public String getChosenText() {
		return chosenText;
	}

	@Override
	public void keyPressed(KeyInputEvent evt) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (isValidKey(evt.getKeyCode())) {
			if (evt.getKeyCode() == KeyInput.KEY_BACK) {
				if (this.chosenText.length() > 0)
					this.chosenText = chosenText.substring(0, chosenText.length() - 1);
			} else {
				if (this.chosenText.equals("Name"))
					this.chosenText = "" + evt.getKeyChar();
				else
					this.chosenText = chosenText + evt.getKeyChar();
			}
		}

		this.nameImage.setText(this.chosenText);
		new Thread(new Runnable() {
			@Override
			public void run() {
				GUI2D.getInstance().addToUpdateQueue(self); // waits for update queue here
			}
		}).start();

		this.lock.unlock();
	}

	private boolean isValidKey(int keyCode) {
		switch (keyCode) {
		case 52:// Point
			return true;
		case KeyInput.KEY_SPACE:
			return true;
		case KeyInput.KEY_MINUS:
			return true;
		case KeyInput.KEY_BACK:
			return true;
		case KeyInput.KEY_0:
			return true;
		case KeyInput.KEY_1:
			return true;
		case KeyInput.KEY_2:
			return true;
		case KeyInput.KEY_3:
			return true;
		case KeyInput.KEY_4:
			return true;
		case KeyInput.KEY_5:
			return true;
		case KeyInput.KEY_6:
			return true;
		case KeyInput.KEY_7:
			return true;
		case KeyInput.KEY_8:
			return true;
		case KeyInput.KEY_A:
			return true;
		case KeyInput.KEY_B:
			return true;
		case KeyInput.KEY_C:
			return true;
		case KeyInput.KEY_D:
			return true;
		case KeyInput.KEY_E:
			return true;
		case KeyInput.KEY_F:
			return true;
		case KeyInput.KEY_G:
			return true;
		case KeyInput.KEY_H:
			return true;
		case KeyInput.KEY_I:
			return true;
		case KeyInput.KEY_J:
			return true;
		case KeyInput.KEY_K:
			return true;
		case KeyInput.KEY_L:
			return true;
		case KeyInput.KEY_M:
			return true;
		case KeyInput.KEY_N:
			return true;
		case KeyInput.KEY_O:
			return true;
		case KeyInput.KEY_P:
			return true;
		case KeyInput.KEY_Q:
			return true;
		case KeyInput.KEY_R:
			return true;
		case KeyInput.KEY_S:
			return true;
		case KeyInput.KEY_T:
			return true;
		case KeyInput.KEY_U:
			return true;
		case KeyInput.KEY_V:
			return true;
		case KeyInput.KEY_W:
			return true;
		case KeyInput.KEY_X:
			return true;
		case KeyInput.KEY_Y:
			return true;
		case KeyInput.KEY_Z:
			return true;
		case KeyInput.KEY_UNDERLINE:
			return true;
		}
		return false;
	}
}
