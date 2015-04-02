package gui2d.controller;

import java.util.concurrent.Callable;

import gui2d.GUI2D;

import com.jme3.audio.AudioNode;
import com.jme3.scene.Spatial;
import common.utilities.Threads;

public class EffectController {
	static int activeThreads = 0;

	public static AudioNode createEffectAudioNode(String effectPath) {
		AudioNode clickSoundNode = new AudioNode(GUI2D.getInstance().getAssetManager(), effectPath, false);
		clickSoundNode.setPositional(false);
		clickSoundNode.setLooping(false);
		clickSoundNode.setVolume(2);
		return clickSoundNode;
	}

	/**
	 * Plays the given sound file. WANRNING: Do not call this from the render thread!
	 * 
	 * @param url
	 */
	public static synchronized void playSound(final String url) {
		if (!url.equals("")) {
			Thread t = new Thread(new Runnable() {
				// The wrapper thread is unnecessary, unless it blocks on the
				// Clip finishing; see comments.
				public void run() {
					AudioNode clickSoundNode = new AudioNode(GUI2D.getInstance().getAssetManager(), url, false);
					clickSoundNode.setPositional(false);
					clickSoundNode.setLooping(false);
					clickSoundNode.setVolume(2);
					if (Thread.currentThread().getName().equals(Threads.RENDER_THREAD.toString()))
						System.err.println("[RENDER] Error: Called playSound from render thread.");

					// Start writing:
					if (clickSoundNode != null)
						GUI2D.getInstance().enqueue(new Callable<Spatial>() {
							public Spatial call() throws Exception {
								clickSoundNode.play();
								return null;
							}
						});
				}
			});
			t.setName("SoundThread");
			t.start();
		}
	}
}
