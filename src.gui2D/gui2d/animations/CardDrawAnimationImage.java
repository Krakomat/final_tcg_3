package gui2d.animations;

import com.jme3.asset.TextureKey;
import common.utilities.LinearFunction;

import gui2d.GUI2D;
import gui2d.geometries.Image2D;

/**
 * Animation object for the card draw animation.
 * 
 * @author Michael
 *
 */
public class CardDrawAnimationImage extends Image2D implements AnimateableObject {

	private float maxAnimationTime;
	private float animationTime;
	private LinearFunction xFunction, yFunction;

	public CardDrawAnimationImage(String name, TextureKey texture, float width, float height, float startX, float startY, float finishX, float finishY,
			float animationTime) {
		super(name, texture, width, height);
		this.maxAnimationTime = animationTime;
		this.animationTime = 0;

		this.xFunction = new LinearFunction(0, startX, animationTime, finishX);
		this.yFunction = new LinearFunction(0, startY, animationTime, finishY);

		this.setLocalTranslation(startX, startY, 0);
		this.setSelected(false);
		this.setGlowing(false);
		GUI2D.getInstance().addToUpdateQueue(this);
	}

	@Override
	public void mouseSelect() {

	}

	@Override
	public void mouseSelectRightClick() {

	}

	@Override
	public boolean animationDone() {
		return this.animationTime == maxAnimationTime;
	}

	@Override
	public void simpleUpdate(float tpf) {
		this.animationTime = this.animationTime + tpf;
		if (this.animationTime > maxAnimationTime)
			this.animationTime = maxAnimationTime;

		this.setLocalTranslation(xFunction.function(animationTime), yFunction.function(animationTime), 0);
	}

}
