package gui2d.animations;

import java.util.concurrent.Callable;

import gui2d.GUI2D;
import gui2d.controller.EffectController;
import gui2d.geometries.HandCardManager2D;
import gui2d.geometries.Image2D;

import com.jme3.asset.TextureKey;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import common.utilities.LinearFunction;

public class CardMoveAnimationImage extends Image2D implements AnimateableObject {

	private LinearFunction xFunction, yFunction;
	private float animationTime;
	private String sound;

	public CardMoveAnimationImage(Node from, Node to, String name, TextureKey texture, float width, float height, String sound) {
		super(name, texture, width, height);
		this.animationTime = 0;
		this.sound = sound;

		float startX = from instanceof HandCardManager2D ? ((HandCardManager2D) from).getxPos() : from.getLocalTranslation().x;
		float finishX = to instanceof HandCardManager2D ? ((HandCardManager2D) to).getxPos() : to.getLocalTranslation().x;
		float startY = from instanceof HandCardManager2D ? ((HandCardManager2D) from).getyPos() : from.getLocalTranslation().y;
		float finishY = to instanceof HandCardManager2D ? ((HandCardManager2D) to).getyPos() : to.getLocalTranslation().y;

		this.xFunction = new LinearFunction(0, startX, AnimationParameters.CARD_MOVE_TIME, finishX);
		this.yFunction = new LinearFunction(0, startY, AnimationParameters.CARD_MOVE_TIME, finishY);

		this.setLocalTranslation(from.getLocalTranslation().x, from.getLocalTranslation().y, 0);
		this.setSelected(false);
		this.setGlowing(false);
		GUI2D.getInstance().addToUpdateQueue(this);
	}

	@Override
	public void startAnimation() {
		// Add to gui node:
		final Spatial self = this;
		GUI2D.getInstance().enqueue(new Callable<Spatial>() {
			public Spatial call() throws Exception {
				GUI2D.getInstance().getGuiNode().attachChild(self);
				return null;
			}
		});
	}

	@Override
	public void resetAnimation() {

	}

	@Override
	public boolean animationDone() {
		return this.animationTime == AnimationParameters.CARD_MOVE_TIME;
	}

	@Override
	public void simpleUpdate(float tpf) {
		this.animationTime = this.animationTime + tpf;
		if (this.animationTime > AnimationParameters.CARD_MOVE_TIME)
			this.animationTime = AnimationParameters.CARD_MOVE_TIME;

		this.setLocalTranslation(xFunction.function(animationTime), yFunction.function(animationTime), 0);

		if (this.animationDone()) {
			if (!sound.equals(""))
				EffectController.playSound(sound);
			GUI2D.getInstance().getGuiNode().detachChild((Spatial) this);
		}
	}

	@Override
	public void mouseSelect() {

	}

	@Override
	public void mouseSelectRightClick() {

	}
}
