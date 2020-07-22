package executables.clients;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.jme3.system.AppSettings;

import gui2d.GUI2D;
import model.database.Database;
import network.tcp.messages.MessageRegister;

public class FinalTCG3 {
	public static final String VERSION = "6.00";

	public static void main(String[] args) {
		try {
			Database.init();
			MessageRegister.registerSerializables();
			GUI2D view = new GUI2D();

			view.setShowSettings(false);
			AppSettings settings = new AppSettings(true);
			settings.put("Width", (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.85f));
			settings.put("Height", (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.85f));
			settings.setTitle("Final TCG 3 Version " + FinalTCG3.VERSION);
			settings.put("VSync", true);
			settings.put("Samples", 16);
			settings.setFullscreen(false);
			
			try {
				// the path must be relative to your *class* files
				String imagePath1 = "/tilesets/other/pokeball16x16.png";
				String imagePath2 = "/tilesets/other/pokeball32x32.png";
				InputStream imgStream1 = GUI2D.class.getResourceAsStream(imagePath1);
				InputStream imgStream2 = GUI2D.class.getResourceAsStream(imagePath2);
				BufferedImage myImg1 = ImageIO.read(imgStream1);
				BufferedImage myImg2 = ImageIO.read(imgStream2);
				settings.setIcons(new BufferedImage[] { myImg1, myImg2 });
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
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error");
		}
	}
}
