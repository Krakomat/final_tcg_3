package gui2d.controller;

import gui2d.GUI2D;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.scene.Node;

import common.utilities.Lock;

/**
 * Manages the background music that is played.
 * 
 * @author Michael
 *
 */
public class MusicController extends Node {

	static final int TITLE_MUSIC_TRACKS = 1;
	static final int LOBBY_MUSIC_TRACKS = 1;
	static final int DECK_EDIT_MUSIC_TRACKS = 6;
	static final int INGAME_MUSIC_TRACKS = 13;
	static final float MUSIC_FADE_TIME = 150;
	static final float MUSIC_VOLUME = 2f;

	/**
	 * Defines the classes of tracks.
	 * 
	 * @author Michael
	 *
	 */
	public enum MusicType {
		INGAME_MUSIC, LOBBY_MUSIC, TITLE_MUSIC, DECK_EDIT_MUSIC, BOT_CHOOSE_MUSIC;
	}

	private List<AudioNode> fadeOutList;
	private AudioNode currentlyPlayingNode;
	private String currentlyPlayingTrack;
	private boolean switchMusic;
	private Map<MusicType, List<String>> musicTrackMap;
	private Lock lock;

	public MusicController() {
		this.lock = new Lock();
		this.currentlyPlayingNode = null;
		this.currentlyPlayingTrack = null;
		this.switchMusic = false;
		this.fadeOutList = new ArrayList<>();
		this.initMusicTracks();
	}

	public void initMusicTracks() {
		this.musicTrackMap = new HashMap<>();
		List<String> titleMusicList = new ArrayList<>();
		List<String> lobbyMusicList = new ArrayList<>();
		List<String> deckEditMusicList = new ArrayList<>();
		List<String> botChooseMusicList = new ArrayList<>();
		List<String> ingameMusicList = new ArrayList<>();

		for (int i = 1; i <= TITLE_MUSIC_TRACKS; i++)
			titleMusicList.add("music/title/track" + i + ".wav");
		this.musicTrackMap.put(MusicType.TITLE_MUSIC, titleMusicList);

		for (int i = 1; i <= LOBBY_MUSIC_TRACKS; i++)
			lobbyMusicList.add("music/lobby/track" + i + ".wav");
		this.musicTrackMap.put(MusicType.LOBBY_MUSIC, lobbyMusicList);

		for (int i = 1; i <= DECK_EDIT_MUSIC_TRACKS; i++)
			deckEditMusicList.add("music/deck_editor/track" + i + ".wav");
		this.musicTrackMap.put(MusicType.DECK_EDIT_MUSIC, deckEditMusicList);

		for (int i = 1; i <= DECK_EDIT_MUSIC_TRACKS; i++)
			botChooseMusicList.add("music/deck_editor/track" + i + ".wav");
		this.musicTrackMap.put(MusicType.BOT_CHOOSE_MUSIC, botChooseMusicList);

		for (int i = 1; i <= INGAME_MUSIC_TRACKS; i++)
			ingameMusicList.add("music/ingame/track" + i + ".wav");
		this.musicTrackMap.put(MusicType.INGAME_MUSIC, ingameMusicList);
	}

	private AudioNode createAudioNode(String trackPath) {
		AudioNode soundNode = new AudioNode(GUI2D.getInstance().getAssetManager(), trackPath, true);
		soundNode.setPositional(false);
		soundNode.setLooping(false);
		soundNode.setVolume(MUSIC_VOLUME);
		this.attachChild(soundNode); // Attach child here!
		return soundNode;
	}

	/**
	 * Will be called from the simpleUpdate method of the render thread.
	 * 
	 * @param tps
	 */
	public void update(float tpsMilis) {
		try {
			this.lock.lock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (!this.switchMusic) {
			if (this.currentlyPlayingNode == null && this.currentlyPlayingTrack != null) {
				this.currentlyPlayingNode = this.createAudioNode(currentlyPlayingTrack);
				this.currentlyPlayingNode.play();
			}

			if (this.currentlyPlayingNode.getStatus() == AudioSource.Status.Stopped) {
				this.fadeOutList.add(currentlyPlayingNode);
				this.currentlyPlayingNode = null;
			}
		} else {
			if (this.currentlyPlayingNode != null) {
				this.fadeOutList.add(currentlyPlayingNode);
			}
			this.currentlyPlayingNode = null;
			this.switchMusic = false;
		}

		for (int i = 0; i < this.fadeOutList.size(); i++) {
			AudioNode audioNode = this.fadeOutList.get(i);
			if (audioNode.getVolume() > 0) {
				// Reduce volume:
				float newVolume = audioNode.getVolume() - MUSIC_VOLUME / (MUSIC_FADE_TIME / tpsMilis);
				if (newVolume < 0)
					audioNode.setVolume(0);
				else
					audioNode.setVolume(newVolume);
			} else {
				// Stop & Drop node:
				audioNode.stop();
				this.detachChild(audioNode);
				this.fadeOutList.remove(i);
				i--;
			}
		}
		this.lock.unlock();
	}

	/**
	 * Switches to a new type of track.
	 * 
	 * @param newMusicType
	 */
	public void switchMusic(MusicType newMusicType) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					lock.lock();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				List<String> newMusicList = musicTrackMap.get(newMusicType);
				Random r = new SecureRandom();
				int trackIndex = r.nextInt(newMusicList.size());
				currentlyPlayingTrack = newMusicList.get(trackIndex);
				switchMusic = true;

				lock.unlock();
			}
		}).start();
	}
}
