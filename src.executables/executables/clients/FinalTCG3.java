package executables.clients;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jme3.system.AppSettings;

import gui2d.GUI2D;
import model.database.Database;
import network.tcp.messages.MessageRegister;

public class FinalTCG3 {
	public static final String VERSION = "3.50";

	public static void main(String[] args) {
		Database.init();
		MessageRegister.registerSerializables();
		GUI2D view = new GUI2D();

		view.setShowSettings(false);
		AppSettings settings = new AppSettings(true);
		settings.put("Width", 1280);
		settings.put("Height", 720);
		// settings.put("Width", (int)
		// (Toolkit.getDefaultToolkit().getScreenSize().width * 0.9f));
		// settings.put("Height", (int)
		// (Toolkit.getDefaultToolkit().getScreenSize().height * 0.9f));
		settings.setTitle("Final TCG 3 Version " + FinalTCG3.VERSION);
		settings.put("VSync", true);
		settings.put("Samples", 16);
		settings.setFullscreen(false);
		try {
			BufferedImage newimg = ImageIO.read(GUI2D.class.getClass().getResourceAsStream("/tilesets/other/pokeball16x16.png"));
			BufferedImage newimg2 = ImageIO.read(GUI2D.class.getClass().getResourceAsStream("/tilesets/other/pokeball32x32.png"));
			settings.setIcons(new BufferedImage[] { newimg, newimg2 });
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		view.setSettings(settings);
		view.start();
		while (!view.isStarted())
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
