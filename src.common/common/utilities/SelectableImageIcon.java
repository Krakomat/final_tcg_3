package common.utilities;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import editor.cardeditor.ISelectable;

public class SelectableImageIcon extends ImageIcon implements ISelectable {

	private static final long serialVersionUID = -8424503991777129321L;
	private String path;
	protected BufferedImage img;

	public SelectableImageIcon(String file) {
		super(file);
		try {
			img = ImageIO.read(getClass().getResourceAsStream(file));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		path = file;
	}

	@Override
	public ImageIcon getImageIcon() {
		return new ImageIcon(img);
	}

	public String getPfad() {
		return path;
	}
}
