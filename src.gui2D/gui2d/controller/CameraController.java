package gui2d.controller;

import com.jme3.input.FlyByCamera;
import com.jme3.renderer.Camera;

public class CameraController {

	private Camera cam;
	private FlyByCamera flyCam;

	public CameraController(Camera cam, FlyByCamera flyCam) {
		this.cam = cam;
		this.flyCam = flyCam;
	}

	public void initCam() {
		flyCam.setEnabled(false);
		flyCam.setMoveSpeed(50);
		cam.update();
	}

	public Camera getCam() {
		return cam;
	}

	public FlyByCamera getFlyCam() {
		return flyCam;
	}
}
