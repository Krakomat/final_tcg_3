package gui2d.animations;

import java.util.concurrent.Callable;

import model.enums.Sounds;
import gui2d.GUI2D;
import gui2d.controller.EffectController;

import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import common.utilities.Function;
import common.utilities.LinearFunction;

public class CoinflipAnimationModel implements AnimateableObject {
	private float animationTime;
	private float maxAnimationTime;
	private Function yFunction;
	private Spatial coin;
	private AmbientLight light;
	private boolean heads, soundPlayed;

	public CoinflipAnimationModel(boolean heads, float startX, float startY, float finishY, float animationTime) {
		this.heads = heads;
		this.soundPlayed = false;
		this.animationTime = 0;
		this.maxAnimationTime = animationTime;

		this.coin = GUI2D.getInstance().getAssetManager().loadModel("assets/models/coin/coin.scene");
		this.coin.scale(0.5f, 0.5f, 0.5f);
		this.coin.rotate(180.0f, 0.0f, 0.0f);
		this.coin.setLocalTranslation(-1, 0.25f, 1);

		light = new AmbientLight();
		light.setColor(ColorRGBA.White.mult(0.5f));

		LinearFunction startFunction = new LinearFunction(0, startY, animationTime / 4, startY);
		LinearFunction tossUpFunction = new LinearFunction(animationTime / 4, startY, animationTime / 2, finishY);
		LinearFunction tossDownFunction = new LinearFunction(animationTime / 2, finishY, animationTime * 0.75f, startY);
		LinearFunction finishFunction = new LinearFunction(animationTime * 0.75f, startY, animationTime, startY);

		yFunction = new Function();
		yFunction.addFunctionPart(0, animationTime / 4, startFunction);
		yFunction.addFunctionPart(animationTime / 4, animationTime / 2, tossUpFunction);
		yFunction.addFunctionPart(animationTime / 2, animationTime * 0.75f, tossDownFunction);
		yFunction.addFunctionPart(animationTime * 0.75f, animationTime, finishFunction);
	}

	@Override
	public void startAnimation() {
		// Add to gui node:
		GUI2D.getInstance().enqueue(new Callable<Spatial>() {
			public Spatial call() throws Exception {
				GUI2D.getInstance().getRootNode().attachChild(coin);
				// You must add a light to make the model visible
				GUI2D.getInstance().getRootNode().addLight(light);
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

		this.coin.setLocalTranslation(this.coin.getLocalTranslation().x, yFunction.function(animationTime), 1);
		if (this.animationTime <= this.maxAnimationTime * 0.25f)
			this.coin.rotate(tpf / 100, 0, 0);
		else if (this.animationTime <= this.maxAnimationTime * 0.75f) {
			if (!this.soundPlayed) {
				EffectController.playSound(Sounds.COINFLIP);
				this.soundPlayed = true;
			}
			this.coin.rotate(tpf / 50, 0, 0);
		} else {
			Quaternion roll180 = new Quaternion();
			roll180.fromAngleAxis(this.heads ? 4 : 1, new Vector3f(1, 0, 0));
			coin.setLocalRotation(roll180);
		}

		if (this.animationDone()) {
			GUI2D.getInstance().getRootNode().detachChild(coin);
			GUI2D.getInstance().getRootNode().removeLight(light);
		}
	}

}
