package editor.cardeditor;

import javax.swing.ImageIcon;

public class SelectableImagePath implements ISelectable {

	private String path;

	public SelectableImagePath(String file) {
		path = file;
	}

	@Override
	public ImageIcon getImageIcon() {
		return null;
	}

	public String getPfad() {
		return path;
	}
}
