package gui2d.controller;

import gui2d.controller.MusicController.MusicType;

public interface GUI2DController {

	public void hide();

	public void restart();
	
	public MusicType getAmbientMusic();
}
