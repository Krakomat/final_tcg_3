package gui2d.controller;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import gui2d.GUI2D;

import com.jme3.audio.AudioNode;

public class EffectController {
	static int activeThreads = 0;
	public static AudioNode createEffectAudioNode(String effectPath) {
		AudioNode clickSoundNode = new AudioNode(GUI2D.getInstance().getAssetManager(), effectPath, false);
		clickSoundNode.setPositional(false);
		clickSoundNode.setLooping(false);
		clickSoundNode.setVolume(2);
		return clickSoundNode;
	}

	public static synchronized void playSound(final String url) {
		if (!url.equals("")) {
			Thread t = new Thread(new Runnable() {
				// The wrapper thread is unnecessary, unless it blocks on the
				// Clip finishing; see comments.
				public void run() {
					try {
						Clip clip = AudioSystem.getClip();
						AudioInputStream inputStream = AudioSystem.getAudioInputStream(EffectController.class.getResource(url));
						clip.open(inputStream);
						clip.start();
						while (clip.getMicrosecondLength() > clip.getMicrosecondPosition())
							;
						clip.close();
						clip.stop();
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
			});
			t.setName("SoundThread");
			t.start();
		}
	}
}
