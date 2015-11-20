package gui2d.controller;

import gui2d.animations.AnimateableObject;

import java.util.ArrayList;
import java.util.List;

import common.utilities.Lock;

/**
 * Maintains a list with all animation objects that are currently being animated.
 * 
 * @author Michael
 *
 */
public class AnimationController {

	private List<AnimateableObject> animatedObjects;
	private Lock lock;

	public AnimationController() {
		this.animatedObjects = new ArrayList<>();
		this.lock = new Lock();
	}

	/**
	 * Simple update method called from the main simple update method in the gui.
	 * 
	 * @param tpf
	 *            time per frame in miliseconds
	 * @throws InterruptedException
	 */
	public void simpleUpdate(float tpf) throws InterruptedException {
		this.lock.lock();

		for (AnimateableObject animObj : animatedObjects)
			animObj.simpleUpdate(tpf);

		for (int i = 0; i < this.animatedObjects.size(); i++) {
			if (this.animatedObjects.get(i).animationDone()) {
				this.animatedObjects.remove(i);
				i--;
			}
		}
		this.lock.unlock();
	}

	/**
	 * Adds an animateable object to this controller.
	 * 
	 * @param animObj
	 * @throws InterruptedException
	 */
	public void addAnimation(AnimateableObject animObj) throws InterruptedException {
		this.lock.lock();
		this.animatedObjects.add(animObj);
		this.lock.unlock();
	}
}
