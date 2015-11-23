package gui2d.animations;

import java.util.concurrent.Callable;

import gui2d.GUI2D;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

import common.utilities.LinearFunction;

public class DamageAnimationImage extends BitmapText implements AnimateableObject {
	private float animationTime;
	private float maxAnimationTime;
	private float startX;
	private LinearFunction yFunction;

	public DamageAnimationImage(BitmapFont font, ColorRGBA fontColor, int amount, float startX, float startY, float finishY, float animationTime) {
		super(font);
		float size = GUI2D.getInstance().getGuiFont().getCharSet().getRenderedSize();
		this.setSize(size * 3); // font size
		this.setText(amount + ""); // the text
		this.setColor(fontColor); // font color
		this.startX = startX;
		this.animationTime = 0;
		this.maxAnimationTime = animationTime;
		this.yFunction = new LinearFunction(0, startY - this.getLineHeight() / 2, animationTime, finishY);
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
		return this.animationTime == maxAnimationTime;
	}

	@Override
	public void simpleUpdate(float tpf) {
		this.animationTime = this.animationTime + tpf;
		if (this.animationTime > maxAnimationTime)
			this.animationTime = maxAnimationTime;

		this.setLocalTranslation(this.startX - this.getLineWidth() / 2, yFunction.function(animationTime), 1);

		if (this.animationDone())
			GUI2D.getInstance().getGuiNode().detachChild((Spatial) this);
	}

}
