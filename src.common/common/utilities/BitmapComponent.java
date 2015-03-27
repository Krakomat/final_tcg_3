package common.utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import editor.cardeditor.ISelectable;

/**
 * The BitmapComponent is a JPanel, which has an image as background.
 * 
 * @author Michael
 * 
 */
public class BitmapComponent extends JPanel implements ISelectable, Serializable {

	/**
	 * version
	 */
	protected static final long serialVersionUID = 1L;
	protected BufferedImage img;
	protected String pfad;
	protected boolean isCyanMarked;
	protected boolean isBlueMarked;
	protected boolean isRedMarked;
	protected JLabel elementValueLabel;

	/**
	 * 
	 * @param fname
	 *            Path to the Backgroundimage.
	 */
	public BitmapComponent(String fname) {
		isCyanMarked = false;
		setIsBlueMarked(false);
		isRedMarked = false;
		pfad = fname;
		try {
			img = ImageIO.read(getClass().getResourceAsStream(fname));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		MediaTracker mt = new MediaTracker(this);

		mt.addImage(img, 0);
		try {
			mt.waitForAll();

		} catch (InterruptedException e) {
		}
		this.setOpaque(false);

		elementValueLabel = new JLabel("", SwingConstants.CENTER);
		elementValueLabel.setFont(new Font("Arial", Font.BOLD, 20));
		elementValueLabel.setVisible(false);
		elementValueLabel.setOpaque(true);
		this.add(elementValueLabel);
	}

	public BufferedImage getImageFile() {
		return img;
	}

	public ImageIcon getImageIcon() {
		return new ImageIcon(img);
	}

	/**
	 * Changes the Image.
	 * 
	 * @param path
	 *            Path of the new Image.
	 */
	public void setImage(String path) {
		try {
			try {
				img = ImageIO.read(getClass().getResourceAsStream(path));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			MediaTracker mt = new MediaTracker(this);

			mt.addImage(img, 0);
			try {
				mt.waitForAll();

			} catch (InterruptedException e) {
			}
			repaint();
			pfad = path;
		} catch (IllegalArgumentException e) {
			System.out.println(path);
		}
	}

	/**
	 * Changes the Image.
	 * 
	 * @param im
	 *            the image to set
	 */
	public void setImage(BufferedImage im) {
		img = im;
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
		if (isCyanMarked) {
			g.setColor(Color.CYAN);
			g.fillRect(0, 0, this.getWidth(), 3);
			g.fillRect(0, this.getHeight(), this.getWidth(), -3);
			g.fillRect(0, 0, 3, this.getHeight());
			g.fillRect(this.getWidth() - 3, 0, this.getWidth(), this.getHeight());
		}
		if (isBlueMarked) {
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, this.getWidth(), 3);
			g.fillRect(0, this.getHeight(), this.getWidth(), -3);
			g.fillRect(0, 0, 3, this.getHeight());
			g.fillRect(this.getWidth() - 3, 0, this.getWidth(), this.getHeight());
		}
		if (isRedMarked) {
			g.setColor(Color.RED);
			g.fillRect(0, 0, this.getWidth(), 5);
			g.fillRect(0, this.getHeight(), this.getWidth(), -5);
			g.fillRect(0, 0, 5, this.getHeight());
			g.fillRect(this.getWidth() - 5, 0, this.getWidth(), this.getHeight());
		}
		if (elementValueLabel.getText().equals(""))
			elementValueLabel.setVisible(false);
		else {
			elementValueLabel.setBounds(this.getWidth() - 25, this.getHeight() - 25, 25, 25);
			if (elementValueLabel.getText().equals("+1"))
				elementValueLabel.setBackground(Color.GREEN);
			else
				elementValueLabel.setBackground(Color.PINK);
			elementValueLabel.setVisible(true);
		}
	}

	public void setPfad(String pfad) {
		this.pfad = pfad;
	}

	public String getPfad() {
		return this.pfad;
	}

	public void setElementText(String text) {
		this.elementValueLabel.setText(text);
		repaint();
	}

	/**
	 * @param selected
	 *            The selected to set.
	 */
	public void setIsBlueMarked(boolean selected) {
		this.isBlueMarked = selected;
		repaint();
	}

	/**
	 * @return Returns the selected.
	 */
	public boolean isBlueMarked() {
		return this.isBlueMarked;
	}

	/**
	 * @param isRedMarked
	 *            The isRedMarked to set.
	 */
	public void setRedMarked(boolean isRedMarked) {
		this.isRedMarked = isRedMarked;
		repaint();
	}

	/**
	 * @return Returns the isRedMarked.
	 */
	public boolean isRedMarked() {
		return this.isRedMarked;
	}

	/**
	 * @param isBabyBlueMarked
	 *            The isBabyBlueMarked to set.
	 */
	public void setIsCyanMarked(boolean isCyanMarked) {
		this.isCyanMarked = isCyanMarked;
		repaint();
	}

	/**
	 * @return Returns the isBabyBlueMarked.
	 */
	public boolean isCyanMarked() {
		return this.isCyanMarked;
	}
}
