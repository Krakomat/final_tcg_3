package gui2d.controller;

import gui2d.GUI2D;

import com.jme3.audio.AudioNode;

public class EffectController {
	public static final String BUTTON_CLICKED = "effects/button_click.ogg";
	public static final String ACTIVATE_TRAINER = "effects/activateTrainer.wav";
	public static final String COINFLIP = "effects/coinflip.wav";
	public static final String DAMAGE = "effects/damage.wav";
	public static final String EVOLVE = "effects/evolve.wav";
	public static final String ON_BENCH = "effects/onBench.wav";
	public static final String ON_TURN = "effects/onTurn.wav";
	public static final String SHUFFLE = "effects/shuffle.wav";
	public static final String DRAW = "effects/draw.wav";

	public static AudioNode createEffectAudioNode(String effectPath) {
		AudioNode clickSoundNode = new AudioNode(GUI2D.getInstance().getAssetManager(), effectPath, false);
		clickSoundNode.setPositional(false);
		clickSoundNode.setLooping(false);
		clickSoundNode.setVolume(2);
		return clickSoundNode;
	}
}
