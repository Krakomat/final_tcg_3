package gui2d.controller;

import gui2d.GUI2D;
import gui2d.animations.AnimateableObject;
import gui2d.animations.Animation;
import gui2d.animations.CardDrawAnimation;
import gui2d.animations.CardDrawAnimationImage;
import gui2d.geometries.HandCardManager2D;

import java.util.ArrayList;
import java.util.List;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import model.database.Database;
import model.enums.Color;
import model.enums.PositionID;
import model.enums.Sounds;
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
	private Color ownColor;

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
				GUI2D.getInstance().getGuiNode().detachChild((Spatial) this.animatedObjects.get(i));
				this.animatedObjects.remove(i);
				i--;
			}
		}
		this.lock.unlock();
	}

	/**
	 * Adds an animateable object to this controller.
	 * 
	 * @param animation
	 * @throws InterruptedException
	 */
	public AnimateableObject[] addAnimation(Animation animation) throws InterruptedException {
		GUI2D gui = GUI2D.getInstance();
		this.lock.lock();
		AnimateableObject[] animObjects = null;
		switch (animation.getAnimationType()) {
		case CARD_DRAW:
			CardDrawAnimation drawAnimation = (CardDrawAnimation) animation;
			if (drawAnimation.getPlayerColor() != null) {
				Color drawColor = drawAnimation.getPlayerColor();
				Node deck = (Node) gui.getIngameController().getPositionGeometry(drawColor == Color.BLUE ? PositionID.BLUE_DECK : PositionID.RED_DECK, ownColor);
				HandCardManager2D hand = (HandCardManager2D) gui.getIngameController().getPositionGeometry(
						drawColor == Color.BLUE ? PositionID.BLUE_HAND : PositionID.RED_HAND, ownColor);

				animObjects = new AnimateableObject[1];
				AnimateableObject animObj = new CardDrawAnimationImage("AnimatedHandCard", Database.getTextureKey("00000"), gui.getResolution().getKey() * 0.06f,
						gui.getResolution().getKey() * 0.06f * 1.141f, deck.getLocalTranslation().x, deck.getLocalTranslation().y, hand.getxPos(), hand.getyPos(),
						200);
				gui.getGuiNode().attachChild((Spatial) animObj);
				this.animatedObjects.add(animObj);
				animObjects[0] = animObj;
			} else {
				animObjects = new AnimateableObject[2];
				{
					Node deck = (Node) gui.getIngameController().getPositionGeometry(PositionID.BLUE_DECK, ownColor);
					HandCardManager2D hand = (HandCardManager2D) gui.getIngameController().getPositionGeometry(PositionID.BLUE_HAND, ownColor);

					AnimateableObject animObj = new CardDrawAnimationImage("AnimatedHandCard", Database.getTextureKey("00000"),
							gui.getResolution().getKey() * 0.06f, gui.getResolution().getKey() * 0.06f * 1.141f, deck.getLocalTranslation().x,
							deck.getLocalTranslation().y, hand.getxPos(), hand.getyPos(), 200);
					gui.getGuiNode().attachChild((Spatial) animObj);
					this.animatedObjects.add(animObj);
					animObjects[0] = animObj;
				}
				{
					Node deck = (Node) gui.getIngameController().getPositionGeometry(PositionID.RED_DECK, ownColor);
					HandCardManager2D hand = (HandCardManager2D) gui.getIngameController().getPositionGeometry(PositionID.RED_HAND, ownColor);

					AnimateableObject animObj = new CardDrawAnimationImage("AnimatedHandCard", Database.getTextureKey("00000"),
							gui.getResolution().getKey() * 0.06f, gui.getResolution().getKey() * 0.06f * 1.141f, deck.getLocalTranslation().x,
							deck.getLocalTranslation().y, hand.getxPos(), hand.getyPos(), 200);
					gui.getGuiNode().attachChild((Spatial) animObj);
					this.animatedObjects.add(animObj);
					animObjects[1] = animObj;
				}
			}
			EffectController.playSound(Sounds.DRAW);
			break;
		default:
			System.err.println("Could not parse AnimationType of animation object in addAnimation!");
			break;
		}

		this.lock.unlock();
		return animObjects;
	}

	public void setPlayerColor(Color color) {
		ownColor = color;
	}
}
